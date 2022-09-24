package root.main;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import root.async.AsyncExecutor;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

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
    public void showDataRecord(int from, int to) {
        //interupt the thread if running
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }


        if (backgroundExecutor.getQueue().isEmpty()) {
            backgroundExecutor.execute(() -> {
                try {
                    thread = Thread.currentThread();
                    preLoadInterrupt();
//                    System.out.println("from: " + from + " to: " + to);
                    this.from = from;
                    this.to = to;

                    List<DataRecord> dataRecordsFromTo = dataModel.getDataRecordFromTo(from, to);

                    List<Double>[] channelsOriginalRes = Util.dataRecordsRepackage(dataRecordsFromTo, i -> updateHandler.getMyPolylineList().stream().anyMatch(myPolyline -> myPolyline.getChannelNumber() == i));

                    List<Double>[] downSampledChannels = Util.getLists(channelsOriginalRes,
                            (i) -> i < numberOfChannels ? Optional.of(updateHandler.getMyPolylineList().get(i).getHorizontalResolution().get()) : Optional.empty());

                    updateHandler.setYVectors(downSampledChannels);
                    updateHandler.update();
                    //asyncExecutor.preLoadAroundPage(3);
                } catch (InterruptedException e) {
                    log.info("Thread: " + Thread.currentThread() + " interrupted");
                } catch (ClosedByInterruptException e) {
                    log.info("ClosedByInterruptException: " + Thread.currentThread() + " interrupted");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
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

    @SneakyThrows
    public void showFirstPage(int range) {
        showDataRecord(0, range);
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
