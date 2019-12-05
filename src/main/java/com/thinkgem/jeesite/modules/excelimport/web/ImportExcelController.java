package com.thinkgem.jeesite.modules.excelimport.web;

import com.thinkgem.jeesite.modules.utils.OperateTipsUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;

import com.thinkgem.jeesite.modules.excelimport.process.EntityImport;
import com.thinkgem.jeesite.modules.excelimport.util.ExcelImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author sunc create on 2019-05-21 14:34
 * 导入excel文件前后台处理接口
 */
@Controller
@RequestMapping(value = "${adminPath}/importExcel")
public class ImportExcelController extends BaseController {

    @Autowired
    private ExcelImportService excelService;


    /**
     * 处理前台请求 返回每次Excel导入的唯一线程信息
     * @param request request请求
     * @param response 请求响应
     */
    @RequestMapping(value="getThread", method = RequestMethod.GET)
    public void getThread(HttpServletRequest request, HttpServletResponse response) {
        this.renderString(request, response, excelService.generateRandomThread(), true);
    }


    /**
     * 处理Excel导入
     * @param params 包含传递实体类型、实体json格式相关业务id的map对应
     * @param request request请求
     * @param response 请求响应
     * @throws Exception 异常信息抛出
     */
    @RequestMapping(value="ImportExcel")
    public void importProcess(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String entityType = (String) params.get("type");
        String content = (String) params.get("content");
        String businessId = (String) params.get("businessId");

        if(StringUtils.isBlank(entityType) || StringUtils.isBlank(content)) {
            throw new Exception("ImportExcelController:未传递 entityType 或 content参数！");
        }

        // 增加文件导入过程中进度提示功能
        String monitorId = request.getParameter("thread");

        //处理文件上传
        List<MultipartFile> file = excelService.getFile(request);

        if(file == null || file.size() == 0) {
            renderString(request, response, "请选择文件上传！", true);
        }
        if(file.size() > 1) {
            renderString(request, response, "上传的文件多大于1！", true);
        }
        //判断文件是否为Excel文件
        String upLoadFileName = file.get(0).getOriginalFilename();
        if(!upLoadFileName.endsWith(".xls") && !upLoadFileName.endsWith(".xlsx")){
            renderString(request, response, OperateTipsUtils.operateTips(OperateTipsUtils.FILEUPDOWN_MODEL_ERR,"请上传Excel文件！", ""), true);
            return;
        }

        // 错误收集文件名称
        String errorFilePath = request.getSession().getServletContext().getRealPath("/") + "\\tmpFiles\\";

        // 新建实体导入类 并设置实体类型和导入进度监控Id
        EntityImport entityImport = new EntityImport();
        entityImport.setEntityType(entityType);
        entityImport.setMonitorId(monitorId);

        // 执行导入process 返回导入的结果
        Map<String,Object> processRes = entityImport.process(file, errorFilePath, entityType, content, businessId);

        if(processRes == null){
            renderString(request, response, OperateTipsUtils.operateTips(OperateTipsUtils.FILEUPDOWN_OK,"文件上传成功！",""), true);
        } else{
            renderString(request, response, processRes, true);
        }


    }


}
