package edffilereader.header;

import java.util.HashMap;

/**
 *
 * @author balin
 */
public class BDF_Header extends EEG_Header{
    
    
    private Integer startData;
    private Integer dataRecordSize;
    
    public BDF_Header(){
        extraParameters = new HashMap<>();
    }
    
    
    public Integer getStartData() {
        return startData;
    }

    public Integer getDataRecordSize() {
        return dataRecordSize;
    }
    
    public void setStartData(Integer startData) {
        this.startData = startData;
    }

    public void setDataRecordSize(Integer dataRecordSize) {
        this.dataRecordSize = dataRecordSize;
    }

    public Integer getHeaderType() {
        return (Integer)extraParameters.get("headerType");
    }

    public String getPatient() {
        return (String)extraParameters.get("patient");
    }

    public String getRecording() {
        return (String)extraParameters.get("recording");
    }

    public String getStartDate() {
        return (String)extraParameters.get("startDate");
    }

    public String getStartTime() {
        return (String)extraParameters.get("startTime");
    }

    public Integer getNumberOfBytes() {
        return (Integer)extraParameters.get("numberOfBytes");
    }

    public String getVersion() {
        return (String)extraParameters.get("version");
    }

    public Integer getHeaderSize() {
        return (Integer)extraParameters.get("headerSize");
    }

    public Double getDurationOfDataRecord() {
        return (Double)extraParameters.get("durationOfDataRecord");
    }

    public void setHeaderType(Integer headerType) {
        extraParameters.put("headerType", headerType);
    }

    public void setPatient(String patient) {
        extraParameters.put("patient", patient);
    }

    public void setRecording(String recording) {
        extraParameters.put("recording", recording);
    }

    public void setStartDate(String startDate) {
        extraParameters.put("startDate", startDate);
    }

    public void setStartTime(String startTime) {
        extraParameters.put("startTime", startTime);
    }

    public void setNumberOfBytes(Integer numberOfBytes) {
        extraParameters.put("numberOfBytes", numberOfBytes);
    }

    public void setVersion(String version) {
        extraParameters.put("version", version);
    }

    public void setHeaderSize(Integer headerSize) {
        extraParameters.put("headerSize", headerSize);
    }

    public void setDurationOfDataRecord(Double durationOfDataRecord) {
        extraParameters.put("durationOfDataRecord", durationOfDataRecord);
    }

    
    
}
