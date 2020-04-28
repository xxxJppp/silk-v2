package com.spark.bitrade.common.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spark.bitrade.entity.RiskDetailed;

import lombok.Data;

@Data
public class RiskDetailedCacheVo {

    /**
     * 用户编号
     */
    private Long memberId;

    /**
     * 出入类型0出1入
     */
    private String inOut;

    /**
     * 出入分类备注
     */
    private String typeDesc;

    /**
     * 出入币种
     */
    private String unti;

    /**
     * 币种数量
     */
    private BigDecimal amount;

    /**
     * 汇率转换币种
     */
    private String exchangeSource;

    /**
     * 计算汇率
     */
    private BigDecimal exchange;

    /**
     * 转换后金额
     */
    private BigDecimal convertAmount;

    /**
     * 备注
     */
    private String detailedDesc;

    /**
     * 出入时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 唯一id
     */
    private Long rfId;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 摘要签名
     */
    private String abstractKey;
    
    /**
     * 处理次数
     */
    private int workNumber;
    
    private boolean check; //是否需要再次计算
    
    public RiskDetailedCacheVo() {}
    
    public RiskDetailed toRiskDetailed() {
    	RiskDetailed rd = new RiskDetailed();
		rd.setAmount(getAmount());
		rd.setConvertAmount(getConvertAmount());
		rd.setCreateTime(getCreateTime());
		rd.setDetailedDesc(getDetailedDesc());
		rd.setExchange(getExchange());
		rd.setExchangeSource(getExchangeSource());
		rd.setInOut(getInOut());
		rd.setMemberId(getMemberId());
		rd.setMessageId(getMessageId());
		rd.setRfId(getRfId());
		rd.setTypeDesc(getTypeDesc());
		rd.setUnti(getUnti());
		rd.setAbstractKey(getAbstractKey());
		return rd;
    }
    
    public void setAmount(BigDecimal amount) {
    	if(amount.compareTo(BigDecimal.ZERO) == -1) {
    		amount = amount.multiply(new BigDecimal(-1));
    	}
    	this.amount = amount;
    }
    
    public String toJson() {
    	return JSON.toJSONString(this);
    }
}
