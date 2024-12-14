package cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 多平台订单处理批次分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MultiPlatformOrderBatchPageReqVO extends PageParam {

    @Schema(description = "平台", example = "平台 1:工行 2:有赞 3:天猫 9.其他")
    private Integer platformType;

    @Schema(description = "补充平台名称")
    private String platformName;

    @Schema(description = "店铺名称", example = "店铺名称 1: 八神旗舰店 2：蟹品仙旗舰店 3: 有赞商城-湖里仙  4:八神阳澄湖蟹天堂旗舰店 ")
    private String storeName;

    @Schema(description = "文件url", example = "https://www.iocoder.cn")
    private String fileUrl;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}