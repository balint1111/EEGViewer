package edffilereader.file;

import edffilereader.data.Channel;
import edffilereader.data.EEG_Data;
import edffilereader.exceptions.UsupportedFileFormatException;
import edffilereader.header.EEG_Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public abstract class EEG_File {

    public EEG_Header header;

    public abstract void readHeader();

    public abstract Channel getChannel(int channelNumber) throws IOException, InterruptedException;

    public abstract EEG_Data readRecordFromTo(int from, int to) throws IOException, InterruptedException;

    public EEG_Header getHeader() {
        return header;
    }


    public static EEG_File build(String path) throws UsupportedFileFormatException, FileNotFoundException {
        String extension = path.substring(path.lastIndexOf(".") + 1);
        EEG_File ret;
        switch (extension) {
            case "bdf" -> ret = new BDF_File(path);
            case "edf" -> ret = new EDF_File(path);
            default -> throw new UsupportedFileFormatException("Unsupported extension: " + extension);
        }
        return ret;
    }

    public static EEG_File build(File file) throws UsupportedFileFormatException, FileNotFoundException {
        String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        EEG_File ret;
        switch (extension) {
            case "bdf" -> ret = new BDF_File(file);
            case "edf" -> ret = new EDF_File(file);
            default -> throw new UsupportedFileFormatException("Unsupported extension: " + extension);
        }
        return ret;
    }

    protected List<Integer> readASCII_Integers(int start, int dataLength, byte[] bytes) throws UnsupportedEncodingException {
        List<Integer> returnList = new ArrayList<>();
        for (int i = start; i < start + (dataLength * header.getNumberOfChannels()); i += dataLength) {
            returnList.add(Integer.parseInt(new String(bytes, i, dataLength, "US-ASCII").trim()));
        }
        return returnList;
    }

    protected List<Double> readASCII_Doubles(int start, int dataLength, byte[] bytes) throws UnsupportedEncodingException {
        List<Double> returnList = new ArrayList<>();
        for (int i = start; i < start + (dataLength * header.getNumberOfChannels()); i += dataLength) {
            returnList.add(Double.parseDouble(new String(bytes, i, dataLength, "US-ASCII").trim()));
        }
        return returnList;
    }

    protected List<String> readStrings(int start, int dataLength, byte[] bytes) throws UnsupportedEncodingException {
        List<String> returnList = new ArrayList<>();
        for (int i = start; i < start + (dataLength * header.getNumberOfChannels()); i += dataLength) {
            returnList.add(new String(bytes, i, dataLength, "US-ASCII").trim());
        }
        return returnList;
    }

}
