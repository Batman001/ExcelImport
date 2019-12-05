package com.thinkgem.jeesite.modules.excelimport.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author sunc
 * Date 2019/11/1 14:24
 * Description: 对properties的文件进行读取 并返回map对象
 */
public class LoadPropertiesFile {

    private String filePath;

    public LoadPropertiesFile(String filePath) {
        this.filePath = filePath;
    }

    public Map<String, String> loadProperties() {

        Map<String, String> result = new HashMap<>(8);
        if(null == filePath || "".equals(filePath.trim())) {
            return null;
        }

        Properties prop = new Properties();
        try{
            InputStream is = new BufferedInputStream(new FileInputStream(new File(filePath)));
            prop.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 返回Properties中包含的key-value的Set视图
        Set<Entry<Object, Object>> set = prop.entrySet();
        Iterator<Entry<Object, Object>> it = set.iterator();
        String key = null, value = null;
        // 循环取出key-value
        while (it.hasNext()) {

            Entry<Object, Object> entry = it.next();

            key = String.valueOf(entry.getKey());
            value = String.valueOf(entry.getValue());
            result.put(key, value);
        }
        return result;
    }

}
