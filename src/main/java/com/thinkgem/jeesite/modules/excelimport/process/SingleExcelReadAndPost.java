package com.thinkgem.jeesite.modules.excelimport.process;

import com.thinkgem.jeesite.modules.excelimport.util.ConstEntityExcelHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sunc create on 2019-05-10 17:10
 * 单例 Excel后处理方法基类
 */
public class SingleExcelReadAndPost implements IReadAndPostProcess {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private String entityType;

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * 策略模式 对Excel数据进行校验使用不同的校验方法
     */
    protected Map<String, ObjectImportJustifyStrategy> strategyMap = null;

    protected SingleExcelReadAndPost(){

    }

    private static Map<Class<? extends SingleExcelReadAndPost>,
            IReadAndPostProcess> instanceMap = new HashMap<>();

    /**
     * 单例 线程安全的 getInstance方法
     * @param <T> 泛型
     * @param clz 继承基本Excel后处理基类的 具体实现类 如SupportBodyPostProcess.class
     * @return 返回Excel后处理具体实现类的实例
     * @throws InstantiationException 不能实例化Exception
     * @throws IllegalAccessException 安全权限Exception 一般是由于java在反射时调用private方法导致
     */
    public synchronized static <T extends SingleExcelReadAndPost> IReadAndPostProcess getInstance(Class<T> clz)
            throws InstantiationException, IllegalAccessException{
        if(clz == null) {
            return null;
        }
        if(instanceMap.containsKey(clz)) {
            return instanceMap.get(clz);
        }
        else {
            IReadAndPostProcess obj = clz.newInstance();
            instanceMap.put(clz, obj);
            return obj;
        }
    }


    /**
     * 初始化后处理器 接口方法(包含两步处理)
     * 第一步：针对不同的实体的进行导入 对后处理器初始化其特定属性
     * 第二步：后处理器中添加检验策略
     * @param entity 待导入的实体
     * @param entityType 前台controller层传递的实体的类型
     * @return IReadAndPostProcess
     */
    @Override
    public IReadAndPostProcess initial(Object entity, String entityType) {
        return this;
    }

    /**
     * 根据从excel读出的bodyList，判断模板数据是否符合要求 采用调用校验策略类中的justifyList实现
     *
     * @param dataList    excel解析得到数据
     * @param errorMsgMap 记录相关错误信息 返回给前台 存放模板中行号和对应的错误提示数据,errorMsgMap必须非NULL
     * @param begRow  bodyList开始行数
     * @param isHeaderOrNot 用于标识本次校验的数据是否为TYPE_COMMON的headerDataList
     * @return 成功返回true 否则返回false
     * @throws Exception 异常信息
     */
    @Override
    public boolean justifyDataList(List<Object> dataList, Map<Integer, String> errorMsgMap, int begRow, boolean isHeaderOrNot) throws Exception {
        if(dataList == null || dataList.size() == 0 ) {
            return true;
        }
        boolean justifyResult;
        // 如果本次校验的数据为TYPE_COMMON的headerDataList 调用justifyHeaderList方法
        if(isHeaderOrNot) {
            // 对bodyList中全部为Object类的进行跳过 直接返回true
            if(dataList.get(0).toString().startsWith("java.lang.Object")) {
                return true;
            }
            justifyResult = this.strategyMap.get(this.getEntityType() + dataList.get(0).getClass().getSimpleName()).justifyHeaderList(dataList, errorMsgMap, begRow);
            // 如果数据校验未通过，则向errorMsgMap中添加错误类型信息 用于区分写入本地错误数据类型 TYPE_COMMON header错误
            // 由于errorMsgMap中存储具体错误信息是从key为1开始，因此设置key为0 存储错误类型信息
            if(!justifyResult) {
                errorMsgMap.put(0, ConstEntityExcelHelper.HEADER_ERROR);
            }
        } else {
            // 本次校验数据是对TYPE_CONTENT数据进行校验 通过调用校验策略类中的justifyContentList方法
            justifyResult = this.strategyMap.get(this.getEntityType() + dataList.get(0).getClass().getSimpleName()).justifyContentList(dataList, errorMsgMap, begRow);
            // 如果数据校验未通过，向errorMsgMap中添加错误类型信息 用于区分写入本地错误数据类型 TYPE_CONTENT content错误
            if(!justifyResult) {
                errorMsgMap.put(0, ConstEntityExcelHelper.CONTENT_ERROR);
            }
        }
        return justifyResult;

    }

    /**
     * 保存方法
     *
     * @param objList 将解析后的Excel数据保存接口方法
     * @return 保存成功返回true 否则返回false
     * @throws Exception 异常信息
     */
    @Override
    public boolean createAndSaveObjects(List<Object> objList) throws Exception {
        return false;
    }

    /**
     * 每次导入后清除校验策略 保证多线程状态下 校验策略的正确性
     */
    @Override
    public void clear() {
        for(ObjectImportJustifyStrategy strategy : strategyMap.values()) {
            strategy.clear();
        }
    }
}