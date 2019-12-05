package com.thinkgem.jeesite.modules.excelimport.base;


import java.util.List;

/**
 * @author SongLingC
 * description: Excel导入的中用于保存模板配置信息的类
 * @param <T>
 */
public class ExcelImportAssistant<T> extends ExcelAssistantBase {
	private Class<T> clz;
	protected InnerClass dataForThread;

	public ExcelImportAssistant(int headType, int headerPropertyRowType, int headerRowNo, List<String> headerNames, List<String> properties,
                                int[] headerMeaning, List<Integer[]> begRowAndColumns, List<Integer[]> propertyValBegRowAndCols) {
		this.headType = headType;
		this.headerPropertyRowType = headerPropertyRowType;
		this.headerRowNo = headerRowNo;
		this.headerNames = headerNames;
		this.properties = properties;
		this.headerMeaning = headerMeaning;
		this.headerNameBegRowAndCols = begRowAndColumns;
		this.propertyValBegRowAndCols = propertyValBegRowAndCols;
		this.dataForThread = new InnerClass();
	}

	/**
	 * 新建只有headerRowNo的构造函数 保证外部和内部的headerRowNo的值保持一致
	 * @param headerRowNo headerRowNo表示从第几行开始进行解析
	 */
	public ExcelImportAssistant(int headerRowNo){
		this.dataForThread = new InnerClass();
		this.headerRowNo = headerRowNo;
	}


	/**
	 * 为了支持多线程，将变化的量单独声明成内部类
	 */
	protected class InnerClass {
		public InnerClass() {
			this.headerRowNoForThread = headerRowNo;
		}
		private int headerRowNoForThread;
		private List<Object> dataList;
	}

	@Override
	public void reset() {
		this.dataForThread = new InnerClass();
	}

	@Override
	public Integer getHeaderRowNo() {
		return dataForThread.headerRowNoForThread;
	}

	@Override
	public void setHeaderRowNo(Integer headerRowNo) {
		this.dataForThread.headerRowNoForThread = headerRowNo;
	}

	@Override
	public List<Object> getDataList() {
		return dataForThread.dataList;
	}

	@Override
	public void setDataList(List<Object> dataList) {
		dataForThread.dataList = dataList;
	}

	@Override
	public Class<?> getClz() {
		return this.clz;
	}

	@Override
	public void refresh() {
		if(dataForThread == null) {
			return ;
		}
		dataForThread.dataList = null;
		dataForThread = null;
//		dataForThread.headerRowNoForThread = this.headerRowNo;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void setClz(Class<?> clz) {
		this.clz = (Class<T>) clz;
	}

}
