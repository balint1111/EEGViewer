package root.main;

import com.github.ggalmazor.ltdownsampling.LTThreeBuckets;
import edffilereader.data.EEG_Data;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import root.async.AsyncExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Component
@Data
public class DataController {

    private UpdateHandler updateHandler;

    private DataModel dataModel;

    private final AsyncExecutor asyncExecutor;

    private final ExecutorService backgroundExecutor;

    public DataController(AsyncExecutor asyncExecutor, ExecutorService backgroundExecutor) {
        this.asyncExecutor = asyncExecutor;
        this.backgroundExecutor = backgroundExecutor;
    }


    private Integer from = 0;
    private Integer to;

    public void setNumberOfChannels(Integer numberOfChannels) {
        this.numberOfChannels = numberOfChannels;
        updateHandler.setNumberOfChannels(numberOfChannels);
    }

    private Integer numberOfChannels;

    private Thread thread;


    public void showDataRecord(int from, int to) {
        if (thread != null && !thread.isInterrupted())
            thread.interrupt();
        backgroundExecutor.submit(() -> {
            try {
                thread = Thread.currentThread();
                preLoadInterrupt();
                System.out.println("from: " + from + " to: " + to);
                this.from = from;
                this.to = to;
                List<Double>[] myDoubleArray = new ArrayList[numberOfChannels];

                List<DataRecord> dataRecordFromTo = dataModel.getDataRecordFromTo(from, to);
                for (int i = 0; i < myDoubleArray.length; i++) {
                    myDoubleArray[i] = new ArrayList<>();
                    for (DataRecord dataRecord : dataRecordFromTo) {
                        myDoubleArray[i].addAll(new ArrayList<>(Arrays.stream(dataRecord.getData().channels[i].getDoubleArray()).boxed().toList()));
                    }
                }
//                for (int i = from; i <= to; i++) {
//                    EEG_Data data = dataModel.getDataRecord(i);
//                    double[][] doubleArray = data.getDoubleArray();
//                    for (int j = 0; j < myDoubleArray.length; j++) {
//                        myDoubleArray[j].addAll(new ArrayList<>(Arrays.stream(doubleArray[j]).boxed().toList()));
//                    }
//                }
                List<Double>[] finalArray = new ArrayList[numberOfChannels];
                for (int i = 0, myDoubleArrayLength = myDoubleArray.length; i < myDoubleArrayLength; i++) {
                    List<Double> doubles = myDoubleArray[i];
                    doubles = Util.convertPointsToDouble(LTThreeBuckets.sorted(Util.getPhysicalPoints(doubles), 998));
                    finalArray[i] = doubles;
                }

                updateHandler.setYVectors(finalArray);
                updateHandler.update();
                //asyncExecutor.preLoadAroundPage(3);
                System.out.println("ay");
            } catch (InterruptedException e) {
                System.out.println("Thread: " + Thread.currentThread() + " interrupted");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @SneakyThrows
    public void showNextPage() {
        int range = to - from;
        showDataRecord(to + 1, to + 1 + range);
    }

    @SneakyThrows
    public void showPreviousPage() {
        int range = to - from;
        int newFrom = from - range - 1;
        if (newFrom > 0) {
            showDataRecord(newFrom, from - 1);
        } else {
            showDataRecord(0, range);
        }
    }

    public void preLoadAroundPage(int numberOfPages) throws IOException, InterruptedException {
        int range = to - from + 1;
        int loadFrom1 = from - (numberOfPages * range);
        int loadTo1 = from - 1;
        int loadFrom2 = to + 1;
        int loadTo2 = to + (numberOfPages * range);
        preLoadDataRecord(loadFrom1, loadTo1);
        preLoadDataRecord(loadFrom2, loadTo2);
    }

    private Boolean preLoadRunning = false;

    private void preLoadInterrupt() {
        preLoadRunning = false;
    }

    public void preLoadDataRecord(int from, int to) throws IOException, InterruptedException {
        preLoadRunning = true;
        from = Math.max(from, 0);
        System.out.println("preLoad from: " + from + " to: " + to);
        for (int i = from; i <= to; i++) {
            if (!preLoadRunning) {
                System.out.println("preLoad from: " + from + " to: " + to + " interrupted");
                return;
            }
            dataModel.getDataRecord(i);
        }
        preLoadRunning = false;
    }

    public void rangeChange(Integer range) throws IOException, InterruptedException {
        showDataRecord(from, from + range - 1);
    }

    public UpdateHandler getUpdateHandler() {
        return updateHandler;
    }

    public void setUpdateHandler(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }
}
