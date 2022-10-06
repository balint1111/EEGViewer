package root.main;

import edffilereader.data.EEG_Data;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DataRecord {

    private double[][] data;

    private Integer dataRecordNumber;

    private Long lastRequestTime;

    DataRecord(){};

    DataRecord(double[][] data, Integer dataRecordNumber) {
        this.data = data;
        this.dataRecordNumber = dataRecordNumber;
    }

    public Integer getDataRecordNumber() {
        return dataRecordNumber;
    }

    public void setDataRecordNumber(Integer dataRecordNumber) {
        this.dataRecordNumber = dataRecordNumber;
    }

    public double[][] getData() {
        lastRequestTime = System.currentTimeMillis();
        return data;
    }
    public double[] getData(int i) {
        lastRequestTime = System.currentTimeMillis();
        return data[i];
    }
}
