package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


public class BuilderMapperXml {
    private static final Logger logger = LoggerFactory.getLogger(BuilderMapperXml.class.getName());
    private static final String COLUMN_LIST = "columnList";
    private static final String BASE_QUERY_CONDITION = "baseQueryCondition";
    private static final String BASE_QUERY_CONDITION_EXTEND = "baseQueryConditionExtend";
    private static final String BASE_CONDITION = "queryCondition";


    public static void execute(TableInfo tableInfo) {
        // 判断目录是否存在
        File folder = new File(Constants.PATH_MAPPER_XML);
        // 如果不存在则创建
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // 创建文件
        String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPER;
        File poFile = new File(folder, className + ".xml");
        // 创建字节输出流
        OutputStream out = null;
        // 创建字符输出流包装类
        OutputStreamWriter outw = null;
        // 创建字符缓冲流
        BufferedWriter bw = null;
        try {
            // 指定写入的文件
            out = new FileOutputStream(poFile);
            // 将字节流包装为字符流
            outw = new OutputStreamWriter(out, "utf8");
            // 在用缓冲流进行包装
            bw = new BufferedWriter(outw);


            bw.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
            bw.newLine();
            bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
            bw.newLine();
            bw.write("<mapper namespace=\"" + Constants.PACKAGE_MAPPER + "." + className + "\">");
            bw.newLine();

            bw.write("\t<!--实体映射-->");
            bw.newLine();
            String poClass = Constants.PACKAGE_PO + "." + tableInfo.getBeanName();
            bw.write("\t<resultMap id=\"" + tableInfo.getBeanName() + "ResultMap\" type=\"" + poClass + "\">");
            bw.newLine();
            // 判断是否为主键
            FieldInfo idFieldInfo = null;
            Map<String, List<FieldInfo>> keyIndexList = tableInfo.getKeyIndexList();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexList.entrySet()) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                if (fieldInfoList.size() == 1) {
                    idFieldInfo = fieldInfoList.get(0);
                    break;
                }
            }

            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t<!--" + fieldInfo.getComment() + "-->");
                bw.newLine();
                if (idFieldInfo != null && fieldInfo.getPropertyName().equals(idFieldInfo.getPropertyName())) {
                    bw.write("\t\t<id property=\"" + fieldInfo.getPropertyName() + "\" column=\"" + fieldInfo.getFieldName() + "\" />");
                } else {
                    bw.write("\t\t<result property=\"" + fieldInfo.getPropertyName() + "\" column=\"" + fieldInfo.getFieldName() + "\"/>");
                }
                bw.newLine();
            }
            bw.write("\t</resultMap>");
            bw.newLine();
            // 通用查询列
            bw.write("\t<!--通用查询列-->");
            bw.newLine();

            bw.write("\t<sql id=\"" + COLUMN_LIST + "\">");
            bw.newLine();
            StringBuilder columnBuilder = new StringBuilder();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                columnBuilder.append(fieldInfo.getFieldName()).append(",");
            }
            String substring = columnBuilder.substring(0, columnBuilder.lastIndexOf(","));
            bw.write("\t\t" + substring);
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();
            bw.newLine();

            // 基础查询条件
            bw.write("\t<!--基础查询条件-->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION + "\">");
            bw.newLine();
            String stringQuery = "";
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                stringQuery = "";
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    stringQuery = "and query." + fieldInfo.getPropertyName() + "!=''";
                }
                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null " + stringQuery + "\">");
                bw.newLine();
                bw.write("\t\t\t and " + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "} ");
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</sql>");
            bw.newLine();

            bw.write("\t<!--扩展查询条件-->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION_EXTEND + "\">");
            bw.newLine();
            String andWhere = "";
            for (FieldInfo fieldInfo : tableInfo.getFieldExtendList()) {
                String sqlType = fieldInfo.getSqlType();
                String propertyName = fieldInfo.getPropertyName();
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    andWhere = "and " + fieldInfo.getFieldName() + " like concat('%', #{query." + fieldInfo.getPropertyName() + "},'%')";
                } // 2. 日期/时间类型：范围查询（区分开始/结束）
                else if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, sqlType)
                        || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, sqlType)) {
                    // 区分开始时间（TimeStart）和结束时间（TimeEnd）
                    String compareSign = "";
                    if (propertyName.endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_START)) {
                        compareSign = ">="; // 开始时间：大于等于
                    } else if (propertyName.endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_END)) {
                        compareSign = "<"; // 结束时间：小于
                    } else {
                        continue; // 非时间范围字段，跳过
                    }
                    // 正确的CDATA格式 + 闭合括号 + 带query.的参数
                    andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " " + compareSign + " str_to_date(#{query."
                            + propertyName + "}, '%Y-%m-%d') ]]>";
                }
                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null and query." + fieldInfo.getPropertyName() + " != ''\">");
                bw.newLine();
                bw.write("\t\t\t" + andWhere);
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }
            bw.write("\t</sql>");
            bw.newLine();

            // 通用查询条件
            bw.write("\t<!--通用查询条件-->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_CONDITION + "\">");
            bw.newLine();
            bw.write("\t\t<where>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION_EXTEND + "\"/>");
            bw.newLine();
            bw.write("\t\t</where>");
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();


            // 查询列表
            bw.write("\t<!--查询列表-->");
            bw.newLine();
            bw.write("\t<select id=\"selectList\" resultMap=\"" + tableInfo.getBeanName() + "ResultMap\"" + ">");
            bw.newLine();
            bw.write("\t\tselect <include refid=\"" + COLUMN_LIST + "\"/>" + " from " + tableInfo.getTableName() + " <include refid=\"" + BASE_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.orderBy != null\"> order by ${query.orderBy}</if>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.simplePage != null\"> limit ${query.simplePage.start},${query.simplePage.end}</if>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();


            // 查询数量
            bw.write("\t<!--查询数量-->");
            bw.newLine();
            bw.write("\t<select id=\"selectCount\" resultType=\"java.lang.Long\">");
            bw.newLine();
            bw.write("\t\tselect count(1) from " + tableInfo.getTableName() + " <include refid=\"" + BASE_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();
            // 插入或更新
            // 找到自增长字段
            FieldInfo auto_FieldInfo = null;
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.isAutoIncrement()) {
                    auto_FieldInfo = fieldInfo;
                    break;
                }
            }
            bw.newLine();
            bw.write("\t<!--插入(匹配有值的字段)-->");
            bw.newLine();
            bw.write("\t<insert id=\"insert\" parameterType=\"" + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + "\">");
            bw.newLine();
            if (auto_FieldInfo != null) {
                bw.write("\t\t<selectKey keyProperty=\"bean." + auto_FieldInfo.getFieldName() + "\" resultType=\"" + auto_FieldInfo.getJavaType() + "\" order=\"AFTER\">");
                bw.newLine();
                bw.write("\t\t\tSELECT LAST_INSERT_ID()");
                bw.newLine();
                bw.write("\t\t</selectKey>");
                bw.newLine();
            }
            bw.write("\t\t insert into " + tableInfo.getTableName());
            bw.newLine();
            // prefix 添加前缀  suffix 添加后缀    suffixOverrides 添加后缀，并且去掉多余的逗号
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.write("\t\t<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();

            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null and bean." + fieldInfo.getPropertyName() + " != ''\">");
                    bw.newLine();
                } else {
                    bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                    bw.newLine();
                }
                bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();


            // 插入或更新
            bw.newLine();
            bw.write("\t<!--插入或更新(匹配有值的字段)-->");
            bw.newLine();
            bw.write("\t<insert id=\"insertOrUpdate\" parameterType=\"" + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + "\">");
            bw.newLine();
            if (auto_FieldInfo != null) {
                bw.write("\t\t<selectKey keyProperty=\"bean." + auto_FieldInfo.getFieldName() + "\" resultType=\"" + auto_FieldInfo.getJavaType() + "\" order=\"AFTER\">");
                bw.newLine();
                bw.write("\t\t\tSELECT LAST_INSERT_ID()");
                bw.newLine();
                bw.write("\t\t</selectKey>");
                bw.newLine();
            }
            bw.write("\t\t insert into " + tableInfo.getTableName());
            bw.newLine();
            // prefix 添加前缀  suffix 添加后缀    suffixOverrides 添加后缀，并且去掉多余的逗号
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null and bean." + fieldInfo.getPropertyName() + " != ''\">");
                    bw.newLine();
                } else {
                    bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                    bw.newLine();
                }
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();

            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null and bean." + fieldInfo.getPropertyName() + " != ''\">");
                    bw.newLine();
                } else {
                    bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                    bw.newLine();
                }
                bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\ton duplicate key update");
            bw.newLine();

            // 过滤掉主键
            Map<String , String> tempMap = new HashMap<>();
            for(Map.Entry<String, List<FieldInfo>> entry: keyIndexList.entrySet()){
                List<FieldInfo> list = entry.getValue();
                for (FieldInfo fieldInfo : list){
                    tempMap.put(fieldInfo.getFieldName(), fieldInfo.getFieldName());
                }
            }
            bw.write("\t\t<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if(tempMap.containsKey(fieldInfo.getFieldName())){
                    continue;
                }
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null and bean." + fieldInfo.getPropertyName() + " != ''\">");
                    bw.newLine();
                } else {
                    bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                    bw.newLine();
                }
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = values(" + fieldInfo.getFieldName() + "),");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();

            // 批量插入
            bw.write("\t<!--批量插入-->");
            bw.newLine();
            bw.write("\t<insert id=\"insertBatch\" parameterType=\"java.util.List\" useGeneratedKeys=\"true\" keyProperty=\"id\">");
            bw.newLine();
            StringBuffer sb = new StringBuffer();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()){
                if(fieldInfo.isAutoIncrement()){
                    continue;
                }
                sb.append(fieldInfo.getFieldName()).append(",");
            }
            sb.setLength(sb.length() - 1);
            bw.write("\t\t insert into " + tableInfo.getTableName() + " ("+ sb +") values");
            bw.newLine();
            bw.write("\t\t<foreach item=\"item\" collection=\"list\" separator=\",\">");
            bw.newLine();
            bw.write("\t\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if(fieldInfo.isAutoIncrement()){
                    continue;
                }
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\t\t\t\t<if test=\"item." + fieldInfo.getPropertyName() + " != null and item." + fieldInfo.getPropertyName() + " != ''\">");
                    bw.newLine();
                } else {
                    bw.write("\t\t\t\t<if test=\"item." + fieldInfo.getPropertyName() + " != null\">");
                    bw.newLine();
                }
                bw.write("\t\t\t\t\t#{item." + fieldInfo.getPropertyName() + "},");
                bw.newLine();
                bw.write("\t\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t\t</trim>");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();





            // 批量新增修改
            bw.write("\t<!--批量新增修改-->");
            bw.newLine();
            bw.write("\t<insert id=\"insertOrUpdateBatch\" parameterType=\"java.util.List\">");
            bw.newLine();
            bw.write("\t\t insert into " + tableInfo.getTableName() + " ("+ sb +") values ");
            bw.newLine();
            bw.write("\t\t<foreach item=\"item\" collection=\"list\" separator=\",\">");
            bw.newLine();
            bw.write("\t\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {

                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\t\t\t\t<if test=\"item." + fieldInfo.getPropertyName() + " != null and item." + fieldInfo.getPropertyName() + " != ''\">");
                    bw.newLine();
                } else {
                    bw.write("\t\t\t\t<if test=\"item." + fieldInfo.getPropertyName() + " != null\">");
                    bw.newLine();
                }
                bw.write("\t\t\t\t\t#{item." + fieldInfo.getPropertyName() + "},");
                bw.newLine();
                bw.write("\t\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t\t</trim>");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t\ton duplicate key update");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()){
                if(tempMap.containsKey(fieldInfo.getFieldName())){
                    continue;
                }
                bw.write("\t\t\t" + fieldInfo.getFieldName() + " = values(" + fieldInfo.getFieldName() + "),");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();




            Map<String, List<FieldInfo>> indexList = tableInfo.getKeyIndexList();

            for (Map.Entry<String, List<FieldInfo>> entry : indexList.entrySet()) {
                // 存储索引字段
                StringBuffer indexBuffer = new StringBuffer();
                StringBuffer indexFieldBuffer = new StringBuffer();
                String[] indexFieldArray;
                List<FieldInfo> value = entry.getValue();
                if(!value.isEmpty()){
                    for (FieldInfo temp : value){
                        // 属性名
                        indexBuffer.append(temp.getPropertyName()).append(",");
                        // 数据库字段
                        indexFieldBuffer.append(temp.getFieldName()).append(",");
                    }
                }
                indexBuffer.setLength(indexBuffer.length() - 1);
                indexFieldArray = indexBuffer.toString().split(",");

                String idName = createComment("修改", indexFieldArray, bw);
                bw.newLine();
                bw.write("\t<update id=\"updateBy" + idName + "\" parameterType=\""+ Constants.PACKAGE_PO + "." + tableInfo.getBeanName() +"\">");
                bw.newLine();
                bw.write("\t\t update " + tableInfo.getTableName());
                bw.newLine();
                bw.write("\t\t<set>");
                bw.newLine();
                for (FieldInfo fieldInfo : tableInfo.getFieldList()){
                    // 过滤索引字段
                    if(indexBuffer.toString().contains(fieldInfo.getPropertyName())){
                        continue;
                    }
                    if(fieldInfo.isAutoIncrement()){
                        continue;
                    }

                    if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                        bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null and bean." + fieldInfo.getPropertyName() + " != ''\">");
                        bw.newLine();
                    }
                    else {
                        bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                        bw.newLine();
                    }
                    bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = #{bean." + fieldInfo.getPropertyName() + "},");
                    bw.newLine();
                    bw.write("\t\t</if>");
                    bw.newLine();
                }
                bw.write("\t\t</set>");
                bw.newLine();

                bw.write("\t\t\twhere ");
                // 分割数据库字段
                String[] fieldArray;
                fieldArray = indexFieldBuffer.toString().split(",");
                for (int i = 0; i < indexFieldArray.length; i++){
                    if(i == indexFieldArray.length - 1){
                        bw.write(fieldArray[i] + " = #{" + indexFieldArray[i] + "}");

                    }else{
                        bw.write(fieldArray[i] + " = #{" + indexFieldArray[i] + "} and ");
                    }
                }
                bw.newLine();
                bw.write("\t\t</update>");
                bw.newLine();


                bw.newLine();
                idName = createComment("查询", indexFieldArray, bw);
                bw.newLine();
                bw.write("\t<select id=\"selectBy" + idName + "\" resultMap=\"" + tableInfo.getBeanName() + "ResultMap\" parameterType=\"" + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + "\">");
                bw.newLine();
                bw.write("\t\t select <include refid=\"" + COLUMN_LIST +"\"/> from " + tableInfo.getTableName() + " where ");
                if(indexFieldArray.length == 1){
                    bw.write(fieldArray[0] + " = #{" + indexFieldArray[0] + "}");
                }else{
                    for (int i = 0; i < indexFieldArray.length; i++){
                        if(i == indexFieldArray.length - 1){
                            bw.write(fieldArray[i] + " = #{" + indexFieldArray[i] + "}");

                        }else{
                            bw.write(fieldArray[i] + " = #{" + indexFieldArray[i] + "} and ");
                        }
                    }
                }
                bw.newLine();
                bw.write("\t</select>");
                bw.newLine();


                // 删除
                idName = createComment("删除", indexFieldArray, bw);
                bw.newLine();
                bw.write("\t<delete id=\"deleteBy" + idName + "\">");
                bw.newLine();
                bw.write("\t\t delete from " + tableInfo.getTableName() + " where ");
                if(indexFieldArray.length == 1){
                    bw.write(fieldArray[0] + " = #{" + indexFieldArray[0] + "}");
                }else{
                    for (int i = 0; i < indexFieldArray.length; i++){
                        if(i == indexFieldArray.length - 1){
                            bw.write(fieldArray[i] + " = #{" + indexFieldArray[i] + "}");

                        }else{
                            bw.write(fieldArray[i] + " = #{" + indexFieldArray[i] + "} and ");
                        }
                    }
                }
                bw.newLine();
                bw.write("\t</delete>");
                bw.newLine();
            }



            bw.newLine();
            bw.write("</mapper>");
            bw.newLine();
            bw.flush();
        } catch (Exception e) {
            logger.error("生成Mapper失败：{}", e.getMessage());
        }

    }

    private static String createComment(String comment, String[] indexFieldArray,BufferedWriter bw) throws IOException {
        // 根据索引查询
        bw.write("\t<!--根据");
        // 根据索引修改
        StringBuffer idName = new StringBuffer();
        for (int i = 0; i < indexFieldArray.length; i++) {
            if(i > 0){
                idName.append("And");
            }
            idName.append( StringUtils.uperCaseFirstLetter(indexFieldArray[i]));
        }
        bw.write( idName + comment + "-->");
        return idName.toString();
    }
}