package com.example.easyjavadome;

import com.easyjava.RunApplication;
import com.easyjava.entity.po.Users;
import com.easyjava.entity.query.UsersQuery;
import com.easyjava.mapper.UsersMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(classes = RunApplication.class)
public class MapperTest {
    @Resource
    private UsersMapper<Users, UsersQuery> userMapper;
    @Test
    public void selectList() {
//        List<Users> list = userMapper.selectList(new UsersQuery());
//        System.out.println(list);
//        Long count = userMapper.selectCount(new UsersQuery());
//        System.out.println(count);

        UsersQuery query = new UsersQuery();
        query.setCreateTimeStartTime("2023-08-23");
        List<Users> selectList = userMapper.selectList(query);
        System.out.println(selectList);
    }

    @Test
    public void insert(){
        Users users = new Users();
        users.setUsername("三");
        users.setOrderCode("202308231");
        users.setGender("0");
        users.setPosition("程序员");
        users.setPhone("13888888888");
        users.setUpdateTime(new java.util.Date());
        users.setCreateTime(new java.util.Date());
        Long insert = userMapper.insert(users);
        System.out.println(insert);
    }
    @Test
    public void insertOrUpdate(){
        Users users = new Users();
        users.setUsername("小红");
        users.setOrderCode("202323001");
        users.setGender("1");
        users.setPosition("打工把");
        users.setPhone("14888888213");
        users.setUpdateTime(new java.util.Date());
        Long insertOrUpdate = userMapper.insertOrUpdate(users);
        System.out.println(insertOrUpdate);
    }
    @Test
    public void insertBatch(){
        List<Users> list = List.of(
                new Users("小赖", "202308231", "1", "程序员python", "13888832432", new java.util.Date(), new java.util.Date()),
                new Users("测试", "202308232", "1", "程序员java", "13888885342", new java.util.Date(), new java.util.Date())
        );
        Long insertBatch = userMapper.insertBatch(list);
        System.out.println(insertBatch);
    }

    @Test
    public void insertOrUpdateBatch(){
        List<Users> list = List.of(
                new Users("小赖", "202308231", "1", "程序员java", "13888832432", new java.util.Date(), new java.util.Date()),
                new Users("测试", "202308232", "1", "程序员python", "13888885342", new java.util.Date(), new java.util.Date())
        );
        Long insertBatchOrUpdate = userMapper.insertOrUpdateBatch(list);
        System.out.println(insertBatchOrUpdate);
    }
    @Test
    public void updateById(){
        Users users = new Users();
        users.setUsername("小兰");
        Long selectById = userMapper.updateById(users, 1);
        System.out.println(selectById);
    }
    @Test
    public void updateByUsernameAndOrderCode(){
        Users users = new Users();
        users.setPosition("程序员aaa");
        Long selectById = userMapper.updateByUsernameAndOrderCode(users, "小红", "202323001");
        System.out.println(selectById);
    }
    @Test
    public void selectByUsernameAndOrderCode(){
        Users selectByUsernameAndOrderCode = userMapper.selectByUsernameAndOrderCode("小红", "202323001");
        System.out.println(selectByUsernameAndOrderCode);
    }
    @Test
    public void deleteByUsernameAndOrderCode(){

        Long deleteByUsernameAndOrderCode = userMapper.deleteByUsernameAndOrderCode("niu", "12312");
        System.out.println(deleteByUsernameAndOrderCode);
    }

}
