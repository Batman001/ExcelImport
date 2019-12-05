package com.thinkgem.jeesite.modules.excelimport.util;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.excelimport.base.ExcelAssistantBase;
import com.thinkgem.jeesite.modules.excelimport.base.ExcelImportAssistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * program 通用Excel导入功能
 * description 导出xml配置解析类
 * @author  sunc 2019-09-30 11:10
 **/
class ImportConfigParser extends ConfigParserBase {
    private Logger logger = LoggerFactory.getLogger(ImportConfigParser.class);


    /**
     * 解析导入xml，解析后headerNames标签和properties标签的信息存入到缓存中，而解析后的content标签信息，则返回
     * @param filePath 配置xml文件路径
     * @return 解析后的配置文件 List<ExcelAssistance>
     * @throws Exception 解析错误的Exception
     */
    List<ExcelAssistantBase> parseConfigXml(String filePath) throws Exception{
        this.getDocument(filePath);
        if (this.dom == null) {
            logger.error("dom对象初始化异常！");
            return null;
        }

        //判断根节点有效性(有且只有一个根节点)
        NodeList root = this.dom.getElementsByTagName("template");
        int size = 0;
        if (root == null || (size = root.getLength()) == 0 || size > 2) {
            throw new Exception("模板中有" + size + "template" + " 个根节点");
        }

        List<ExcelAssistantBase> result = new LinkedList<>();

        NodeList childNodes = root.item(0).getChildNodes();
        int childLen = childNodes.getLength();
        String errMsg;
        for (int index = 0; index < childLen; index++) {
            Node child = childNodes.item(index);
            if(!(child instanceof Element)){
                continue;
            }
            //获取结点的名称
            String nodeName = child.getNodeName();
            //解析headerNames标签
            if("headerNames".equals(nodeName)){
                errMsg = parseHeaderNames(child,null);
                if(!StringUtils.isBlank(errMsg)){
                    throw new Exception(errMsg);
                }
                //解析properties标签
            }else if("properties".equals(nodeName)){
                errMsg = parseProperties(child,null);
                if(!StringUtils.isBlank(errMsg)){
                    throw new Exception(errMsg);
                }
                //解析解析content标签
            }else if("contents".equals(nodeName)){
                errMsg = parseContents(child,result);
                if(!StringUtils.isBlank(errMsg)){
                    throw new Exception(errMsg);
                }
            }
        }
        //返回要填充的信息及其填充格式
        return result;
    }

    /**
     * 解析headerNames标签 并将其更新到 以ExcelImportAssistant类型的eia参数中
     * @param headerNamesNode headName Node 的节点
     * @param eia Excel导入类
     * @return 解析后直接放回
     */
    private String parseHeaderNames(Node headerNamesNode, ExcelImportAssistant eia){

        // 1. 首先解析headerNames上层标签
        String headerId = ((Element)headerNamesNode).getAttribute("id");

        /*
          如果存在 headerNames 中全部headerName的begRow 和 begCol 通用配置的问题 直接获取上层标签的 headerNameBegRow 以及 headerNameBegCol
          否则读取下层标签headerName获取begRow和begCol 然后用列表进行存储
        */
        String headerNameBegRow = ((Element) headerNamesNode).getAttribute("headerNameBegRow");
        String headerNameBegCol = ((Element) headerNamesNode).getAttribute("headerNameBegCol");
        List<Integer[]> headerNameBegRowAndCols = new ArrayList<>();
        if(StringUtils.isNotBlank(headerNameBegRow) && StringUtils.isNotBlank(headerNameBegCol)) {
            headerNameBegRowAndCols.add(new Integer[]{Integer.valueOf(headerNameBegRow), Integer.valueOf(headerNameBegCol)});
            eia.setHeaderNameBegRowAndCols(headerNameBegRowAndCols);
        }


        //2、解析headerName子标签
        NodeList nodes = headerNamesNode.getChildNodes();
        //获取子标签数目
        int nodeLen = nodes.getLength();
        //存放表头名称
        List<String> headerNames = null;
        // 如果存在对于每一个headerName的配置 则通过headerBegRowAndColumnDetails存放详细配置的每一个headerName 开始的行数和列数
        List<Integer[]> headerBegRowAndColumnDetails = new ArrayList<>();
        // 存放header的headerMeaning信息
        List<Integer> headerMeaning = new ArrayList<>();

        for (int index = 0; index < nodeLen; index++) {
            Node node = nodes.item(index);
            if(!(node instanceof Element)){
                continue;
            }
            Element ele = ((Element)node);
            //设置表头名称
            String name = ele.getAttribute("name");
            if(!StringUtils.isBlank(name)){
                if(headerNames == null){
                    headerNames = new ArrayList<>();
                }
                headerNames.add(name);
            }

            //设置表头开始行和列值
            String begRow = ele.getAttribute("begRow");
            String begCol = ele.getAttribute("begCol");
            String currHeaderMeaning = ele.getAttribute("headerMeaning");

            if(!StringUtils.isBlank(currHeaderMeaning)) {
                headerMeaning.add(Integer.valueOf(currHeaderMeaning));
            }
            if(!StringUtils.isBlank(begCol) && !StringUtils.isBlank(begRow)){
                headerBegRowAndColumnDetails.add(new Integer[]{Integer.valueOf(begRow),Integer.valueOf(begCol)});
                eia.setHeaderNameBegRowAndCols(headerBegRowAndColumnDetails);
            }

        }
        //3、遍历完headerNames的子标签后 将headerMeaning和headerNames 设置为当前解析对象的对应属性
        if (!headerMeaning.isEmpty()) {
            eia.setHeaderMeaning(headerMeaning.stream().mapToInt(Integer::intValue).toArray());
        } else{
            eia.setHeaderMeaning(null);
        }
        eia.setHeaderNames(headerNames);

        return null;
    }

    /**
     * 解析properties标签 并将其更新到 以ExcelImportAssistant类型的eia参数中
     * @param propertiesNode property node
     * @param eia Excel解析配置文件
     * @return 解析后直接返回
     */
    private String parseProperties(Node propertiesNode, ExcelImportAssistant eia){
        //获取properties标签的id号
        String propertyId = ((Element)propertiesNode).getAttribute("id");

         /*
          如果存在property通用配置的问题 直接获取上层标签的 propertyValBegRow 以及 propertyValBegCol
          如果不是需要去下层标签获取begRow和begCol
        */
        String propertyValBegRow = ((Element) propertiesNode).getAttribute("propertyValBegRow");
        String propertyValBegCol = ((Element) propertiesNode).getAttribute("propertyValBegCol");
        List<Integer[]> propertyValBegRowAndCols = new ArrayList<>();
        if(StringUtils.isNotBlank(propertyValBegRow)  && StringUtils.isNotBlank(propertyValBegCol)) {
            propertyValBegRowAndCols.add(new Integer[]{Integer.valueOf(propertyValBegRow),Integer.valueOf(propertyValBegCol)});
            eia.setPropertyValBegRowAndCols(propertyValBegRowAndCols);
        }

        //解析该标签，先获取其所有子标签
        NodeList nodes = propertiesNode.getChildNodes();
        //子标签的个数
        int nodeLen = nodes.getLength();
        //存放属性名称字段
        List<String> properties = null;
        //存放属性默认值
        List<String> propertyDefaultValue = null;
        //遍历子标签（一个标签是一个结点）
        for (int index = 0; index < nodeLen; index++) {
            Node node = nodes.item(index);
            if(!(node instanceof Element)){
                continue;
            }
            Element ele = ((Element)node);
            //获取属性name的值
            String name = ele.getAttribute("name");
            if(!StringUtils.isBlank(name)){
                if(properties == null){
                    properties = new ArrayList<>();
                }
                //将属性name的值存入集合中
                properties.add(name);
            }

            //获取defaultValue的值
            String defaultValue = ele.getAttribute("DefaultValue");
            if(!StringUtils.isBlank(defaultValue)){
                if(propertyDefaultValue == null){
                    propertyDefaultValue = new ArrayList<String>();
                }
                //将属性defaultValue的值存入集合中
                propertyDefaultValue.add(defaultValue);
            }

            //获取begRow和begCol的属性值
            String begRow = ele.getAttribute("begRow");
            String begCol = ele.getAttribute("begCol");
            if(!StringUtils.isBlank(begCol) && !StringUtils.isBlank(begRow)){
                //将begRow和begCol的值存入到集合中
                propertyValBegRowAndCols.add(new Integer[]{Integer.valueOf(begRow),Integer.valueOf(begCol)});
                eia.setPropertyValBegRowAndCols(propertyValBegRowAndCols);
            }

        }

        eia.setProperties(properties);
        eia.setPropertyDefaultValue(propertyDefaultValue);

        return null;
    }

    /**
     * 解析contents标签 每个content 对应一个 ExcelAssistantBase对象
     * @param node content node 节点
     * @param eeaList 保存解析 content 解析结果
     * @return 解析后直接返回
     */
    private String parseContents(Node node, List<ExcelAssistantBase> eeaList){
        //1、判断传参是否为空
        if(eeaList == null){
            return "Parameter error,eeaList is null!";
        }
        //2、获取所有子结点
        NodeList children = node.getChildNodes();
        int size = children.getLength();

        //3、遍历处理
        for (int index = 0; index < size; index++) {
           Node child = children.item(index);
           if(!(child instanceof Element)){
               continue;
           }
           //判断contents标签的子结点是否是content类型，是则解析并存入到集合中，否则报错
           try{
               if("content".equals(child.getNodeName())) {
                   ExcelImportAssistant eea = this.parseContentTag(child);
                   //将解析后的content标签，存入到result中
                   eeaList.add(eea);
               }else{
                   return "<contents> tag can only has <content> subNodes!";
               }
           } catch (Exception e) {
               logger.error("Exception Message : {}", e.getMessage());
               logger.error("Exception toString abd track space : {} ", "\r\n" + e);
               logger.error(ExcelImportService.errorTraceSpace(e));
               return e.getMessage();
           }
        }
        return null;
    }

    /**
     * 解析content标签（contents标签中的子标签）
     * @param contentNode content的节点
     * @return 解析content的 ExcelImportAssistant列表
     * @throws Exception 解析content时出现错误 抛出异常
     */
    private ExcelImportAssistant parseContentTag(Node contentNode) throws Exception{
        //1、获取content标签的id，判断是否为空，为空则抛出异常
        String contentId = ((Element)contentNode).getAttribute("id");
        if(StringUtils.isBlank(contentId)){
            throw new Exception("the <content> tag does not has id property!");
        }

        /* 解析当前content标签，初始化ExcelImportAssistant，每个content对应一个ExcelImportAssistant */


        //1. 首先解析headerNames上层标签 将每一个content标签中的header和property标签共性的部分解析
        String headerRowNo = ((Element)contentNode).getAttribute("headerRowNo");
        int curNodeHeaderRowNo = Integer.parseInt(headerRowNo);
        ExcelImportAssistant curEia = new ExcelImportAssistant(curNodeHeaderRowNo);
        curEia.setHeaderRowNo(curNodeHeaderRowNo);
        // 解析是否忽略掉该content 通过ignoreOrNot标签判断
        String ignoreOrNot = ((Element) contentNode).getAttribute("ignoreOrNot");
        if(StringUtils.isBlank(ignoreOrNot)) {
            curEia.setIgnoreOrNot(false);
        }else {
            curEia.setIgnoreOrNot(Boolean.parseBoolean(ignoreOrNot));
        }

        // 解析是否该content是否存在合并单元格情况
        String mergeCellOrNot = ((Element) contentNode).getAttribute("mergeCellOrNot");
        if(StringUtils.isBlank(mergeCellOrNot)) {
            curEia.setMergeCellOrNot(false);
        } else {
            curEia.setMergeCellOrNot(Boolean.parseBoolean(mergeCellOrNot));
        }

        String headType = ((Element)contentNode).getAttribute("headType");
        curEia.setHeadType(Integer.valueOf(headType));

        // 通用Headers的配置 通过解析headerNames标签进行获得
        String headerPropertyRowType = ((Element)contentNode).getAttribute("headerPropertyRowType");
        curEia.setHeaderPropertyRowType(Integer.valueOf(headerPropertyRowType));
        String strClass = ((Element)contentNode).getAttribute("class");
        curEia.reset();

        //2、判断属性class不为空的情况下，实例化该类对象
        if(!StringUtils.isBlank(strClass)){
            try {
                curEia.setClz(Class.forName(strClass).newInstance().getClass());
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                logger.error("Exception Message : {}", e.getMessage());
                logger.error("Exception toString abd track space : {} ", "\r\n" + e);
                logger.error(ExcelImportService.errorTraceSpace(e));
            }
        }

        //3、获取该标签下的所有子结点 包含 headerNames 和 properties标签
        NodeList children = contentNode.getChildNodes();
        int childLen = children.getLength();
        String errMsg = null;
        //4、遍历子结点，并进行处理
        for (int index = 0; index < childLen; index++) {
            //获取子结点
            Node child = children.item(index);
            if(!(child instanceof Element)){
                continue;
            }
            String tagName = child.getNodeName();
            if("headerNames".equals(tagName)){
                errMsg = parseHeaderNames(child, curEia);
            }else if("properties".equals(tagName)){
                errMsg = parseProperties(child, curEia);
            }
            if(!StringUtils.isBlank(errMsg)){
                throw new Exception(errMsg);
            }
        }
        return curEia;
    }


}
