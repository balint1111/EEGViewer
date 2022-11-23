package root.main;

import javafx.application.Platform;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import root.async.AsyncExecutor;
import root.exceptions.DataControllerException;
import root.exceptions.DataModelException;
import root.main.common.DataRecord;
import root.main.common.Util;
import root.main.fx.UpdateHandlerController;
import root.main.fx.custom.ScrollProperty;

import java.nio.channels.ClosedByInterruptException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

@Getter
@Slf4j
@Component
public class DataController {

    private UpdateHandlerController updateHandlerController;
    private DataModel dataModel;
    //    private AsyncExecutor asyncExecutor;
    private ThreadPoolExecutor backgroundExecutor;
    private General general;
    private Thread thread = new Thread();

    @Autowired
    private void init(
//            AsyncExecutor asyncExecutor,
            ThreadPoolExecutor backgroundExecutor,
            @Lazy General general,
            @Lazy DataModel dataModel,
            @Lazy UpdateHandlerController updateHandlerController) {
//        this.asyncExecutor = asyncExecutor;
        this.backgroundExecutor = backgroundExecutor;
        this.general = general;
        this.dataModel = dataModel;
        this.updateHandlerController = updateHandlerController;
    }

    @SneakyThrows
    public void showDataRecord() {
        //interupt the thread if running
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }


        if (backgroundExecutor.getQueue().isEmpty()) {
            backgroundExecutor.execute(() -> {
                try {
                    if (backgroundExecutor.getQueue().isEmpty()) {
                        thread = Thread.currentThread();
//                    preLoadInterrupt();
                        ScrollProperty scrollProperty = Optional.ofNullable(general.getScrollBarValue()).orElseThrow(() -> new DataControllerException("scrollBarValue is Null"));
                        int offset = scrollProperty.getPosition().getOffsetProperty().get();
                        int from = scrollProperty.getPosition().getRecordProperty().get();
                        int to = from + general.getPageSizeProperty().get() - 1 + (offset != 0 ? 1 : 0);
                        if ((to - from) < 0) return;
//                    System.out.println("from: " + from + " to: " + to);

                        if (Thread.interrupted()) throw new InterruptedException();
                        List<DataRecord> dataRecordsFromTo = dataModel.getDataRecordsFromTo(from, to);
                        if (Thread.interrupted()) throw new InterruptedException();
                        List<Double>[] downSampledChannels;

                        synchronized (updateHandlerController.getMyPolylineList()) {
                            float[][] channelsOriginalRes = Util.dataRecordsRepackage(dataRecordsFromTo, i -> updateHandlerController.getMyPolylineList().stream().anyMatch(myPolyline -> myPolyline.getChannelNumber().equals(i)));
                            if (offset != 0)
                                Util.offsetData(channelsOriginalRes, offset, dataModel.getEeg_file().getHeader().getNumberOfSamples());
                            dataRecordsFromTo = null;
                            if (Thread.interrupted()) throw new InterruptedException();
                            downSampledChannels = Util.getLists(channelsOriginalRes, (i) -> updateHandlerController.getMyPolylineList().stream().filter(myPolyline -> myPolyline.getChannelNumber().equals(i)).findFirst());
                            channelsOriginalRes = null;
                            if (Thread.interrupted()) throw new InterruptedException();
                            updateHandlerController.setYVectors(downSampledChannels);
                            downSampledChannels = null;
                            if (Thread.interrupted()) throw new InterruptedException();
                            updateHandlerController.update();
                        }
                    }
                    //asyncExecutor.preLoadAroundPage(3);
                } catch (InterruptedException e) {
//                    log.info("Thread: " + Thread.currentThread() + " interrupted");
                } catch (ClosedByInterruptException e) {
//                    log.info("ClosedByInterruptException: " + Thread.currentThread() + " interrupted");
                } catch (DataModelException | DataControllerException e) {
                    log.info(e.getMessage());
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

//    public void preLoadAroundPage(int numberOfPages) throws Exception {
//        int range = to - from + 1;
//        int loadFrom1 = from - (numberOfPages * range);
//        int loadTo1 = from - 1;
//        int loadFrom2 = to + 1;
//        int loadTo2 = to + (numberOfPages * range);
//        preLoadDataRecord(loadFrom1, loadTo1);
//        preLoadDataRecord(loadFrom2, loadTo2);
//    }

//    private Boolean preLoadRunning = false;

//    private void preLoadInterrupt() {
//        preLoadRunning = false;
//    }

//    public void preLoadDataRecord(int from, int to) throws Exception {
//        preLoadRunning = true;
//        dataModel.getDataRecordsFromTo(from, to);
//        preLoadRunning = false;
//    }


}
