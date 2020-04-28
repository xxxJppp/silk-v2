package com.spark.bitrade.entity;
//                           _ooOoo_
//                          o8888888o
//                          88" . "88
//                          (| -_- |)
//                          O\  =  /O
//                       ____/`---'\____
//                     .'  \\|     |//  `.
//                    /  \\|||  :  |||//  \
//                   /  _||||| -:- |||||-  \
//                   |   | \\\  -  /// |   |
//                   | \_|  ''\---/''  |   |
//                   \  .-\__  `-`  ___/-. /
//                 ___`. .'  /--.--\  `. . __
//              ."" '<  `.___\_<|>_/___.'  >'"".
//             | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//             \  \ `-.   \_ __\ /__ _/   .-` /  /
//        ======`-.____`-.___\_____/___.-`____.-'======
//                           `=---='
//       ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//                      佛祖保佑, 永无 Bug !
//

import com.spark.bitrade.constant.ExchangeCoinDisplayArea;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: ss
 * @date: 2020/2/27
 */
@Data
public class SymbolConifg {
    @ApiModelProperty(value = "展示区域")
    private ExchangeCoinDisplayArea displayArea;
    @ApiModelProperty(value = "是否隐藏")
    private Integer isShow;
    @ApiModelProperty(value = "交易对")
    private String symbol;
    public SymbolConifg(){}
    public SymbolConifg(ExchangeCoin coin){
        this.displayArea = coin.getDisplayArea();
        this.isShow = coin.getIsShow();
        this.symbol = coin.getSymbol();
    }
}
