package com.spark.bitrade.api.controller;

import com.spark.bitrade.api.dto.MinerAssetDTO;
import com.spark.bitrade.api.dto.MinerTransferConfigDTO;
import com.spark.bitrade.api.vo.MinerBalanceTransactionsVO;
import com.spark.bitrade.api.vo.MinerBalanceVO;
import com.spark.bitrade.api.vo.MinerOrderTransactionsVO;
import com.spark.bitrade.api.vo.MinerOrdersVO;
import com.spark.bitrade.biz.MinerService;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.BtBankMinerBalanceTransaction;
import com.spark.bitrade.repository.entity.BtBankMinerOrder;
import com.spark.bitrade.repository.entity.BtBankMinerOrderTransaction;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 矿工资产服务控制器
 *
 * @author davi
 */

@Slf4j
@Api(tags = {"矿工资产控制器 , 主动推送websocket 地址 : /api/v2/miner/webSocket"})
@RequestMapping(path = "api/v2/miner", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class MinerController {


    private MinerService minerService;
    private BtBankConfigService configService;

    @ApiOperation(value = "查询资产", response = MinerAssetDTO.class)
    @GetMapping(value = "queryAsset")
    public MessageRespResult<MinerAssetDTO> queryAsset(@MemberAccount Member member) {
        MinerAssetDTO dto = minerService.queryMinerAsset(member.getId());
        return MessageRespResult.success4Data(dto);
    }

    @ApiOperation(value = "划转资产到矿池")
    @PostMapping(value = "transferAsset")
    public MessageRespResult transferAsset(@RequestParam BigDecimal amount, @MemberAccount Member member) {
        minerService.transferAsset(amount, member.getId());
        return MessageRespResult.success();
    }


    @ApiOperation(value = "查询矿池资金明细", response = BtBankMinerBalanceTransaction.class, responseContainer = "List")
    @PostMapping(value = "minerBalanceTransaction")
    public MessageRespResult getMinerBalanceTransaction(@MemberAccount Member member, @RequestParam(defaultValue = "20", name = "size") int size, @RequestParam(defaultValue = "1", name = "current") int current, @RequestParam(required = false) String types) {

        List<Integer> typeList = new ArrayList<>();
        if (types != null && !StringUtils.isEmpty(types)) {
            for (String s : types.trim().split(",")) {
                try {
                    typeList.add(Integer.valueOf(s));
                } catch (Exception e) {
                    log.info("types 转换出错");
                }
            }
        }
        MinerBalanceTransactionsVO transactions = minerService.getMinerBalanceTransactionsByMemberId(member.getId(), typeList, current, size);

        return MessageRespResult.success("success", transactions);
    }


    @ApiOperation(value = "查询我的订单列表", response = BtBankMinerOrder.class, responseContainer = "List")
    @PostMapping(value = "minerOrders")
    public MessageRespResult getMinerOrdersByMemberId(@MemberAccount Member member, @RequestParam(defaultValue = "20", name = "size") int size, @RequestParam(defaultValue = "1", name = "current") int current, @RequestParam(required = false) String types) {

        List<Integer> typeList = new ArrayList<>();
        if (types != null && !StringUtils.isEmpty(types)) {
            for (String s : types.trim().split(",")) {
                try {
                    typeList.add(Integer.valueOf(s));
                } catch (Exception e) {
                    log.info("types 转换出错");
                }
            }
        }

        MinerOrdersVO minerOrders = minerService.getMyMinerOrdersByMemberId(member.getId(), typeList, current, size);
        //MinerOrdersVO minerOrders = minerService.getMinerOrdersByMemberId(member.getId(), typeList, current, size);
        return MessageRespResult.success("success", minerOrders);
    }


    @ApiOperation(value = "抢单")
    @PostMapping(value = "grabMineOrder")
    public MessageRespResult grabMineOrderByMemberId(@MemberAccount Member member, Long orderId) {


        if (null == orderId) {
            return MessageRespResult.error("orderId 不能为空");
        } else {
            BtBankMinerOrderTransaction orderTransaction = minerService.grabMineOrder(member.getId(), orderId);
            if (orderTransaction != null) {
                return MessageRespResult.success("抢单成功", orderTransaction);
            }
        }

        return MessageRespResult.error("抢单失败");

    }


    @ApiOperation(value = "查询我的帐户信息", response = MinerBalanceVO.class)
    @PostMapping(value = "minerBalance")
    public MessageRespResult getMinerBalance(@MemberAccount Member member) {

        MinerBalanceVO minerBalance = minerService.getMinerBalance(member.getId());

        minerBalance.setTotalRewardSum(BigDecimalUtil.add(minerBalance.getProcessingRewardSum(), minerBalance.getGotRewardSum()));

        return MessageRespResult.success("success", minerBalance);

    }

    @ApiOperation(value = "查询矿池列表", response = BtBankMinerOrder.class, responseContainer = "List")
    @PostMapping(value = "orders")
    public MessageRespResult getOrders(@MemberAccount Member member, @RequestParam(defaultValue = "20", name = "size") int size, @RequestParam(defaultValue = "1", name = "current") int current) {

        MinerOrdersVO minerOrders = minerService.getMinerOrders(current, size);
        return MessageRespResult.success("success", minerOrders);

    }

    @ApiOperation(value = "查询订单记录", response = BtBankMinerOrderTransaction.class, responseContainer = "List")
    @PostMapping(value = "orderTransactions")
    public MessageRespResult getOrderTransactions(@MemberAccount Member member, @RequestParam(defaultValue = "1") String types, @RequestParam(defaultValue = "20", name = "size") int size, @RequestParam(defaultValue = "1", name = "current") int current) {

        List<Integer> typeList = new ArrayList<>();
        if (types != null && !StringUtils.isEmpty(types)) {
            for (String s : types.trim().split(",")) {
                try {
                    typeList.add(Integer.valueOf(s));
                } catch (Exception e) {
                    log.info("types 转换出错");
                }
            }
        }
        MinerOrderTransactionsVO minerOrderTransactions = minerService.getMinerOrderTransactionsByMemberId(member.getId(), typeList, current, size);
        return MessageRespResult.success("success", minerOrderTransactions);

    }

    @ApiOperation(value = "查询最低划转资金")
    @GetMapping(value = "config")
    public MessageRespResult<MinerTransferConfigDTO> getConfig() {
        String config = (String) configService.getConfig(BtBankSystemConfig.MINIMUM_TRANSFER_AMOUNT);
        String secKillCommissionRate = (String) configService.getConfig(BtBankSystemConfig.SECKILL_COMMISSION_RATE);
        String dispatchCommissionRate = (String) configService.getConfig(BtBankSystemConfig.DISPATCH_COMMISSION_RATE);
        String fixedCommissionRate = (String) configService.getConfig(BtBankSystemConfig.FIXED_COMMISSION_RATE);
        String autoRefreshRate = (String) configService.getConfig(BtBankSystemConfig.AUTO_REFRESH_RATE);

        MinerTransferConfigDTO dto = new MinerTransferConfigDTO();
        dto.setMinimum(BigDecimal.ZERO);
        dto.setSecKillCommissionRate(BigDecimal.ZERO);
        dto.setSecKillCommissionRate(BigDecimal.ZERO);
        dto.setFixedCommissionRate(BigDecimal.ZERO);

        if (config != null) {
            dto.setMinimum(new BigDecimal(config));
        }

        if (secKillCommissionRate != null) {
            dto.setSecKillCommissionRate(new BigDecimal(secKillCommissionRate));
        }

        if (dispatchCommissionRate != null) {
            dto.setDispatchCommissionRate(new BigDecimal(dispatchCommissionRate));
        }

        if (fixedCommissionRate != null) {
            dto.setFixedCommissionRate(new BigDecimal(fixedCommissionRate));
        }

        if (autoRefreshRate != null) {
            dto.setAutoRefreshRate(Long.valueOf(autoRefreshRate));
        }

        return MessageRespResult.success4Data(dto);
    }

}
