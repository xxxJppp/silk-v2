package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.AppItem;
import com.spark.bitrade.entity.SilkPayApp;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 应用管理(SilkPayApp)表数据库访问层
 *
 * @author wsy
 * @since 2019-07-19 16:28:05
 */
public interface SilkPayAppMapper extends BaseMapper<SilkPayApp> {

    @Select("select id appId,package_name,app_label,version_name,version_code,app_size,app_file,remark,app_icon iconFile from silk_pay_app")
    List<AppItem> getAppItems();

}