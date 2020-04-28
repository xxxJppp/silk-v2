package com.spark.bitrade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.*;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

/**
 * SLP加速释放页面控制层
 *
 * @author zhongxj
 * @since 2019-07-09 14:27:03
 */
@Slf4j
@RestController
@RequestMapping("api/v2/slpAcceleratedRelease")
@Api(description = "SLP购买记录以及加速释放页面控制层")
public class SlpAcceleratedReleaseController extends ApiController {

    /**
     * 本金返还计划,服务对象
     */
    @Autowired
    private LockSlpReleasePlanService lockSlpReleasePlanService;
    /**
     * 本金返还记录，服务对象
     */
    @Autowired
    private LockSlpReleasePlanRecordService lockSlpReleasePlanRecordService;


    /**
     * 会员社区奖励实时统计,服务对象
     */
    @Resource
    private LockSlpMemberSummaryService lockSlpMemberSummaryService;

    /**
     * 更新推荐人实时数据任务,服务对象
     */
    @Resource
    private LockSlpUpdateTaskService lockSlpUpdateTaskService;

    /**
     * 推荐人奖励基金释放记录,服务对象
     */
    @Resource
    private LockSlpReleaseTaskRecordService lockSlpReleaseTaskRecordService;

    /**
     * 获取用户参与记录
     *
     * @param member 会员ID
     * @return 用户参与记录
     * @author liaoqh
     */
    @ApiOperation(value = "获取用户参与记录", notes = "获取用户参与记录")
    @RequestMapping(value = "/findMemberRecord", method = RequestMethod.POST)
    public MessageRespResult<LockSlpMemberRecordVo> findMemberRecord(@ApiIgnore @MemberAccount Member member) {
        return MessageRespResult.success4Data(lockSlpReleasePlanService.findMemberRecordAnlys(member.getId()));
    }

    /**
     * 获取用户参与记录行明细
     *
     * @param member 会员ID
     * @return 用户参与记录
     * @author liaoqh
     */
    @ApiOperation(value = "获取用户参与记录行明细", notes = "获取用户参与记录行明细")
    @RequestMapping(value = "/findMemberRecordLines", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页,每页数量.eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页,当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true)
    })
    public MessageRespResult<IPage<LockSlpMemberRecordDetailVo>> findMemberRecordLines(@ApiIgnore @MemberAccount Member member,
                                                                                       @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                                                       @RequestParam(name = "current", defaultValue = "1") Integer current) {
        return MessageRespResult.success4Data(lockSlpReleasePlanService.findMemberRecordsLine(member.getId(), current, size));
    }

    /**
     * 获取用户当前节点以及分类释放汇总
     *
     * @param member    会员ID
     * @param startTime 查询起始时间
     * @param endTime   查询结束时间
     * @return 用户当前节点以及分类释放汇总
     * @author liaoqh
     */
    @ApiOperation(value = "获取用户当前节点以及分类释放汇总", notes = "获取用户当前节点以及分类释放汇总")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "获取开始时间,时间戳", name = "startTime", dataTypeClass = Long.class),
            @ApiImplicitParam(value = "获取结束时间,时间戳", name = "endTime", dataTypeClass = Long.class),
    })
    @RequestMapping(value = "/findCurrentNode", method = RequestMethod.POST)
    public MessageRespResult<LockSlpCurrentNodeVo> findCurrentNode(@MemberAccount Member member,
                                                                   Long startTime,
                                                                   Long endTime) {
        return MessageRespResult.success4Data(lockSlpMemberSummaryService.findCurrentNode(member.getId(), startTime, endTime));
    }

    /**
     * 获取锁仓汇总
     *
     * @param size      分页.每页数量
     * @param current   分页.当前页码
     * @param member    推荐人ID
     * @param startTime 获取起始时间,时间戳
     * @param endTime   获取结束时间,时间戳
     * @return 锁仓汇总
     * @author zhongxj
     */
    @ApiOperation(value = "锁仓汇总", notes = "锁仓汇总接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页,每页数量.eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页,当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "获取起始时间,时间戳", name = "startTime", dataTypeClass = Long.class),
            @ApiImplicitParam(value = "获取结束时间,时间戳", name = "endTime", dataTypeClass = Long.class)
    })
    @RequestMapping(value = "/lockSummation/list", method = RequestMethod.POST)
    public MessageRespResult<IPage<LockSummationVo>> listLockSummation(@MemberAccount Member member,
                                                                       @RequestParam(value = "current", defaultValue = "1") Integer current,
                                                                       @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                       Long startTime,
                                                                       Long endTime) {
        Long memberId = member.getId();
        log.info("获取锁仓汇总,接收参数:size={},current={},startTime={},endTime={},memberId={}", size, current, startTime, endTime, memberId);
        return success(lockSlpMemberSummaryService.listLockSummation(size, current, memberId, startTime, endTime));
    }

    /**
     * 获取锁仓记录
     *
     * @param size      分页.每页数量
     * @param current   分页.当前页码
     * @param member    会员ID
     * @param startTime 获取起始时间,时间戳
     * @param endTime   获取结束时间,时间戳
     * @return 锁仓记录
     * @author zhongxj
     */
    @ApiOperation(value = "锁仓记录", notes = "锁仓记录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页,每页数量.eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页,当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "获取起始时间,时间戳", name = "startTime", dataTypeClass = Long.class),
            @ApiImplicitParam(value = "获取结束时间,时间戳", name = "endTime", dataTypeClass = Long.class)
    })
    @RequestMapping(value = "/lockRecords/list", method = RequestMethod.POST)
    public MessageRespResult<IPage<LockRecordsVo>> listLockRecords(@MemberAccount Member member,
                                                                   @RequestParam(value = "current", defaultValue = "1") Integer current,
                                                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                   Long startTime,
                                                                   Long endTime) {
        Long memberId = member.getId();
        log.info("获取锁仓记录,接收参数:size={},current={},startTime={},endTime={},memberId={}", size, current, startTime, endTime, memberId);
        return success(lockSlpUpdateTaskService.listLockRecords(size, current, memberId, startTime, endTime));
    }

    /**
     * 获取加速记录
     *
     * @param size      分页.每页数量
     * @param current   分页.当前页码
     * @param member    会员ID
     * @param startTime 获取起始时间,时间戳
     * @param endTime   获取结束时间,时间戳
     * @return 加速记录
     * @author zhongxj
     */
    @ApiOperation(value = "加速记录", notes = "加速记录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页,每页数量.eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页,当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "获取起始时间,时间戳", name = "startTime", dataTypeClass = Long.class),
            @ApiImplicitParam(value = "获取结束时间,时间戳", name = "endTime", dataTypeClass = Long.class)
    })
    @RequestMapping(value = "/accelerationRecords/list", method = RequestMethod.POST)
    public MessageRespResult<IPage<AccelerationRecordsVo>> listAccelerationRecords(@MemberAccount Member member,
                                                                                   @RequestParam(value = "current", defaultValue = "1") Integer current,
                                                                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                                   Long startTime,
                                                                                   Long endTime) {
        Long memberId = member.getId();
        log.info("获取加速记录,接收参数:size={},current={},startTime={},endTime={},memberId={}", size, current, startTime, endTime, memberId);
        return success(lockSlpReleaseTaskRecordService.listAccelerationRecords(size, current, memberId, startTime, endTime));
    }

    /**
     * 获取静态释放记录
     *
     * @param member  会员ID
     * @param current 当前页
     * @param size    条数
     * @return com.spark.bitrade.util.MessageRespResult<com.baomidou.mybatisplus.core.metadata.IPage < com.spark.bitrade.vo.LockSlpPlanRecordsVo>>
     * @author zhangYanjun
     * @time 2019.08.08 18:03
     */
    @ApiOperation(value = "静态释放记录", notes = "静态释放记录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页,每页数量.eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页,当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true)
    })
    @RequestMapping(value = "/accelerationRecords/planRecords", method = RequestMethod.POST)
    public MessageRespResult<IPage<LockSlpPlanRecordsVo>> planRecords(@MemberAccount Member member,
                                                                      @RequestParam(value = "current", defaultValue = "1") Integer current,
                                                                      @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Long memberId = member.getId();
        log.info("获取静态释放记录，memberId-{},current-{},size-{}", memberId, current, size);
        return success(lockSlpReleasePlanRecordService.findMyReleaseRecord(current, size, memberId));
    }
}