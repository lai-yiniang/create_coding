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
import java.util.ArrayList;
import java.util.List;

// 创建PO类
public class BuilderQuery {
    private static final Logger logger = LoggerFactory.getLogger(BuilderQuery.class);
    public static void execute(TableInfo tableInfo){
        // 判断目录是否存在
        File folder = new File(Constants.PATH_QUERY);
        // 如果不存在则创建
        if (!folder.exists()){
            folder.mkdirs();
        }
        String className = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY;
        // 创建文件
        File poFile = new File(folder, className +".java");
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
            bw.write("package "+ Constants.PACKAGE_QUERY+";\n");
            bw.newLine();
            // 导入数据类型的包
            if(tableInfo.getHaveDate() || tableInfo.getHaveDateTime()){
                bw.write("import java.util.Date;");
                bw.newLine();
            }

            // BigDecimal导包
            if(tableInfo.getHaveBigDecimal()){
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            // 构建类注释
            if(tableInfo.getComment() != null){
            BuilderComment.createClassComment(bw, tableInfo.getComment() + "查询对象");
            }
            // 创建类名并实现
            bw.write("public class "+ className + " extends BaseQuery {");
            bw.newLine();
            // 创建属性并判断是否日期或时间
            for(FieldInfo field: tableInfo.getFieldList()){
                if (field.getComment() != null) {
                    BuilderComment.createFieldComment(bw, field.getComment());
                }
                bw.write("\tprivate "+ field.getJavaType() + " " + field.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();
                // String类型的参数
                if(ArrayUtils.contains(Constants.SQL_STRING_TYPES, field.getSqlType())){
                    String propertyName = field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY;
                    bw.write("\tprivate "+ field.getJavaType() + " " + propertyName + ";");
                    bw.newLine();
                    bw.newLine();
                }
                if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, field.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATE_TYPES, field.getSqlType())){
                    bw.write("\tprivate String" + " " + field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START + ";");
                    bw.newLine();
                    bw.newLine();
                    bw.write("\tprivate String" + " " + field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END + ";");
                    bw.newLine();
                    bw.newLine();
                }
            }
            List<FieldInfo> fieldInfoList = new ArrayList<>(tableInfo.getFieldList());
            fieldInfoList.addAll(tableInfo.getFieldExtendList());
            // 生成get/set方法
            try {
                BuilderGetSet(bw, fieldInfoList);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
    private static void BuilderGetSet(BufferedWriter bw, List<FieldInfo> fieldInfoList) throws IOException {
        for(FieldInfo field: fieldInfoList){
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
    }
}
