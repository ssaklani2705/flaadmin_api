package com.webelement.esgadminportal.common;

public class GroupDTO {

    private Integer groupId;
    private String name;

    public GroupDTO(Integer groupId, String name) {
        this.groupId = groupId;
        this.name = name;
    }

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    
}