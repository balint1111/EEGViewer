package edffilereader.header;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author balin
 */
public abstract class EEG_Header {
    
    protected FileChannel fileChannel;

    protected String filename;
    protected Integer numberOfDataRecords;
    protected Integer numberOfChannels;
    protected List<String> labelsOfTheChannels;
    protected List<String> transducerTypes;
    protected List<String> physicalDimensionOfChannels;
    protected List<Double> physicalMinimums;
    protected List<Double> physicalMaximums;
    protected List<Double> digitalMinimums;
    protected List<Double> digitalMaximums;
    protected List<String> prefilterings;
    protected List<Integer> numberOfSamples;
    
    protected Map<String,Object> extraParameters;
    
    public List<String> getKeys(){
        return extraParameters.entrySet().stream().map((entry) -> {return entry.getKey();}).collect(Collectors.toList());
    }
    
    public FileChannel getFileChannel() {
        return fileChannel;
    }

    public void setFileChannel(FileChannel fileChannel) {
        this.fileChannel = fileChannel;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getNumberOfDataRecords() {
        return numberOfDataRecords;
    }

    public void setNumberOfDataRecords(Integer numberOfDataRecords) {
        this.numberOfDataRecords = numberOfDataRecords;
    }

    public Integer getNumberOfChannels() {
        return numberOfChannels;
    }

    public void setNumberOfChannels(Integer numberOfChannels) {
        this.numberOfChannels = numberOfChannels;
    }

    public String getLabelsOfTheChannels(int channelNumber) {
        return labelsOfTheChannels.get(channelNumber);
    }

    public void setLabelsOfTheChannels(List<String> labelsOfTheChannels) {
        this.labelsOfTheChannels = labelsOfTheChannels;
    }

    public String getTransducerType(int channelNumber) {
        return transducerTypes.get(channelNumber);
    }

    public void setTransducerTypes(List<String> transducerTypes) {
        this.transducerTypes = transducerTypes;
    }

    public String getPhysicalDimensionOfChannel(int channelNumber) {
        return physicalDimensionOfChannels.get(channelNumber);
    }

    public void setPhysicalDimensionOfChannels(List<String> physicalDimensionOfChannels) {
        this.physicalDimensionOfChannels = physicalDimensionOfChannels;
    }

    public Double getPhysicalMinimum(int channelNumber) {
        return physicalMinimums.get(channelNumber);
    }

    public void setPhysicalMinimums(List<Double> physicalMinimums) {
        this.physicalMinimums = physicalMinimums;
    }

    public Double getPhysicalMaximum(int channelNumber) {
        return physicalMaximums.get(channelNumber);
    }

    public void setPhysicalMaximums(List<Double> physicalMaximums) {
        this.physicalMaximums = physicalMaximums;
    }

    public Double getDigitalMinimum(int channelNumber) {
        return digitalMinimums.get(channelNumber);
    }

    public void setDigitalMinimums(List<Double> digitalMinimums) {
        this.digitalMinimums = digitalMinimums;
    }

    public Double getDigitalMaximum(int channelNumber) {
        return digitalMaximums.get(channelNumber);
    }

    public void setDigitalMaximums(List<Double> digitalMaximums) {
        this.digitalMaximums = digitalMaximums;
    }

    public String getPrefiltering(int channelNumber) {
        return prefilterings.get(channelNumber);
    }

    public void setPrefilterings(List<String> prefilterings) {
        this.prefilterings = prefilterings;
    }

    public Integer getNumberOfSample(int channelNumber) {
        return numberOfSamples.get(channelNumber);
    }

    public void setNumberOfSamples(List<Integer> numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

    public List<String> getLabelsOfTheChannels() {
        return labelsOfTheChannels;
    }

    public List<String> getTransducerTypes() {
        return transducerTypes;
    }

    public List<String> getPhysicalDimensionOfChannels() {
        return physicalDimensionOfChannels;
    }

    public List<Double> getPhysicalMinimums() {
        return physicalMinimums;
    }

    public List<Double> getPhysicalMaximums() {
        return physicalMaximums;
    }

    public List<Double> getDigitalMinimums() {
        return digitalMinimums;
    }

    public List<Double> getDigitalMaximums() {
        return digitalMaximums;
    }

    public List<String> getPrefilterings() {
        return prefilterings;
    }

    public List<Integer> getNumberOfSamples() {
        return numberOfSamples;
    }

    public Map<String, Object> getExtraParameters() {
        return extraParameters;
    }
    
    
}
