package com.thinkgem.jeesite.modules.excelimport.demo.process;

import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.modules.excelimport.demo.depenency.PsJHKeypointExamine;
import com.thinkgem.jeesite.modules.excelimport.process.ObjectImportJustifyStrategy;

import org.springframework.util.StringUtils;

import java.util.*;

public class PsJHKeyPointJustifyStrategy implements ObjectImportJustifyStrategy {


    @Override
    public boolean justifyContentList(List<Object> list, Map<Integer, String> map, int begRow) throws Exception {
        //错误信息
        String errorMessage = null;
        //累计错误信息返回给前端错误文件的
        StringBuffer sbuf = new StringBuffer();
        boolean result = true;

        if (list == null || list.size() <= 0){
            throw new Exception(this.getClass().getName() + "获取数据为空");
        }
        for(int i = 0 ;i < list.size();i++){
            sbuf.delete(0, sbuf.length());
            boolean errFlag=false;
            PsJHKeypointExamine psJHKeypointExamine = (PsJHKeypointExamine) list.get(i);

            //节点检查信息
            String pointContent = psJHKeypointExamine.getPointContent();
            if (StringUtils.isEmpty(pointContent)){
                errorMessage = "节点内容不能为空";
                sbuf.append(errorMessage);
                errFlag = true;
            }

            String researchCycle = psJHKeypointExamine.getResearchCycle();
            if (StringUtils.isEmpty(researchCycle)){
                errorMessage = "研究周期不能为空";
                sbuf.append(errorMessage);
                errFlag = true;
            }
            String pointName = psJHKeypointExamine.getPointName();
            if (StringUtils.isEmpty(pointName)){
                errorMessage = "节点名称不能为空";
                sbuf.append(errorMessage);
                errFlag = true;
            }
            String finishDateStr = psJHKeypointExamine.getFinishDateStr();
            if (StringUtils.isEmpty(finishDateStr)){
                errorMessage = "完成时间不能为空";
                sbuf.append(errorMessage);
                errFlag = true;
            }
            try{
                Calendar calendar = new GregorianCalendar(1900,0,-1);
                Date d = calendar.getTime();
                Date dd = DateUtils.addDays(d,Integer.valueOf(finishDateStr));
                psJHKeypointExamine.setFinishDate(dd);
            }catch (Exception e) {
                errorMessage = "完成时间格式不对，应为yyyy-MM-dd\t\n";
                sbuf.append(errorMessage);
                errFlag = true;
            }

            String organization = psJHKeypointExamine.getOrganization();
            if (StringUtils.isEmpty(organization)){
                errorMessage = "主持部门不能为空";
                sbuf.append(errorMessage);
                errFlag = true;
            }
            String inspectionPattern = psJHKeypointExamine.getInspectionPattern();
            if (StringUtils.isEmpty(inspectionPattern)){
                errorMessage = "检查形式不能为空";
                sbuf.append(errorMessage);
                errFlag = true;
            }
            String detailedList = psJHKeypointExamine.getDetailedList();
            if (StringUtils.isEmpty(detailedList)){
                errorMessage = "需提交的材料和实物清单不能为空";
                sbuf.append(errorMessage);
                errFlag = true;
            }
            String status = psJHKeypointExamine.getStatus();
            if (StringUtils.isEmpty(status)){
                errorMessage = "状态不能为空";
                sbuf.append(errorMessage);
                errFlag = true;
            }

            if(errFlag) {
                map.put(begRow + i + 1, sbuf.toString());
                result = false;
            }
        }

        return result;
    }

    @Override
    public boolean justifyHeaderList(List<Object> list, Map<Integer, String> map, int begRow) throws Exception {
        //错误信息
        String errorMessage = null;
        //累计错误信息返回给前端错误文件的
        StringBuffer sbuf = new StringBuffer();
        boolean result = true;

        if (list == null || list.size() <= 0){
            throw new Exception(this.getClass().getName() + "获取数据为空");
        }

        for(int i = 0 ;i < list.size();i++){
            sbuf.delete(0, sbuf.length());
            boolean errFlag = false;
            PsJHKeypointExamine psJHKeypointExamine = (PsJHKeypointExamine) list.get(i);
            //项目信息
            String projectName = psJHKeypointExamine.getProjectName();
            if (StringUtils.isEmpty(projectName)){
                errorMessage = "项目名称为空";
                sbuf.append(errorMessage);
                errFlag = true;
            }

            String organizationP = psJHKeypointExamine.getOrganizationP();
            if (StringUtils.isEmpty(organizationP)){
                errorMessage = "主管部门为空";
                sbuf.append(errorMessage);
                errFlag = true;
            }
            String administration = psJHKeypointExamine.getAdministration();
            if (StringUtils.isEmpty(administration)){
                errorMessage = "项目承担单位为空";
                sbuf.append(errorMessage);
                errFlag = true;
            }
            if(errFlag) {
                map.put(begRow + i + 1, sbuf.toString());
                result = false;
            }
        }
        return result;
    }

    @Override
    public void clear() {

    }
}
