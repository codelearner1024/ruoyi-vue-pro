package cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 多平台订单处理批次 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MultiPlatformOrderBatchRespVO {

    @Schema(description = "多平台订单文件id", requiredMode = Schema.RequiredMode.REQUIRED, example = "4287")
    @ExcelProperty("多平台订单文件id")
    private Long id;

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

    @Schema(description = "文件url", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    @ExcelProperty("文件url")
    private String fileUrl;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}