package edffilereader.data;

import com.github.ggalmazor.ltdownsampling.Point;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author balin
 */
public class EEG_Data {

    public Channel[] channels;

    public EEG_Data() {

    }

    public EEG_Data copy(EEG_Data original) {
        EEG_Data newEeg_data = new EEG_Data();
        newEeg_data.channels = original.channels;
        return newEeg_data;
    }

    public EEG_Data getRecordOfTheEEG_Data(int relativeRecordNumber) throws Exception {
        EEG_Data copy = copy(this);
        Channel[] channels = new Channel[this.channels.length];
        for (int i = 0; i < copy.channels.length; i++) {
            channels[i] = this.channels[i].getRecordOfTheChanel(relativeRecordNumber);
        }
        copy.channels = channels;
        return copy;
    }

    public EEG_Data(
            int storedRecordNumber,
            int channelNumber,
            List<String> labelsOfTheChannels,
            List<String> transducerTypes,
            List<String> physicalDimensionOfChannels,
            List<Double> physicalMinimums,
            List<Double> physicalMaximums,
            List<Double> digitalMinimums,
            List<Double> digitalMaximums,
            List<String> prefilterings,
            List<Integer> numberOfSamples,
            int sampleLength
    ) throws InterruptedException {
        channels = new Channel[channelNumber];
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < channelNumber; i++) {
            int temp = i;
            Runnable task = () -> {
                channels[temp] = new Channel(labelsOfTheChannels.get(temp),
                        transducerTypes.get(temp),
                        physicalDimensionOfChannels.get(temp),
                        physicalMinimums.get(temp),
                        physicalMaximums.get(temp),
                        digitalMinimums.get(temp),
                        digitalMaximums.get(temp),
                        prefilterings.get(temp),
                        numberOfSamples.get(temp),
                        storedRecordNumber,
                        sampleLength
                );
            };
            executor.execute(task);

        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
    }

    public int[][] getIntArray() {
        int[][] arr = new int[channels.length][];
        for (int i = 0; i < channels.length; i++) {
            arr[i] = channels[i].getIntArray();
        }
        return arr;
    }

//    public double[][] getDoubleArray(){
//        double[][] arr = new double[channels.length][];
//        for (int i=0;i<channels.length;i++) {
//            arr[i] = channels[i].getDoubleArray();
//        }
//        return arr;
//    }

    public double[][] getDoubleArray() throws InterruptedException {
        double[][] arr = new double[channels.length][];
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < channels.length; i++) {
            int temp = i;
            Runnable task = () -> {
                arr[temp] = channels[temp].getDoubleArray();
            };
            executor.execute(task);
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
        return arr;
    }


}
