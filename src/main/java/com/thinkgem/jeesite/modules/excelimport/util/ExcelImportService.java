package com.thinkgem.jeesite.modules.excelimport.util;

import com.thinkgem.jeesite.modules.utils.OperateTipsUtils;
import com.thinkgem.jeesite.modules.excelimport.base.ExcelAssistantBase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author sunc create on 2019-05-21 15:07
 */
@Service
public class ExcelImportService {

    /**
     * 根据当前时间戳 + UUID生成目前实体导入的Id
     *
     * @return 随机生成的Id
     */
    public String generateRandomThread() {
        long startImportTime = System.currentTimeMillis();
        UUID uuid = UUID.randomUUID();
        return startImportTime + uuid.toString();
    }

    /**
     * 获取前台上传的文件
     *
     * @param request request请求
     * @return request中文件
     */
    public List<MultipartFile> getFile(HttpServletRequest request) {
        //处理多文件上传
        MultipartHttpServletRequest multiRequest = null;
        try {
            multiRequest = (MultipartHttpServletRequest) request;
            //设置编码格式
            multiRequest.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //获取上传文件名列表
        List<MultipartFile> files = multiRequest.getFiles("file");
        return files;
    }


    /**
     * 判断Excel导入不同流程中返回的状态值 如果成功 返回 true 否则返回 false
     *
     * @param processRes 不同流程的返回结果状态值
     * @return boolean
     */
    public boolean checkProcessState(Map<String, Object> processRes) {
        return new Integer(OperateTipsUtils.STATUS_OK).equals(processRes.get("state"));
    }

    /**
     * 判断Excel导入的配置文件中 Content内容中是否含有合并单元格情况
     * @param excelModelConfig 待判断Excel导入的配置信息
     * @return 如果Content内容包含合并单元格情况 则返回 true 否则返回false
     */
    public boolean judgeContentExistsMergeCell(List<ExcelAssistantBase> excelModelConfig) {
        boolean result = false;

        for(ExcelAssistantBase eab: excelModelConfig) {
            if (eab.getHeadType() == ConstEntityExcelHelper.TYPE_CONTENT && eab.getMergeCellOrNot()) {
                result = true;
                break;
            }
        }

        return result;


    }

    /**
     * 处理 Excel文件中出现合并单元格情形的错误数据导出 只导出错误信息
     * @param errorMsgMap 导出的错误信息 map
     * @param errorRowNos 导出的错误信息行数
     */
    public boolean exportMergeErrorData(Sheet toSheet, Map<Integer, String> errorMsgMap, ArrayList<Integer> errorRowNos) {

        if(toSheet == null  || errorMsgMap == null || errorRowNos == null) {
            return false;
        }
        // 由于错误数据存在 错误数据标题行（rowNo=0) + 出错行数与出错原因(rowNo=1) 两行
        // 因此导出错误数据时从第三行开始 即 begRow=2
        int begRow=2;
        for(int index=0; index<errorRowNos.size(); index++) {
            Row row = toSheet.createRow(begRow);
            begRow += 1;

            Cell cellRowNo = row.createCell(0);
            cellRowNo.setCellValue(errorRowNos.get(index));
            Cell cellErrorInfo = row.createCell(1);
            cellErrorInfo.setCellValue(errorMsgMap.get(errorRowNos.get(index)));
        }
        return true;
    }

    /**
     * 将异常信息输出至日志
     * @param e 异常Exception e
     * @return 输入到日志文件中的异常信息
     */
    public static String errorTraceSpace(Exception e) {
        StringBuilder sb = new StringBuilder();
        if(e != null) {
            for(StackTraceElement element: e.getStackTrace()) {
                sb.append("\r\n\t").append(element);
            }
        }
        return sb.length() == 0 ? null : sb.toString();
    }
}

