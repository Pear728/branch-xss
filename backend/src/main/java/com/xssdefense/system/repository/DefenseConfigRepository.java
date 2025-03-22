package com.xssdefense.system.repository;

import com.xssdefense.system.model.DefenseConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefenseConfigRepository extends JpaRepository<DefenseConfig, Long> {

} 