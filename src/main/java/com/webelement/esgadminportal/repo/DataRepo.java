package com.webelement.esgadminportal.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webelement.esgadminportal.entity.DataEntity;

@Repository
public interface DataRepo extends JpaRepository<DataEntity, Integer> {

}
