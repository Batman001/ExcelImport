package com.thinkgem.jeesite.modules.excelimport.util;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * description: 配置解析基类
 * @author JSJS-Songlc create 2019-05-22 11:20
 **/
public class ConfigParserBase {
    private Logger logger = LoggerFactory.getLogger(ConfigParserBase.class);
    protected Document dom;

    /**
     * Description: 校验xml文件的合法性
     *
     * @param filePath xml的文件路径
     * @return org.w3c.dom.Document
     * @author JSJS-Songlc
     * 2019/5/5
     */

    protected Document getDocument(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            logger.error(filePath + " 配置文件路径错误");
            return null;
        }
        if (this.dom == null) {
            try {
                this.dom = XMLUtils.getDocument(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.dom;
    }
}
