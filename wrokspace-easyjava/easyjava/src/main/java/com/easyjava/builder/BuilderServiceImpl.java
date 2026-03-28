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

public class BuilderServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(BuilderServiceImpl.class);
    public static void execute(TableInfo tableInfo){
        // 判断目录是否存在
        File folder = new File(Constants.PATH_SERVICE_IMPL);
        // 如果不存在则创建
        if (!folder.exists()){
            folder.mkdirs();
        }
        // 创建文件
        File poFile = new File(folder, tableInfo.getBeanName()+"ServiceImpl.java");
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

            bw.write("package "+Constants.PACKAGE_SERVICE_IMPL+";");
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

            bw.write("import "+Constants.PACKAGE_SERVICE+"." + tableInfo.getBeanName() +"Service;");
            bw.newLine();

            bw.write("import org.springframework.beans.factory.annotation.Autowired;");
            bw.newLine();

            String mapperClassName = tableInfo.getBeanName() + "Mapper";
            bw.write("import "+Constants.PACKAGE_MAPPER+"." + mapperClassName + ";");
            bw.newLine();

            bw.write("import org.springframework.stereotype.Service;");
            bw.newLine();

            bw.write("import " + Constants.PACKAGE_ENUM + ".PageSize;");
            bw.newLine();

            bw.write("import " + Constants.PACKAGE_QUERY + ".SimplePage;");
            bw.newLine();
            String className = tableInfo.getBeanName() + "ServiceImpl";


            BuilderComment.createClassComment(bw, className);
            bw.write("@Service(\"" + tableInfo.getBeanName() + "Service\")");
            bw.newLine();
            bw.write("public class " + className +" implements " + tableInfo.getBeanName() +"Service {");
            bw.newLine();
            bw.write("\t@Autowired");

            bw.newLine();
            bw.write("\tprivate " + mapperClassName + "<" + tableInfo.getBeanName() + ", " + tableInfo.getBeanParamName() + "> " + StringUtils.lowerCaseFirstLetter(mapperClassName) + ";");
            bw.newLine();
            bw.newLine();
            bw.newLine();


            BuilderComment.createMethodComment(bw, "根据条件查询列表");
            bw.write("\tpublic List<"+tableInfo.getBeanName()+"> queryList("+tableInfo.getBeanParamName()+" query){");
            bw.newLine();
            bw.write("\t\treturn " + StringUtils.lowerCaseFirstLetter(mapperClassName) + ".selectList(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();


            BuilderComment.createMethodComment(bw, "根据条件查询数量");
            bw.write("\tpublic Long queryCount("+tableInfo.getBeanParamName()+" query){");
            bw.newLine();
            bw.write("\t\treturn " + StringUtils.lowerCaseFirstLetter(mapperClassName) + ".selectCount(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();


            BuilderComment.createMethodComment(bw, "分页查询");
            bw.write("\tpublic PageResultVO<"+tableInfo.getBeanName()+"> queryPage("+tableInfo.getBeanParamName()+" query){");
            bw.newLine();
            bw.write("\t\tInteger count = Math.toIntExact(" + StringUtils.lowerCaseFirstLetter(mapperClassName) + ".selectCount(query));");
            bw.newLine();
            bw.write("\t\tInteger pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();");
            bw.newLine();
            bw.write("\t\tSimplePage page = new SimplePage(query.getPageNo(), count, pageSize);");
            bw.newLine();
            bw.write("\t\tList<"+tableInfo.getBeanName()+"> list = " + StringUtils.lowerCaseFirstLetter(mapperClassName) + ".selectList(query);");
            bw.newLine();
            bw.write("\t\tPageResultVO<" + tableInfo.getBeanName() + "> result = new PageResultVO(count, page.getPageNo(), page.getPageSize(), page.getPageTotal(), list);");
            bw.newLine();
            bw.write("\t\treturn result;");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();


            BuilderComment.createMethodComment(bw, "新增");
            bw.write("\tpublic Long insert("+tableInfo.getBeanName()+" bean){");
            bw.newLine();
            bw.write("\t\treturn " + StringUtils.lowerCaseFirstLetter(mapperClassName) + ".insert(bean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();


            BuilderComment.createMethodComment(bw, "批量新增");
            bw.write("\tpublic Long insertBatch(List<"+tableInfo.getBeanName()+"> list){");
            bw.newLine();
            bw.write("\t\treturn " + StringUtils.lowerCaseFirstLetter(mapperClassName) + ".insertBatch(list);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();


            BuilderComment.createMethodComment(bw, "批量新增或修改");
            bw.write("\tpublic Long insertOrUpdateBatch(List<"+tableInfo.getBeanName()+"> list){");
            bw.newLine();
            bw.write("\t\treturn " + StringUtils.lowerCaseFirstLetter(mapperClassName) + ".insertOrUpdateBatch(list);");
            bw.write("\t}");
            bw.newLine();


            for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexList().entrySet()) {
                // 获取对象的value
                List<FieldInfo> fieldInfoList = entry.getValue();

                Integer index = 0;
                StringBuilder methodName = new StringBuilder();

                StringBuilder methodParams = new StringBuilder();
                StringBuilder methodFields = new StringBuilder();
                for (FieldInfo field : fieldInfoList) {
                    index++;
                    methodName.append(StringUtils.uperCaseFirstLetter(field.getPropertyName()));
                    // 判断是否为联合索引，是便加入and
                    if (index < fieldInfoList.size()) {
                        methodName.append("And");
                    }
                    methodParams.append(field.getJavaType() + " " + field.getPropertyName());
                    methodFields.append(field.getPropertyName());
                    if (index < fieldInfoList.size()) {
                        methodParams.append(", ");
                        methodFields.append(", ");
                    }
                }
                // // 查询方法、注解
                BuilderComment.createMethodComment(bw, "根据" + methodName + "查询");
                bw.write("\tpublic "+ tableInfo.getBeanName() + " selectBy" + methodName + "(" + methodParams + "){");
                bw.newLine();
                bw.write("\t\treturn " + StringUtils.lowerCaseFirstLetter(mapperClassName) + ".selectBy" + methodName + "(" + methodFields + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                // 删除方法、注解
                BuilderComment.createMethodComment(bw, "根据" + methodName + "删除");
                bw.write("\tpublic Long deleteBy" + methodName + "(" + methodParams + "){");
                bw.newLine();
                bw.write("\t\treturn " + StringUtils.lowerCaseFirstLetter(mapperClassName) + ".deleteBy" + methodName + "(" + methodFields + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                // 更新方法、注解
                BuilderComment.createMethodComment(bw, "根据" + methodName + "更新");
                bw.write("\tpublic Long updateBy" + methodName + "("+ tableInfo.getBeanName() + " bean," + methodParams + "){");
                bw.newLine();
                bw.write("\t\treturn " + StringUtils.lowerCaseFirstLetter(mapperClassName) + ".updateBy" + methodName + "(bean, " + methodFields  + ");");
                bw.newLine();
                bw.write("\t}");
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
