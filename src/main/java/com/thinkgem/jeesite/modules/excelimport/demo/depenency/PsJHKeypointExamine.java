/**
 * Copyright &copy; 2015-2018 CAINI All rights reserved.
 */
package com.thinkgem.jeesite.modules.excelimport.demo.depenency;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.beans.Transient;
import java.util.Date;

/**
 * 关键节点检查Entity
 * @author zhaox
 * @version 2019-10-28
 */

public class PsJHKeypointExamine extends DataEntity<PsJHKeypointExamine> {
	
	private static final long serialVersionUID = 1L;
	private String projectId;		// 项目id
	private String projectName;		// 项目名称
	private String projectLevel;		// 项目密级

	private String pointName;		// 节点名称
	private String pointContent; 		// 节点内容
	private Date finishDate;		// 完成时间
	private String organization;		// 主持部门
	private String inspectionPattern;		// 检查形式(会议评审)
	private String status;		// 状态(完成，滞后，未完成)
	private String detailedList;		// 需提交的材料和实物清单

	/*
	报表字段
	 */
	private String year;
	private String organizationP;//承担单位  对应project表的organization字段
	private String administration;//主管部门 对应project表的administration字段
	private String researchCycle;//研究周期
	private String finishDateStr;		// 完成时间String
	private String researchStartCycleStr;		// 研究周期(开始)String
	private String researchEndCycleStr;		// 研究周期(结束)String
	/*
		查询条件
	 */
	private String projectType;		// 项目类型
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date finishStartDate;//完成开始时间
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date finishEndDate;	//完成结束时间

	/*
		保留字段
	 */
	private Date researchStartCycle;		// 研究周期(开始)
	private Date researchEndCycle;		// 研究周期(结束)


	@Transient
	public String getFinishDateStr() {
		return finishDateStr;
	}

	public void setFinishDateStr(String finishDateStr) {
		this.finishDateStr = finishDateStr;
	}

	public String getResearchStartCycleStr() {
		return researchStartCycleStr;
	}

	public void setResearchStartCycleStr(String researchStartCycleStr) {
		this.researchStartCycleStr = researchStartCycleStr;
	}

	public String getResearchEndCycleStr() {
		return researchEndCycleStr;
	}

	public void setResearchEndCycleStr(String researchEndCycleStr) {
		this.researchEndCycleStr = researchEndCycleStr;
	}

	public String getResearchCycle() {
		return researchCycle;
	}

	public void setResearchCycle(String researchCycle) {
		this.researchCycle = researchCycle;
	}

	public String getOrganizationP() {
		return organizationP;
	}

	public void setOrganizationP(String organizationP) {
		this.organizationP = organizationP;
	}

	public String getAdministration() {
		return administration;
	}

	public void setAdministration(String administration) {
		this.administration = administration;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public Date getFinishStartDate() {
		return finishStartDate;
	}

	public void setFinishStartDate(Date finishStartDate) {
		this.finishStartDate = finishStartDate;
	}

	public Date getFinishEndDate() {
		return finishEndDate;
	}

	public void setFinishEndDate(Date finishEndDate) {
		this.finishEndDate = finishEndDate;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public PsJHKeypointExamine() {
		super();
	}

	public PsJHKeypointExamine(String id){
		super(id);
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	@Length(min=1, max=255, message="请选择项目")
	@ExcelField(title="项目名称", align=2, sort=60)
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectLevel() {
		return projectLevel;
	}

	public void setProjectLevel(String projectLevel) {
		this.projectLevel = projectLevel;
	}
	public String getProjectLevelForPSMSGrid() {
		return DictUtils.getDictLabel(projectLevel, "gf_project_level", "");
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="研究周期(开始)", align=2, sort=60)
	public Date getResearchStartCycle() {
		return researchStartCycle;
	}

	public void setResearchStartCycle(Date researchStartCycle) {
		this.researchStartCycle = researchStartCycle;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="研究周期(结束)", align=2, sort=60)
	public Date getResearchEndCycle() {
		return researchEndCycle;
	}

	public void setResearchEndCycle(Date researchEndCycle) {
		this.researchEndCycle = researchEndCycle;
	}
	
//	@Length(min=1, max=255, message="节点名称长度必须介于 1 和 255 之间")
	@ExcelField(title="节点名称", align=2, sort=60)
	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}
	
	@ExcelField(title="节点内容", align=2, sort=60)
	public String getPointContent() {
		return pointContent;
	}

	public void setPointContent(String pointContent) {
		this.pointContent = pointContent;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@ExcelField(title="完成时间", align=2, sort=60)
	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}
	
	@ExcelField(title="主持部门", align=2, sort=60)
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
//	@Length(min=0, max=255, message="检查形式(会议评审)长度必须介于 0 和 255 之间")
	@ExcelField(title="检查形式(会议评审)", align=2, sort=60)
	public String getInspectionPattern() {
		return inspectionPattern;
	}

	public void setInspectionPattern(String inspectionPattern) {
		this.inspectionPattern = inspectionPattern;
	}
	
//	@Length(min=1, max=255, message="状态(完成，滞后，未完成)长度必须介于 1 和 255 之间")
	@ExcelField(title="状态(完成，滞后，未完成)", align=2, sort=60)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getDetailedList() {
		return detailedList;
	}

	public void setDetailedList(String detailedList) {
		this.detailedList = detailedList;
	}
	
}