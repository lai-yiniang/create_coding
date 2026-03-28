package com.easyjava.builder;

import com.easyjava.bean.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
// 生成基础类
public class BuilderBase {
    private static final Logger logger = LoggerFactory.getLogger(BuilderBase.class);
    public static void execute() throws IOException {
        // 导入包名
        List<String> headerInfoList = new ArrayList<>();

        // 生成DateeNum
        headerInfoList.add("package " + Constants.PACKAGE_ENUM+";\n");
        builder(headerInfoList, "DateTimePatternEnum", Constants.PATH_ENUM);
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_UTILS + ";\n");
        builder(headerInfoList,"DateUtils",Constants.PATH_UTILS);
        // 生成BaseMapper
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_MAPPER + ";\n");
        builder(headerInfoList,"BaseMapper",Constants.PATH_MAPPER);
        // 生成PageSize
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_ENUM+";\n");
        builder(headerInfoList, "PageSize", Constants.PATH_ENUM);
        headerInfoList.clear();
        // SimplePage
        headerInfoList.add("package " + Constants.PACKAGE_QUERY+";\n");
        headerInfoList.add("import " + Constants.PACKAGE_ENUM + ".PageSize;\n");
        builder(headerInfoList, "SimplePage", Constants.PATH_QUERY);
        headerInfoList.clear();
        // BaseQuery
        headerInfoList.add("package " + Constants.PACKAGE_QUERY+";\n");
        builder(headerInfoList, "BaseQuery", Constants.PATH_QUERY);
        headerInfoList.clear();
        // pageResultVO
        headerInfoList.add("package " + Constants.PACKAGE_VO+";\n");
        builder(headerInfoList, "PageResultVO", Constants.PATH_VO);
        headerInfoList.clear();
        // BusinessException
        headerInfoList.add("package " + Constants.PACKAGE_EXCEPTION+";\n");
        builder(headerInfoList, "BusinessException", Constants.PATH_EXCEPTION);
        headerInfoList.clear();
        // AGlobalExceptionHandlerController
        headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER+";\n");
        builder(headerInfoList, "AGlobalExceptionHandlerController", Constants.PATH_CONTROLLER);
        headerInfoList.clear();
        // ABaseController
        headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER+";\n");
        builder(headerInfoList, "ABaseController", Constants.PATH_CONTROLLER);
        headerInfoList.clear();
        // ResponseCodeEnum
        headerInfoList.add("package " + Constants.PACKAGE_ENUM+";\n");
        builder(headerInfoList, "ResponseCodeEnum", Constants.PATH_ENUM);
        headerInfoList.clear();
        // ResponseVO
        headerInfoList.add("package " + Constants.PACKAGE_VO+";\n");
        builder(headerInfoList, "ResponseVO", Constants.PATH_VO);
        headerInfoList.clear();

    }
    private static void builder(List<String> headerInfoList, String filename, String outputPath) throws IOException {
        File floder = new File(outputPath);
        if(!floder.exists()){
            floder.mkdirs();
        }
        File javeFile = new File(outputPath, filename+".java");
        // 写
        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;

        // 读
        InputStream in = null;
        InputStreamReader inr = null;
        BufferedReader br = null;
        try{
            out = new FileOutputStream(javeFile);
            outw = new OutputStreamWriter(out, "utf-8");
            bw = new BufferedWriter(outw);

            String templatePath = BuilderBase.class.getClassLoader().getResource("template/"+filename+".txt").getPath();
            in = new FileInputStream(templatePath);
            inr = new InputStreamReader(in, "utf-8");
            br = new BufferedReader(inr);
            // 导包
            for (String headerInfo:headerInfoList){
                bw.write(headerInfo);
                bw.newLine();
            }

            String lineInfo =null;
            while((lineInfo = br.readLine()) != null){
                bw.write(lineInfo);
                bw.newLine();
            }
            bw.flush();
        }catch (Exception e){
            logger.error("生成基础类：{}，失败",filename, e);
        }finally {
            if(bw != null){
                bw.close();
            }
            if(outw != null){
                outw.close();
            }
            if(out != null){
                out.close();
            }
            if(br != null){
                br.close();
            }
            if(inr != null){
                inr.close();
            }
            if(in != null){
                in.close();
            }
        }
    }
}
