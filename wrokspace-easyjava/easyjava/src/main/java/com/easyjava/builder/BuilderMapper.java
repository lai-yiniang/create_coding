package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

public class BuilderMapper {
    private static final Logger logger = LoggerFactory.getLogger(BuilderMapper.class);

    public static void execute(TableInfo tableInfo) {
        // 判断目录是否存在
        File folder = new File(Constants.PATH_MAPPER);
        // 如果不存在则创建
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // 创建文件
        String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPER;
        File poFile = new File(folder, className + ".java");
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

            bw.write("package " + Constants.PACKAGE_MAPPER + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import org.apache.ibatis.annotations.Mapper;");
            bw.newLine();
//            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
//            bw.newLine();
            String classQueryName = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY;
//            bw.write("import " + Constants.PACKAGE_QUERY + "." + classQueryName + ";");
//            bw.newLine();
            bw.write("import org.apache.ibatis.annotations.Param;");
            bw.newLine();
            // 类注解
            BuilderComment.createClassComment(bw, tableInfo.getComment());
            // 构建类
            bw.write("@Mapper");
            bw.newLine();
//            bw.write("public interface " + className + " extends BaseMapper<"+tableInfo.getBeanName() +"," +classQueryName + "> {");
            bw.write("public interface " + className + "<T, P> extends BaseMapper{");
            bw.newLine();
            // 获取索引map
            Map<String, List<FieldInfo>> keyIndexList = tableInfo.getKeyIndexList();
            // 调用entrySet方法获取键值对对象
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexList.entrySet()) {
                // 获取对象的value
                List<FieldInfo> fieldInfoList = entry.getValue();

                Integer index = 0;
                StringBuilder methodName = new StringBuilder();

                StringBuilder methodParams = new StringBuilder();
                for (FieldInfo field : fieldInfoList) {
                    index++;
                    methodName.append(StringUtils.uperCaseFirstLetter(field.getPropertyName()));
                    // 判断是否为联合索引，是便加入and
                    if (index < fieldInfoList.size()) {
                        methodName.append("And");
                    }
                    methodParams.append("@Param(" + "\"" + field.getPropertyName() + "\"" + ") " + field.getJavaType() + " " + field.getPropertyName());
                    if (index < fieldInfoList.size()) {
                        methodParams.append(", ");
                    }
                }
                // // 查询方法、注解
                BuilderComment.createMethodComment(bw, "根据" + methodName + "查询");
                bw.write("\tT selectBy" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();

                // 删除方法、注解
                BuilderComment.createMethodComment(bw, "根据" + methodName + "删除");
                bw.write("\tLong deleteBy" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();

                // 更新方法、注解
                BuilderComment.createMethodComment(bw, "根据" + methodName + "更新");
                bw.write("\tLong updateBy" + methodName + "(@Param(\"bean\") T t, " + methodParams + ");");
                bw.newLine();
                bw.newLine();

//                // 插入方法、注解
//                BuilderComment.createMethodComment(bw, "根据" + methodName + "插入");
//                bw.write("\t"+ tableInfo.getBeanName() + " insertBy" + methodName + "();");
//                bw.newLine();
//                bw.newLine();
            }
            bw.newLine();
            bw.write("}");

        } catch (Exception e) {
            logger.error("生成Mapper失败：{}", e.getMessage());
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (outw != null) {
                try {
                    outw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
