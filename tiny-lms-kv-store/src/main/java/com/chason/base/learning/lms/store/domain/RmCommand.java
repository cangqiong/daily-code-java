package com.chason.base.learning.lms.store.domain;

import lombok.Data;

@Data
public class RmCommand implements Command {

    private String key;

    /**
     * 命令类型
     */
    private CommandTypeEnum type;

    public RmCommand(String key) {
        this.key = key;
        this.type = CommandTypeEnum.RM;
    }
}
