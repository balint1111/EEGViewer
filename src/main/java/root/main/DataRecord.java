package root.main;

import edffilereader.data.EEG_Data;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DataRecord {

    private EEG_Data data;

    private Integer dataRecordNumber;

    private Long lastRequestTime;

    DataRecord(){};

    DataRecord(EEG_Data data, Integer dataRecordNumber) {
        this.data = data;
        this.dataRecordNumber = dataRecordNumber;
    }

    public Integer getDataRecordNumber() {
        return dataRecordNumber;
    }

    public void setDataRecordNumber(Integer dataRecordNumber) {
        this.dataRecordNumber = dataRecordNumber;
    }

    public EEG_Data getData() {
        lastRequestTime = System.currentTimeMillis();
        return data;
    }
}
