package com.startup.authapi.repo;

import com.startup.authapi.model.ProcessingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProcessingLogRepo extends JpaRepository<ProcessingLog, UUID> { }