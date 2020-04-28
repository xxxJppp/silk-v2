//package com.spark.bitrade.mapper;
//
//import com.spark.bitrade.entity.Member;
//import com.spark.bitrade.mapper.annotation.MybatisRepository;
//import org.apache.ibatis.annotations.Param;
//import org.apache.ibatis.annotations.Select;
//
///**
// * MemberAccountMapper
// *
// * @author archx
// * @since 2019/5/8 18:37
// */
//@MybatisRepository
//public interface MemberAccountMapper {
//
//    @Select("select id, api_key, api_secret, otc_allow, status from member where api_key = #{apiKey}")
//    Member findMemberByApiKey(@Param("apiKey") String apiKey);
//
//    @Select("select id, api_key, api_secret, otc_allow, status from member where id = #{id}")
//    Member findMemberById(@Param("id") Long id);
//}
