package com.thinkgem.jeesite.modules.excelimport.base;

import java.util.List;

/**
 * 导出功能辅助类的基类
 * @author Lemon-song
 */
public abstract class ExcelAssistantBase {
	/** 是否忽略读取 */
	protected boolean ignoreOrNot = false;

	/** 是否存在单元格跨列即合并单元格情况 */
	protected boolean mergeCellOrNot = false;

	/** excel头类型，COMMON,CONTENT中的一个 */
	protected Integer headType;

	/** 要预留的空行数量,从第headerRowNo+1行开始写入数据 */
	protected Integer headerRowNo = 0;

	/**序号 */
	protected String propertiesNo;

	/** 列名称和列名称对应的属性值是否在同一行 */
	protected Integer headerPropertyRowType;

	/**当前headerType为COMMON的含义,可以为PROPERTY_TYPE_CNT等,若为null则表示header为普通标题，无特殊处理 */
	protected int[] headerMeaning;

	/**<headerName name="表头名称" columSpan="表头占用的列数"
	 * begRow="开始的行数" begCol="开始的列数">  20代表本行，21代表下一行
	 * header列占据的列数，即需合并后的列数
	 * */
	private List<Integer[]> headerRowAndColumnSpan;

	/** 列名称*/
	protected List<String> headerNames;

	/** 若为null则表示，表头的值默认从第0行第0 列开始。*/
	protected List<Integer[]>headerNameBegRowAndCols;

	/** <property name="属性名"  defaultValue="默认值"
	 * begRow="开始的行数"  begCol="开始的列数">
	 * 对应的属性字段名称，支持级联例如，container.code,如果 headType为COMMON，
	 * 代表将Content中properties的值设置为与COMMON中properties相同属性字段的值
	 * */
	protected List<String> properties;

	/** 对应的属性字段的属性值所在的行和列。*/
	protected List<Integer[]>propertyValBegRowAndCols;

	/**
	 * 对应的属性字段的默认属性值
	 */
	protected List<String> propertyDefaultValue;

	//protected Class<?>[]propertyValTypes;
	//protected List<Object> dataList;//存放本配置读取到的数据列表

	public abstract void reset();
	public void refresh() {}

	public boolean getIgnoreOrNot() {
		return ignoreOrNot;
	}
	public void setIgnoreOrNot(boolean ignoreOrNot) {
		this.ignoreOrNot = ignoreOrNot;
	}

	public boolean getMergeCellOrNot() {
		return mergeCellOrNot;
	}

	public void setMergeCellOrNot(boolean mergeCellOrNot) {
		this.mergeCellOrNot = mergeCellOrNot;
	}

	public Integer getHeadType() {
		return headType;
	}
	public void setHeadType(Integer headType) {
		this.headType = headType;
	}

	public Integer getHeaderRowNo(){return headerRowNo;};
	public void setHeaderRowNo(Integer headerRowNo){
		this.headerRowNo = headerRowNo;
	};

	public String getPropertiesNo() {
		return propertiesNo;
	}
	public void setPropertiesNo(String propertiesNo) {
		this.propertiesNo = propertiesNo;
	}

	public Integer getHeaderPropertyRowType() {
		return headerPropertyRowType;
	}
	public void setHeaderPropertyRowType(Integer headerPropertyRowType) {
		this.headerPropertyRowType = headerPropertyRowType;
	}
	public int[] getHeaderMeaning() {
		return headerMeaning;
	}
	public void setHeaderMeaning(int[] headerMeaning) {
		this.headerMeaning = headerMeaning;
	}

	public List<String> getHeaderNames() {
		return headerNames;
	}
	public void setHeaderNames(List<String> headerNames) {
		this.headerNames = headerNames;
	}
	public List<Integer[]> getHeaderRowAndColumnSpan() {
		return headerRowAndColumnSpan;
	}
	public void setHeaderRowAndColumnSpan(List<Integer[]> headerRowAndColumnSpan) {
		this.headerRowAndColumnSpan = headerRowAndColumnSpan;
	}
	public List<Integer[]> getHeaderNameBegRowAndCols() {
		return headerNameBegRowAndCols;
	}
	public void setHeaderNameBegRowAndCols(List<Integer[]> begRowAndColumns) {
		this.headerNameBegRowAndCols = begRowAndColumns;
	}

	public List<String> getProperties() {
		return properties;
	}
	public void setProperties(List<String> properties) {
		this.properties = properties;
	}
	public List<String> getPropertyDefaultValue() {
		return propertyDefaultValue;
	}
	public void setPropertyDefaultValue(List<String> propertyDefaultValue) {
		this.propertyDefaultValue = propertyDefaultValue;
	}
	public List<Integer[]> getPropertyValBegRowAndCols() {
		return propertyValBegRowAndCols;
	}
	public void setPropertyValBegRowAndCols(List<Integer[]> propertnValBegRowAndCols) {
		this.propertyValBegRowAndCols = propertnValBegRowAndCols;
	}


	public  List<Object> getDataList(){
		return null;
	}
	public void setDataList(List<Object> dataList) {
	}
	/**
	 * 释放内部类dataForThread中dataList占用的空间
	 */

	public Class<?> getClz(){return null;};
	public void setClz(Class<?> clz){};

	/**
	 * 返回ExcelAssistantBase配置中的最大列号，从列号1开始
	 * @param eab，当前ExcelAssistantBase配置对象
	 * @return 此配置的最大列号
	 */
	public int getLongestCol(ExcelAssistantBase eab){
		int maxLen = 0;
		int propertiesLen = 0;
		int headerNamesLen = 0;
		//获取有多少个属性值
		if(eab.getProperties() != null) {
			propertiesLen = eab.getProperties().size();
			if(maxLen < propertiesLen){
				maxLen = propertiesLen;
			}
		}
		//获取行和列的值
		List<Integer[]> rowCols = eab.getHeaderNameBegRowAndCols();
		if(rowCols != null) {
            for (Integer[] rowCol : rowCols) {
                if (maxLen < rowCol[1] + 1) {
                    maxLen = rowCol[1] + 1;
                }
            }
        }
		if(eab.getHeaderNames() != null) {
			headerNamesLen = eab.getHeaderNames().size();
			if(maxLen < headerNamesLen){
				maxLen = headerNamesLen;
			}
		}
		rowCols = eab.getPropertyValBegRowAndCols();
		if(rowCols != null) {
            for (Integer[] rowCol : rowCols) {
                if (maxLen < rowCol[1] + 1) {
                    maxLen = rowCol[1] + 1;
                }
            }
        }
		return maxLen;
	}

	/**
	 * 获得配置中最大的列号，从0开始计算
	 * @param eabs，配置列表
	 * @return 配置中最大的列号
	 */
	public static int getLongestCols(List<ExcelAssistantBase> eabs) {
		int maxLen = 0;
		int tmp = 0;
		for(ExcelAssistantBase eab : eabs) {
			tmp = eab.getLongestCol(eab);
			maxLen = maxLen > tmp ? maxLen : tmp;
		}
		return maxLen;
	}
}
