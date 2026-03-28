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
import java.util.Map;

public class BuilderService {
    private static final Logger logger = LoggerFactory.getLogger(BuilderService.class);
    public static void execute(TableInfo tableInfo){
        // 判断目录是否存在
        File folder = new File(Constants.PATH_SERVICE);
        // 如果不存在则创建
        if (!folder.exists()){
            folder.mkdirs();
        }
        // 创建文件
        File poFile = new File(folder, tableInfo.getBeanName()+"Service.java");
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
            bw.write("package "+Constants.PACKAGE_SERVICE+";");
            bw.newLine();
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.write("import "+Constants.PACKAGE_PO+"." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import "+Constants.PACKAGE_QUERY+"." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import "+Constants.PACKAGE_VO+"." + "PageResultVO;");
            bw.newLine();
            String className = tableInfo.getBeanName() + "Service";
            BuilderComment.createClassComment(bw, className);
            bw.write("public interface " + className +"{");
            bw.newLine();
            bw.newLine();
            BuilderComment.createMethodComment(bw, "根据条件查询列表");
            bw.write("\tList<"+tableInfo.getBeanName()+"> queryList("+tableInfo.getBeanParamName()+" param);");
            bw.newLine();
            BuilderComment.createMethodComment(bw, "根据条件查询数量");
            bw.write("\tLong queryCount("+tableInfo.getBeanParamName()+" param);");
            bw.newLine();
            BuilderComment.createMethodComment(bw, "分页查询");
            bw.write("\tPageResultVO<"+tableInfo.getBeanName()+"> queryPage("+tableInfo.getBeanParamName()+" param);");
            bw.newLine();
            BuilderComment.createMethodComment(bw, "新增");
            bw.write("\tLong insert("+tableInfo.getBeanName()+" bean);");
            bw.newLine();
            BuilderComment.createMethodComment(bw, "批量新增");
            bw.write("\tLong insertBatch(List<"+tableInfo.getBeanName()+"> list);");
            bw.newLine();
            BuilderComment.createMethodComment(bw, "批量新增或修改");
            bw.write("\tLong insertOrUpdateBatch(List<"+tableInfo.getBeanName()+"> list);");
            bw.newLine();


            for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexList().entrySet()) {
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
                    methodParams.append(field.getJavaType() + " " + field.getPropertyName());
                    if (index < fieldInfoList.size()) {
                        methodParams.append(", ");
                    }
                }
                // // 查询方法、注解
                BuilderComment.createMethodComment(bw, "根据" + methodName + "查询");
                bw.write("\t"+ tableInfo.getBeanName() + " selectBy" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();

                // 删除方法、注解
                BuilderComment.createMethodComment(bw, "根据" + methodName + "删除");
                bw.write("\tLong deleteBy" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();

                // 更新方法、注解
                BuilderComment.createMethodComment(bw, "根据" + methodName + "更新");
                bw.write("\tLong updateBy" + methodName + "("+ tableInfo.getBeanName() + " bean," + methodParams + ");");
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
            bw.flush();
        }catch (Exception e){
            logger.error("生成Service失败：{}",e.getMessage());
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
