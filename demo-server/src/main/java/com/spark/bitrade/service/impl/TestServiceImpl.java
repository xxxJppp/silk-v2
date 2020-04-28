package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.TestMapper;
import com.spark.bitrade.entity.Test;
import com.spark.bitrade.service.TestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * (Test)表服务实现类
 *
 * @author young
 * @since 2019-06-09 15:56:37
 */
@Service("testService")
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements TestService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean testTransaction() {
        //备注：测试结果为事务正常回滚，未插入到数据库
        Test test = new Test();
        test.setCol1(1000);
        test.setCol2("test1000");

        //保存数据
        this.baseMapper.insert(test);

        //事务测试，模拟异常
        int i = 1 / 0;

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) //注意：需要添加事务注解，否则异常后 不会回滚
    public boolean testTransaction2() {
        //模拟事务中调用 同一类中的多个方法
        //备注：测试结果为事务正常回滚，未插入到数据库
        this.saveTest20000();
        this.saveTest2000X();

        //事务测试，模拟异常
        int i = 1 / 0;

        return true;
    }

    //保存数据
    //@Transactional(rollbackFor = Exception.class) //添加与不添加事务都正常回滚了
    public void saveTest20000(){
        Test test = new Test();
        test.setCol1(20000);
        test.setCol2("test20000");

        //保存数据
        this.baseMapper.insert(test);
    }

    //保存数据
     public void saveTest2000X(){ //public 事务正常
    //private void saveTest2000X(){   //private 事务正常
        Test test = new Test();
        test.setCol1(20001);
        test.setCol2("test20001");

        //保存数据
        this.baseMapper.insert(test);

        Test test2 = new Test();
        test2.setCol1(20002);
        test2.setCol2("test20002");

        //保存数据
        this.baseMapper.insert(test2);
    }
}