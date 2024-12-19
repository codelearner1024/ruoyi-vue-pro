package cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 多平台订单处理批次新增/修改 Request VO")
@Data
public class MultiPlatformOrderBatchSaveReqVO {

    @Schema(description = "多平台订单文件id", requiredMode = Schema.RequiredMode.REQUIRED, example = "4287")
    private Long id;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "平台 1:工行 2:有赞 3:天猫 9.其他")
    @NotNull(message = "平台不能为空")
    private Integer platformType;

    @Schema(description = "补充平台名称")
    private String platformName;

    @Schema(description = "店铺名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "店铺名称 1: 八神旗舰店 2：蟹品仙旗舰店 3: 有赞商城-湖里仙  4:八神阳澄湖蟹天堂旗舰店 ")
    @NotEmpty(message = "店铺名称不能为空")
    private String storeName;

    @Schema(description = "文件url", example = "https://www.iocoder.cn")
    private String fileUrl;

    @Schema(description = "备注", example = "你猜")
    private String remark;

}