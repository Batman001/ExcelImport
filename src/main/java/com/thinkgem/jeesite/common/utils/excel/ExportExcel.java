/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.utils.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.utils.Encodes;
import com.thinkgem.jeesite.common.utils.Reflections;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 导出Excel文件（导出“XLSX”格式，支持大数据量导出   @see org.apache.poi.ss.SpreadsheetVersion）
 * @author ThinkGem
 * @version 2013-04-21
 */
public class ExportExcel {
	
	protected static Logger log = LoggerFactory.getLogger(ExportExcel.class);
			
	/**
	 * 工作薄对象
	 */
	private SXSSFWorkbook wb;
	
	/**
	 * 工作表对象
	 */
	private Sheet sheet;
	
	/**
	 * 样式列表
	 */
	protected Map<String, CellStyle> styles;
	
	/**
	 * 当前行号
	 */
	private int rownum;
	
	/**
	 * 注解列表（Object[]{ ExcelField, Field/Method }）
	 */
	List<Object[]> annotationList = Lists.newArrayList();

	/**
	 * 构造函数
	 * @param title 表格标题
	 */
	public ExportExcel(String title) {
		initialize(title);
	}

	private void initialize(String title) {
		this.wb = new SXSSFWorkbook(500);
		this.sheet = wb.createSheet("Export");
		this.styles = createStyles(wb);
		// Create title
		if (StringUtils.isNotBlank(title)) {
			Row titleRow = sheet.createRow(rownum++);
			titleRow.setHeightInPoints(30);
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellStyle(styles.get("title"));
			titleCell.setCellValue(title);
		}
	}

	/**
	 * 构造函数
	 * @param title 表格标题，传“空值”，表示无标题
	 * @param cls 实体对象，通过annotation.ExportField获取标题
	 */
	public ExportExcel(String title, Class<?> cls){
		this(title, cls, 1);
	}

	/**
	 * 构造函数
	 * @param title 表格标题，传“空值”，表示无标题
	 * @param cls 实体对象，通过annotation.ExportField获取标题
	 * @param type 导出类型（1:导出数据；2：导出模板）
	 * @param groups 导入分组
	 */
	public ExportExcel(String title, Class<?> cls, int type, int... groups){
		// Get annotation field
		Field[] fs = cls.getDeclaredFields();
		for (Field f : fs){
			ExcelField ef = f.getAnnotation(ExcelField.class);
			if (ef != null && (ef.type()==0 || ef.type()==type)){
				if (groups!=null && groups.length>0){
					boolean inGroup = false;
					for (int g : groups){
						if (inGroup){
							break;
						}
						for (int efg : ef.groups()){
							if (g == efg){
								inGroup = true;
								annotationList.add(new Object[]{ef, f});
								break;
							}
						}
					}
				}else{
					annotationList.add(new Object[]{ef, f});
				}
			}
		}
		// Get annotation method
		Method[] ms = cls.getDeclaredMethods();
		for (Method m : ms){
			ExcelField ef = m.getAnnotation(ExcelField.class);
			if (ef != null && (ef.type()==0 || ef.type()==type)){
				if (groups!=null && groups.length>0){
					boolean inGroup = false;
					for (int g : groups){
						if (inGroup){
							break;
						}
						for (int efg : ef.groups()){
							if (g == efg){
								inGroup = true;
								annotationList.add(new Object[]{ef, m});
								break;
							}
						}
					}
				}else{
					annotationList.add(new Object[]{ef, m});
				}
			}
		}
		// Field sorting
		Collections.sort(annotationList, new Comparator<Object[]>() {
			public int compare(Object[] o1, Object[] o2) {
				return new Integer(((ExcelField)o1[0]).sort()).compareTo(
						new Integer(((ExcelField)o2[0]).sort()));
			};
		});
		// Initialize
		List<String> headerList = Lists.newArrayList();
		for (Object[] os : annotationList){
			String t = ((ExcelField)os[0]).title();
			// 如果是导出，则去掉注释
			if (type==1){
				String[] ss = StringUtils.split(t, "**", 2);
				if (ss.length==2){
					t = ss[0];
				}
			}
			headerList.add(t);
		}
		initialize(title, headerList);
	}

	/**
	 * 构造函数
	 * @param title 表格标题，传“空值”，表示无标题
	 * @param headers 表头数组
	 */
	public ExportExcel(String title, String[] headers) {
		initialize(title, Lists.newArrayList(headers));
	}

	/**
	 * 构造函数
	 * @param title 表格标题，传“空值”，表示无标题
	 * @param headerList 表头列表
	 */
	public ExportExcel(String title, List<String> headerList) {
		initialize(title, headerList);
	}

	/**
	 * 构造函数
	 *
	 * @param title
	 *            表格标题，传“空值”，表示无标题
	 * @param headerList
	 *            标题 list
	 * @param colList
	 *            标题的字段属性 list
	 * @param list
	 *            数据 list
	 */
	public <E> ExportExcel(String title, List<String> headerList, List<String> colList, List<E> list) {
		initialize(title, headerList);
		JsonMapper objectMapper = JsonMapper.getInstance();
		try {
			String jsonString = objectMapper.writeValueAsString(list);
			JsonNode array = objectMapper.readTree(jsonString);
			Iterator<JsonNode> ite = array.iterator();
			while (ite.hasNext()) {
				Row row = this.addRow();
				JsonNode node = (JsonNode) ite.next();
				int colunm = 0;
				for (String s : colList) {
					// 对于例如 user.name 多层级字段的处理
					String[] field = s.split("\\.");
					String result = "";
					JsonNode nodetmp = node;
					for (int i = 0; i < field.length; i++) {
						if (nodetmp != null) {
							nodetmp = nodetmp.get(field[i]);
						}
					}
					if (nodetmp != null) {
						result = nodetmp.asText();
					}
					this.addCell(row, colunm++, result);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ExportExcel(String title, int megergedRegionsize) {
		this.initialize(title, megergedRegionsize);
	}

	//初始化
	private void  initialize(String title,int megergedRegionsize) {
		this.wb = new SXSSFWorkbook(500);
		this.sheet = wb.createSheet("Export");
		this.styles = createStyles(wb);
		// Create title
		if (StringUtils.isNotBlank(title)){
			Row titleRow = sheet.createRow(rownum++);
			titleRow.setHeightInPoints(30);
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellStyle(styles.get("title"));
			titleCell.setCellValue(title);
			sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),
					titleRow.getRowNum(), titleRow.getRowNum(),megergedRegionsize - 1));
		}
	}

	/**
	 * 初始化函数
	 * @param title 表格标题，传“空值”，表示无标题
	 * @param headerList 表头列表
	 */
	private void initialize(String title, List<String> headerList) {
		if (headerList == null){
			throw new RuntimeException("headerList must not null!");
		}
		this.wb = new SXSSFWorkbook(500);
		this.sheet = wb.createSheet("Export");
		this.styles = createStyles(wb);
		// Create title
		if (StringUtils.isNotBlank(title)){
			Row titleRow = sheet.createRow(rownum++);
			titleRow.setHeightInPoints(30);
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellStyle(styles.get("title"));
			titleCell.setCellValue(title);
			sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),
					titleRow.getRowNum(), titleRow.getRowNum(), headerList.size()-1));
		}

		// Create header
		Row headerRow = sheet.createRow(rownum++);
		headerRow.setHeightInPoints(16);
		for (int i = 0; i < headerList.size(); i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellStyle(styles.get("header"));
			String[] ss = StringUtils.split(headerList.get(i), "**", 2);
			if (ss.length==2){
				cell.setCellValue(ss[0]);
				Comment comment = this.sheet.createDrawingPatriarch().createCellComment(
						new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
				comment.setString(new XSSFRichTextString(ss[1]));
				cell.setCellComment(comment);
			}else{
				cell.setCellValue(headerList.get(i));
			}
//			sheet.autoSizeColumn(i);
		}
		for (int i = 0; i < headerList.size(); i++) {
			int colWidth = sheet.getColumnWidth(i)*2;
	        sheet.setColumnWidth(i, colWidth < 3000 ? 3000 : colWidth);
		}
		log.debug("Initialize success.");
	}

	protected int getRowNum() {
		return this.rownum;
	}

	/**
	 * 创建表格样式
	 * @param wb 工作薄对象
	 * @return 样式列表
	 */
	private Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

		CellStyle style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		Font titleFont = wb.createFont();
		titleFont.setFontName("Arial");
		titleFont.setFontHeightInPoints((short) 16);
		titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setFont(titleFont);
		styles.put("title", style);

		style = wb.createCellStyle();
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		Font dataFont = wb.createFont();
		dataFont.setFontName("Arial");
		dataFont.setFontHeightInPoints((short) 10);
		style.setFont(dataFont);
		styles.put("data", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(CellStyle.ALIGN_LEFT);
		styles.put("data1", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(CellStyle.ALIGN_CENTER);
		styles.put("data2", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		styles.put("data3", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
//		style.setWrapText(true);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Font headerFont = wb.createFont();
		headerFont.setFontName("Arial");
		headerFont.setFontHeightInPoints((short) 10);
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerFont.setColor(IndexedColors.WHITE.getIndex());
		style.setFont(headerFont);
		styles.put("header", style);

		return styles;
	}

	/**
	 * 可以公用的方法
	 * 合并单元格之后 加边框
	 * @param sheet
	 * @param cs
	 * 2017-7-24 WXD 修改
	 */
	public static void setRegionStyle(HSSFSheet sheet, CellRangeAddress region, HSSFCellStyle cs) {
		for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
			HSSFRow row = HSSFCellUtil.getRow(i, sheet);
			for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
				HSSFCell cell = HSSFCellUtil.getCell(row, (short) j);
				cell.setCellStyle(cs);
			}
		}
	}

	public Sheet getSheet(int sheetIndex) {
		if (this.wb.getNumberOfSheets()<sheetIndex){
			throw new RuntimeException("文档中没有工作表!");
		}
		return this.wb.getSheetAt(sheetIndex);
	}

	/**
	 * 添加一行
	 * @return 行对象
	 */
	public Row addRow(){
		return sheet.createRow(rownum++);
	}


	/**
	 * 添加一个单元格
	 * @param row 添加的行
	 * @param column 添加列号
	 * @param val 添加值
	 * @return 单元格对象
	 */
	public Cell addCell(Row row, int column, Object val){
		return this.addCell(row, column, val, 0, Class.class);
	}

	/**
	 * 添加一个单元格
	 * @param row 添加的行
	 * @param column 添加列号
	 * @param val 添加值
	 * @param align 对齐方式（1：靠左；2：居中；3：靠右）
	 * @return 单元格对象
	 */
	public Cell addCell(Row row, int column, Object val, int align, Class<?> fieldType){
		Cell cell = row.createCell(column);
		CellStyle style = styles.get("data"+(align>=1&&align<=3?align:""));
		try {
			if (val == null){
				cell.setCellValue("");
			} else if (val instanceof String) {
				cell.setCellValue((String) val);
			} else if (val instanceof Integer) {
				cell.setCellValue((Integer) val);
			} else if (val instanceof Long) {
				cell.setCellValue((Long) val);
			} else if (val instanceof Double) {
				cell.setCellValue((Double) val);
			} else if (val instanceof Float) {
				cell.setCellValue((Float) val);
			} else if (val instanceof Date) {
				DataFormat format = wb.createDataFormat();
	            style.setDataFormat(format.getFormat("yyyy-MM-dd"));
				cell.setCellValue((Date) val);
			} else {
				if (fieldType != Class.class){
					cell.setCellValue((String)fieldType.getMethod("setValue", Object.class).invoke(null, val));
				}else{
					cell.setCellValue((String)Class.forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(),
						"fieldtype."+val.getClass().getSimpleName()+"Type")).getMethod("setValue", Object.class).invoke(null, val));
				}
			}
		} catch (Exception ex) {
			log.info("Set cell value ["+row.getRowNum()+","+column+"] error: " + ex.toString());
			cell.setCellValue(val.toString());
		}
		cell.setCellStyle(style);
		return cell;
	}

	/**
	 * 用object对象里的属性来给某个cell赋值
	 * @param cell 要赋值的单元格
	 * @param obj 用来赋值的对象
	 * @param labels 对象中需要赋值的元素中文名称
	 * @param properties 对象中需要赋值的元素名
	 * @return
	 * @author zhaorc 2018-8-8
	 */
	public static HSSFCell fillCellWithString(HSSFCell cell, Object obj, String[] labels, String[] properties){
		String value = "";
		try {
			//若传过来的对象为空，或是properties为空，直接给此单元格置为空
			if (obj == null || properties == null || properties.length == 0){
				cell.setCellValue("");
				return cell;
			} else {
				for(int i=0; i<properties.length; i++){
					String property = properties[i];
					Object val = Reflections.invokeGetter(obj, property); //获取属性值
					//value += label+"："+val+"\n"; //拼成这种格式：“中文属性名：xxxx\n” ##:更新需求，去掉中文标示
					value += val+"\n";
				}
				value = value.substring(0, value.lastIndexOf('\n')); //去掉最后一个换行符
			}
		} catch (Exception ex) {
			log.info("Set cell value error: " + ex.toString());
			cell.setCellValue(obj.toString());
		}
		cell.setCellValue(value);
		return cell;
	}

	/**
	 * 在指定的行上添加cell，并将val对象中的Properties中的值以字符串的形式填充到cell中，每个属性占一行
	 * @param row 指定的行
	 * @param column 指定的列号，从0开始
	 * @param val 要写入的对象
	 * @param align 自定单元格样式，align 对齐方式（1：靠左；2：居中；3：靠右）
	 * @param properties val对象中要写入的属性数组
	 * @author songlc 2018-07-27
	 * @return
	 */
	public Cell addAndFillCellWithString(Row row, int column, Object val, int align, String[]properties){
		Cell cell = row.createCell(column);
		CellStyle style = styles.get("data"+(align>=1&&align<=3?align:""));
		try {
			if (val == null){
				cell.setCellValue("");
				cell.setCellStyle(style);
				return cell;
			}
			if(properties == null || properties.length == 0) {
				cell.setCellValue(val.toString());
				cell.setCellStyle(style);
				return cell;
			}else {
				for(String property :properties){
					Reflections.invokeGetter(val, property);
				}
			}
		} catch (Exception ex) {
			log.info("Set cell value ["+row.getRowNum()+","+column+"] error: " + ex.toString());
			cell.setCellValue(val.toString());
		}
		cell.setCellStyle(style);
		return cell;
	}




	/**
	 * 添加数据（通过annotation.ExportField添加数据）
	 * @return list 数据列表
	 */
	public <E> ExportExcel setDataList(List<E> list){
		DecimalFormat df=new DecimalFormat("#0.00000");
		for (E e : list){
			int colunm = 0;
			Row row = this.addRow();
			StringBuilder sb = new StringBuilder();
			for (Object[] os : annotationList){
				ExcelField ef = (ExcelField)os[0];
				Object val = null;
				// Get entity value
				try{
					if (StringUtils.isNotBlank(ef.value())){
						val = Reflections.invokeGetter(e, ef.value());
						if(val.getClass()==double.class||val.getClass()==Double.class){
							val=df.format(val);
						}else  if(val.getClass()==int.class||val.getClass()==Integer.class){
							val=val.toString();
						}
					}else{
						if (os[1] instanceof Field){
							val = Reflections.invokeGetter(e, ((Field)os[1]).getName());
						}else if (os[1] instanceof Method){
							val = Reflections.invokeMethod(e, ((Method)os[1]).getName(), new Class[] {}, new Object[] {});
						}
					}
					// If is dict, get dict label
					if (StringUtils.isNotBlank(ef.dictType())){
						val = DictUtils.getDictLabel(val==null?"":val.toString(), ef.dictType(), "");
					}
				}catch(Exception ex) {
					// Failure to ignore
					log.info(ex.toString());
					val = "";
				}
				this.addCell(row, colunm++, val, ef.align(), ef.fieldType());
				sb.append(val + ", ");
			}
			log.debug("Write success: ["+row.getRowNum()+"] "+sb.toString());
		}
		return this;
	}

	/**
	 * 添加数据
	 * @return ExportExcel
	 */
	public ExportExcel setDataList(List<String> fieldList, List<?> list)
	{
			for (Object o : list) {
			int col = 0;
			Row row = this.addRow();
			for (String field : fieldList) {
				Object val;
				try {
					val = Reflections.invokeGetter(o, field);
				} catch (Exception e) {
					val = null;
				}
				this.addCell(row, col++, val);
			}
		}
		return this;
	}

	/**
	 * 输出数据流
	 * @param os 输出数据流
	 */
	public ExportExcel write(OutputStream os) throws IOException{
		wb.write(os);
		return this;
	}

	/**
	 * 输出到客户端
	 * @param fileName 输出文件名
	 */
	public ExportExcel write(HttpServletResponse response, String fileName) throws IOException{
		response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename="+ Encodes.urlEncode(fileName));
		write(response.getOutputStream());
		return this;
	}

	public ExportExcel write(HttpServletRequest request, HttpServletResponse response, String fileName, boolean crossOriginOrNot) throws IOException{
		response.reset();
        response.setContentType("application/octet-stream; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename="+ Encodes.urlEncode(fileName));
        if(crossOriginOrNot) {
			response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
			response.setHeader("Access-Control-Allow-Methods", "*");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
			response.setHeader("Access-Control-Allow-Credentials", "true");
        }
		write(response.getOutputStream());
		return this;
	}

	/**
	 * 输出到文件
	 * @param fileName 输出文件名
	 */
	public ExportExcel writeFile(String name) throws FileNotFoundException, IOException{
		FileOutputStream os = new FileOutputStream(name);
		this.write(os);
		return this;
	}


	/**
	 * 将指定行的数据从fromSheet拷贝到toSheet中
	 * @param fromSheet 起始sheet
	 * @param toSheet 目的sheet
	 * @param rowNos 指定的行号
	 * @param columns 列数
	 * @return true or false
	 */
	public static boolean copyExcelByRowNum(Sheet fromSheet, Sheet toSheet, int toSheetBegRow, List<Integer> rowNos, int columns, Map<Integer,String> errorMsgMap, int begRowNo) {
		if(fromSheet == null || toSheet == null || rowNos == null || columns == 0) {
			return false;
		}
		for (int rowNo : rowNos) {
			Row fromRow = fromSheet.getRow(rowNo + begRowNo - 1);
			if (fromRow == null) {
				continue;
			}
			Object val = null;
			Row destRow = toSheet.createRow(toSheetBegRow++);
			for (int index = 0; index <= columns; index++) {

				//新增单元格填入错误数据的信息
				if (index == columns) {
					Cell destCell = destRow.createCell(index);
					destCell.setCellType(Cell.CELL_TYPE_STRING);
					destCell.setCellValue("第" + rowNo + "行," + errorMsgMap.get(rowNo));
				}

				Cell cell = fromRow.getCell(index);
				if (cell != null) {
					Cell destCell = destRow.createCell(index);
					if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						val = cell.getNumericCellValue();
						destCell.setCellValue((Double) val);
					} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
						val = cell.getStringCellValue();
						destCell.setCellValue((String) val);
					} else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
						String value = null;
						try {
							value = String.valueOf(cell.getNumericCellValue());
							destCell.setCellValue(value);
						} catch (IllegalStateException e) {
							value = String.valueOf(cell.getRichStringCellValue());
							destCell.setCellValue(value);
						}
					} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
						val = cell.getBooleanCellValue();
						destCell.setCellValue((Boolean) val);
					} else if (cell.getCellType() == Cell.CELL_TYPE_ERROR) {
						val = cell.getErrorCellValue();
						destCell.setCellValue((Byte) val);
					}
				}
			}
		}
		return true;
	}

	/**
	 * 设置excel的打印样式，横向打印，上下左右边距拉小，传入一个HSSFSheet和其HSSFPrintSetup
	 * （按照A3纸的大小设置的排版，打到A4纸上也可以适应布局）
	 * @param print
	 * @param sheet
	 * @author zhaorc 2018-8-14
	 */
	public static void setPrintFormat(HSSFPrintSetup print, HSSFSheet sheet){
		print.setLandscape(true);//true，则表示页面方向为横向；否则为纵向
		print.setScale((short)100);//缩放比例80%(设置为0-100之间的值)
		print.setFitWidth((short)1);//设置页宽
		print.setFitHeight((short)1);//设置页高
		print.setPaperSize(HSSFPrintSetup.A3_PAPERSIZE);//纸张设置
		sheet.setAutobreaks(true);//Sheet页自适应页面大小
		sheet.setFitToPage(true);//是否自适应界面
		sheet.setMargin(HSSFSheet.TopMargin,(double) 0.2); // 上边距
		sheet.setMargin(HSSFSheet.BottomMargin,(double) 0.2); // 下边距
		sheet.setMargin(HSSFSheet.LeftMargin,(double) 0.2); // 左边距
		sheet.setMargin(HSSFSheet.RightMargin,(double) 0.2); // 右边距
	}

	/**
	 * 清理临时文件
	 */
	public ExportExcel dispose(){
		wb.dispose();
		return this;
	}

//	/**
//	 * 导出测试
//	 */
//	public static void main(String[] args) throws Throwable {
//		
//		List<String> headerList = Lists.newArrayList();
//		for (int i = 1; i <= 10; i++) {
//			headerList.add("表头"+i);
//		}
//		
//		List<String> dataRowList = Lists.newArrayList();
//		for (int i = 1; i <= headerList.size(); i++) {
//			dataRowList.add("数据"+i);
//		}
//		
//		List<List<String>> dataList = Lists.newArrayList();
//		for (int i = 1; i <=1000000; i++) {
//			dataList.add(dataRowList);
//		}
//
//		ExportExcel ee = new ExportExcel("表格标题", headerList);
//		
//		for (int i = 0; i < dataList.size(); i++) {
//			Row row = ee.addRow();
//			for (int j = 0; j < dataList.get(i).size(); j++) {
//				ee.addCell(row, j, dataList.get(i).get(j));
//			}
//		}
//		
//		ee.writeFile("target/export.xlsx");
//
//		ee.dispose();
//		
//		log.debug("Export success.");
//		
//	}

}
