package com.xssdefense.system.repository;

import com.xssdefense.system.model.AttackLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttackLogRepository extends JpaRepository<AttackLog, Long> {

    List<AttackLog> findBySeverity(String severity);
    
    List<AttackLog> findBySource(String source);
    
    List<AttackLog> findByPrevented(Boolean prevented);
    
    @Query("SELECT COUNT(a) FROM AttackLog a WHERE a.severity = ?1")
    Long countBySeverity(String severity);
} 