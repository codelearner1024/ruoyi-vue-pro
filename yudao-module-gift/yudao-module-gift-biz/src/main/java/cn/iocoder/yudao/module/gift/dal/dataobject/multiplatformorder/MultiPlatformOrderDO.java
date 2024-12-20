package cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 多平台订单 DO
 *
 * @author reprejudicer1024
 */
@TableName("gift_multi_platform_order")
@KeySequence("gift_multi_platform_order_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiPlatformOrderDO extends BaseDO {

    /**
     * 多平台订单id
     */
    @TableId
    private Long id;
    /**
     * 订单处理批次号
     */
    private Long orderBatchId;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 收件人姓名
     */
    private String recipientName;
    /**
     * 收件人昵称
     */
    private String recipientNickname;
    /**
     * 收件人电话
     */
    private String recipientPhone;
    /**
     * 收件省
     */
    private String recipientProvince;
    /**
     * 收件市
     */
    private String recipientCity;
    /**
     * 收件区
     */
    private String recipientArea;
    /**
     * 收件人地址
     */
    private String recipientAddress;
    /**
     * 备注说明
     */
    private String remark;
    /**
     * 订单金额
     */
    private BigDecimal orderAmount;
    /**
     * 下单时间
     */
//    @NotNull(message = "订单创建时间不能为空",groups = Add.class)
    private LocalDateTime orderCreateTime;
    /**
     * 销售渠道SKU
     */
    private String salesChannelSku;
    /**
     * 品规
     */
    private String productSpecification;
    /**
     * Item_ID
     */
    private String itemId;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 小计
     */
    private BigDecimal totalAmount;
    /**
     * 快递单号
     */
    private String expressTrackingNumber;
    /**
     * 销售渠道
     *
     * 枚举 {@link TODO gift_store_name 对应的类}
     */
    private String salesChannel;
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
     * 原始订单数据
     */
    private String originConfig;
    /**
     * 扩展字段1
     */
    private String extFieldOne;
    /**
     * 扩展字段2
     */
    private String extFieldTwo;
    /**
     * 扩展字段3
     */
    private String extFieldThree;
}