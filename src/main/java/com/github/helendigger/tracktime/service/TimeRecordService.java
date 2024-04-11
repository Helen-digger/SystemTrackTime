package com.github.helendigger.tracktime.service;

import com.github.helendigger.tracktime.model.TimeRecord;
import com.github.helendigger.tracktime.repository.TimeRecordsRepository;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

/**
 * Service for creating time statistics on methods
 */
@AllArgsConstructor
@Service
public class TimeRecordService {

    private final TimeRecordsRepository timeRecordsRepository;

    /**
     * Create a new incomplete statistic on a method. The newly created statistic is not getting into statistics.
     * @param startTime start time in epoch milliseconds
     * @param methodName method name
     * @param className class name
     * @param packageName package
     * @return returns unique identifier of a new statistic record
     */
    public Long createNewTimeRecord(long startTime, String methodName, String className, String packageName) {
        TimeRecord record = new TimeRecord(startTime, methodName, className, packageName);
        TimeRecord saved = timeRecordsRepository.save(record);
        return saved.getId();
    }

    /**
     * Complete and update the statistic calculation
     * @param id unique identifier of a new statistic record
     * @param endTime end time in epoch milliseconds
     */
    public void updateTimeRecord(long id, long endTime) {
        timeRecordsRepository.findById(id).ifPresent(timeRecord -> {
            timeRecord.setEndTime(endTime);
            timeRecord.setExecutionTime(endTime - timeRecord.getStartTime());
            timeRecordsRepository.saveAndFlush(timeRecord);
        });
    }

}
