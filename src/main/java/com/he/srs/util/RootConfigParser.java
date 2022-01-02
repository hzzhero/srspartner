package com.he.srs.util;

import com.he.srs.bean.entity.ConfigItem;
import com.he.srs.enums.ConfigItemTypeEnum;

import java.util.List;

public class RootConfigParser extends Parser{

    @Override
    public String getText(List<ConfigItem> all, String id) {
        ConfigItem c = new ConfigItem();
        List<ConfigItem> list = getList(all, id);
        StringBuffer sb = new StringBuffer();
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
