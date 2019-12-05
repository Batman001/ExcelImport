package com.thinkgem.jeesite.modules.excelimport.demo.process;
/**
 * 对于SupportBody进行导入的校验策略类 SupportBody校验策略类 实现ObjectImportJustifyStrategy
 */

import com.thinkgem.jeesite.modules.excelimport.process.ObjectImportJustifyStrategy;


import java.util.*;

/**
 * @author Leamon-Song
 * 针对SupportBody对象进行校验的策略实现类
 */
public class SupportBodyJustifyStrategy implements ObjectImportJustifyStrategy {


	@Override
	public boolean justifyContentList(List<Object> dataList, Map<Integer, String> errorMsgMap, int begRow) throws Exception {
		return true;
	}

	@Override
	public boolean justifyHeaderList(List<Object> headerDataList, Map<Integer, String> errorMsgMap, int begRow) throws Exception {
		return true;
	}

	@Override
	public void clear() {

	}


}
