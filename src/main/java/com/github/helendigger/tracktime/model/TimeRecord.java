package com.github.helendigger.tracktime.model;

import jakarta.persistence.*;
import lombok.*;


/**
 * Time record of a method execution, stashed as a statistical data to manual review
 * Further statistics recalculation
 */
@Table(indexes = {@Index(name = "methodIdx", columnList = "methodName,className,packageName"),
        @Index(name = "methodIdx", columnList = "methodName,className,packageName"),
        @Index(name = "startIdx", columnList = "startTime"),
        @Index(name = "endIdx", columnList = "endTime"),
        @Index(name = "executionIdx", columnList = "executionTime")})
@Entity
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String methodName;
    @Column(nullable = false)
    private String className;
    @Column(nullable = false)
    private String packageName;

    private Long startTime;
    private Long endTime;
    private Long executionTime;

    public TimeRecord(Long startTime, String methodName, String className, String packageName) {
        this.startTime = startTime;
        this.methodName = methodName;
        this.className = className;
        this.packageName = packageName;
    }
}
