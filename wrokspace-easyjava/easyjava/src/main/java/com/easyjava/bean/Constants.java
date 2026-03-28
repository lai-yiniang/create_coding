package com.easyjava.bean;

import com.easyjava.utils.PropertiesUtils;

public class Constants {
    public static Boolean IGNORE_TABLE_PERFIX;

    public static String PATH_BASE;
    public static String PATH_JAVA = "java";
    public static String PATH_RESOURCES = "resources";
    public static String PATH_UTILS;
    public static String PATH_PO;
    public static String AUTHOR;

    public static String PACKAGE_BASE;
    public static String PACKAGE_PO;

    // Utils包
    public static String PACKAGE_UTILS;
    // 需要忽略的属性
    public static String IQNORE_BEAN_TOJSON_FIELD;
    public static String IQNORE_BEAN_TOJSON_EXPREESION;
    public static String IQNORE_BEAN_TOJSON_CLASS;

    // 日期序列化
    public static String BEAN_DATE_FORMAT_EXPREESION;
    public static String BEAN_DATE_FORMAT_CLASS;
    // 日期反序列化
    public static String BEAN_DATE_UNFORMAT_EXPREESION;
    public static String BEAN_DATE_UNFORMAT_CLASS;
    // enum
    public static String PACKAGE_ENUM;
    public static String PATH_ENUM;
    // query
    public static String PACKAGE_QUERY;
    public static String PATH_QUERY;
    public static String SUFFIX_BEAN_QUERY;
    // 参数模糊查询后缀
    public static String SUFFIX_BEAN_QUERY_FUZZY;
    // 参数日期起至
    public static String SUFFIX_BEAN_QUERY_TIME_START;
    public static String SUFFIX_BEAN_QUERY_TIME_END;
    // mapper
    public static String PACKAGE_MAPPER;
    public static String PATH_MAPPER;
    public static String SUFFIX_MAPPER;
    // MapperXml
    public static String PATH_MAPPER_XML;

    // service
    public static String PACKAGE_SERVICE;
    public static String PATH_SERVICE;
    public static String PACKAGE_SERVICE_IMPL;
    public static String PATH_SERVICE_IMPL;

    // Vo
    public static String PACKAGE_VO;
    public static String PATH_VO;

    // controller
    public static String PACKAGE_CONTROLLER;
    public static String PATH_CONTROLLER;
    // exception
    public static String PACKAGE_EXCEPTION;
    public static String PATH_EXCEPTION;
    static {
        // 需要忽略的属性
        IQNORE_BEAN_TOJSON_FIELD = PropertiesUtils.getString("iqnore.bean.tojson.field");
        IQNORE_BEAN_TOJSON_EXPREESION = PropertiesUtils.getString("iqnore.bean.tojson.expreesion");
        IQNORE_BEAN_TOJSON_CLASS = PropertiesUtils.getString("iqnore.bean.tojson.class");

        // 日期序列化
        BEAN_DATE_FORMAT_EXPREESION = PropertiesUtils.getString("bean.date.format.expreesion");
        BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getString("bean.date.format.class");
        // 日期反序列化
        BEAN_DATE_UNFORMAT_EXPREESION = PropertiesUtils.getString("bean.date.parse.expreesion");
        BEAN_DATE_UNFORMAT_CLASS = PropertiesUtils.getString("bean.date.parse.class");
        // 是否忽略表前缀
        IGNORE_TABLE_PERFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.perfix"));
        // 参数bean后缀
        SUFFIX_BEAN_QUERY = PropertiesUtils.getString("suffix.bean.Query");

        PACKAGE_BASE = PropertiesUtils.getString("package.base");
        // PO 包
        PACKAGE_PO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.po");
        // Utils 包
        PACKAGE_UTILS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.utils");
        PATH_BASE = PropertiesUtils.getString("path.base") + PATH_JAVA + "/" + PACKAGE_BASE;
        PATH_BASE = PATH_BASE.replace(".", "/");

        PATH_PO = PATH_BASE + "/" + PropertiesUtils.getString("package.po").replace(".", "/");
        PATH_UTILS = PATH_BASE + "/" + PropertiesUtils.getString("package.utils").replace(".", "/");
        // 作者名称
        AUTHOR = PropertiesUtils.getString("author.comment");
        // enum
        PACKAGE_ENUM = PACKAGE_BASE + "."+PropertiesUtils.getString("package.enum");
        PATH_ENUM = PATH_BASE + "/" + PropertiesUtils.getString("package.enum").replace(".", "/");
        // query
        PACKAGE_QUERY = PACKAGE_BASE + "."+PropertiesUtils.getString("package.query");
        PATH_QUERY = PATH_BASE + "/" + PropertiesUtils.getString("package.query").replace(".", "/");
        SUFFIX_BEAN_QUERY_FUZZY = PropertiesUtils.getString("suffix.bean.query.fuzzy");
        SUFFIX_BEAN_QUERY_TIME_START = PropertiesUtils.getString("suffix.bean.query.time.start");
        SUFFIX_BEAN_QUERY_TIME_END = PropertiesUtils.getString("suffix.bean.query.time.end");
        // mapper
        PACKAGE_MAPPER = PACKAGE_BASE + "."+PropertiesUtils.getString("package.mapper");
        PATH_MAPPER = PATH_BASE + "/" + PropertiesUtils.getString("package.mapper").replace(".", "/");
        SUFFIX_MAPPER = PropertiesUtils.getString("suffix.mapper");
        // resources
        PATH_RESOURCES = PropertiesUtils.getString("path.base") + PATH_RESOURCES;
        PATH_MAPPER_XML = PATH_RESOURCES + "/" + PropertiesUtils.getString("package.mapper").replace(".", "/");

        //  service
        PACKAGE_SERVICE = PACKAGE_BASE + "."+PropertiesUtils.getString("package.service");
        PATH_SERVICE = PATH_BASE + "/" + PropertiesUtils.getString("package.service").replace(".", "/");
        PACKAGE_SERVICE_IMPL = PACKAGE_BASE + "."+PropertiesUtils.getString("package.service.impl");
        PATH_SERVICE_IMPL = PATH_BASE + "/" + PropertiesUtils.getString("package.service.impl").replace(".", "/");

        // Vo
        PACKAGE_VO = PACKAGE_BASE + "."+PropertiesUtils.getString("package.vo");
        PATH_VO = PATH_BASE + "/" + PropertiesUtils.getString("package.vo").replace(".", "/");

        // controller
        PACKAGE_CONTROLLER = PACKAGE_BASE + "."+PropertiesUtils.getString("package.controller");
        PATH_CONTROLLER = PATH_BASE + "/" + PropertiesUtils.getString("package.controller").replace(".", "/");

        // exception
        PACKAGE_EXCEPTION = PACKAGE_BASE + "."+PropertiesUtils.getString("package.exception");
        PATH_EXCEPTION = PATH_BASE + "/" + PropertiesUtils.getString("package.exception").replace(".", "/");
    }

    // Date
    public static String[] SQL_DATE_TIME_TYPES = new String[]{"datetime", "timestamp"};
    // 日期
    public static String[] SQL_DATE_TYPES = new String[]{"date"};
    // BigDecimal
    public static String[] SQL_DECIMAL_TYPES = new String[]{"decimal", "double", "float"};
    // String
    public static String[] SQL_STRING_TYPES = new String[]{"char", "varchar", "text", "mediumtext", "longtext"};
    // Integer
    public static String[] SQL_INTEGER_TYPES = new String[]{"int", "tinyint"};
    // Long
    public static String[] SQL_LONG_TYPES = new String[]{"bigint"};

    public static void main(String[] args) {
        System.out.println(PATH_SERVICE_IMPL);
        System.out.println(PACKAGE_SERVICE_IMPL);
    }
}
