package root.main.common;

import com.github.ggalmazor.ltdownsampling.Point;
import root.main.fx.custom.MyPolyline;
import edffilereader.data.EEG_Data;
import eu.bengreen.data.utility.LargestTriangleThreeBuckets;
import org.rrd4j.graph.DownSampler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

    public static List<Double> downSample(float[] doubles, int horizontalResolution) {
        doubles = upSample(doubles, horizontalResolution);
        LargestTriangleThreeBuckets largestTriangleThreeBuckets = new LargestTriangleThreeBuckets(horizontalResolution);
//        double[] array = doubles.stream().mapToDouble(d -> d).map(d -> (double) d).toArray();
        long[] timestamps = LongStream.range(0, doubles.length).map(operand -> (long) operand).toArray();
        DownSampler.DataSet downsize = largestTriangleThreeBuckets.downsize(timestamps, convert(doubles));
        return Arrays.stream(downsize.values).boxed().collect(Collectors.toList());
    }

    private static float[] upSample(float[] doubles, int horizontalResolution) {
        if (horizontalResolution > doubles.length) {
            int multiplier = (int) ((double) horizontalResolution / (double) doubles.length) + 1;
            float[] newData = new float[doubles.length * multiplier];
            for (int i = 0; i < doubles.length; i++) {
                for (int j = 0; j < multiplier; j++) {
                    newData[i*multiplier + j] = doubles[i];
                }
            }
            doubles = newData;
        }
        return doubles;
    }

    private static double[] convert(float[] floats) {
        double[] DoubleArray = new double[floats.length];
        for (int i = 0; i < floats.length; i++) {
            DoubleArray[i] = (double) floats[i];
        }
        return DoubleArray;
    }


    //diuble version
    public static List<DataRecord> EEG_DataToDataRecords(EEG_Data eeg_data, int from) {
        ArrayList<DataRecord> dataRecords = new ArrayList<>();
        for (int j = 0; j < eeg_data.getStoredRecordNumber(); j++) {
            float[][] arr = new float[eeg_data.channels.length][];
            for (int i = 0; i < eeg_data.channels.length; i++) {
                int temp = i;
                arr[temp] = eeg_data.channels[temp].getDoubleArrayOfRecord(j);
            }
            dataRecords.add(new DataRecord(arr, from + j));
        }
        return dataRecords;
    }


    public static float[][] dataRecordsRepackage(List<DataRecord> dataRecordsFromTo, Function<Integer, Boolean> predicate) {
        int numberOfChannels = dataRecordsFromTo.get(0).getData().length;
        float[][] channelsOriginalRes = new float[numberOfChannels][];
        for (int i = 0; i < channelsOriginalRes.length; i++) {
            if (predicate.apply(i)) {
                channelsOriginalRes[i] = getChannel(dataRecordsFromTo, i);
            }
        }
        return channelsOriginalRes;
    }

    public static float[][] offsetData(float[][] data, int offset, List<Integer> numberOfSamples) {
        Integer maxOffset = numberOfSamples.stream().max(Integer::compare).get();
        double offsetMultiplier = (double) offset / (double) maxOffset;
        for (int i = 0; i < data.length; i++) {
            Integer numberOfSample = numberOfSamples.get(i);
            int channelOffset = maxOffset.equals(numberOfSample) ? offset: (int)(numberOfSample * offsetMultiplier);
            float[] channel = data[i];
            if (channel != null) {
                float[] newChannel = new float[channel.length - numberOfSample];
                for (int j = 0; j < newChannel.length; j++) {
                    newChannel[j] = channel[j + channelOffset];
                }
                data[i] = newChannel;
            }
        }
        return data;
    }

    private static float[] getChannel(List<DataRecord> dataRecordsFromTo, int channelNUmber) {
        List<float[]> collect = dataRecordsFromTo.stream().map(dataRecord -> dataRecord.getChannelData(channelNUmber)).collect(Collectors.toList());
        float[] finalList = new float[collect.stream().map(doubles -> doubles.length).reduce(0, Integer::sum)];
        int start = 0;
        for (int i = 0; i < collect.size(); i++) {
            float[] record = collect.get(i);
            for (int j = 0; j < record.length; j++) {
                finalList[start + j] = record[j];
            }
            start += record.length;
        }
        return finalList;
    }

    public static List<Double>[] downSampleAndRepackage(float[][] channelsOriginalRes, Function<Integer, Optional<MyPolyline>> function) {
        List<Double>[] downSampledChannels = new ArrayList[channelsOriginalRes.length];
        for (int i = 0; i < channelsOriginalRes.length; i++) {
            int finalI = i;
            function.apply(i).ifPresent(myPolyline -> {
                List<Double> downSampledChannel = Util.downSample(channelsOriginalRes[myPolyline.getChannelNumber()], myPolyline.getMyPolyLineProperty().getHorizontalResolution().get());
                downSampledChannels[finalI] = downSampledChannel;
            });
        }
        return downSampledChannels;
    }

}
