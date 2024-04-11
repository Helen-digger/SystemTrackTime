package com.github.helendigger.tracktime.repository;

import com.github.helendigger.tracktime.constant.MethodCacheConst;
import com.github.helendigger.tracktime.dto.MethodStatisticsDTO;
import com.github.helendigger.tracktime.model.TimeRecord;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeRecordsRepository extends JpaRepository<TimeRecord, Long> {

    @Query(value = "SELECT AVG(execution_time) AS mean, STDDEV(execution_time) AS standardDeviation," +
            "MODE() WITHIN GROUP (ORDER BY execution_time) AS mode," +
            "PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY execution_time) AS median," +
            "PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY execution_time) AS seventyFivePercentile," +
            "PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY execution_time) AS ninetyNinePercentile," +
            "MIN(execution_time) AS minimum, MAX(execution_time) AS maximum FROM time_record WHERE " +
            "method_name = :method AND class_name = :class AND package_name = :package", nativeQuery = true)
    @Cacheable(cacheNames = MethodCacheConst.METHOD_CACHE_NAME,
            key = "T(com.github.helendigger.tracktime.util.MethodStatsCacheUtil).getCacheKey(args[0], args[1], args[2])")
    MethodStatisticsDTO getStatisticsByFullName(@Param("method") String methodName,
                                            @Param("class") String className,
                                            @Param("package") String packageName);

    @Query("SELECT DISTINCT t.methodName FROM TimeRecord t WHERE t.className = :class " +
            "AND t.packageName = :package")
    List<String> getAllMethodsWithinClass(@Param("class") String className,
                                          @Param("package") String packageName);
    @Query("SELECT DISTINCT className FROM TimeRecord")
    List<String> getAllClasses(@Param("package") String packageName);

    @Query("SELECT DISTINCT packageName FROM TimeRecord")
    List<String> getAllPackages();

    @Override
    @CacheEvict(cacheNames = MethodCacheConst.METHOD_CACHE_NAME,
            key = "T(com.github.helendigger.tracktime.util.MethodStatsCacheUtil).getMethodKey(#result)")
    <S extends TimeRecord> S saveAndFlush(S entity);
}
