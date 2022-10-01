package root.main;

import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Range;
import edffilereader.data.EEG_Data;
import edffilereader.file.EEG_File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DataModel {

    private EEG_File eeg_file;

    private MinMaxPriorityQueue<DataRecord> queue;

    private final DataController dataController;

    private int pageNumber;

    public EEG_Data nextPage() {
        Integer rangeSize = dataController.getFrom() - dataController.getTo();
        for (int i = 0; i < rangeSize; i++) {
            int finalI = i;
            Stream<DataRecord> dataRecordStream = queue.stream().filter(dataRecord -> dataRecord.getDataRecordNumber().equals(dataController.getTo() + finalI));
        }
        return null;
    }

//    public EEG_Data getDataRecord(int dataRecordNumber) throws IOException, InterruptedException {
//        Optional<DataRecord> optional;
//        synchronized (queue) {
//            optional = queue.stream().filter(dataRecord -> dataRecord.getDataRecordNumber().equals(dataRecordNumber)).findFirst();
//        }
//        if (optional.isPresent()) {
//            return optional.get().getData();
//        } else {
//            EEG_Data eeg_data = eeg_file.readRecordFromTo(dataRecordNumber, dataRecordNumber + 1);
//            DataRecord dataRecord = new DataRecord(eeg_data, dataRecordNumber);
//            EEG_Data data = dataRecord.getData();
//            synchronized (queue) {
//                queue.add(dataRecord);
//            }
//            return data;
//        }
//    }

    public List<DataRecord> getDataRecordFromTo(int from, int to) throws Exception {
        List<DataRecord> memoryList = new ArrayList<>();
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
        for (int i = 1, finalListSize = finalList.size(); i < finalListSize; i++) {
            if (finalList.get(i).getDataRecordNumber() - finalList.get(i - 1).getDataRecordNumber() != 1) {
                System.out.println(finalList.get(i).getDataRecordNumber() + " " + finalList.get(i - 1).getDataRecordNumber());
                int finalI = i;
                long count = memoryList.stream().filter(dataRecord -> dataRecord.getDataRecordNumber().equals(finalList.get(finalI).getDataRecordNumber())).count();
                System.out.println("count: " + count);
                throw new Exception("baj");
            }
        }
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


    public DataModel(int maxQueueSize, DataController dataController) {
        queue = MinMaxPriorityQueue.<DataRecord>orderedBy((o1, o2) -> o1.getLastRequestTime().compareTo(o2.getLastRequestTime()) * -1).maximumSize(maxQueueSize).create();
        this.dataController = dataController;
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
        //System.out.println("ret: " + toReturn);
        return toReturn;
    }

    public EEG_File getEeg_file() {
        return eeg_file;
    }

    public void setEeg_file(EEG_File eeg_file) {
        this.eeg_file = eeg_file;
        queue.clear();
    }

}
