package com.github.helendigger.tracktime.service;

import com.github.helendigger.tracktime.dto.ClassStatisticsDTO;
import com.github.helendigger.tracktime.dto.MethodStatisticsDTO;
import com.github.helendigger.tracktime.dto.PackageStatisticsDTO;
import com.github.helendigger.tracktime.repository.TimeRecordsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A service that is used for getting statistic on annotated methods
 */
@Service
@AllArgsConstructor
public class StatisticsService {
    private final TimeRecordsRepository timeRecordsRepository;

    /**
     * Get statistics on a method by a fully qualified name
     * @param methodName method name
     * @param className class name
     * @param packageName package name
     * @return statistics on a method
     */
    public MethodStatisticsDTO getStatsByMethod(String methodName, String className, String packageName) {
        return timeRecordsRepository.getStatisticsByFullName(methodName, className, packageName);
    }

    /**
     * Get statistics on a group of methods within specified class
     * @param className class name
     * @param packageName package name
     * @return statistics on a group of methods within specified class
     */
    public ClassStatisticsDTO getStatsByClass(String className, String packageName) {
        var stats = timeRecordsRepository.getAllMethodsWithinClass(className, packageName).stream()
                .collect(Collectors.toMap(Function.identity(),
                        m -> getStatsByMethod(m, className, packageName)));
        return new ClassStatisticsDTO(stats);
    }

    /**
     * Get statistics on a group of methods within specified package
     * @param packageName package name
     * @return statistics on a group of methods within specified package
     */
    public Map<String, ClassStatisticsDTO> getStatsByPackage(String packageName) {
        var stats = timeRecordsRepository.getAllClasses(packageName).stream()
                .collect(Collectors.toMap(Function.identity(),
                        className -> getStatsByClass(className, packageName)));
        return stats.entrySet().stream()
                .filter(Predicate.not(p -> p.getValue().isEmpty()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Get statistics on all annotated and tracked methods within all packages and classes
     * @return statistics on all annotated methods
     */
    public PackageStatisticsDTO getAllStats() {
        var map = timeRecordsRepository.getAllPackages().stream()
                .collect(Collectors.toMap(Function.identity(), this::getStatsByPackage));
        return new PackageStatisticsDTO(map);
    }
}
