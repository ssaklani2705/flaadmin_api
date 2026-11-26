package com.webelement.esgadminportal.Mapper;

import java.time.temporal.ChronoUnit;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.webelement.esgadminportal.common.DataInfoDTO;
import com.webelement.esgadminportal.entity.DataEntity;

@Component
public class DataMapperImpl implements DataMapper {

	@Override
	public DataInfoDTO toInfoDto(DataEntity d) {
		if (d == null) {
			return null;
		}

		Long noOfDays = null;

		if (d.getOccurrence() != null && d.getDetection() != null) {
			noOfDays = ChronoUnit.DAYS.between(d.getOccurrence(), d.getDetection()) + 1;
		}
		return DataInfoDTO.builder().dataId(d.getId()).entityCode(d.getCode()).state(d.getState())
				.branchCategory(d.getCatOfBranch()).perpetratorStatus(d.getStatusOfPerpetrator())
				.accountType(d.getTypeOfAccount()).accountTypeOther(d.getTypeOfAccOther())
				.areaOfOperation(d.getAreaOfOperation()).fraudType(d.getTypeOfFraud()).totalAmount(d.getTotalAmount())
				.dateOfOccurrence(d.getOccurrence()).dateOfDetection(d.getDetection())
				.dateOfClassification(d.getClassification()).dateReportedToRbi(d.getReportedToRbi())
				.howDetected(d.getDetectionMethod()).status(d.getStatus())
				.bankName(d.getBank() != null ? d.getBank().getName() : null).noOfDays(noOfDays).build();
	}

	@Override
	public Page<DataInfoDTO> toInfoDtoPage(Page<DataEntity> page) {
		return page.map(this::toInfoDto);
	}

}
