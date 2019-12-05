package com.thinkgem.jeesite.modules.excelimport.process;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.modules.utils.OperateTipsUtils;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.modules.excelimport.base.ExcelAssistantBase;
import com.thinkgem.jeesite.modules.excelimport.base.ImportExcelWrapper;
import com.thinkgem.jeesite.modules.excelimport.util.ConstEntityExcelHelper;
import com.thinkgem.jeesite.modules.excelimport.util.ExcelImportService;
import com.thinkgem.jeesite.modules.excelimport.util.ProgressSingleton;
import com.thinkgem.jeesite.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author sunc create on 2019-07-01 15:23
 */
public class EntityImport implements ObjectImportExcel {

    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(EntityImport.class);
    /**
     * 基类配置属性 Excel错误详细信息
     */
    protected StringWriter errMsgs = new StringWriter();

    /**
     * 通过调用jeesite平台的importExcel的方法进行解析 引入importExcelWrapper属性
     */
    protected ImportExcelWrapper importExcelWrapper = SpringContextHolder.getBean(ImportExcelWrapper.class);


    protected ExcelImportService excelService = SpringContextHolder.getBean(ExcelImportService.class);


    protected List<ExcelAssistantBase> excelModelConfig = null;

    /**
     * 线程安全的文件上传进度的MonitorId
     */
    private String monitorId;

    /**
     * 导入过程中导入进度的提示
     */
    private String progress;

    /**
     * 需要实现其Excel导入功能的实体类
     */
    private Object entity;

    private String entityType;

    /**
     * 导入过程中返回前台提示消息
     */
    private static String msg = "";

    /**
     * 解析Excel过程错误信息存储map
     */
    protected Map<Integer, String> errorMsgMap;

    /**
     * 为了对错误信息进行收集 设置的exportExcel属性
     */
    protected ExportExcel exportExcel;

    /**
     * Excel导入过程中 错误数据 TYPE_COMMON Header模板表头
     */
    protected String[] commonPropertyStr;

    /**
     * Excel导入过程中 错误数据 TYPE_CONTENT header模板表头
     */
    protected String[] contentPropertyStr;


    /*===============================构造函数以及getter setter方法===============================*/

    public EntityImport(){}

    public String getMonitorId() {
        return monitorId;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType  = entityType;
    }

    /*===============================导入流程处理===============================*/


    /**
     * 首先校验导入功能的前台传参是否正确
     * 然后根据不同的实体类型 生成对应Excel的配置文件
     * 针对不同Object的Excel导入功能都需要的模板流程 List<ExcelAssistantBase> contentPropertyStr 返回针对不同实体的ExcelAssistantBase 配置信息
     *
     * @return 返回处理状态信息
     */
    @Override
    public Map<String,Object> configProcess(String entityType, String content, String businessId) throws ClassNotFoundException {
        progress = "获取Excel导入模板中....";
        logger.info(progress);
        ProgressSingleton.put(this.monitorId, progress);

        // 获取实体对应的模板配置信息
        Map<String, Object> map = ConstEntityExcelHelper.initConfig(entityType, businessId);
        commonPropertyStr = (String[]) map.get("commonPropertyStr");
        contentPropertyStr = (String[]) map.get("contentPropertyStr");
        excelModelConfig = (List<ExcelAssistantBase>) map.get("excelModelConfig");

        // 通过JsonMapper返回json对应的实体类
        Class<?> entityClassName = (Class<?>) map.get("entityClassName");
        this.entity = JsonMapper.fromJsonString(content, entityClassName);

        // 对生成的Excel配置文件进行判断
        if (excelModelConfig == null){
            msg = "Excel导入模板配置未完成,出现错误!";
            return OperateTipsUtils.operateTips(OperateTipsUtils.STATUS_ERROR, msg, null);

        }
        msg = "Excel导入模板配置完成！";
        ProgressSingleton.put(this.monitorId, msg);
        return OperateTipsUtils.operateTips(OperateTipsUtils.STATUS_OK, msg, null);

    }

    /**
     * 针对不同Object的Excel导入功能都需要的解析流程 Jeesite平台的ImportExcel初始化方法
     * @param files 前台获取的excel文件
     * @return 返回处理状态信息
     * @throws Exception jeesite平台的ImportExcel初始化异常
     */
    @Override
    public Map<String,Object> initImportExcel(List<MultipartFile> files) throws Exception {
        // 通过configProcess得到复杂模板的headerNo
        progress = "初始化Excel模板中...";
        ProgressSingleton.put(this.monitorId, progress);
        int headerRowNo = excelModelConfig.get(0).getHeaderRowNo();

        importExcelWrapper.init(files.get(0), headerRowNo, 0);
        if(importExcelWrapper == null){
            msg = "Excel导入解析初始化未完成!";
            return OperateTipsUtils.operateTips(OperateTipsUtils.STATUS_ERROR, msg, null);

        }
        else{
            msg = "Excel导入解析初始化完成!";
            ProgressSingleton.put(this.monitorId, msg);
            return OperateTipsUtils.operateTips(OperateTipsUtils.STATUS_OK, msg, null);
        }
    }


    /**
     * 针对不同Object的Excel导入功能 数据读取和后处理流程
     *
     * @return 返回处理状态信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map<String, Object> readAndPostProcess()  {
        long start = System.currentTimeMillis();
        progress = "Excel导入读取数据和后处理中...";
        ProgressSingleton.put(this.monitorId, progress);

        // 初始化数据读取和后处理器
        IReadAndPostProcess instance = ConstEntityExcelHelper.initPostProcessor(entityType);

        try{
            errorMsgMap = importExcelWrapper.readAndPostProcessExcel(excelModelConfig, errMsgs, instance.initial(entity, entityType));

        } catch (Exception e) {
            logger.error("Exception Message : {}", e.getMessage());
            logger.error("Exception toString abd track space : {} ", "\r\n" + e);
            logger.error(ExcelImportService.errorTraceSpace(e));
            if(e instanceof ImportExcelWrapper.MapWithException) {
                errorMsgMap = ((ImportExcelWrapper.MapWithException)e).getMap();
                if(errorMsgMap == null) {
                    return OperateTipsUtils.operateTips(OperateTipsUtils.FILEUPDOWN_MODEL_ERR,"模板数据错误！",errMsgs.toString());
                }
            }
        }

        logger.info("Excel数据读取以及后处理过程消耗时间为 :" + (System.currentTimeMillis() - start) + "ms");

        //清除缓存
        for (ExcelAssistantBase eab : excelModelConfig) {
            eab.refresh();
        }
        msg = "Excel数据读取和写入数据库成功！文件导入成功！";
        ProgressSingleton.put(this.monitorId, msg);
        return OperateTipsUtils.operateTips(OperateTipsUtils.FILEUPDOWN_OK, msg, "");

    }


    /**
     * 解析过程中处理错误信息 并将错误信息写入本地服务器磁盘
     *
     * @param errorFilePath 错误信息收集文件名称
     * @return 返回信息
     * @throws IOException 写入本地的IO异常处理
     */
    @Override
    public Map<String,Object> errorInfoProcess(String errorFilePath) throws IOException {
        if(errorMsgMap == null) {
            progress = "导入数据校验存在错误，正在收集异常数据...";
            ProgressSingleton.put(this.monitorId, progress);
            return OperateTipsUtils.operateTips(OperateTipsUtils.FILEUPDOWN_MODEL_ERR,"模板数据错误！",errMsgs.toString());
        }
        //将错误数据写入服务器本地
        if(errorMsgMap.size() > 0) {
            String fileName = "errData-" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            if(!FileUtils.createFile(errorFilePath + fileName)) {
                logger.debug(errorFilePath + fileName + "创建失败！");
                return OperateTipsUtils.operateTips(OperateTipsUtils.FILEUPDOWN_MODEL_ERR,"收集错误数据新建文件失败！！","");
            }

            //验证模板数据是否已配置
            if(contentPropertyStr == null || contentPropertyStr.length < 2) {
                return OperateTipsUtils.operateTips(OperateTipsUtils.FILEUPDOWN_MODEL_ERR,"模板数据还未在系统中配置，请先配置模板数据！","");
            }

            //将错误行号递增排序
            ArrayList<Integer> errorRowNos = new ArrayList<>(errorMsgMap.keySet());
            Collections.sort(errorRowNos);
            // 删除判断errorMsgMap的校验错误类型的设置rowNo
            errorRowNos.remove(0);

            // 通常认为TYPE_COMMON的header只有一行 因此可用于判断 errorMsgMap存储错误信息为YPE_COMMON、还是TYPE_CONTENT
            String[] headers;
            // TYPE_COMMON 通过判断errorMsgMap中key为0的value信息 获取校验的错误类型
            if(errorMsgMap.get(0).equals(ConstEntityExcelHelper.HEADER_ERROR)) {
                // 删除判断errorMsgMap的校验错误类型的value
                errorMsgMap.remove(0);
                if(exportExcel == null) {
                    // 判断TYPE_COMMON配置文件中是否存在合并单元格的情形 如存在 则只返回错误信息
                    if(this.excelModelConfig.get(0).getMergeCellOrNot()) {
                        headers = new String[]{"出错行数", "出错原因"};
                        ExportExcel exportExcel = new ExportExcel("错误数据", headers);
                        // 将errorMsgMap中信息写入Excel
                        excelService.exportMergeErrorData(exportExcel.getSheet(0), errorMsgMap, errorRowNos);
                        this.exportExcel = exportExcel;
                    }else {
                        // 由于是TYPE_COMMON(headerNames以及properties在同一行)校验出现错误 因此需要对其特殊处理
                        headers = commonPropertyStr[0].split(",");
                        exportExcel = new ExportExcel("错误数据", headers);
                        //将错误数据copy到新的Excel中，数据还在内存
                        ExportExcel.copyExcelByRowNum(importExcelWrapper.getSheetByIndex(0), exportExcel.getSheet(0),
                                1 , errorRowNos, headers.length + 1, errorMsgMap,0);
                    }

                }
            }
            // TYPE_CONTENT
            else {
                errorMsgMap.remove(0);
                if(exportExcel == null) {
                    if(excelService.judgeContentExistsMergeCell(this.excelModelConfig)) {
                        // 将errorMsgMap中信息写入Excel
                        headers = new String[]{"出错行数", "出错原因"};
                        ExportExcel exportExcel = new ExportExcel("错误数据", headers);
                        excelService.exportMergeErrorData(exportExcel.getSheet(0), errorMsgMap, errorRowNos);
                        this.exportExcel = exportExcel;
                    } else {
                        headers = contentPropertyStr[0].split(",");
                        exportExcel = new ExportExcel("错误数据", headers);
                        //将错误数据copy到新的Excel中，数据还在内存
                        ExportExcel.copyExcelByRowNum(importExcelWrapper.getSheetByIndex(0), exportExcel.getSheet(0),
                                2, errorRowNos, headers.length, errorMsgMap,0);

                    }

                }
            }

            // 使用文件流写入文件
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(errorFilePath + fileName);
                exportExcel.write(fos);
                fos.close();
            } catch (Exception e) {
                //释放错误数据内存
                errorMsgMap.clear();
                logger.error("Exception Message : {}", e.getMessage());
                logger.error("Exception toString abd track space : {} ", "\r\n" + e);
                logger.error(ExcelImportService.errorTraceSpace(e));

            }
            errorMsgMap.clear();
            msg = "上传文件中有错误数据，正在进行下载...";
            ProgressSingleton.put(this.monitorId, msg);

            return OperateTipsUtils.operateTips(OperateTipsUtils.FILEUPDOWN_CONTENT_ERR, msg, fileName);
        }

        return null;
    }

    /**
     * Excel开始进行导入流程的调用方法
     * @param files 前台获取的excel文件
     * @param errorFilePath 传入错误收集文件名称
     * @param entityType 实体的名称
     * @param content 实体的String形式
     * @param businessId 当前上传Excel的业务id
     * @return Excel导入流程初始化 返回状态
     * @throws Exception 异常处理
     */
    @Override
    public Map<String,Object> process(List<MultipartFile> files, String errorFilePath, String entityType, String content, String businessId) throws Exception {

        Map<String, Object> configRes = configProcess(entityType, content, businessId);
        if(!excelService.checkProcessState(configRes)){
            return configRes;
        }

        Map<String, Object> initRes = initImportExcel(files);
        if(!excelService.checkProcessState(initRes)){
            return initRes;
        }
        Map<String, Object> postRes = readAndPostProcess();
        if(!excelService.checkProcessState(postRes)){
            return postRes;
        }

        return errorInfoProcess(errorFilePath);

    }


}
