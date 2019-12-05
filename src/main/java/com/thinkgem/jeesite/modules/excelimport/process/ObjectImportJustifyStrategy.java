package com.thinkgem.jeesite.modules.excelimport.process;

import java.util.List;
import java.util.Map;


/**
 * @author Leamon-Song
 * 实体校验策略类的接口
 */
public interface ObjectImportJustifyStrategy {
	/**
	 * 对dataList的Object列表进行数据校验
	 * @date 2019年1月17日 下午12:21:12
	 * @param dataList 从EXCEL导入的对象列表
	 * @param errorMsgMap 存放行号和错误信息的map
	 * @param begRow 本次校验数据的起始行号
	 * @return 校验的结果
	 * @throws Exception 抛出异常
	 */
	public boolean justifyContentList(List<Object> dataList, Map<Integer, String> errorMsgMap, int begRow) throws Exception;


	/**
	 * 对TRYE_COMMON headerDataList进行校验
	 * @param headerDataList 封装成对象的TYPE_COMMON headerDataList
	 * @param errorMsgMap 存放行号和错误信息的map
	 * @param begRow 本次校验的其实行号
	 * @return 校验通过返回true 否则返回false
	 * @throws Exception 抛出异常
	 */
	boolean justifyHeaderList(List<Object> headerDataList, Map<Integer, String> errorMsgMap, int begRow) throws Exception;

	/**
	 * 不同的策略模式下 清除本地缓存的方法
	 */
	public void clear();

}
