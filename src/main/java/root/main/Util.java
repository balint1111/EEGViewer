package root.main;

import com.github.ggalmazor.ltdownsampling.Point;
import custom.component.MyPolyline;
import edffilereader.data.EEG_Data;
import eu.bengreen.data.utility.LargestTriangleThreeBuckets;
import org.rrd4j.graph.DownSampler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Util {

    public static List<Point> getPhysicalPoints(List<Double> doubleArray) throws InterruptedException {
        ArrayList<Point> channelPoints = new ArrayList<>();
        for (int j = 0; j < doubleArray.size(); j++) {
            BigDecimal x = new BigDecimal(j);
            BigDecimal y = BigDecimal.valueOf(doubleArray.get(j));
            channelPoints.add(new Point(x, y));
        }
        return channelPoints;
    }

    public static List<Double> convertPointsToDouble(List<Point> pointsOfChannels) {
        ArrayList<Double> channelPoints = new ArrayList<>();
        for (int j = 0; j < pointsOfChannels.size(); j++) {
            channelPoints.add(pointsOfChannels.get(j).getY().doubleValue());
        }
        return channelPoints;
    }

    public static List<Double> downSample(List<Double> doubles, int horizontalResolution) {
        LargestTriangleThreeBuckets largestTriangleThreeBuckets = new LargestTriangleThreeBuckets(horizontalResolution);
        double[] array = doubles.stream().mapToDouble(d -> d).map(d -> (double) d).toArray();
        long[] timestamps = LongStream.range(0, doubles.size()).map(operand -> (long) operand).toArray();
        DownSampler.DataSet downsize = largestTriangleThreeBuckets.downsize(timestamps, array);
        doubles = Arrays.stream(downsize.values).boxed().collect(Collectors.toList());
        return doubles;
    }

    public static List<DataRecord> EEG_DataToDataRecords(EEG_Data eeg_data, int from) {
        ArrayList<DataRecord> dataRecords = new ArrayList<>();
        for (int j = 0; j < eeg_data.getStoredRecordNumber(); j++) {
            double[][] arr = new double[eeg_data.channels.length][];
            for (int i = 0; i < eeg_data.channels.length; i++) {
                int temp = i;
                arr[temp] = eeg_data.channels[temp].getDoubleArrayOfRecord(j);
            }
            dataRecords.add(new DataRecord(arr, from + j));
        }
        return dataRecords;
    }

    public static List<Double>[] dataRecordsRepackage(List<DataRecord> dataRecordsFromTo, Function<Integer, Boolean> predicate) {
        int numberOfChannels = dataRecordsFromTo.get(0).getData().length;
        List<Double>[] channelsOriginalRes = new ArrayList[numberOfChannels];
        for (int i = 0; i < channelsOriginalRes.length; i++) {
            if (predicate.apply(i)) {
                channelsOriginalRes[i] = new ArrayList<>();
                for (DataRecord dataRecord : dataRecordsFromTo) {
                    channelsOriginalRes[i].addAll(new ArrayList<>(Arrays.stream(dataRecord.getData()[i]).boxed().toList()));
                }
            }
        }
        return channelsOriginalRes;
    }

    public static List<Double>[] getLists(List<Double>[] channelsOriginalRes, Function<Integer, Optional<MyPolyline>> function) {
        List<Double>[] downSampledChannels = new ArrayList[channelsOriginalRes.length];
        for (int i = 0; i < channelsOriginalRes.length; i++) {
            int finalI = i;
            function.apply(i).ifPresent(myPolyline -> {
                List<Double> downSampledChannel = Util.downSample(channelsOriginalRes[myPolyline.getChannelNumber()], myPolyline.getLineProperty().getHorizontalResolution().get());
                downSampledChannels[finalI] = downSampledChannel;
            });
        }
        return downSampledChannels;
    }

}
