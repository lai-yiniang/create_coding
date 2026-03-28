package com.easyjava.bean;

public class FieldInfo {
    /**
     * 字段名称
     */
    private String FieldName;
    /**
     * bean属性名称
     */
    private String propertyName;
    private String sqlType;
    /**
     * 字段类型
     */
    private String javaType;
    /**
     * 字段注释
     */
    private String comment;
    /**
     * 是否有自增
     */
    private boolean isAutoIncrement;

    public String getFieldName() {
        return FieldName;
    }

    public void setFieldName(String fieldName) {
        FieldName = fieldName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    public void set(String s) {
    }
}
