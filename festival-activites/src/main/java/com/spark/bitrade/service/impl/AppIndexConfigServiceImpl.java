package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.AppIndexConfig;
import com.spark.bitrade.mapper.AppIndexConfigMapper;
import com.spark.bitrade.service.AppIndexConfigService;
import com.spark.bitrade.vo.AppIndexConfigVo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * app首页快捷入口 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2020-01-02
 */
@Service
public class AppIndexConfigServiceImpl extends ServiceImpl<AppIndexConfigMapper, AppIndexConfig> implements AppIndexConfigService {

    @Override
    @Cacheable(cacheNames = "appIndex:languages",key = "'appIndex:language:'+#language")
    public List<AppIndexConfigVo> appIndexList(String language) {
        QueryWrapper<AppIndexConfig> aq=new QueryWrapper<>();
        aq.lambda().orderByDesc(AppIndexConfig::getSorts);

        List<AppIndexConfig> configs = this.list(aq);
        List<AppIndexConfigVo> res=new ArrayList<>();
        AppIndexConfigVo vo;
        for (AppIndexConfig c:configs){
            vo=new AppIndexConfigVo();
            switch (language){
                case "zh_CN":
                    vo.setTitle(c.getTitleCn());
                    vo.setDesc(c.getDescCn());
                    break;
                case "en_US":
                    vo.setTitle(c.getTitleUs());
                    vo.setDesc(c.getDescUs());
                    break;
                case "zh_HK":
                    vo.setTitle(c.getTitleHk());
                    vo.setDesc(c.getDescHk());
                    break;
                case "ko_KR":
                    vo.setTitle(c.getTitleKr());
                    vo.setDesc(c.getDescKr());
                    break;
                default:
                    break;
            }
            vo.setCreateTime(c.getCreateTime());
            vo.setLink(c.getLink());
            vo.setSorts(c.getSorts());
            vo.setUpdateTime(c.getUpdateTime());
            vo.setId(c.getId());
            vo.setIcon(c.getIcon());
            vo.setIsLogin(c.getIsLogin());
            vo.setLinkType(c.getLinkType());
            res.add(vo);
        }
        return res;
    }
}
