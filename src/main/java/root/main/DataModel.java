package root.main;

import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Range;
import edffilereader.data.EEG_Data;
import edffilereader.file.EEG_File;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;
import root.main.common.DataRecord;
import root.main.common.Util;
import root.main.fx.MainController;

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

    @Autowired
    @Lazy
    private MainController mainController;

    public List<DataRecord> getDataRecordsFromTo(int from, int to) throws Exception {
        if (eeg_file == null) throw new Exception("eeg_file is null");
        List<DataRecord> memoryList;
        List<DataRecord> loadedFromDisk = new ArrayList<>();
        synchronized (queue) {
            memoryList = queue.stream().filter(dataRecord -> dataRecord.getDataRecordNumber() >= from && dataRecord.getDataRecordNumber() <= to).collect(Collectors.toList());

            memoryList.sort(Comparator.comparing(DataRecord::getDataRecordNumber));
            if (CollectionUtils.isEmpty(memoryList)) {
                extracted(loadedFromDisk, from, to + 1);
            } else {
                extracted(loadedFromDisk, from, memoryList.get(0).getDataRecordNumber());
                extracted(loadedFromDisk, memoryList.get(memoryList.size() - 1).getDataRecordNumber() + 1, to + 1);
            }

            for (int i = 0, memoryListSize = memoryList.size(); i < memoryListSize - 1; i++) {
                extracted(loadedFromDisk, memoryList.get(i).getDataRecordNumber() + 1, memoryList.get(i + 1).getDataRecordNumber());
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
//        for (int i = 1, finalListSize = finalList.size(); i < finalListSize; i++) {
//            if (finalList.get(i).getDataRecordNumber() - finalList.get(i - 1).getDataRecordNumber() != 1) {
//                System.out.println(finalList.get(i).getDataRecordNumber() + " " + finalList.get(i - 1).getDataRecordNumber());
//                int finalI = i;
//                long count = memoryList.stream().filter(dataRecord -> dataRecord.getDataRecordNumber().equals(finalList.get(finalI).getDataRecordNumber())).count();
//                System.out.println("count: " + count);
//                throw new Exception("baj");
//            }
//        }
        return finalList;
    }

    private void extracted(List<DataRecord> loadedFromDisk, Integer from, Integer to) throws Exception {
        int numberOfRecords = to - from;
        if (numberOfRecords >= 1) {
//            log.info("numberOfRecords: " + numberOfRecords);
//            log.info("loadedFromDisk before: " + loadedFromDisk.size());
//            log.info("reload from: " + from + " to: " + to);

            EEG_Data eeg_data = eeg_file.readRecordFromTo(from, to);
            loadedFromDisk.addAll(Util.EEG_DataToDataRecords(eeg_data, from));
//            log.info("loadedFromDisk after: " + loadedFromDisk.size());
        }
    }


    public DataModel(int maxQueueSize) {
        queue = MinMaxPriorityQueue.<DataRecord>orderedBy((o1, o2) -> o1.getLastRequestTime().compareTo(o2.getLastRequestTime()) * -1).maximumSize(maxQueueSize).create();
    }

    private Integer getDistanceFromRange(DataRecord o1, Range<Integer> showedDataRecords) {
        Integer dataRecordNumberO1 = o1.getDataRecordNumber();
        Integer lowerEndpoint = showedDataRecords.lowerEndpoint();
        Integer upperEndpoint = showedDataRecords.upperEndpoint();
        Integer toReturn;
        if (dataRecordNumberO1.compareTo(lowerEndpoint) < 0) {
            toReturn = lowerEndpoint - dataRecordNumberO1;
        } else if (dataRecordNumberO1.compareTo(upperEndpoint) > 0) {
            toReturn = dataRecordNumberO1 - upperEndpoint;
        } else {
            toReturn = 0;
        }
        return toReturn;
    }

}
