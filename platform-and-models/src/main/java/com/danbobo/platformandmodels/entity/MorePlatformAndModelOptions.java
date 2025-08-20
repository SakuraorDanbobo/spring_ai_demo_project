package com.danbobo.platformandmodels.entity;

import lombok.Data;

/**
 * @author Danbobo
 * @Description
 * @date 2025/8/20 14:01
 */

@Data
public class MorePlatformAndModelOptions {
    // 平台
    private String platform;

    // 模型
    private String model;

    // 温度值
    private Double temperature;
}
