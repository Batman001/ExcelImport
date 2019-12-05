package com.thinkgem.jeesite.modules.excelimport.util;

import com.thinkgem.jeesite.modules.excelimport.base.ExcelAssistantBase;
import com.thinkgem.jeesite.modules.excelimport.process.IReadAndPostProcess;
import com.thinkgem.jeesite.modules.excelimport.process.SingleExcelReadAndPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author sunc create on 2019-06-19 14:40
 */
public final class ConstEntityExcelHelper {


    /** excel模板头的通用部分 */
    public static int TYPE_COMMON = 1;

    /** excel模板的数据部分 */
    public static int TYPE_CONTENT = 2;

    /** 表明当前excel cell中的值无特殊意义 */
    public static int PROPERTY_TYPE_NULL = 10;

    /** 表明当前excel cell中存放的是数量 */
    public static int PROPERTY_TYPE_CNT = 11;

    /** 本行，用于只是模板数据所在行 */
    public static int ROW_TYPE_FOLLOW = 20;

    /** 下行 */
    public static int ROW_TYPE_NEXT = 21;

    /**
     * 表明对于Excel数据进行校验过程的错误类型
     */
    public static final String HEADER_ERROR = "header-error";
    public static final String CONTENT_ERROR = "content-error";

    protected static Logger logger = LoggerFactory.getLogger(ConstEntityExcelHelper.class);

    /**
     * 用于Excel导入业务ID与对应模板的 Map存储信息
     */

    private static Map<String, String> HELPER_MAP;

    /**
     * 组合模式配置不同实体类型获取propertyStr、excelModelConfig、实体类 配置信息
     * @param entityType 实体名称
     * @return 包含propertyStr、excelModelConfig的配置Map信息
     */
    public static Map<String, Object> initConfig(String entityType, String businessId) {

        /*
         *  读取外部引用jar项目的resource目录下的 import-excel.properties 配置文件
         *  获得业务ID配置信息以及后处理器放置位置的配置信息，并存储至HELPER_MAP中
         *  */
        LoadPropertiesFile loadPropertiesFile = new LoadPropertiesFile( Objects.requireNonNull(Thread.currentThread()
                .getContextClassLoader().getResource("/import-excel.properties")).getPath());
        HELPER_MAP = loadPropertiesFile.loadProperties();

        Map<String, Object> res = new HashMap<>(2);
        ImportConfigParser icp = new ImportConfigParser();

        String[] commonPropertyStr = new String[2];
        String[] contentPropertyStr = new String[2];
        Class<?> entityClassName = null;
        String templateName = HELPER_MAP.get(businessId);

        try{
            // 根据不同模板配置不同的文件进行区分 解决方法：业务类型与模板进行绑定
            List<ExcelAssistantBase> descriptions = icp.parseConfigXml(Objects.requireNonNull
                    (Thread.currentThread().getContextClassLoader().getResource("/" + templateName)).getPath());
            res.put("excelModelConfig", descriptions);

            // 从读取的XML配置中 取出如果出现错误数据需要的导出的字段 用propertyStr进行表示 包括 TYPE_COMMON 和 TYPE_CONTENT

            // TYPE_COMMON
            StringBuilder commonHeaderTmpStr = new StringBuilder();
            StringBuilder commonPropertyTmpStr = new StringBuilder();

            // TYPE_CONTENT
            StringBuilder contentHeaderTmpStr = new StringBuilder();
            StringBuilder contentPropertyTmpStr = new StringBuilder();
            for(ExcelAssistantBase item: descriptions) {
                // 筛选全部配置文件中class为导入实体名称的,并且headerNames以及properties非空,作为commonPropertyStr contentPropertyStr
                if (entityType.endsWith(item.getClz().getSimpleName()) && item.getHeaderNames() != null && item.getProperties() != null ) {
                    entityClassName = item.getClz();
                    if(TYPE_COMMON == item.getHeadType()) {

                        for(String headerItem : item.getHeaderNames()) {
                            commonHeaderTmpStr.append(headerItem).append(",");
                        }
                        for(String propertyItem : item.getProperties()) {
                            commonPropertyTmpStr.append(propertyItem).append(",");
                        }
                        // TYPE_COMMON
                        commonPropertyStr[0] = commonHeaderTmpStr.substring(0, commonHeaderTmpStr.length()-1);
                        commonPropertyStr[1] = commonPropertyTmpStr.substring(0, commonPropertyTmpStr.length()-1);

                    } else {
                        for (String headerItem : item.getHeaderNames()) {
                            contentHeaderTmpStr.append(headerItem).append(",");
                        }
                        for (String propertyItem : item.getProperties()) {
                            contentPropertyTmpStr.append(propertyItem).append(",");
                        }
                        // TYPE_CONTENT
                        contentPropertyStr[0] = contentHeaderTmpStr.substring(0, contentHeaderTmpStr.length()-1);
                        contentPropertyStr[1] = contentPropertyTmpStr.substring(0, contentPropertyTmpStr.length()-1);
                        break;
                    }
                }

            }

            res.put("commonPropertyStr", commonPropertyStr);
            res.put("contentPropertyStr", contentPropertyStr);
            res.put("entityClassName", entityClassName);

        } catch(Exception e) {
            logger.error("Exception Message : {}", e.getMessage());
            logger.error("Exception toString abd track space : {} ", "\r\n" + e);
            logger.error(ExcelImportService.errorTraceSpace(e));
            logger.error("没有对Excel文件进行注册配置！需要在TEMPLATE_MAP中对业务ID与Excel模板进行配置！！！");
        }
        return res;
    }


    /**
     * 根据传递的实体类和实体名称 进行后处理器的初始化
     * @param entityType 前台传递实体类型
     * @return 初始化后的后处理器
     */
    public static IReadAndPostProcess initPostProcessor(String entityType) {
        IReadAndPostProcess instance = null;
        try{
            // 通过反射 根据不同的entityType得到对应的后处理类
            String suffix = "ReadAndPostProcess";
            String currClassName = entityType + suffix;
            Class singleInstance = Class.forName(HELPER_MAP.get("basePackageName") + "." + currClassName);
            instance = SingleExcelReadAndPost.getInstance(singleInstance);



        }catch(Exception e){
            logger.error("Exception Message : {}", e.getMessage());
            logger.error("Exception toString abd track space : {} ", "\r\n" + e);
            logger.error(ExcelImportService.errorTraceSpace(e));
        }

        return instance;
    }
}

