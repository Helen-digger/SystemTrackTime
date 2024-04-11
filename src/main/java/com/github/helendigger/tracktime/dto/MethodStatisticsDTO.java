package com.github.helendigger.tracktime.dto;

/**
 * A method statistics view
 * Actual object is generated with Spring Data
 * Describes fields of statistical data gathered from database
 */
public interface MethodStatisticsDTO {
    Double getMean();
    Double getMedian();
    Double getMode();
    Double getNinetyNinePercentile();
    Double getSeventyFivePercentile();
    Double getStandardDeviation();
    Long getMaximum();
    Long getMinimum();
}