package com.webelement.esgadminportal.common;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class ModulePermissionDTO {
	
	    @JsonIgnore
	    private Integer userId;
	    private Integer moduleId;
	    private String name;
	    private Integer type;
	    private String addPer;
	    private String editPer;
	    private String deletePer;
	    private String approvePer;
	    private String adminApprovePer;
	    private String viewPer;

	    
	    
    public ModulePermissionDTO() {
			super();
			// TODO Auto-generated constructor stub
		}

	public ModulePermissionDTO(Integer userId, Integer moduleId, String name, Integer type,
                               String addPer, String editPer, String deletePer,
                               String approvePer, String adminApprovePer, String viewPer) {
        this.userId = userId;
        this.moduleId = moduleId;
        this.name = name;
        this.type = type;
        this.addPer = addPer;
        this.editPer = editPer;
        this.deletePer = deletePer;
        this.approvePer = approvePer;
        this.adminApprovePer = adminApprovePer;
        this.viewPer = viewPer;
    }

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getAddPer() {
		return addPer;
	}

	public void setAddPer(String addPer) {
		this.addPer = addPer;
	}

	public String getEditPer() {
		return editPer;
	}

	public void setEditPer(String editPer) {
		this.editPer = editPer;
	}

	public String getDeletePer() {
		return deletePer;
	}

	public void setDeletePer(String deletePer) {
		this.deletePer = deletePer;
	}

	public String getApprovePer() {
		return approvePer;
	}

	public void setApprovePer(String approvePer) {
		this.approvePer = approvePer;
	}

	public String getAdminApprovePer() {
		return adminApprovePer;
	}

	public void setAdminApprovePer(String adminApprovePer) {
		this.adminApprovePer = adminApprovePer;
	}

	public String getViewPer() {
		return viewPer;
	}

	public void setViewPer(String viewPer) {
		this.viewPer = viewPer;
	}

	@Override
	public String toString() {
		return "ModulePermissionDTO [userId=" + userId + ", moduleId=" + moduleId + ", name=" + name + ", type=" + type
				+ ", addPer=" + addPer + ", editPer=" + editPer + ", deletePer=" + deletePer + ", approvePer="
				+ approvePer + ", adminApprovePer=" + adminApprovePer + ", viewPer=" + viewPer + "]";
	}

    
}
