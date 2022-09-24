package edffilereader.file;

import edffilereader.data.Channel;
import edffilereader.data.EEG_Data;
import edffilereader.header.BDF_Header;
import edffilereader.header.EDF_Header;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author balin
 */
public class BDF_File extends EEG_File {
    
    private static final int SAMPLE_LENGTH = 3;

    
    public BDF_File(String filename) throws FileNotFoundException {
        header= new BDF_Header();
        header.setFilename(filename);

        
        header.setFileChannel(new FileInputStream(filename).getChannel());
        
        readHeader();
    }

    public BDF_File(File file) throws FileNotFoundException {
        header = new BDF_Header();
        header.setFilename(file.getAbsolutePath());


        header.setFileChannel(new FileInputStream(file).getChannel());

        readHeader();
    }
    
    @Override
    public BDF_Header getHeader() {
        return (BDF_Header) header;
    }

    @Override
    public void readHeader() {

        try {
            ByteBuffer buffer = ByteBuffer.allocate(256);

            header.getFileChannel().read(buffer, 0);
            byte[] bytes = buffer.array();

            getHeader().setHeaderType(Integer.valueOf(bytes[0]));

            getHeader().setPatient(new String(bytes, 8, 80, "US-ASCII"));
            getHeader().setRecording(new String(bytes, 88, 80, "US-ASCII"));
            getHeader().setStartDate(new String(bytes, 168, 8, "US-ASCII"));
            getHeader().setStartTime(new String(bytes, 176, 8, "US-ASCII"));
            getHeader().setNumberOfBytes(Integer.parseInt(new String(bytes, 184, 8, "US-ASCII").trim()));
            getHeader().setVersion(new String(bytes, 192, 44, "US-ASCII"));
            header.setNumberOfDataRecords(Integer.parseInt(new String(bytes, 236, 8, "US-ASCII").trim()));
            getHeader().setDurationOfDataRecord(Double.parseDouble(new String(bytes, 244, 8, "US-ASCII").trim()));
            header.setNumberOfChannels(Integer.parseInt(new String(bytes, 252, 4, "US-ASCII").trim()));

            buffer = ByteBuffer.allocate(256 * header.getNumberOfChannels());
            header.getFileChannel().read(buffer, 256);
            bytes = buffer.array();

            int start = 0;
            int dataLength = 16;
            header.setLabelsOfTheChannels(readStrings(start, dataLength, bytes));
            start += (dataLength * header.getNumberOfChannels());

            dataLength = 80;
            header.setTransducerTypes(readStrings(start, dataLength, bytes));
            start += (dataLength * header.getNumberOfChannels());

            dataLength = 8;
            header.setPhysicalDimensionOfChannels(readStrings(start, dataLength, bytes));
            start += (dataLength * header.getNumberOfChannels());

            dataLength = 8;
            header.setPhysicalMinimums(readASCII_Doubles(start, dataLength, bytes));
            start += (dataLength * header.getNumberOfChannels());

            dataLength = 8;
            header.setPhysicalMaximums(readASCII_Doubles(start, dataLength, bytes));
            start += (dataLength * header.getNumberOfChannels());

            dataLength = 8;
            header.setDigitalMinimums(readASCII_Doubles(start, dataLength, bytes));
            start += (dataLength * header.getNumberOfChannels());

            dataLength = 8;
            header.setDigitalMaximums(readASCII_Doubles(start, dataLength, bytes));
            start += (dataLength * header.getNumberOfChannels());

            dataLength = 80;
            header.setPrefilterings(readStrings(start, dataLength, bytes));
            start += (dataLength * header.getNumberOfChannels());

            dataLength = 8;
            header.setNumberOfSamples(readASCII_Integers(start, dataLength, bytes));
            start += (dataLength * header.getNumberOfChannels());

            dataLength = 32;
            start += (dataLength * header.getNumberOfChannels());

            getHeader().setStartData((header.getNumberOfChannels() + 1) * 256);

            int dataRecordSize = 0;
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
                dataRecordSize += header.getNumberOfSample(i) * SAMPLE_LENGTH;
            }
            getHeader().setDataRecordSize(dataRecordSize);

        } catch (IOException ex) {
            Logger.getLogger(BDF_File.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Channel getChannel(int channelNumber) throws IOException, InterruptedException {
        Channel channel =  new Channel(header.getLabelsOfTheChannels(channelNumber),
                header.getTransducerType(channelNumber),
                header.getPhysicalDimensionOfChannel(channelNumber),
                header.getPhysicalMinimum(channelNumber),
                header.getPhysicalMaximum(channelNumber),
                header.getDigitalMinimum(channelNumber),
                header.getDigitalMaximum(channelNumber),
                header.getPrefiltering(channelNumber),
                header.getNumberOfSample(channelNumber),
                header.getNumberOfDataRecords(),
                SAMPLE_LENGTH);

        readBytesToChannel(channelNumber, channel, 0, header.getNumberOfDataRecords());

        return channel;
    }

    private void readBytesToChannel(int channelNumber, Channel channel, int fromRecord, int toRecord) throws IOException {
        int n = header.getNumberOfSample(channelNumber);
        int startInRecord = getStartInRecord(channelNumber);
        channel.data = new byte[(toRecord - fromRecord) * n * SAMPLE_LENGTH];
        for (int dataRecordNumber = fromRecord; dataRecordNumber < toRecord; dataRecordNumber++) {
            int start = getHeader().getStartData() + dataRecordNumber * getHeader().getDataRecordSize() + startInRecord;

            ByteBuffer buffer = ByteBuffer.allocate(SAMPLE_LENGTH * n);
            header.getFileChannel().read(buffer, start);
            int offset = header.getNumberOfSample(channelNumber) * dataRecordNumber * SAMPLE_LENGTH;
            buffer.get(0, channel.data, offset, buffer.limit());

        }
    }

    @Override
    public EEG_Data readRecordFromTo(int from, int to) throws IOException, InterruptedException {
        int length = to - from;
        int start = getHeader().getStartData() + from * getHeader().getDataRecordSize();
        ByteBuffer buffer = ByteBuffer.allocate(getHeader().getDataRecordSize() * (length));
        header.getFileChannel().read(buffer, start);
        EEG_Data data = new EEG_Data(length, header.getNumberOfChannels(), header.getLabelsOfTheChannels(), header.getTransducerTypes(),
                header.getPhysicalDimensionOfChannels(), header.getPhysicalMinimums(), header.getPhysicalMaximums(),
                header.getDigitalMinimums(), header.getDigitalMaximums(), header.getPrefilterings(), header.getNumberOfSamples(), SAMPLE_LENGTH);
        
        for (int channelNumber = 0; channelNumber < header.getNumberOfChannels(); channelNumber++) {
            for (int recordNumber = 0; recordNumber < length; recordNumber++) {
                int channelStart = recordNumber * getHeader().getDataRecordSize() + getStartInRecord(channelNumber);
                int n = header.getNumberOfSample(channelNumber);
                int offset = n * recordNumber * SAMPLE_LENGTH;
                buffer.get(channelStart, data.channels[channelNumber].data, offset, SAMPLE_LENGTH * n);
            }
        }
        return data;
    }

    private Integer getStartInRecord(int channelNumber) {
        int startInRecord = 0;
        for (int i = 0; i < channelNumber; i++) {
            startInRecord += header.getNumberOfSample(i) * SAMPLE_LENGTH;
        }
        return startInRecord;
    }

}
