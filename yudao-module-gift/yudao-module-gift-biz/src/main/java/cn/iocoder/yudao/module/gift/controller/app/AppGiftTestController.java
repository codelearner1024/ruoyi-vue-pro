package cn.iocoder.yudao.module.gift.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "用户app - gift")
@RestController
@RequestMapping("/gift/test")
@Validated
public class AppGiftTestController {

    @GetMapping("/get")
    @Operation(summary = "获取 gift 的test 信息")
    public CommonResult<String> get() {
        return success("true");
    }

}
