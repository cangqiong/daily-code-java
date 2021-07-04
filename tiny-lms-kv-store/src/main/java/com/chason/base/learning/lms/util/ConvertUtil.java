package com.chason.base.learning.lms.util;

import cn.hutool.json.JSONObject;
import com.chason.base.learning.lms.store.conf.LmsKvStoreConstants;
import com.chason.base.learning.lms.store.domain.Command;
import com.chason.base.learning.lms.store.domain.CommandTypeEnum;
import com.chason.base.learning.lms.store.domain.RmCommand;
import com.chason.base.learning.lms.store.domain.SetCommand;

public class ConvertUtil {

    public static Command jsonToCommand(JSONObject value) {
        if (value.getStr(LmsKvStoreConstants.TYPE).equals(CommandTypeEnum.SET.name())) {
            return value.toBean(SetCommand.class);
        } else if (value.getStr(LmsKvStoreConstants.TYPE).equals(CommandTypeEnum.RM.name())) {
            return value.toBean(RmCommand.class);
        }
        return null;
    }
}
