package cn.iocoder.yudao.module.gift.controller.admin.historycardrecord.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HistoryCardRecordPageReqVO extends PageParam {

    @Schema(description = "数据源 ")
    private String dataSource;

    @Schema(description = "卡号")
    private String cardNo;

    @Schema(description = "品规名称")
    private String productSpecName;

    @Schema(description = "品规内配")
    private String inSpecInternalAllocation;

    @Schema(description = "卡券有效期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] validityPeriodOfCoupon;

    @Schema(description = "品规面值")
    private String productSpecValue;

    @Schema(description = "卡券创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] cardCreateTime;

    @Schema(description = "卡券激活时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] cardActivationTime;

    @Schema(description = "卡券销售状态")
    private String cardSalesStatus;

    @Schema(description = "预约状态")
    private String appointmentStatus;

    @Schema(description = "卡券状态")
    private String cardStatus;

    @Schema(description = "售卡订单编号")
    private String cardSalesOrderNo;

    @Schema(description = "售卡订单姓名", example = "张三")
    private String cardSalesOrderUserName;

    @Schema(description = "售卡订单说明", example = "随便")
    private String cardSalesOrderRemark;

    @Schema(description = "提货订单ID", example = "21030")
    private String consigneeOrderId;

    @Schema(description = "提货订单姓名", example = "张三")
    private String consigneeOrderUserName;

    @Schema(description = "提货时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] consigneeTime;

    @Schema(description = "约发时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDate[] appointmentSendingTime;

    @Schema(description = "托寄物内容")
    private String consignedItemContent;

    @Schema(description = "快递单号")
    private String expressTrackingNo;

    @Schema(description = "快递公司")
    private String expressCompany;

    @Schema(description = "提货人手机号")
    private String consigneePhoneNumber;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}