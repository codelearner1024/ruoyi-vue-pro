package cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 多平台订单处理批次 DO
 *
 * @author reprejudicer1024
 */
@TableName("gift_multi_platform_order_batch")
@KeySequence("gift_multi_platform_order_batch_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiPlatformOrderBatchDO extends BaseDO {

    /**
     * 多平台订单文件id
     */
    @TableId
    private Long id;
    /**
     * 平台
     *
     * 枚举 {@link TODO gift_platform_type 对应的类}
     */
    private Integer platformType;
    /**
     * 补充平台名称
     */
    private String platformName;
    /**
     * 店铺名称
     *
     * 枚举 {@link TODO gift_store_name 对应的类}
     */
    private String storeName;
    /**
     * 文件url
     */
    private String fileUrl;
    /**
     * 备注
     */
    private String remark;

}