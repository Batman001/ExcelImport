package com.thinkgem.jeesite.modules.excelimport.process;

import java.util.List;
import java.util.Map;

/**
 * @author sunc create on 2019-05-10 15:12
 * excel导入功能的后处理接口
 */
public interface IReadAndPostProcess {

    /**
     * 初始化接口方法
     * @param entity 对于不同实体进行数据读取和后处理的初始化设置
     * @param entityType 前台传递对于本次实体导入的实体类型
     * @return IReadAndPostProcess
     */
    IReadAndPostProcess initial(Object entity, String entityType);


    /**
     * 根据从excel读出的bodyList，判断模板数据是否符合要求 主要调用校验策略类中justifyList方法实现
     * @param dataList excel解析得到数据
     * @param errorMsgMap 记录相关错误信息 返回给前台 存放模板中行号和对应的错误提示数据,errorMsgMap必须非NULL
     * @param begRow bodyList开始行数
     * @param isHeaderOrNot 用于标识本次校验的数据是否为TYPE_COMMON的headerDataList
     * @return 成功返回true 否则返回false
     * @throws Exception 抛出异常信息
     */
    boolean justifyDataList(List<Object> dataList, Map<Integer, String> errorMsgMap, int begRow, boolean isHeaderOrNot) throws Exception;
    /**
     * 保存方法
     * @param objList 将解析后的Excel数据保存接口方法
     * @return 保存成功返回true 否则返回false
     * @throws Exception 抛出异常
     */
    boolean createAndSaveObjects(List<Object> objList) throws Exception;

    /**
     * 每次导入后清除缓存数据
     */
    void clear();

}
