package com.webelement.esgadminportal.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webelement.esgadminportal.entity.TransactionEntity;

public interface TransactionRepo extends JpaRepository<TransactionEntity, Integer>{

}
