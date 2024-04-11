package com.github.helendigger.tracktime.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * A record that shows statistic by a certain class
 * The string key in a map is a method's name
 * @param methods
 */
public record ClassStatisticsDTO(Map<String, MethodStatisticsDTO> methods) {
    @JsonIgnore
    public boolean isEmpty() {
        return methods.isEmpty();
    }
}
