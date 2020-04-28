package com.spark.bitrade.controller;


        import java.util.List;

        import com.spark.bitrade.constant.MemberLevelTypeEnum;
        import com.spark.bitrade.entity.Member;
        import com.spark.bitrade.web.bind.annotation.MemberAccount;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.PostMapping;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

        import com.spark.bitrade.biz.IBenefitsSettingService;
        import com.spark.bitrade.util.MessageRespResult;
        import com.spark.bitrade.vo.MemberBenefitsSettingVo;

        import io.swagger.annotations.Api;
        import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 会员权益表 前端控制器
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@RestController
@RequestMapping("api/v2/member/memberBenefitsSetting")
@Api(tags = "会员权益详情前端接口")
@Slf4j
public class MemberBenefitsSettingController extends ApiController {

    @Autowired
    private IBenefitsSettingService benefitsSettingService;


    @ApiOperation(value = "获取会员权益详情接口", notes = "获取会员权益详情接口")
    @PostMapping(value = "/no-auth/list")
    public MessageRespResult<List<MemberBenefitsSettingVo>> findMemberBenefitsSettings() {
        List<MemberBenefitsSettingVo> list = benefitsSettingService.getBenefitsSettings();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLevelId() == MemberLevelTypeEnum.NORMAL.getCode()) {
                list.remove(i);
            }
        }
        list.sort((o1, o2) -> o1.getLevelId() - o2.getLevelId());
        return success(list);
    }

    @PostMapping(value = "/list")
    public MessageRespResult<List<MemberBenefitsSettingVo>> findMyMemberBenefitsSettings(@MemberAccount Member member) {
        List<MemberBenefitsSettingVo> list = benefitsSettingService.getBenefitsSettings();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLevelId() == MemberLevelTypeEnum.NORMAL.getCode()) {
                list.remove(i);
            }
        }
        list.sort((o1, o2) -> o1.getLevelId() - o2.getLevelId());
        return success(list);
    }
}
