package com.chason.base.learning.lms.store.domain;

import lombok.Data;

@Data
public class SetCommand implements Command {

    private String key;
    private String val;

    /**
     * 命令类型
     */
    private CommandTypeEnum type;

    public SetCommand(String key, String val) {
        this.key = key;
        this.val = val;
        this.type = CommandTypeEnum.SET;
    }
}
