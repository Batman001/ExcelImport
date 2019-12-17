package com.thinkgem.jeesite.modules.excelimport.base;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.utils.Reflections;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.ImportExcel;
import com.thinkgem.jeesite.modules.excelimport.process.IReadAndPostProcess;
import com.thinkgem.jeesite.modules.excelimport.util.ConstEntityExcelHelper;
import com.thinkgem.jeesite.modules.excelimport.util.ExcelImportService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Jeesite平台上的ImportExcel的装饰者模式类
 * @author JSJS-Songlc
 * @author sunc
 */
@Service
public class ImportExcelWrapper extends ImportExcel {
	private static Logger logger = LoggerFactory.getLogger(ImportExcelWrapper.class);

	private ImportExcelWrapper hiddenObj;

	public ImportExcelWrapper() {}

	public ImportExcelWrapper(String file, int headerNum) throws InvalidFormatException, IOException {
		super(file, headerNum);
	}

	public ImportExcelWrapper(MultipartFile multipartFile, int headerNum, int sheetIndex) throws InvalidFormatException, IOException {
		super(multipartFile, headerNum, sheetIndex);
	}

	public void init(MultipartFile multipartFile, int headerNum, int sheetIndex) throws Exception {
		try {
			this.hiddenObj = new ImportExcelWrapper(multipartFile,headerNum,sheetIndex);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * Excel导入过程中 如果校验过程出现错误 抛出异常类
	 * 用于采集Excel导入数据的校验信息
	 */
	public static class MapWithException extends Exception{
		private static final long serialVersionUID = -7555813309320948477L;
		private Map<Integer, String> map;
		MapWithException(Map<Integer, String> map) {
			this.map = map;
		}
		public Map<Integer, String> getMap(){
			return this.map;
		}
	}

	/**
	 * 释放hiddenObj占用的空间
	 * @author lvff 20181112
	 */
	public void refresh(){
		if(hiddenObj==null){
			return ;
		}
		this.hiddenObj = null;
	}

	public Sheet getSheetByIndex(int sheetIndex) {
		if(this.hiddenObj == null) {
			logger.error("ImportExcelWrapper : this.hiddenObJ is not initialized!");
			return null;
		}
		return this.hiddenObj.getSheet(sheetIndex);
	}

	/**
	 * 根据模板配置信息读取EXCEL数据
	 * @param excelModelConfig 模板配置信息
	 * @param sw 错误数据提示信息
	 * @return  excel 返回经过当前读取处理后 目前所在的行数
	 */
	private int getDataList(List<ExcelAssistantBase> excelModelConfig, StringWriter sw) {
		// 获得本次Excel导入模板的标题行
		int headerNum = excelModelConfig.get(0).headerRowNo;
		int size = excelModelConfig.size();
		boolean errorOrNot = false;
		if(size <= 0) {
			logger.error(ImportExcelWrapper.class + ":getDataList 无excel模板配置信息！");
			return -1;
		}
		/* 解析模板配置内容，根据内容读取相关数据 */
		int curRow = excelModelConfig.get(0).getHeaderRowNo();
		int curCol = 0;
		int rowStep = 1;
		Object obj;
		Map<String, Object> propertyNameValMap = new HashMap<>(8);
		//遍历excel模板配置内容，根据配置读取数据
		int contentCnt = -1;
		for(int listIndex = 0; listIndex < size; listIndex++) {
			ExcelAssistantBase eaBase = excelModelConfig.get(listIndex);
			//清空dataList
			eaBase.setDataList(null);
			if(eaBase.getIgnoreOrNot()) {
				curRow ++;
				continue;
			}
			//如果表头类型为TYPE_COMMON 则采用Excel模板通用部分处理
			int headerType = eaBase.getHeadType();
			// 将TYPE_COMMON的header进行封装成DataList
			if(headerType == ConstEntityExcelHelper.TYPE_COMMON) {
				rowStep = 1;
				if(!this.justifyHeader(eaBase, curRow, curCol,sw)) {
					return -1;
				}
				if(eaBase.getHeaderPropertyRowType() == ConstEntityExcelHelper.ROW_TYPE_NEXT) {
					curRow++;
				}

				// 获取表头数据 封装成对象 进行校验 如果TYPE_COMMON校验不通过 则不读取数据
				// 目前认为TYPE_COMMON的行数为一行
				eaBase.setHeaderRowNo(curRow);
				List<Object> commonHeaderList = this.getDataList(eaBase.getClz(), eaBase, curRow, 1);
				eaBase.setDataList(commonHeaderList);

				//获取表头数据
				List<String> headerArray = eaBase.getProperties();
				if(headerArray == null) {
					continue;
				}
				List<Integer[]> begRowAndColumns = eaBase.getPropertyValBegRowAndCols();
				int arraySize = headerArray.size();
				for(int arrayIndex = 0; arrayIndex < arraySize;arrayIndex++) {
					curCol = begRowAndColumns.get(arrayIndex)[1];
					obj = this.getCellValue(this.getRow(curRow),curCol);

					//PROPERTY_TYPE_CNT表示下面即将导入数据的行数
					if(eaBase.getHeaderMeaning()[arrayIndex] == ConstEntityExcelHelper.PROPERTY_TYPE_CNT) {
						contentCnt = ((Double)obj).intValue();
					}
					//PROPERTY_TYPE_NULL,表示下面即将导入数据的通用属性名称和属性值，例如小批次，将其放入属性名和属性值的map中。
					else if(eaBase.getHeaderMeaning()[arrayIndex] == ConstEntityExcelHelper.PROPERTY_TYPE_NULL) {
						propertyNameValMap.put(eaBase.getProperties().get(arrayIndex), obj);
					}else {
						String msg = "行号：" + curRow + "  " +eaBase.getProperties().get(arrayIndex) + "PROPERTY_TYPE为" +
								eaBase.getHeaderMeaning()[arrayIndex] + "无此类型！";
						sw.append(msg).append("\n");
						logger.error(msg);
						errorOrNot = true;
					}
				}
				//
				if(errorOrNot) {
					return -1;
				}
			}
			//导入实体类数据部分处理
			else if(headerType == ConstEntityExcelHelper.TYPE_CONTENT) {
				// contentCnt值为-1 则说明数据的个数不确定，需要进行特殊处理 （并且需要保证从该行的下一行开始后面全部为数据行）
				if(contentCnt == -1) {
					// 使用 totalRow 计算得到数据的行数 - curRow 作为数据的行数
					int totalRow = this.getLastDataRowNum();
					contentCnt = totalRow - curRow - headerNum;
				}
				//验证表头正确性
				if(!this.justifyHeader(eaBase, curRow, curCol,sw)) {
					return -1;
				}
				if(eaBase.getHeaderPropertyRowType() == ConstEntityExcelHelper.ROW_TYPE_NEXT) {
					curRow++;
				}
				eaBase.setHeaderRowNo(curRow);
				List<Object> tmpList = this.getDataList(eaBase.getClz(), eaBase, curRow, contentCnt);
				//将数据个数恢复为默认值1
				contentCnt = 1;
				rowStep = tmpList.size();
				//设置TYPE_COMMON中的公共属性
				// 将公共propertyNameValMap中公共字段更新设置实体中
				for (Object o : tmpList) {
					if ((obj = o) != null) {
						for (Entry<String, Object> entry : propertyNameValMap.entrySet()) {
							Reflections.invokeSetterWithNull(obj, entry.getKey(), entry.getValue());
						}
					}
				}
				eaBase.setDataList(tmpList);
			}
			curRow += rowStep;
		}
		return curRow;
	}


	/**
	 * 验证excel表头正确性
	 * @param eaBase 配置信息
	 * @param curRow 当前行
	 * @param curCol 当前列
	 * @param sw 存放错误数据的StringWriter
	 * @return true，验证正确；false，验证错误
	 */
	private boolean justifyHeader(ExcelAssistantBase eaBase, int curRow, int curCol, StringWriter sw) {
		boolean justifyRes = true;
		Object obj;
		List<String> headerArray = eaBase.getHeaderNames();
		if(headerArray == null) {
			return true;
		}
		List<Integer[]> begRowAndColumns = eaBase.getHeaderNameBegRowAndCols();
		int arraySize = headerArray.size();
		int tmpSize = begRowAndColumns.size();
		//判断表头是否是符合模板
		for(int arrayIndex = 0; arrayIndex < arraySize;arrayIndex++) {
			if(arrayIndex < tmpSize) {
				curCol = begRowAndColumns.get(arrayIndex)[1];
			}
			obj = this.getCellValue(this.getRow(curRow),curCol);
			//以下为了完成连续表头只配置一个位置的情况，例如：序号、姓名、年龄，对应的行列分别为[0,0][0,1][0,2],在配置中只配置一个[0,0]即可
			curCol++;
			if(!headerArray.get(arrayIndex).equals(obj)) {
				if(sw == null) {
					sw = new StringWriter();
				}
				String msg = "模板信息错误！行序号为：" + (curRow + 1) + " 内容为：" + headerArray.get(arrayIndex) + " : " + obj;
				sw.append(msg).append("\n");
				logger.error(msg);
				justifyRes = false;
			}
		}
		return justifyRes;
	}
	/**
	 * 拿到Excel模板数据后 对Excel中数据部分进行读取 返回DataList
	 * @param clz 泛型 用于表征当前导入实体的class
	 * @param eab 配置文件
	 * @param curRow 当前行
	 * @param cnt Excel中数据部分 包含数据行数
	 * @param <E>
	 * @return 返回读取数据后的DataList
	 */
	private <E> List<Object> getDataList(Class<E> clz, ExcelAssistantBase eab, int curRow, int cnt){
		List<Object> dataList;
		dataList = Lists.newArrayList();
		StringBuilder stringBuilder = new StringBuilder();

		List<Integer[]> begRowAndColumns = eab.getPropertyValBegRowAndCols();
		int tmpSize = begRowAndColumns.size();
		Object val;
		//用于标识导入错误的数据
		E errObj = null;
		int column = 0;
		for (int rowIndex = 0; rowIndex < cnt; rowIndex++) {
			try {
				E obj = clz.newInstance();
				column = 0;
				Row row = this.getRow(rowIndex + curRow);
				stringBuilder.delete(0, stringBuilder.length());
				List<String> properties = eab.getProperties();
				List<String> propertyDefaultValue = eab.getPropertyDefaultValue();
				for (int index = 0; index < properties.size(); index++) {
					// 对TYPE_COMMON的header中None的property进行忽略
					if("None".equals(properties.get(index))) {
						continue;
					}
					// 根据属性获得返回值类型
					Class<?> valType = Reflections.getGetterMethodRetType(obj, properties.get(index));
					// 根据返回值类型将val转化为返回值类型对象
					if(index < tmpSize) {
						column = begRowAndColumns.get(index)[1];
						val = this.getCellValue(row, column);
						// 如果val为空字符串的情况 使用配置文件中的DefaultValue进行设置
						if(StringUtils.isBlank(val.toString())) {
							val = propertyDefaultValue.get(index);
						}

					}else {//以下为了完成连续表头只配置一个位置的情况，例如：序号、姓名、年龄，对应的行列分别为[0,0][0,1][0,2],在配置中只配置一个[0,0]即可
						val = this.getCellValue(row, ++column);
						// 如果val为空字符串的情况 使用配置文件中的DefaultValue进行设置
						if(StringUtils.isBlank(val.toString())) {
							val = propertyDefaultValue.get(index);
						}
					}
					if("--".equals(val)) {
						continue;
					}
					val = transferValueByType(valType, val);
					Reflections.invokeSetterWithNull(obj, properties.get(index), val);
					stringBuilder.append(val).append(", ");
				}
				logger.debug("Read success: [" + rowIndex + "]" + stringBuilder.toString());
				dataList.add(obj);
			} catch (Exception e) {
				//将NULL存入dataList以便区分错误数据
				dataList.add(errObj);
				logger.error("第" + (rowIndex + curRow + 1) + "行第" + (column + 1) +"列数据发生错误！");
				logger.error(e.getMessage());
			}
		}
		return dataList;
	}


	/**
	 * 读取EXCEL模板，并对数据进行校验，并对导入数据存入数据库，若出现异常则进行回滚 即Excel数据读取和后处理过程
	 * @param excelModelConfig 配置信息
	 * @param errMsgs 写入相关错误信息的StringWriter
	 * @param postProcessor 数据后处理器，进行文件的校验和保存
	 * @return Map<Integer,String> 数据的校验信息结果，即导入数据的错误信息以及错误校验过程的错误原因，后续写入EXCEL返回给用户
	 * @author sunc
	 * @throws Exception 对Excel进行数据读取和后处理的可能抛出的异常
	 */
	@Transactional(rollbackFor=Exception.class)
	public Map<Integer,String> readAndPostProcessExcel(List<ExcelAssistantBase> excelModelConfig, StringWriter errMsgs,
                                                       IReadAndPostProcess postProcessor)throws Exception{

		Map<Integer,String> errorMsgMap = this.hiddenObj.readAndPostProcessExcelInner(excelModelConfig,errMsgs,postProcessor);
		if(errMsgs.getBuffer().length() > 0) {
			throw new MapWithException(errorMsgMap);
		}
		if(errorMsgMap != null && errorMsgMap.size() > 0) {
			throw new MapWithException(errorMsgMap);
		}
		return errorMsgMap;
	}

	/**
	 * Excel数据读取和后处理的内部类
	 * @param excelModelConfig 配置信息
	 * @param errMsgs 写入相关错误信息的StringWriter
	 * @param postProcessor 数据后处理器，进行文件的校验和保存
	 * @return 数据的校验信息结果，即导入数据的错误信息以及错误校验过程的错误原因，后续写入EXCEL返回给用户
	 * @throws Exception 对Excel进行数据读取和后处理的可能抛出的异常
	 */
	private Map<Integer,String> readAndPostProcessExcelInner(List<ExcelAssistantBase> excelModelConfig, StringWriter errMsgs,
                                                             IReadAndPostProcess postProcessor) throws Exception {
		if(postProcessor == null) {
			logger.error("postProcessor is null!");
			return null;
		}
		long start, end;
		int rowNo = 0;
		int totalRow = this.getLastDataRowNum();

		// 用于存储Excel导入过程中错误信息
		// 如果TYPE_COMMON数据校验过程出错 不进行TYPE_CONTENT的数据校验 因此errorMsgMap中只保留TYPE_COMMON的出错信息
		// 如果TYPE_CONTENT数据校验过程出错 说明TYPE_COMMON 数据校验通过 因此errorMsgMap中只保留TYPE_CONTENT的出错信息
		Map<Integer,String> errorMsgMap = new HashMap<>(8);

		// 用于存储Excel校验过程中出错详细信息(具体到单元格级别) 用于将错误备注信息在原始文件中显示出来
		Map<Cell, String> errorMsgDetail = new HashMap<>(8);

		// 设置校验是否通过的标记flag
		boolean flag = true;

		while(rowNo < totalRow) {
			start = System.currentTimeMillis();
			rowNo = this.getDataList(excelModelConfig, errMsgs);
			end = System.currentTimeMillis();
			logger.info("读取Excel数据到 [{}] 行，消耗时间为： [{}]ms", rowNo, end-start);
			//导入表头数据有问题
			if(rowNo == -1) {
				return null;
			}

			List<Object> bodyList;
			//对bodyList进行校验,找出问题数据
			for (ExcelAssistantBase eab : excelModelConfig) {
				boolean isHeaderOrNot = false;
				if ((bodyList = eab.getDataList()) != null) {
					// 判断该配置的 ExcelAssistance 是否为TYPE_COMMON header
					isHeaderOrNot = ConstEntityExcelHelper.TYPE_COMMON == eab.getHeadType();
					int begRow = eab.getHeaderRowNo();
					try {
						start = System.currentTimeMillis();
						// 对Excel数据进行校验 并将出错信息写入errorMegMap
						flag = postProcessor.justifyDataList(bodyList, errorMsgMap, begRow, isHeaderOrNot);

						end = System.currentTimeMillis();
						logger.info("对 [{}] 条 [{}] 数据完成校验，消耗时间为 : [{}] ms",
								bodyList.size(), bodyList.get(0).getClass().toString(), end - start );
					} catch (Exception e) {
						postProcessor.clear();
						throw new Exception(e);
					}
				}
				// 如果TYPE_COMMON出现错误 立即返回 不进行TYPE_CONTENT数据校验
				if( isHeaderOrNot && !flag) {
					break;
				}
			}
			//数据无错误
			if(flag) {
				for (ExcelAssistantBase eab : excelModelConfig) {
					// 并且保证dataList为导入实体的类 并且是配置文件的TYPE_CONTENT 才可以进行存储数据库
					if ((bodyList = eab.getDataList()) != null && eab.getHeadType() == ConstEntityExcelHelper.TYPE_CONTENT) {
						try {
							//没有错误数据，再保存
							if (errorMsgMap.size() == 0) {
								start = System.currentTimeMillis();
								flag = postProcessor.createAndSaveObjects(bodyList);
								end = System.currentTimeMillis();
								logger.info("createAndSaveObjects cost time is :" + (end - start) + "ms");
							}
						} catch (Exception e) {
							logger.error("Exception Message : {}", e.getMessage());
							logger.error("Exception toString abd track space : {} ", "\r\n" + e);
							logger.error(ExcelImportService.errorTraceSpace(e));
							throw new RuntimeException();
						}
					}
				}
			}
			//设置下一循环开始行号
			excelModelConfig.get(0).setHeaderRowNo(rowNo);
		}
		postProcessor.clear();
		return errorMsgMap;
	}
}

