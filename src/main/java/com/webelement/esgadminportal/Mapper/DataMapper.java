package com.webelement.esgadminportal.Mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.webelement.esgadminportal.common.DataInfoDTO;
import com.webelement.esgadminportal.entity.DataEntity;

@Component
public interface DataMapper {
	public DataInfoDTO toInfoDto(DataEntity d);

	public Page<DataInfoDTO> toInfoDtoPage(Page<DataEntity> page);
}
