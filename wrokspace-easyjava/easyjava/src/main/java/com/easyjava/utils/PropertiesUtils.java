package com.easyjava.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
// 读取配置文件 获取数据库全类名 url地址 用户名 密码
public class PropertiesUtils {
    // Properties类用于读取配置文件
    private static Properties props = new Properties();
    // 存储配置文件
    private static Map<String,  String> PROPER_MAP = new ConcurrentHashMap<>();
    static{ 
        // 创建输入读取器
        InputStream is =null;
        try{
            // 获取PropertiesUtils类 并加入到类路径下 读取相对路径的配置文件
            is = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");
            // 加载配置文件
            props.load(new InputStreamReader(is, "utf8"));
            // 迭代器
            Iterator<Object> iterator = props.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next().toString();
                PROPER_MAP.put(key, props.getProperty(key));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(is !=null){
                try {
                    is.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public static String getString(String key){
        return PROPER_MAP.get(key);
    }

    public static void main(String[] args) {
        System.out.println(getString("db.driver.name"));
    }
}
