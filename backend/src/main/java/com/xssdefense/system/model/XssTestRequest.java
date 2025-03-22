package com.xssdefense.system.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class XssTestRequest {
    @Id
    private Long id;

    /**
     * 用户输入的内容
     */
    private String input;

    /**
     * 防御选项配置
     */
    @Embedded
    private DefenseOptions defenseOptions;

    /**
     * 防御选项
     */
    @Data
    @Embeddable
    public static class DefenseOptions extends com.xssdefense.system.model.DefenseOptions {
        /**
         * 是否启用HTML转义
         */
        private boolean htmlEscape = true;

        /**
         * 是否移除JavaScript事件
         */
        private boolean removeJsEvents = true;

        /**
         * 是否移除危险标签
         */
        private boolean removeDangerousTags = true;

        /**
         * 是否启用CSP头
         */
        private boolean enableCsp = true;
    }
}
