package com.spark.bitrade.service.impl;

import com.spark.bitrade.entity.Member;
import com.spark.bitrade.mapper.MemberMapper;
import com.spark.bitrade.service.MemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2020-03-11
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

}
