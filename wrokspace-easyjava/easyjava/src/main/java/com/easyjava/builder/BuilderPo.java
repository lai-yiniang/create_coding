package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.DateUtils;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

// 创建PO类
public class BuilderPo {
    private static final Logger logger = LoggerFactory.getLogger(BuilderPo.class);
    public static void execute(TableInfo tableInfo){
        // 判断目录是否存在
        File folder = new File(Constants.PATH_PO);
        // 如果不存在则创建
        if (!folder.exists()){
            folder.mkdirs();
        }
        // 创建文件
        File poFile = new File(folder, tableInfo.getBeanName()+".java");
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
            bw.write("package "+ Constants.PACKAGE_PO+";\n");
            bw.newLine();
            bw.write("import java.io.Serializable;");
            bw.newLine();
            // 导入数据类型的包
            if(tableInfo.getHaveDate() || tableInfo.getHaveDateTime()){
                bw.write("import java.util.Date;");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_FORMAT_CLASS);
                bw.newLine();
                bw.write(Constants.BEAN_DATE_UNFORMAT_CLASS);
                bw.newLine();
                bw.write("import "+Constants.PACKAGE_ENUM+".DateTimePatternEnum;");
                bw.newLine();
                bw.write("import "+Constants.PACKAGE_UTILS+".DateUtils;");
                bw.newLine();
            }
            // IgnoreBeanToJson 是否有忽略属性
            Boolean haveIgnoreBeanToJson = false;
            for(FieldInfo field: tableInfo.getFieldList()){
                if (Constants.IQNORE_BEAN_TOJSON_FIELD != null) {
                    if(ArrayUtils.contains(Constants.IQNORE_BEAN_TOJSON_FIELD.split(","), field.getPropertyName())){
                        haveIgnoreBeanToJson = true;
                        bw.write(Constants.IQNORE_BEAN_TOJSON_CLASS);
                        bw.newLine();
                        break;
                    }
                }
            }
            // BigDecimal导包
            if(tableInfo.getHaveBigDecimal()){
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            // 构建类注释
            if(tableInfo.getComment() != null){
            BuilderComment.createClassComment(bw, tableInfo.getComment());
            }
            // 创建类名并实现 Serializable接口
            bw.write("public class "+ tableInfo.getBeanName() + " implements Serializable {");
            bw.newLine();
            // 创建属性并判断是否日期或时间
            for(FieldInfo field: tableInfo.getFieldList()){
                if (field.getComment() != null){
                BuilderComment.createFieldComment(bw, field.getComment());
                }
                if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, field.getSqlType())){
                    bw.write("\t"+String.format(Constants.BEAN_DATE_FORMAT_EXPREESION, DateUtils.PATTEN_TIME));
                    bw.newLine();

                    bw.write("\t"+String.format(Constants.BEAN_DATE_UNFORMAT_EXPREESION, DateUtils.PATTEN_TIME));
                    bw.newLine();
                }
                if(ArrayUtils.contains(Constants.SQL_DATE_TYPES, field.getSqlType())){
                    bw.write("\t"+String.format(Constants.BEAN_DATE_FORMAT_EXPREESION, DateUtils.PATTEN_DATE));
                    bw.newLine();

                    bw.write("\t"+String.format(Constants.BEAN_DATE_UNFORMAT_EXPREESION, DateUtils.PATTEN_DATE));
                    bw.newLine();
                }
                // 忽略属性
                if (Constants.IQNORE_BEAN_TOJSON_FIELD != null) {
                    if(ArrayUtils.contains(Constants.IQNORE_BEAN_TOJSON_FIELD.split(","), field.getPropertyName())){
                        bw.write("\t"+Constants.IQNORE_BEAN_TOJSON_EXPREESION);
                        bw.newLine();
                    }
                }
                bw.write("\tprivate "+ field.getJavaType() + " " + field.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();
            }
            // 生成get/set方法
            for(FieldInfo field: tableInfo.getFieldList()){
                // set
                bw.write("\tpublic void set" + StringUtils.uperCaseFirstLetter(field.getPropertyName()) + "("+ field.getJavaType() + " " + field.getPropertyName() + ") {");
                bw.newLine();
                bw.write("\t\tthis." + field.getPropertyName() + " = " + field.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                // get
                bw.write("\tpublic "+ field.getJavaType() + " get" + StringUtils.uperCaseFirstLetter(field.getPropertyName()) + "() {");
                bw.newLine();
                bw.write("\t\treturn " + field.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
            }
            // toString
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic String toString() {");
            bw.newLine();
            bw.write(String.format("\t\treturn \"%s{ \" +", tableInfo.getBeanName()));
            bw.newLine();
            List<FieldInfo> fieldList = tableInfo.getFieldList();
            int fieldListSize = fieldList.size();
            for (int i = 0; i < fieldListSize; i++) {
                FieldInfo field = fieldList.get(i);
                String propertyName = field.getPropertyName();
                String comment = field.getComment();
                String TimeFormatName = null;
                if (i == 0) {
                    // 第一个元素：开头拼接，不带前置逗号
                    bw.write(String.format("\t\t\t\t\"%s=\" + (%s == null ? \"空\" : %s) +", comment, propertyName, propertyName));
                } else {
                    // 非第一个元素：带前置逗号
                    if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, field.getSqlType())){
                        TimeFormatName = "DateUtils.format("+ propertyName +", DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())";
                        bw.write(String.format("\t\t\t\t\", %s=\" + (%s == null ? \"空\" : %s) +", comment, propertyName, TimeFormatName));
                    }else if(ArrayUtils.contains(Constants.SQL_DATE_TYPES, field.getSqlType())){
                        TimeFormatName = "DateUtils.format("+ propertyName +", DateTimePatternEnum.YYYY_MM_DD.getPattern())";
                        bw.write(String.format("\t\t\t\t\", %s=\" + (%s == null ? \"空\" : %s) +", comment, propertyName, TimeFormatName));
                    }else{
                    bw.write(String.format("\t\t\t\t\", %s=\" + (%s == null ? \"空\" : %s) +", comment, propertyName, propertyName));
                    }

                }

                // 判断是否是最后一个元素（替代硬编码6，适配任意长度）
                if (i == fieldListSize - 1) {
                    // 最后一个元素：拼接结束符，去掉多余的+号
                    bw.newLine();
                    bw.write("\t\t\t\t\" }\\n\";");
                    bw.newLine();
                    bw.write("\t}");
                    bw.newLine();
                } else {
                    bw.newLine();
                }
            }


            // 构造函数
            FieldInfo fieldLast = tableInfo.getFieldList().get(tableInfo.getFieldList().size()-1);
            bw.write("\tpublic "+ tableInfo.getBeanName() +"(){}");
            bw.newLine();
            bw.newLine();
            bw.write("\tpublic "+ tableInfo.getBeanName() +"(");
            for(FieldInfo field: tableInfo.getFieldList()){
                if(field.getFieldName().equals(fieldLast.getFieldName())){
                    bw.write(field.getJavaType() + " " + field.getPropertyName());
                }else{
                    bw.write(field.getJavaType() + " " + field.getPropertyName() + ",");
                }
            }
            bw.write("){");
            bw.newLine();
            for(FieldInfo field: tableInfo.getFieldList()){
                bw.write("\t\tthis." + field.getPropertyName() + " = " + field.getPropertyName() + ";");
                bw.newLine();
            }
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
            bw.write("\tpublic "+ tableInfo.getBeanName() +"(");
//            FieldInfo auto_FieldInfo = null;
//            for (FieldInfo field: tableInfo.getFieldList()){
//                if (field.isAutoIncrement()) {
//                    auto_FieldInfo = field;
//                    break;
//                }
//            }
//            List<FieldInfo> fieldList_ReAUTO = tableInfo.getFieldList();
//            fieldList_ReAUTO.remove(auto_FieldInfo);
            for(FieldInfo field: tableInfo.getFieldList()){
                if(field.isAutoIncrement()){
                    continue;
                }
                if(field.getFieldName().equals(fieldLast.getFieldName())){
                    bw.write(field.getJavaType() + " " + field.getPropertyName());
                }else{
                bw.write(field.getJavaType() + " " + field.getPropertyName() + ",");
                }
            }
            bw.write("){");
            bw.newLine();

            for(FieldInfo field: tableInfo.getFieldList()){
                if(field.isAutoIncrement()){
                    continue;
                }
                bw.write("\t\tthis." + field.getPropertyName() + " = " + field.getPropertyName() + ";");
                bw.newLine();
            }
            bw.write("\t}");
            bw.newLine();
            bw.write("}");
            bw.flush();
        }catch (Exception e){
            logger.error("生成PO失败：{}",e.getMessage());
        }finally {
            // 先创建的后关闭，后创建先关闭
            if(bw!=null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outw!=null){
                try {
                    outw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out!=null){
                try {
                    out.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
