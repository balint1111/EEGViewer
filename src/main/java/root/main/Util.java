package root.main;

import com.github.ggalmazor.ltdownsampling.Point;
import edffilereader.data.EEG_Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

}
