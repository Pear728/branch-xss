package com.xssdefense.system.model;

/*
 * @Author: Pear
 * @Date: 2025/3/21 12:47
 * @LastEditors: Pear
 * @LastEditTime: 2025/3/21 12:47
 */

import jakarta.persistence.*;
import lombok.Data; /**
 * 防御选项
 */
@Embeddable
@Data
public class DefenseOptions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
