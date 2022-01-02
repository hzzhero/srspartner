package com.he.srs.util;

import com.he.srs.bean.entity.ConfigItem;
import com.he.srs.enums.ConfigItemTypeEnum;

import java.util.List;
import java.util.Optional;

public class ConfigItemParser extends Parser {

    @Override
    public String getText(List<ConfigItem> all, String id) {
        Optional<ConfigItem> any = all.stream().filter(ci -> id.equals(ci.getId())).findAny();
        if (!any.isPresent()) {
            throw new RuntimeException("数据错误，找不到这个配置---> id=" + id);
        }
        ConfigItem c = any.get();
        if (ConfigItemTypeEnum.PARAM.equals(c.getType())) {
            return genParamTxt(c);
        }
        List<ConfigItem> list = getList(all, id);
        StringBuffer sb = new StringBuffer();
        sb.append(genBlockTxt(c));
        for (ConfigItem ci : list){
            if(ConfigItemTypeEnum.PARAM.equals(ci.getType())){
                sb.append("  ").append(genParamTxt(ci)).append("\n");
            }else{
                sb.append("  ").append(this.getText(all,ci.getId()));
            }
        }
        sb.append(BLOCK_TEMPLATE_SUF);
        return sb.toString();
    }
}
