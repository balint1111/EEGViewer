package root.main.common;

import edffilereader.data.EEG_Data;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DataRecord {

    private float[][] data;

    private Integer dataRecordNumber;

    private Long lastRequestTime;

    DataRecord(){};

    public DataRecord(float[][] data, Integer dataRecordNumber) {
        this.data = data;
        this.dataRecordNumber = dataRecordNumber;
    }

    public Integer getDataRecordNumber() {
        return dataRecordNumber;
    }

    public void setDataRecordNumber(Integer dataRecordNumber) {
        this.dataRecordNumber = dataRecordNumber;
    }

    public float[][] getData() {
        lastRequestTime = System.currentTimeMillis();
        return data;
    }
    public float[] getChannelData(int i) {
        lastRequestTime = System.currentTimeMillis();
        return data[i];
    }
}
