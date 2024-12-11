package cn.iocoder.yudao.module.gift.dal.dataobject.historycardrecord;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询 DO
 *
 * @author reprejudicer1024
 */
@TableName("gift_history_card_record")
@KeySequence("gift_history_card_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryCardRecordDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 数据源 
     *
     * 枚举 {@link // TODO xy_old_card_belong_sys 对应的类}
     */
    private String dataSource;
    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 品规名称
     */
    private String productSpecName;
    /**
     * 品规内配
     */
    private String inSpecInternalAllocation;
    /**
     * 卡券有效期
     */
    private LocalDateTime validityPeriodOfCoupon;
    /**
     * 品规面值
     */
    private String productSpecValue;
    /**
     * 卡券创建时间
     */
    private LocalDateTime cardCreateTime;
    /**
     * 卡券激活时间
     */
    private LocalDateTime cardActivationTime;
    /**
     * 卡券销售状态
     *
     * 枚举 {@link // TODO 蟹云卡券销售状态 对应的类}
     */
    private String cardSalesStatus;
    /**
     * 预约状态
     *
     * 枚举 {@link // TODO 蟹云卡券预约状态 对应的类}
     */
    private String appointmentStatus;
    /**
     * 卡券状态
     *
     * 枚举 {@link // TODO 蟹云卡券状态 对应的类}
     */
    private String cardStatus;
    /**
     * 售卡订单编号
     */
    private String cardSalesOrderNo;
    /**
     * 售卡订单姓名
     */
    private String cardSalesOrderUserName;
    /**
     * 售卡订单说明
     */
    private String cardSalesOrderRemark;
    /**
     * 提货订单ID
     */
    private String consigneeOrderId;
    /**
     * 提货订单姓名
     */
    private String consigneeOrderUserName;
    /**
     * 提货时间
     */
    private LocalDateTime consigneeTime;
    /**
     * 约发时间
     */
    private LocalDate appointmentSendingTime;
    /**
     * 托寄物内容
     */
    private String consignedItemContent;
    /**
     * 快递单号
     */
    private String expressTrackingNo;
    /**
     * 快递公司
     */
    private String expressCompany;
    /**
     * 提货人手机号
     */
    private String consigneePhoneNumber;
    /**
     * 备注
     */
    private String remark;

}