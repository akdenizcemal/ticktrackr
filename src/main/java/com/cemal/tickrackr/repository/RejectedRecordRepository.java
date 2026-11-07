package com.cemal.tickrackr.repository;

import com.cemal.tickrackr.model.RejectedRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RejectedRecordRepository extends JpaRepository<RejectedRecord, Long> {}