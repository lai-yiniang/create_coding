package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.DateUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BuilderComment {
    // 构建类注释
    public static void createClassComment(BufferedWriter bw, String Comment) throws Exception {
        bw.write("/**");
        bw.newLine();
        bw.write(" * @Description: " + Comment);
        bw.newLine();
        bw.write(" * @Author: " + Constants.AUTHOR);
        bw.newLine();
        bw.write(" * @Date:"+ " " + DateUtils.format(new Date(), DateUtils._PATTEN_DATE));
        bw.newLine();
        bw.write(" */");
        bw.newLine();
    }
    // 构建属性注释
    public static void createFieldComment(BufferedWriter bw, String Comment) throws IOException {
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * " + Comment);
        bw.newLine();
        bw.write("\t */");
        bw.newLine();
    }
    // 构建方法注释
    public static void createMethodComment(BufferedWriter bw,String Comment) throws IOException {
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * " + Comment);
        bw.newLine();
        bw.write("\t */");
        bw.newLine();
    }
}
