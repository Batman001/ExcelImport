package com.thinkgem.jeesite.modules.excelimport.util;

import java.util.Hashtable;

/**
 * 文件上传状态监控
 * @author lvff 2018-8-22
 *
 */
public class ProgressSingleton {

    /**
     * 为了防止多用户并发，使用线程安全的Hashtable
     */
    private static Hashtable<Object, Object> table = new Hashtable<>();

    public static void put(Object key, Object value){
        table.put(key, value);
    }
    
    public static Object get(Object key){
        Object res = null;
        try{
            res = table.get(key);
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        return res;
    }
    
    public static Object remove(Object key){
        return table.remove(key);
    }

}
