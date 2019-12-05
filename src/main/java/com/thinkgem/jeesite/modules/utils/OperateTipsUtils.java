package com.thinkgem.jeesite.modules.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 该类提供返回前端提示信息的通用方法
 * @author mx
 *
 */
public class OperateTipsUtils {
	
	/**
	 * 返回给前端的结果状态码：成功
	 */
	public static int STATUS_OK = 0;
	
	/**
	 * 返回给前端的结果状态码：业务错误（删除时有关联提示信息等）
	 */
	public static int STATUS_ERROR = 1;
	
	/**
	 * 返回给前端的结果状态码：后台代码异常，此时弹出提示信息“服务器数据错误”
	 */
	public static int STATUS_EXCEPTION = 3;
	
	/**
	 * R系列报表返回给前端的结果状态码：检验存在未生成盘存的Mba,需用户确认是否继续添加
	 */
	public static int STATUS_RP_NOTICE = 6;
	
	//用于登陆功能
	public static final int RESPONSE_OK_LOGIN = 0;
	public static final int RESPONSE_ERR_RELOGIN = 2;
	//用于支持性文件，即supportHead/Body
	public static final int RESPONSE_ERR_RECORD_PARAM = 11;//前台参数错误，请根据接口协议更改参数键值对，用于SupportHead/bodyController
	
	//文件导入导出状态
	public static final int FILEUPDOWN_OK = 0;//文件上传下载成功。
	public static final int FILEUPDOWN_CONTENT_ERR = 21;//文件上传成功，但有错误数据需要下载
	public static final int FILEUPDOWN_MODEL_ERR = 22;//导入模板错误
	public static final int FILEUPDOWN_NEED_MORE = 23;//导入文件数量不足
	public static final int FILUPDOWN_ERR = 24;//文件导入失败
	
	//文件导入状态值
	public static final int FILEUP_STATE = 101;//导入模板检查
	public static final int FILEUP_XML_STATE = 102;//导入模板检查
	
	private static final Map<String,Object> OPERATE_TIPS = new HashMap<String,Object>(5);
	
	/**
	 * 异步请求-提示信息。
	 * @param state 成功或者失败的状态
	 * @param msg 成功或者失败的提示信息
	 * @param detail 成功或者失败的详细信息
	 * @return 包含有状态，提示信息，详细信息的map集合。
	 */
	@Deprecated
	public static Map<String,Object> operateTips(String state,String msg,String detail){
		OPERATE_TIPS.clear();
		OPERATE_TIPS.put("state", state);
		OPERATE_TIPS.put("msg", msg);
		OPERATE_TIPS.put("detail", detail);
		return OPERATE_TIPS;
	}
	
	public static Map<String,Object> operateTips(int state,String msg,String detail){
		OPERATE_TIPS.clear();
		OPERATE_TIPS.put("state", state);
		OPERATE_TIPS.put("msg", msg);
		OPERATE_TIPS.put("detail", detail);
		return OPERATE_TIPS;
	}
	

	
	/**
	 * 根据变长paraKeyValues生成Map<String, Object>，若paraKeyValues长度为奇数，最后一个key的值设置为null
	 * @param paras 变长参数，存放要生成参数的key和Value,偶数索引为key值，奇数索引为Value值
	 * @return
	 */
	public  static Map<String, Object> generateParasMap(String...paraKeyValues){
		Map<String, Object> paraValues = new HashMap<String, Object>();
		int length = paraKeyValues.length;
		for(int index = 0; index < length;) {
			if(index + 1 <= length -1) {
				paraValues.put(paraKeyValues[index++], paraKeyValues[index++]);
			}else {
				paraValues.put(paraKeyValues[index++], null);
			}
		}
		return paraValues;
	}
	

}
