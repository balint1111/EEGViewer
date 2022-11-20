package root.main;

import com.google.common.collect.MinMaxPriorityQueue;
import edffilereader.data.EEG_Data;
import edffilereader.file.EEG_File;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import root.exceptions.DataModelException;
import root.main.common.DataRecord;
import root.main.common.Util;
import root.main.fx.custom.Position;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DataModel {

    @Getter
    private EEG_File eeg_file;

    public void setEeg_file(EEG_File eeg_file) {
        this.eeg_file = eeg_file;
        queue.clear();
    }

    private MinMaxPriorityQueue<DataRecord> queue;

    @SneakyThrows
    public List<Float> getDataAtPosition(Position position) {
        float[][] data = getDataRecordsFromTo(position.getRecordProperty().get(), position.getRecordProperty().get()).get(0).getData();
        int maxOffset = Arrays.stream(data).map(floats -> floats.length).max(Integer::compareTo).get();
        double offsetMultiplier = (double) position.getOffsetProperty().get() / (double) maxOffset;
        return Arrays.stream(data).map(floats -> floats[(int) (floats.length * offsetMultiplier)]).collect(Collectors.toList());
    }

    public List<DataRecord> getDataRecordsFromTo(int from, int to) throws Exception {
        if (eeg_file == null) throw new DataModelException("eeg_file is null");
        List<DataRecord> memoryList;
        List<DataRecord> loadedFromDisk = new ArrayList<>();
        synchronized (queue) {
            memoryList = queue.stream().filter(dataRecord -> dataRecord.getDataRecordNumber() >= from && dataRecord.getDataRecordNumber() <= to).collect(Collectors.toList());

            memoryList.sort(Comparator.comparing(DataRecord::getDataRecordNumber));
            if (CollectionUtils.isEmpty(memoryList)) {
                readFromFile(loadedFromDisk, from, to + 1);
            } else {
                readFromFile(loadedFromDisk, from, memoryList.get(0).getDataRecordNumber());
                readFromFile(loadedFromDisk, memoryList.get(memoryList.size() - 1).getDataRecordNumber() + 1, to + 1);
            }

            for (int i = 0, memoryListSize = memoryList.size(); i < memoryListSize - 1; i++) {
                readFromFile(loadedFromDisk, memoryList.get(i).getDataRecordNumber() + 1, memoryList.get(i + 1).getDataRecordNumber());
            }
            if (!CollectionUtils.isEmpty(loadedFromDisk)) {
                loadedFromDisk.forEach(dataRecord -> dataRecord.getData());
                queue.addAll(loadedFromDisk);
            }
        }
        List<DataRecord> finalList = new ArrayList<>();
        finalList.addAll(memoryList);
        finalList.addAll(loadedFromDisk);
        finalList.sort(Comparator.comparing(DataRecord::getDataRecordNumber));
        return finalList;
    }

    private void readFromFile(List<DataRecord> loadedFromDisk, Integer from, Integer to) throws Exception {
        int numberOfRecords = to - from;
        if (numberOfRecords >= 1) {
            EEG_Data eeg_data = eeg_file.readRecordFromTo(from, to);
            loadedFromDisk.addAll(Util.EEG_DataToDataRecords(eeg_data, from));
        }
    }


    public DataModel(int maxQueueSize) {
        setMaxQueueSize(maxQueueSize);
    }

    public void setMaxQueueSize(int maxQueueSize) {
        queue = MinMaxPriorityQueue.<DataRecord>orderedBy((o1, o2) -> o1.getLastRequestTime().compareTo(o2.getLastRequestTime()) * -1).maximumSize(maxQueueSize).create();
    }
}
