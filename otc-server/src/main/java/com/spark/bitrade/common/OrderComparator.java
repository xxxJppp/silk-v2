package com.spark.bitrade.common;


import com.spark.bitrade.entity.vo.MemberOrderCount;
import com.spark.bitrade.enums.AdvertiseRankType;

import java.util.Comparator;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.08.23 16:35  
 */
public class OrderComparator implements Comparator<MemberOrderCount> {

    private AdvertiseRankType advertiseRankType;

    public OrderComparator(AdvertiseRankType advertiseRankType) {
        this.advertiseRankType = advertiseRankType;
    }

    @Override
    public int compare(MemberOrderCount o1, MemberOrderCount o2) {
        int price = o1.getPrice().compareTo(o2.getPrice());
        int hasT = o2.getHasTrade() - o1.getHasTrade();
        Long tra = o1.getTradingCounts() - o2.getTradingCounts();
        Long tra48 = o1.getCount48() - o2.getCount48();
        int money = o1.getMoney48().compareTo(o2.getMoney48());
        int sort=o2.getSort()-o1.getSort();
        int c;
        if (advertiseRankType == AdvertiseRankType.PRICE) {
            c=price;
        }else {
            c=hasT;
        }
        if(sort==0){
            if (c == 0) {
                if (tra == 0) {
                    if (tra48 == 0) {
                        return money;
                    } else {
                        return tra48.intValue();
                    }
                } else {
                    return tra.intValue();
                }
            } else {
                return c;
            }
        }else {
            return sort;
        }
    }
}
