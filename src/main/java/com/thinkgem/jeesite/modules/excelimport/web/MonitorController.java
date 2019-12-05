package com.thinkgem.jeesite.modules.excelimport.web;

import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.excelimport.util.ProgressSingleton;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sunc create on 2019-06-19 15:46
 * description: 导入excel文件进度监控controller
 * 每隔固定的时间访问MonitorController 获取当前导入进度
 * 处理逻辑:ImportExcelController将导入进度写入本地, MonitorController每隔固定时间进行读取并返回给前台
 */
@Controller
@RequestMapping(value = "${adminPath}/importExcel")
public class MonitorController extends BaseController {


    @RequestMapping(value="monitor")
    public void monitorProgress(HttpServletRequest request, HttpServletResponse response) {

        String status = "";
        String monitorId = request.getParameter("thread");
        try{
            status = ProgressSingleton.get(monitorId).toString();
        }catch (NullPointerException e) {
            status = "该文件未执行导入任务...";
            e.printStackTrace();
        }
        this.renderString(request, response, status, true);
    }


}
