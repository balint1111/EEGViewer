package root.main;

import com.github.ggalmazor.ltdownsampling.Point;
import edffilereader.data.EEG_Data;
import eu.bengreen.data.utility.LargestTriangleThreeBuckets;
import org.rrd4j.graph.DownSampler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static List<DataRecord> EEG_DataToDataRecords(EEG_Data eeg_data) {
//        eeg_data.getRecordOfTheEEG_Data()
        return null;
    }

}
