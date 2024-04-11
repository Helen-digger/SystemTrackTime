package com.github.helendigger.tracktime.controller;

import com.github.helendigger.tracktime.aspect.annotation.TrackTime;
import com.github.helendigger.tracktime.aspect.annotation.TrackTimeAsync;
import com.github.helendigger.tracktime.dto.ClassStatisticsDTO;
import com.github.helendigger.tracktime.dto.MethodStatisticsDTO;
import com.github.helendigger.tracktime.dto.PackageStatisticsDTO;
import com.github.helendigger.tracktime.service.StatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * A sample method that returns Hello, World to the specified endpoint
     * This method is time tracked with @TrackTime annotation
     * @return Hello, World and OK
     */
    @GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    @TrackTime
    public ResponseEntity<String> getHello() {
        return ResponseEntity.ok().body("Hello, world!");
    }

    /**
     * A sample method that returns Hello, World to the specified endpoint
     * This method is time tracked with @TrackTimeAsync annotation
     * @return Hello, World and OK
     */
    @GetMapping(value = "/async/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    @TrackTimeAsync
    public ResponseEntity<String> asyncHello() {
        return ResponseEntity.ok("Hello, world!");
    }

    /**
     * Show all packages tracked either by TrackTime or TrackTimeAsync
     * @return all packages with all classes and methods tracked by TrackTime of TrackTimeAsync
     */
    @GetMapping(value = "/track/package/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PackageStatisticsDTO> showAllPackages() {
        var stats = statisticsService.getAllStats();
        if (stats.packages().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    /**
     * Show all classes in a specified package
     * It will only show classes and methods that are annotated with TrackTime or TrackTimeAsync
     * @param packageName package name
     * @return List of
     */
    @GetMapping(value = "/track/{packageName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ClassStatisticsDTO>> showAllInPackage(
            @PathVariable String packageName) {
        var stats = statisticsService.getStatsByPackage(packageName);
        if (stats.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    /**
     * Show all methods that is in specified class
     * It will only show classes and methods that are annotated with TrackTime or TrackTimeAsync
     * @param packageName package name
     * @param className class name
     * @return list of method statistics in specified class
     */
    @GetMapping(value = "/track/{packageName}/{className}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClassStatisticsDTO> showAllInClass(
            @PathVariable String packageName, @PathVariable String className) {
        var stats = statisticsService.getStatsByClass(className, packageName);
        if (stats.methods().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    /**
     * Show method statistics by fully qualified name
     * @param packageName package name
     * @param className class name
     * @param methodName method name
     * @return statistics for the provided method
     */
    @GetMapping(value = "/track/{packageName}/{className}/{methodName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MethodStatisticsDTO> showMethod(
            @PathVariable String packageName,
            @PathVariable String className,
            @PathVariable String methodName) {
        var stats = statisticsService.getStatsByMethod(methodName, className, packageName);
        if (isMethodPresent(stats)) {
            return ResponseEntity.ok(stats);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Check any field to know present or not
     * @param methodStatisticsDTO entity to check
     * @return presence of a statistics
     */
    private static boolean isMethodPresent(MethodStatisticsDTO methodStatisticsDTO) {
        return methodStatisticsDTO.getMean() != null;
    }

}
