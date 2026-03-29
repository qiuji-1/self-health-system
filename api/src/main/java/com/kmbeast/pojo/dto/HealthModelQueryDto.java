package com.kmbeast.pojo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 健康模组查询条件类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HealthModelQueryDto extends QueryDto {

    /**
     * 模组名称
     */
    private String name;
    /**
     * 用户ID，外键，关联的是用户表
     */
    private Integer userId;
    /**
     * 是否是全局模组 （0：false;1:true）
     */
    private Boolean isGlobal;

}
