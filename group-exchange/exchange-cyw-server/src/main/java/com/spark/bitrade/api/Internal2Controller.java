package com.spark.bitrade.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.api.client.TransferClient;
import com.spark.bitrade.api.vo.PageResultVo;
import com.spark.bitrade.api.vo.TransferDirectVo;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.ExchangeCywMsgCode;
import com.spark.bitrade.controller.ApiController;
import com.spark.bitrade.entity.CywWallet;
import com.spark.bitrade.entity.CywWalletWalRecord;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.constants.CywProcessStatus;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.service.CywOrderService;
import com.spark.bitrade.service.CywWalletService;
import com.spark.bitrade.service.CywWalletWalRecordService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 内部接口，专供后台
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/10 14:41
 */
@RestController
@RequestMapping("/internal/v2")
public class Internal2Controller extends ApiController {

    private CywWalletService walletService;
    private CywWalletWalRecordService walRecordService;
    private TransferClient transferClient;
    private CywOrderService orderService;

    /**
     * 查看钱包列表
     *
     * @param pageNo   页码
     * @param size     每页显示数量
     * @param memberId 会员id
     * @return list
     */
    @GetMapping("/wallets")
    public MessageRespResult<PageResultVo<CywWallet>> wallet(@RequestParam("page") int pageNo, @RequestParam("size") int size,
                                                             @RequestParam("memberId") Long memberId) {
        QueryWrapper<CywWallet> query = new QueryWrapper<>();

        if (memberId != null) {
            query.eq("member_id", memberId);
        }
        IPage<CywWallet> page = walletService.page(new Page<>(pageNo, size), query);

        return success(toPageResultVo(pageNo, size, page));
    }

    /**
     * 创建钱包
     *
     * @param memberId 会员id
     * @param coinUnit 币种
     * @return wallet
     */
    @PostMapping("/create/wallet")
    public MessageRespResult<CywWallet> create(@RequestParam("memberId") Long memberId,
                                               @RequestParam("coinUnit") String coinUnit) {
        Optional<CywWallet> optional = walletService.findOne(memberId, coinUnit);

        if (optional.isPresent()) {
            return success(optional.get());
        }

        if (walletService.create(memberId, coinUnit)) {
            return success(walletService.getById(memberId + ":" + coinUnit));
        }
        return failed(CommonMsgCode.FAILURE);
    }

    /**
     * 查看资金流水列表
     *
     * @param pageNo   页码
     * @param size     每页显示数量
     * @param memberId 会员id
     * @param coinUnit 币种
     * @param type     交易类型
     * @param status   流水状态
     * @return list
     */
    @GetMapping("/wal/records")
    public MessageRespResult<PageResultVo<CywWalletWalRecord>> wal(@RequestParam("page") int pageNo, @RequestParam("size") int size,
                                                                   @RequestParam(value = "memberId", required = false) Long memberId,
                                                                   @RequestParam(value = "coinUnit", required = false) String coinUnit,
                                                                   @RequestParam(value = "type", required = false) WalTradeType type,
                                                                   @RequestParam(value = "status", required = false) CywProcessStatus status) {
        QueryWrapper<CywWalletWalRecord> query = new QueryWrapper<>();
//        query.eq("member_id", memberId).eq("coin_unit", coinUnit);

        if (memberId != null) {
            query.eq("member_id", memberId);
        }

        if (coinUnit != null && !coinUnit.isEmpty()) {
            query.eq("coin_unit", coinUnit);
        }


        if (type != null) {
            query.eq("trade_type", type);
        }

        if (status != null) {
            query.eq("status", status);
        }
        query.orderByDesc("create_time");
        IPage<CywWalletWalRecord> page = walRecordService.page(new Page<>(pageNo, size), query);

        return success(toPageResultVo(pageNo, size, page));
    }

    /**
     * 转账接口
     *
     * @param memberId  会员ID
     * @param coinUnit  币种
     * @param amount    数量
     * @param direction 方向
     * @return record
     */
    @PostMapping("/transfer")
    public MessageRespResult<CywWalletWalRecord> transfer(@RequestParam("memberId") Long memberId,
                                                          @RequestParam("coinUnit") String coinUnit,
                                                          @RequestParam("amount") BigDecimal amount,
                                                          @RequestParam("direction") TransferDirectVo direction) {
        // 必须时正数
        AssertUtil.isTrue(BigDecimalUtil.gt0(amount), ExchangeCywMsgCode.ILLEGAL_TRANS_AMOUNT);

        return success(transferClient.transfer(memberId, coinUnit, amount, direction));
    }

    /**
     * 查看历史订单
     *
     * @param pageNo   页码
     * @param size     每页显示条数
     * @param memberId 会员id
     * @param symbol   交易对
     * @return
     * @author zhangYanjun
     * @time 2019.09.19 10:26
     */
    @GetMapping("/historyOrders")
    public MessageRespResult<PageResultVo<ExchangeOrder>> historyOrders(@RequestParam("page") int pageNo, @RequestParam("size") int size,
                                                                        @RequestParam(value = "memberId") Long memberId,
                                                                        @RequestParam(value = "symbol") String symbol) {
        IPage<ExchangeOrder> page = orderService.historyOrders(symbol, memberId, size, pageNo);
        return success(toPageResultVo(pageNo, size, page));
    }



    /**
     * 转换为后台接收格式
     *
     * @param pageNo 页码
     * @param size   每页显示数量
     * @param page   查询结果
     * @param <T>    泛型
     * @return vo
     */
    private <T> PageResultVo<T> toPageResultVo(int pageNo, int size, IPage<T> page) {
        return PageResultVo.<T>builder()
                .total(page.getTotal())
                .rows(page.getRecords())
                .hasNext(page.getCurrent() < page.getPages())
                .pageNo(pageNo)
                .pageSize(size).build();
    }


    @Autowired
    public void setWalletService(CywWalletService walletService) {
        this.walletService = walletService;
    }

    @Autowired
    public void setWalRecordService(CywWalletWalRecordService walRecordService) {
        this.walRecordService = walRecordService;
    }

    @Autowired
    public void setTransferClient(TransferClient transferClient) {
        this.transferClient = transferClient;
    }

    @Autowired
    public void setOrderService(CywOrderService orderService) {
        this.orderService = orderService;
    }
}
