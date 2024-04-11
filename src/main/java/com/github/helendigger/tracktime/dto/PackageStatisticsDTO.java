package com.github.helendigger.tracktime.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * A record that shows statistics by all packages, classes and methods
 * The first String key is a package name
 * The second String key present in the nested map is a class name
 * @param packages
 */
public record PackageStatisticsDTO(Map<String, Map<String, ClassStatisticsDTO>> packages) {

    @JsonIgnore
    public boolean isEmpty() {
        return packages.isEmpty();
    }
}
