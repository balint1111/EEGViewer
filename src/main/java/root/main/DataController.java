package root.main;

import edffilereader.data.Channel;
import javafx.beans.property.SimpleLongProperty;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import root.async.AsyncExecutor;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Component
@Data
@Slf4j
public class DataController {

    private UpdateHandler updateHandler;

    private DataModel dataModel;

    private final AsyncExecutor asyncExecutor;

    private final ThreadPoolExecutor backgroundExecutor;

    public DataController(AsyncExecutor asyncExecutor, ThreadPoolExecutor backgroundExecutor) {
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


    @SneakyThrows
    public void showDataRecord(int from, int to, SimpleLongProperty lastUpdate) {
        if (thread != null && !thread.isInterrupted()) {
            System.out.println("interruptRequest");
            thread.interrupt();
        }


        if (backgroundExecutor.getQueue().isEmpty()) {
            System.out.println("update start");
            backgroundExecutor.execute(() -> {
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
                            myDoubleArray[i].addAll(new ArrayList<>(Arrays.stream(dataRecord.getData()[i]).boxed().toList()));
                        }
                    }
                    List<Double>[] finalArray = new ArrayList[numberOfChannels];
                    for (int i = 0, myDoubleArrayLength = myDoubleArray.length; i < myDoubleArrayLength; i++) {
                        List<Double> doubles = myDoubleArray[i];
                        int horizontalResolution = updateHandler.getMyPolylineList().get(i).getHorizontalResolution().get();
                        horizontalResolution=800;
                        doubles = Util.downSample(doubles, horizontalResolution);
                        finalArray[i] = doubles;
                    }

                    updateHandler.setYVectors(finalArray);
                    updateHandler.update();
                    if (lastUpdate != null) {
                        lastUpdate.setValue(System.currentTimeMillis());
                    }
                    //asyncExecutor.preLoadAroundPage(3);
                    System.out.println("ay");
                } catch (InterruptedException e) {
                    System.out.println("Thread: " + Thread.currentThread() + " interrupted");
                } catch (ClosedByInterruptException e) {
                    e.printStackTrace();
                    System.out.println("ClosedByInterruptException: " + Thread.currentThread() + " interrupted");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }


    @SneakyThrows
    public void showNextPage() {
        int range = to - from;
        showDataRecord(to + 1, to + 1 + range, null);
    }

    @SneakyThrows
    public void showPreviousPage() {
        int range = to - from;
        int newFrom = from - range - 1;
        if (newFrom > 0) {
            showDataRecord(newFrom, from - 1, null);
        } else {
            showDataRecord(0, range, null);
        }
    }

    public void preLoadAroundPage(int numberOfPages) throws Exception {
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

    public void preLoadDataRecord(int from, int to) throws Exception {
        preLoadRunning = true;
        dataModel.getDataRecordFromTo(from, to);
        preLoadRunning = false;
    }

    public void rangeChange(Integer range) throws IOException, InterruptedException {
        showDataRecord(from, from + range - 1, null);
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
