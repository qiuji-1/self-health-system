package com.kmbeast.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模组统计VO类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthModelCountVO {
    /**
     * 公共模组数
     */
    private Integer globalModelCount;
    /**
     * 私人模组数
     */
    private Integer privateModelCount;
}
