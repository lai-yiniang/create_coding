package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.JsonUtils;
import com.easyjava.utils.PropertiesUtils;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 读取表信息
public class BuilderTable {
    private static final Logger logger = LoggerFactory.getLogger(BuilderTable.class);
    // Connection类用于与数据库进行交互
    private static Connection conn = null;
    private static String SQL_SHOW_TABLE_STATUS = "show table status";  // 获取数据库中有多少表
    private static String SQL_SHOW_TABLE_FIELDS = "show full fields from %s";  // 获取数据库中有多少表
    private static String SQL_SHOW_INDEX = "show index from %s";  //

    static {
        String driverName = PropertiesUtils.getString("db.driver.name");
        String url = PropertiesUtils.getString("db.url");
        String userName = PropertiesUtils.getString("db.username");
        String password = PropertiesUtils.getString("db.password");

        try {
            // 加载数据库驱动类
            Class.forName(driverName);
            // 获取数据库连接
            conn = DriverManager.getConnection(url, userName, password);

        } catch (Exception e) {
            logger.error("数据库连接失败！", e);
        }

    }

    public static List<TableInfo> getTables() {
        // 预编译 SQL 语句对象 用于执行sql语句
        PreparedStatement ps = null;
        // SQL 查询结果集 存储查询结果
        ResultSet tableResult = null;
        List<TableInfo> tableInfoList = new ArrayList<>();
        try {
            ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
            // executeQuery()执行查询，返回ResultSet（结果集）
            tableResult = ps.executeQuery();
            while (tableResult.next()) {
                String tableName = tableResult.getString("Name");
                String comment = tableResult.getString("Comment");

                String beanName = tableName;
                if (Constants.IGNORE_TABLE_PERFIX) {
                    beanName = tableName.substring(beanName.indexOf("_") + 1);
                }
                beanName = processFiled(beanName, true);

                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(comment);
                tableInfo.setBeanParamName(beanName + Constants.SUFFIX_BEAN_QUERY);
//                logger.info("tableInfo{}", JsonUtils.convertObjectJson(tableInfo));
                // 字段 信息集合
                readFieldInfo(tableInfo);
                getKeyIndexInfo(tableInfo);
                tableInfoList.add(tableInfo);


            }
        } catch (Exception e) {
            logger.error("获取表失败！", e);
        } finally {
            if (tableResult != null) {
                try {
                    tableResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return tableInfoList;
    }
    private static void readFieldInfo(TableInfo tableInfo){
        // 预编译 SQL 语句对象 用于执行sql语句
        PreparedStatement ps = null;
        // SQL 查询结果集 存储查询结果
        ResultSet fieldResult = null;
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        List<FieldInfo> fieldExtendList = new ArrayList<>();
        try {
            ps = conn.prepareStatement(String.format((SQL_SHOW_TABLE_FIELDS),tableInfo.getTableName()));
            // executeQuery()执行查询，返回ResultSet（结果集）
            fieldResult = ps.executeQuery();
            tableInfo.setHaveBigDecimal(false);
            tableInfo.setHaveDate(false);
            tableInfo.setHaveDateTime(false);
            while (fieldResult.next()) {
                String field = fieldResult.getString("field");
                String type = fieldResult.getString("type");
                String extra = fieldResult.getString("extra");
                String comment = fieldResult.getString("comment");
                // 去掉字段类型中的括号
                if(type.indexOf("(") > 0){
                    type = type.substring(0, type.indexOf("("));
                }
                // 将字段转化为驼峰命名
                String propertyName = processFiled(field, false);

                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setFieldName(field);
                fieldInfo.setComment(comment);
                fieldInfo.setSqlType(type);
                fieldInfo.setAutoIncrement("auto_increment".equalsIgnoreCase(extra));
                fieldInfo.setPropertyName(propertyName);

                fieldInfoList.add(fieldInfo);
                // sql类型转java类型
                fieldInfo.setJavaType(processJaveType(type));


                // 判断是否有日期和bigDecimal类型
                if(ArrayUtils.contains(Constants.SQL_DATE_TYPES, type)){
                    tableInfo.setHaveDate(true);
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type)){
                    tableInfo.setHaveDateTime(true);
                }
                if(ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type)){
                    tableInfo.setHaveBigDecimal(true);
                }
                if(ArrayUtils.contains(Constants.SQL_STRING_TYPES, type)){
                    FieldInfo fuzzyField = new FieldInfo();
                    fuzzyField.setJavaType(fieldInfo.getJavaType());
                    fuzzyField.setPropertyName(propertyName  + Constants.SUFFIX_BEAN_QUERY_FUZZY);
                    fuzzyField.setFieldName(fieldInfo.getFieldName());
                    fuzzyField.setSqlType(fieldInfo.getSqlType());
                    fieldExtendList.add(fuzzyField);
                }
                if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,type) || ArrayUtils.contains(Constants.SQL_DATE_TYPES, type)){
                    FieldInfo timeStartField = new FieldInfo();
                    timeStartField.setJavaType("String");
                    timeStartField.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START);
                    timeStartField.setFieldName(fieldInfo.getFieldName());
                    timeStartField.setSqlType(fieldInfo.getSqlType());
                    fieldExtendList.add(timeStartField);

                    FieldInfo timeEndField = new FieldInfo();
                    timeEndField.setJavaType("String");
                    timeEndField.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END);
                    timeEndField.setFieldName(fieldInfo.getFieldName());
                    timeEndField.setSqlType(fieldInfo.getSqlType());
                    fieldExtendList.add(timeEndField);
                }
            }
            tableInfo.setFieldList(fieldInfoList);
            tableInfo.setFieldExtendList(fieldExtendList);
        } catch (Exception e) {
            logger.error("获取字段失败！", e);
        } finally {
            if (fieldResult != null) {
                try {
                    fieldResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 遍历唯一索引并存入tableInfo中
     * @param tableInfo
     * @return
     */

    private static void getKeyIndexInfo(TableInfo tableInfo){
        // 预编译 SQL 语句对象 用于执行sql语句
        PreparedStatement ps = null;
        // SQL 查询结果集 存储查询结果
        ResultSet fieldResult = null;
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        try {
            Map<String, FieldInfo> tempMap = new HashMap<>();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()){
               tempMap.put(fieldInfo.getFieldName(),fieldInfo);
            }
            ps = conn.prepareStatement(String.format((SQL_SHOW_INDEX),tableInfo.getTableName()));
            // executeQuery()执行查询，返回ResultSet（结果集）
            fieldResult = ps.executeQuery();
            while (fieldResult.next()) {
                String keyName = fieldResult.getString("key_name");
                Integer nonUnique = fieldResult.getInt("non_unique");
                String columnName = fieldResult.getString("column_name");
                if(nonUnique == 1){
                    continue;
                }
                List<FieldInfo> keyFieldList = tableInfo.getKeyIndexList().get(keyName);
                if(keyFieldList == null){
                    keyFieldList = new ArrayList<>();
                    tableInfo.getKeyIndexList().put(keyName, keyFieldList);
                }
               keyFieldList.add(tempMap.get(columnName));
            }
        } catch (Exception e) {
            logger.error("获取索引失败！", e);
        } finally {
            if (fieldResult != null) {
                try {
                    fieldResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    /**
     *
     * @param filed 字段或表名
     * @param upperCaseFirstLetter 是否将首字母大写
     * @return
     */
    private static String processFiled(String filed, Boolean upperCaseFirstLetter) {
        StringBuffer sb = new StringBuffer();
        String[] fileds = filed.split("_");
        sb.append(upperCaseFirstLetter ? StringUtils.uperCaseFirstLetter(fileds[0]) : fileds[0]);
        for (int i = 1, len = fileds.length; i < len; i++) {
            sb.append(StringUtils.uperCaseFirstLetter(fileds[i]));
        }
        return sb.toString();
    }
    // 用于匹配SQL类型转为java类型返回
    private static String processJaveType(String type){
        if(ArrayUtils.contains(Constants.SQL_INTEGER_TYPES, type)){
            return "Integer";
        }else if(ArrayUtils.contains(Constants.SQL_LONG_TYPES, type)){
            return "Long";
        }else if(ArrayUtils.contains(Constants.SQL_STRING_TYPES, type)){
            return "String";
        }else if(ArrayUtils.contains(Constants.SQL_DATE_TYPES, type) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type)){
            return "Date";
        } else if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type)) {
            return "BigDecimal";
        }else{
            throw new RuntimeException("无法识别的类型"+type);
        }

    }
}

