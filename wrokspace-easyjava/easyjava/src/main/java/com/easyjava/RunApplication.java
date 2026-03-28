package com.easyjava;

import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.builder.*;
import com.easyjava.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class RunApplication {
        private static final Logger logger = LoggerFactory.getLogger(RunApplication.class);
    public static void main(String[] args) {
        List<TableInfo> tableInfoList = BuilderTable.getTables();
        try {
            BuilderBase.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(TableInfo tableInfo: tableInfoList){
            BuilderPo.execute(tableInfo);
            BuilderQuery.execute(tableInfo);
            BuilderMapper.execute(tableInfo);
            BuilderMapperXml.execute(tableInfo);
            BuilderService.execute(tableInfo);
            BuilderServiceImpl.execute(tableInfo);
            BuilderController.execute(tableInfo);
        }
    }
}
