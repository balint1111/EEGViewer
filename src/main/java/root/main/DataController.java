package root.main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import root.async.AsyncExecutor;

import java.nio.channels.ClosedByInterruptException;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Data
@Slf4j
@Component
public class DataController {

    private UpdateHandlerController updateHandlerController;
    private DataModel dataModel;
    private AsyncExecutor asyncExecutor;
    private ThreadPoolExecutor backgroundExecutor;
    private General general;
    private Thread thread;

    @Autowired
    private void init(AsyncExecutor asyncExecutor,
                      ThreadPoolExecutor backgroundExecutor,
                      @Lazy General general,
                      @Lazy DataModel dataModel,
                      @Lazy UpdateHandlerController updateHandlerController) {
        this.asyncExecutor = asyncExecutor;
        this.backgroundExecutor = backgroundExecutor;
        this.general = general;
        this.dataModel = dataModel;
        this.updateHandlerController = updateHandlerController;


    }

    @SneakyThrows
    public void showDataRecord() {
        int from = (int) general.getScrollBarValue().get();
        int to = from + general.getPageSizeProperty().get() - 1;
        if ((to - from) < 0) return;

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


                    List<DataRecord> dataRecordsFromTo = dataModel.getDataRecordsFromTo(from, to);

                    List<Double>[] downSampledChannels;

                    synchronized (updateHandlerController.getMyPolylineList()) {
                        List<Double>[] channelsOriginalRes = Util.dataRecordsRepackage(dataRecordsFromTo, i -> updateHandlerController.getMyPolylineList().parallelStream().anyMatch(myPolyline -> myPolyline.getChannelNumber().equals(i)));

                        downSampledChannels = Util.getLists(channelsOriginalRes, (i) -> updateHandlerController.getMyPolylineList().stream().filter(myPolyline -> myPolyline.getChannelNumber().equals(i)).findFirst());
                        updateHandlerController.setYVectors(downSampledChannels);
                        updateHandlerController.update();
                    }


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
    public void jumpToPosition() {
        showDataRecord();
    }

    public void preLoadAroundPage(int numberOfPages) throws Exception {
//        int range = to - from + 1;
//        int loadFrom1 = from - (numberOfPages * range);
//        int loadTo1 = from - 1;
//        int loadFrom2 = to + 1;
//        int loadTo2 = to + (numberOfPages * range);
//        preLoadDataRecord(loadFrom1, loadTo1);
//        preLoadDataRecord(loadFrom2, loadTo2);
    }

    private Boolean preLoadRunning = false;

    private void preLoadInterrupt() {
        preLoadRunning = false;
    }

    public void preLoadDataRecord(int from, int to) throws Exception {
        preLoadRunning = true;
        dataModel.getDataRecordsFromTo(from, to);
        preLoadRunning = false;
    }


}
