package com.thinkgem.jeesite.modules.excelimport.demo.process;

import com.thinkgem.jeesite.modules.excelimport.demo.depenency.BatchSubtotal;
import com.thinkgem.jeesite.modules.excelimport.demo.depenency.SupportBody;
import com.thinkgem.jeesite.modules.excelimport.process.IReadAndPostProcess;
import com.thinkgem.jeesite.modules.excelimport.process.SingleExcelReadAndPost;
import java.util.*;

/**
 * SupportBody的数据读取和后处理类 继承SingleExcelReadAndPost
 *
 * @author sunc create on 2019-05-27 10:07
 * 针对SupportBody的数据后处理业务逻辑类
 */

public class SupportBodyReadAndPostProcess extends SingleExcelReadAndPost {


    /**
     * 初始化数据读取和后处理器接口方法(包含三步处理)
     * 第一步 设置前台controller层传递的type字段 在使用不同校验策略时 保证key值的唯一性
     * 保证对于同一个系统的多个不同实体的校验策略例如（SupportBody的校验策略 可以保证不同hclType类型的校验策略类）
     * 第二步：针对SupportBody的导入 设置SupportHead的hclType属性
     * 第二步：后处理器中添加检验策略(添加BatchSubtotalJustifyStrategy && SupportBodyJustifyStrategy 两种校验策略)
     * @return IReadAndPostProcess 初始化的后处理器
     */
    @Override
    public synchronized IReadAndPostProcess initial(Object entity, String entityType) {
        // step1 设置前台controller层传递的type字段 用于区分数据读取和后处理器的type字段
        this.setEntityType(entityType);

//        // step2 针对SupportBody的导入设置SupportHead的HclType属性
//        SupportBody sb = (SupportBody) entity;
//        HclTypeEnum curHclType = sb.getSupportHead().getHclType();
//        this.setHclType(curHclType);

        // step3 针对SupportBody后处理器 添加对于数据的校验策略(包括SupportBodyJustifyStrategy和BatchSubtotalJustifyStrategy)
        if(strategyMap == null){
            strategyMap = new HashMap<>(4);
            // 使用前台传递实体类型 entityType + 校验策略类名称  作为校验策略map的key值
            strategyMap.put(entityType + BatchSubtotal.class.getSimpleName(), new BatchSubtotalJustifyStrategy());
            strategyMap.put(entityType + SupportBody.class.getSimpleName(), new SupportBodyJustifyStrategy());
        }
        return this;


    }


    /**
     * 每次导入后清除缓存数据 主要清空ThreadLocal内容
     */
    @Override
    public void clear() {
    }


    /**
     * 将读取数据用SupportBody对象封装 并保存至数据库
     * @param objects 解析后的Excel数据使用列表进行保存
     * @return 保存成功返回true 否则返回false
     * @throws Exception
     */
    @Override
    public boolean createAndSaveObjects(List<Object> objects) throws Exception {
        return true;
    }


}
