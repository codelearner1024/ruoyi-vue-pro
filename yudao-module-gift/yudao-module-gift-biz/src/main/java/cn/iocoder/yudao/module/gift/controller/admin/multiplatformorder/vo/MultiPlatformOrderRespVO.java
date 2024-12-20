package cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.converters.localdatetime.LocalDateTimeStringConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 *  "订单号", "姓名", "别名", "电话", "地址", "备注说明",
 *  "订单金额", "下单时间", "销售渠道SKU", "SKU", "Item_ID",
 *  "产品名称", "单价", "数量", "小计", "快递单号", "销售渠道"
 */
@Schema(description = "管理后台 - 多平台订单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MultiPlatformOrderRespVO {

    @Schema(description = "多平台订单文件id", requiredMode = Schema.RequiredMode.REQUIRED, example = "4287")
    @ExcelIgnore
    private Long id;

    /**
     * 订单号
     */
    @Schema(description = "多平台订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "4287")
    @ExcelProperty("订单号")
    private String orderNo;

    /**
     * 收件人姓名
     */
    @ExcelProperty("姓名")
    private String recipientName;
    /**
     * 收件人昵称
     */
    @ExcelProperty("别名")
    private String recipientNickname;
    /**
     * 收件人电话
     */
    @ExcelProperty("电话")
    private String recipientPhone;

    /**
     * 收件人地址
     */
    @ExcelProperty("地址")
    private String recipientAddress;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注说明")
    private String remark;

    /**
     * 订单金额
     */
    @ExcelProperty("订单金额")
    private BigDecimal orderAmount;

    /**
     * 下单时间
     */
    @ExcelProperty(value = "下单时间",converter = LocalDateTimeStringConverter.class)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderCreateTime;

    /**
     * 销售渠道SKU
     */
    @ExcelProperty("销售渠道SKU")
    private String salesChannelSku;
    /**
     * 品规
     */
    @ExcelProperty("SKU")
    private String productSpecification;
    /**
     * Item_ID
     */
    @ExcelProperty("Item_ID")
    private String itemId;

    /**
     * 产品名称
     */
    @ExcelProperty("产品名称")
    private String productName;
    /**
     * 单价
     */
    @ExcelProperty("单价")
    private BigDecimal price;
    /**
     * 数量
     */
    @ExcelProperty("数量")
    private Integer quantity;
    /**
     * 小计
     */
    @ExcelProperty("小计")
    private BigDecimal totalAmount;
    /**
     * 快递单号
     */
    @ExcelProperty("快递单号")
    private String expressTrackingNumber;

    /**
     * 销售渠道
     *
     * 枚举 {@link //TODO gift_store_name 对应的类}
     */
    @Schema(description = "销售渠道", requiredMode = Schema.RequiredMode.REQUIRED, example = "店铺名称 1: 八神旗舰店 2：蟹品仙旗舰店 3: 有赞商城-湖里仙  4:八神阳澄湖蟹天堂旗舰店 ")
    @ExcelProperty(value = "销售渠道", converter = DictConvert.class)
    @DictFormat("gift_store_name") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String salesChannel;


    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "平台 1:工行 2:有赞 3:天猫 9.其他")
    @ExcelProperty(value = "平台", converter = DictConvert.class)
    @DictFormat("gift_platform_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer platformType;

    @Schema(description = "补充平台名称")
    @ExcelProperty("补充平台名称")
    private String platformName;

    @Schema(description = "店铺名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "店铺名称 1: 八神旗舰店 2：蟹品仙旗舰店 3: 有赞商城-湖里仙  4:八神阳澄湖蟹天堂旗舰店 ")
    @ExcelProperty(value = "店铺名称", converter = DictConvert.class)
    @DictFormat("gift_store_name") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String storeName;

    @Schema(description = "文件url", example = "https://www.iocoder.cn")
    @ExcelProperty("文件url")
    private String fileUrl;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    /**
     * 订单处理批次号
     */

    @Schema(description = "多平台订单处理批次", requiredMode = Schema.RequiredMode.REQUIRED, example = "4287")
    @ExcelProperty("多平台订单处理批次号")
    private Long orderBatchId;


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