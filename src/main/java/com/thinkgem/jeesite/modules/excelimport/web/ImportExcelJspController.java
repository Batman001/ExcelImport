package com.thinkgem.jeesite.modules.excelimport.web;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.excelimport.process.EntityImport;
import com.thinkgem.jeesite.modules.excelimport.util.ExcelImportService;
import com.thinkgem.jeesite.modules.utils.OperateTipsUtils;
import org.activiti.engine.impl.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sunc
 * Date 2019/12/4 17:11
 * Description
 */
@Controller
@RequestMapping(value = "${adminPath}/import")
public class ImportExcelJspController extends BaseController {
    @Autowired
    private ExcelImportService excelService;

    @RequestMapping(value = "getThread", method = RequestMethod.GET)
    public void getThread(String type, String businessId, HttpServletRequest request, HttpServletResponse response) {
        String thread = excelService.generateRandomThread();
        HttpSession session = request.getSession();
        session.setAttribute("thread",thread);
        session.setAttribute("content", JSONObject.stringToValue("{}"));
        session.setAttribute("type", type);
//        session.setAttribute("type", "PsJHKeypointExamine");
        session.setAttribute("businessId", businessId);
//        session.setAttribute("businessId", "1");
        this.renderString(response, thread);
    }

    @RequestMapping(value = "import", method = RequestMethod.POST)
    public void importFile(MultipartFile file, @RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<MultipartFile> files = new ArrayList<>();
        files.add(file);

        HttpSession session = request.getSession();
        String entityType = (String)session.getAttribute("type");;
        String content = (String)session.getAttribute("content");;
        String businessId = (String)session.getAttribute("businessId");;
        if (!StringUtils.isBlank(entityType) && !StringUtils.isBlank(content)) {
            String monitorId = (String) session.getAttribute("thread");;
            if (files == null || files.size() == 0) {
                this.renderString(request, response, "请选择文件上传！", true);
            }

            if (files.size() > 1) {
                this.renderString(request, response, "上传的文件多大于1！", true);
            }

            String upLoadFileName = (files.get(0)).getOriginalFilename();
            if (!upLoadFileName.endsWith(".xls") && !upLoadFileName.endsWith(".xlsx")) {
                this.renderString(request, response, OperateTipsUtils.operateTips(22, "请上传Excel文件！", ""), true);
            } else {
                String errorFilePath = request.getSession().getServletContext().getRealPath("/static/errorfile/");
                EntityImport entityImport = new EntityImport();
                entityImport.setEntityType(entityType);
                entityImport.setMonitorId(monitorId);
                Map<String, Object> processRes = entityImport.process(files, errorFilePath, entityType, content, businessId);
                if (processRes == null) {
                    this.renderString(request, response, OperateTipsUtils.operateTips(0, "文件上传成功！", ""), true);
                } else {
                    this.renderString(request, response, processRes, true);
                }

            }
        } else {
            throw new Exception("ImportExcelController:未传递 entityType 或 content参数！");
        }

    }
}
