package com.thinkgem.jeesite.modules.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 利用JAXB封装了XML转换成object，object转换成XML的代码 利用javax.xml.transform将XML转化为String
 * 从网上找的现成的
 * 
 * @author songlc
 * 
 */
public class XMLUtils {
	private static DocumentBuilderFactory factory;
	private static DocumentBuilder domBuilder;
	
	static Map<String, JAXBContext> jaxbContextMap = new HashMap<String, JAXBContext>();
	
	/**
	 * 将对象直接转换成String类型的 XML输出
	 * 
	 * @param obj
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static String convertToXml(Object obj) {
		// 创建输出流
		StringWriter sw = new StringWriter();
		try {
			// 利用jdk中自带的转换类实现
//			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			JAXBContext jaxbContext = jaxbContextMap.get(obj.getClass().getName());
            if(jaxbContext == null){
                // 如果每次都调用JAXBContext.newInstance方法，会导致性能急剧下降
                jaxbContext = JAXBContext.newInstance(obj.getClass());
                jaxbContextMap.put(obj.getClass().getName(), jaxbContext);
            }
			Marshaller marshaller = jaxbContext.createMarshaller();
			// 格式化xml输出的格式
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			// 避免中文乱码
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
			// 将对象转换成输出流形式的xml
			marshaller.marshal(obj, sw);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}
	
	/**
	 * 将对象直接转换成String类型的 XML输出
	 * 
	 * @param objs
	 * @return
	 */
	public static String convertListToXml(List<?> objs) {
		// 创建输出流
		StringWriter sw = new StringWriter();
		try {
			for(Object obj : objs){
				// 利用jdk中自带的转换类实现
				JAXBContext context = JAXBContext.newInstance(obj.getClass());

				Marshaller marshaller = context.createMarshaller();
				// 格式化xml输出的格式
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				// 将对象转换成输出流形式的xml
				marshaller.marshal(obj, sw);
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}

	/**
	 * 将对象根据路径转换成xml文件
	 * 
	 * @param obj
	 * @param path
	 * @return
	 */
	public static void convertToXml(Object obj, String path) {
		try {
			// 利用jdk中自带的转换类实现
			JAXBContext context = JAXBContext.newInstance(obj.getClass());

			Marshaller marshaller = context.createMarshaller();
			// 格式化xml输出的格式
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			// 将对象转换成输出流形式的xml
			// 创建输出流
			FileWriter fw = null;
			try {
				fw = new FileWriter(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			marshaller.marshal(obj, fw);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes" })
	/**
	 * 将String类型的xml转换成对象
	 */
	public static Object convertXmlStrToObject(Class clazz, String xmlStr) {
		Object xmlObject = null;
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			// 进行将Xml转成对象的核心接口
			Unmarshaller unmarshaller = context.createUnmarshaller();
			StringReader sr = new StringReader(xmlStr);
			xmlObject = unmarshaller.unmarshal(sr);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return xmlObject;
	}


	/**
	 * 将file类型的xml转换成对象
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Object convertXmlFileToObject(Class clazz, String xmlPath) {
		Object xmlObject = null;
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			FileReader fr = null;
			try {
				fr = new FileReader(xmlPath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			xmlObject = unmarshaller.unmarshal(fr);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return xmlObject;
	}
	
	public static String xmlToString(String xmlFilePath) {
		String rsltStr = null;
		File xmlFile = new File(xmlFilePath);
		try {
			Document document = getDomBulider().parse(xmlFile);
			rsltStr = xmlToString(document);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rsltStr;
	}
	/**
	 * 以下为XML和字符串之间的转化
	 */

	/**
	 * 
	 * @param document Document对象（读xml生成的）
	 * @return String字符串
	 * @throws Throwable
	 */
	public static String xmlToString(Document document) throws Throwable {
		TransformerFactory ft = TransformerFactory.newInstance();
		Transformer ff = ft.newTransformer();
		ff.setOutputProperty("encoding", "UTF-8");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ff.transform(new DOMSource(document), new StreamResult(bos));
		return bos.toString();
	}

	/**
	 * 
	 * @param str xml形状的str串
	 * @return Document 对象
	 */
	public Document StringTOXml(String str) {

		StringBuilder sXML = new StringBuilder();
		sXML.append(str);
		Document doc = null;
		try {
			InputStream is = new ByteArrayInputStream(sXML.toString().getBytes("utf-8"));
			doc = getDomBulider().parse(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}


	public static Document getDocument(String filePath) throws Exception{
		try {
			File file = new File(filePath);
			if(!file.exists()){
				throw new Exception("文件不存在，请检查路径：" + filePath);
			}
			if(!file.isFile()){
				throw new Exception("此路径不包含文件:" + filePath);
			}
			return getDomBulider().parse(filePath);
		} catch (SAXException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 
	 * @param document
	 * @return 某个节点的值 前提是需要知道xml格式，知道需要取的节点相对根节点所在位置
	 */
	public String getNodeValue(Document document, String nodePaht) {
		XPathFactory xpfactory = XPathFactory.newInstance();
		XPath path = xpfactory.newXPath();
		String servInitrBrch = "";
		try {
			servInitrBrch = path.evaluate(nodePaht, document);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return servInitrBrch;
	}

	/**
	 * 
	 * @param document
	 * @param nodePath
	 *            需要修改的节点相对根节点所在位置
	 * @param vodeValue
	 *            替换的值
	 */
	public void setNodeValue(Document document, String nodePath, String vodeValue) {
		XPathFactory xpfactory = XPathFactory.newInstance();
		XPath path = xpfactory.newXPath();
		Node node = null;
		;
		try {
			node = (Node) path.evaluate(nodePath, document, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		node.setTextContent(vodeValue);
	}
	
	private synchronized static DocumentBuilderFactory getDomBuilderFactory() {
		
		if(factory == null) {
			factory = DocumentBuilderFactory.newInstance();
		}
		return factory;
	}
	
	private synchronized static DocumentBuilder getDomBulider() {
		if(domBuilder == null) {
			try {
				domBuilder = getDomBuilderFactory().newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return domBuilder;
	}

	/**
	 * 查找并删除以<DataSet>或<DataTable>、以<(/)xs:为开头标签、<diffgr:diffgram>标签的内容
	 * @param xmlStr xml对应的String
	 */
	public static String removeUnusedTags(String xmlStr) {
		if(xmlStr == null || xmlStr.length() == 0) {
			return xmlStr;
		}
		//
		String regex = "<\\/?DataSet.*?>|<\\/?DataTable.*?>|<\\/?xs:.*?>|<\\/?diffgr:diffgram.*?>|diffgr:id=\".*?\"|msdata:rowOrder=\".*?\"";
		//regex = "<\\/?DataTable.*?>|<\\/?xs:.*?>|<\\/?diffgr:diffgram.*?>";

		return replaceAllByRegex(regex, "", xmlStr);
	}
	
	/**
	 *根据文件名将不同的模板中xml的root标签同一更换成系统内部管理的标签，便于利用JAXB转化为object
	 * @param xmlStr xml字符串表示
	 * @param fileName 文件名
	 * @return
	 */
	public static String replaceXmlRootElementName(String xmlStr,String fileName) {
		if(xmlStr == null || xmlStr.length() == 0 || fileName == null || fileName.length() == 0) {
			return xmlStr;
		}
		String regex = "_UniqueDataTableName_.*?>";
		//根据文件名区分模板文件
		if(fileName.contains("HEAD")) {
			return replaceAllByRegex(regex, "SupportHead_Item>", xmlStr);
		}else if(fileName.contains("ELEM")) {
			return replaceAllByRegex(regex, "FuelEle_Item>", xmlStr);
		}else if(fileName.contains("BODY")) {
			return replaceAllByRegex(regex, "SupportBody_Item>", xmlStr);
		}else {
			return replaceAllByRegex(regex, "Container_Item>", xmlStr);
		}
	}
	
	public static String replaceAllByRegex(String regex,String replaceStr,String originStr) {
		return originStr.replaceAll(regex, replaceStr);
	}	
	
	public static void main(String[] args) throws FileNotFoundException {
		
		/*String XmlStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + 
				"    <NewDataSet>\r\n" + 
				"      <_UniqueDataTableName_22>\r\n" + 
				"        <FB_IFUSED>0</FB_IFUSED>\r\n" + 
				"        <FB_FB_ID>bb8248a8-2ecd-46e4-8bed-4494711b7191</FB_FB_ID>\r\n" + 
				"        <FB_FH_ID>a68598f3-ef0c-4923-8c1e-28a53f2e53fb</FB_FH_ID>\r\n" + 
				"        <FB_MTCODE>122FN-00.7116_____0_</FB_MTCODE>\r\n" + 
				"        <FB_OBJECT>N</FB_OBJECT>\r\n" + 
				"        <FB_CHANGECODE>SD</FB_CHANGECODE>\r\n" + 
				"        <FB_CHANGEFASHION>-1</FB_CHANGEFASHION>\r\n" + 
				"        <FB_BATCHID>6</FB_BATCHID>\r\n" + 
				"        <FB_PIECENUM>2180</FB_PIECENUM>\r\n" + 
				"        <FB_APIECENUM>0</FB_APIECENUM>\r\n" + 
				"        <FB_KMPCODE />\r\n" + 
				"        <FB_MEADATE>2016-08-09T14:46:55.5+08:00</FB_MEADATE>\r\n" + 
				"        <FB_GROSSW>2240</FB_GROSSW>\r\n" + 
				"        <FB_TAREW>60</FB_TAREW>\r\n" + 
				"        <FB_NETW>2180</FB_NETW>\r\n" + 
				"        <FB_AGROSSW>2240</FB_AGROSSW>\r\n" + 
				"        <FB_ANETW>2180</FB_ANETW>\r\n" + 
				"        <FB_UNIT0>1000000</FB_UNIT0>\r\n" + 
				"        <FB_UNIT1>1000000</FB_UNIT1>\r\n" + 
				"        <FB_CIRCLE>0</FB_CIRCLE>\r\n" + 
				"        <FB_AVERAGE>0</FB_AVERAGE>\r\n" + 
				"        <FB_REMARK />\r\n" + 
				"        <FB_CONCODE>TEST0006</FB_CONCODE>\r\n" + 
				"        <FB_CO_ID>6755e27b-fadf-4566-b972-daecc63fb189</FB_CO_ID>\r\n" + 
				"        <FB_PO_ID>-1</FB_PO_ID>\r\n" + 
				"        <MT_GENERALNAME>122YF</MT_GENERALNAME>\r\n" + 
				"        <MT_INTERNAME>石墨球</MT_INTERNAME>\r\n" + 
				"        <MT_UNIT>质量</MT_UNIT>\r\n" + 
				"      </_UniqueDataTableName_22>" +
				"    </NewDataSet>";*/
		//XmlToObjListWrapper<?> xmlToObjListWrapper = new XmlToObjListWrapper<SupportBody>();
		/*SupportBody b1 = new SupportBody();
		b1.setBatch("6");
		SupportBody b2 = new SupportBody();
		b2.setBatch("7");
		List<SupportBody> bSupportBodies = Lists.newArrayList();
		bSupportBodies.add(b1);
		bSupportBodies.add(b2);
		
		xmlToObjListWrapper.setObjList(bSupportBodies);
		convertToXml(xmlToObjListWrapper,"C:\\Users\\JSJS-Songlc\\Desktop\\test.xml");*/
		//String string = FileUtils.readFileToString("E:\\0-Leamon_Dev\\0-Projects\\0-项目\\核材料管控\\303-核材料智能管控系统\\单据模板\\测试\\BODY - 副本.xml");
		//System.out.println(string);
		//string = removeUnusedTags(string);
		//System.out.println("\n\n\n\n");
		//System.out.println(string);
		//List rsltList = convertXmlFileToObjList(XmlToObjListWrapper.class, "C:\\Users\\JSJS-Songlc\\Desktop\\BODY.xml");
		//XmlToObjListWrapper<SupportBody> test = new XmlToObjListWrapper<SupportBody>();
		//List<SupportBody> list  =  (List<SupportBody>)(((XmlToObjListWrapper<SupportBody>)XMLUtils.convertXmlStrToObject(test.getClass(), XmlStr)).getObjList());
		return ;
	}
	

}
