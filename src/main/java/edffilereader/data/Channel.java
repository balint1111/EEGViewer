package edffilereader.data;


import java.nio.ByteBuffer;

/**
 *
 * @author balin
 */
public class Channel{

    String label;
    String transducerType;
    String physicalDimension;
    Double physicalMinimum;
    Double physicalMaximum;
    Double digitalMinimum;
    Double digitalMaximum;
    String preFiltering;
    Integer numberOfSamples;


    Integer storedRecordsOfTheChannel;
    float bitValue;
    float offset;
    int sampleLength;

    public byte[] data;

    public Channel(){

    }

    public static Channel copy(Channel original){
        return new Channel(original.label, original.transducerType, original.physicalDimension,
                original.physicalMinimum, original.physicalMaximum, original.digitalMinimum,
                original.digitalMaximum, original.preFiltering, original.numberOfSamples,
                original.storedRecordsOfTheChannel, original.sampleLength);
    }

    public Channel(String label,
            String transducerType,
            String physicalDimension,
            Double physicalMinimum,
            Double physicalMaximum,
            Double digitalMinimum,
            Double digitalMaximum,
            String preFiltering,
            Integer numberOfSamples,
            Integer storedRecordsOfTheChannel,
            int sampleLength) {
        this.label = label;
        this.transducerType = transducerType;
        this.physicalDimension = physicalDimension;
        this.physicalMinimum = physicalMinimum;
        this.physicalMaximum = physicalMaximum;
        this.digitalMinimum = digitalMinimum;
        this.digitalMaximum = digitalMaximum;
        this.preFiltering = preFiltering;
        this.numberOfSamples = numberOfSamples;
        this.storedRecordsOfTheChannel = storedRecordsOfTheChannel;
        data = new byte[numberOfSamples*storedRecordsOfTheChannel*sampleLength];
        this.sampleLength = sampleLength;
        calculateValues();
    }

    public int[] getIntArray(){
        int [] arr = new int[data.length/sampleLength];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = getInt(i);
        }
        return arr;
    }

    public float [] getDoubleArrayOfRecord(int relativeRecordNumber){
        float [] arr = new float[numberOfSamples];
        int recordStart = relativeRecordNumber * numberOfSamples;
        for (int i = 0; i < arr.length; i++) {
            arr[i] = bitValue * (offset + (float) getInt(recordStart + i));
        }
        return arr;
    }

    public byte[][] getByteArrayOfRecord(int relativeRecordNumber){
        byte [][] arr = new byte[numberOfSamples*sampleLength][];
        int recordStart = relativeRecordNumber * numberOfSamples;
        for (int i = 0; i < arr.length; i++) {
            arr[i] = getBytes(recordStart + i);
        }
        return arr;
    }

    public float [] getDoubleArray(){
        float [] arr = new float[data.length/sampleLength];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = bitValue * (offset + (float) getInt(i));
        }
        return arr;
    }

    public Float [] getDoubleArrayObject(){
        Float [] arr = new Float[data.length/sampleLength];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = bitValue * (offset + (float) getInt(i));
        }
        return arr;
    }

    private byte[] getBytes(int sampleNumber){
        byte[] value = new byte[sampleLength];
        for(int i = 0;i < sampleLength-1 ;i++){
            value[i] = data[sampleNumber*sampleLength + i];
        }
        return value;
    }

    private Integer getInt(int sampleNumber){
        Integer value = 0;
        for(int i = 0;i < sampleLength-1 ;i++){
            value |= (data[sampleNumber*sampleLength + i] & 0xFF) << (8*i);
        }
        value |= (data[sampleNumber*sampleLength + sampleLength-1]) << (8*(sampleLength-1));
        return value;
    }

    public void calculateValues() {
        bitValue = ((float) (physicalMaximum - physicalMinimum)) / ((float)(digitalMaximum - digitalMinimum));
        offset = ((float)physicalMaximum.floatValue()) / bitValue - ((float)digitalMaximum.floatValue());
    }

    public Channel getRecordOfTheChanel(int relativeRecordNumber) throws Exception {
        ByteBuffer buffer =  ByteBuffer.wrap(data);
        if (relativeRecordNumber <= storedRecordsOfTheChannel) {
            Channel copy = copy(this);
            copy.data = new byte[numberOfSamples * sampleLength];
            int channelStart = relativeRecordNumber * numberOfSamples * sampleLength;
            buffer.get(channelStart, copy.data, 0, sampleLength * numberOfSamples);
            copy.setStoredRecordsOfTheChannel(1);
            return copy;
        } else {
            throw new Exception("Invalid relative record number");
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTransducerType() {
        return transducerType;
    }

    public void setTransducerType(String transducerType) {
        this.transducerType = transducerType;
    }

    public String getPhysicalDimension() {
        return physicalDimension;
    }

    public void setPhysicalDimension(String physicalDimension) {
        this.physicalDimension = physicalDimension;
    }

    public Double getPhysicalMinimum() {
        return physicalMinimum;
    }

    public void setPhysicalMinimum(Double physicalMinimum) {
        this.physicalMinimum = physicalMinimum;
    }

    public Double getPhysicalMaximum() {
        return physicalMaximum;
    }

    public void setPhysicalMaximum(Double physicalMaximum) {
        this.physicalMaximum = physicalMaximum;
    }

    public Double getDigitalMinimum() {
        return digitalMinimum;
    }

    public void setDigitalMinimum(Double digitalMinimum) {
        this.digitalMinimum = digitalMinimum;
    }

    public Double getDigitalMaximum() {
        return digitalMaximum;
    }

    public void setDigitalMaximum(Double digitalMaximum) {
        this.digitalMaximum = digitalMaximum;
    }

    public String getPreFiltering() {
        return preFiltering;
    }

    public void setPreFiltering(String preFiltering) {
        this.preFiltering = preFiltering;
    }

    public Integer getStoredRecordsOfTheChannel() {
        return storedRecordsOfTheChannel;
    }

    public void setStoredRecordsOfTheChannel(Integer storedRecordsOfTheChannel) {
        this.storedRecordsOfTheChannel = storedRecordsOfTheChannel;
    }

}
