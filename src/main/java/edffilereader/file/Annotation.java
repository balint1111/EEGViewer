package edffilereader.file;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
public class Annotation {

    private Double onset;
    private Double duration;
    private String description;

    private static final char TWENTY = 20;
    private static final char TWENTY_ONE = 21;

    @SneakyThrows
    public static Annotation fromBinary(byte[] data, ByteOrder byteOrder) {
        String annotationString = new String(data, 0, data.length, "US-ASCII");
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(byteOrder);

//        Pattern r = Pattern.compile("([+-][\\d]+.?[\\d]+)+("+TWENTY_ONE+"[+-][\\d]+.?[\\d]+)?"+TWENTY+"?("+TWENTY+".*"+TWENTY+")+");
//
//        Matcher m = r.matcher(annotationString);
        Annotation annotation = new Annotation();
//        if (m.find( )) {
//            System.out.println("match");
//            annotation.setOnset(m.group(0));
//            annotation.setDuration(m.group(1) != null ? m.group().substring(1) : null);
//            annotation.setDescription(m.group(2));
//        }
        String[] split = annotationString.split(String.valueOf(TWENTY));
        if (split.length <= 3) {
            return null;
        }
        for (String s1 : split) {
            System.out.println(s1);
        }
        String[] onSetAndDuration = split[2].split(String.valueOf(TWENTY_ONE));
        annotation.setOnset(Double.valueOf(onSetAndDuration[0]));
        annotation.setDuration(onSetAndDuration.length != 1 ? Double.valueOf(onSetAndDuration[1]) : null);
        annotation.setDescription(split[3]);
        return annotation;
    }

//    public String getOnsetAsString() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis((long) (onset * 1000));
//        return dateFormat.format(calendar.getTime());
//    }
//
//    public String getEndTimeAsString() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis((long) ((onset + duration) * 1000));
//        return dateFormat.format(calendar.getTime());
//    }
}
