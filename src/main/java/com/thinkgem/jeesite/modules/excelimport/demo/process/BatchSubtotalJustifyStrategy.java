package com.thinkgem.jeesite.modules.excelimport.demo.process;

import com.thinkgem.jeesite.modules.excelimport.process.ObjectImportJustifyStrategy;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 对于SupportBody进行导入的校验策略类 BatchSubtotal校验 实现ObjectImportJustifyStrategy
 * @author Leamon-Song
 * BatchSubtotal的校验策略实现类
 */
public class BatchSubtotalJustifyStrategy implements ObjectImportJustifyStrategy {

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
