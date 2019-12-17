package com.thinkgem.jeesite.modules.excelimport.process;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author sunc create on 2019-05-17 09:46
 * description: 对象导入的接口
 */
public interface ObjectImportExcel {

    /**
     * 针对不同Object的Excel导入功能都需要的模板流程 List<ExcelAssistantBase>  返回针对不同实体的ExcelAssistantBase 配置信息
     * @param entityType 实体的名称
     * @param content 实体的String形式
     * @param businessId 当前上传Excel的业务id
     * @throws ClassNotFoundException 初始化过程中需要反射得到实体的class 可能会抛出异常
     * @return 返回处理状态信息
     */
    Map<String,Object> configProcess(String entityType, String content, String businessId) throws ClassNotFoundException;

    /**
     * 针对不同Object的Excel导入功能的Jeesite平台的ImportExcel初始化方法
     * @return 返回处理状态信息
     * @param files 前台获取的excel文件
     * @throws Exception 异常信息
     */
    Map<String,Object> initImportExcel(List<MultipartFile> files) throws Exception;


    /**
     * 针对不同Object的Excel导入功能 数据读取和后处理流程
     *  @return 返回处理状态信息
     * @throws Exception 异常信息
     */
    Map<String,Object> readAndPostProcess() throws Exception;


    /**
     * 解析过程中处理错误信息 并将错误信息写入本地服务器磁盘
     * @param filePath 错误信息收集文件名称
     * @param errorMsgMap 校验策略出错信息map
     * @return 返回信息
     * @throws IOException 写入本地的IO异常处理
     */
    Map<String,Object> errorInfoProcess(String filePath, Map<Integer, String> errorMsgMap) throws IOException;

    /**
     * 初始化 抽象 BaseExcel方法
     * @param errorFilePath 传入错误收集文件名称
     * @param files 前台获取的excel文件
     * @param entityType 实体类型
     * @param content 实体的json格式
     * @param businessId 当前上传Excel的业务id
     * @return Excel导入流程初始化 返回状态
     * @throws Exception 异常处理
     */
    Map<String,Object> process(List<MultipartFile> files, String errorFilePath, String entityType, String content, String businessId) throws Exception;
}
