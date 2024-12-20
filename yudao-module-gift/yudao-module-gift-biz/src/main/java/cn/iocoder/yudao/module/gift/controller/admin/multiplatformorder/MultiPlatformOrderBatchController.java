package cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo.*;
import cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder.MultiPlatformOrderBatchDO;
import cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder.MultiPlatformOrderDO;
import cn.iocoder.yudao.module.gift.service.multiplatformorder.MultiPlatformOrderBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 多平台订单处理批次")
@RestController
@RequestMapping("/gift/multi-platform-order-batch")
@Validated
public class MultiPlatformOrderBatchController {

    @Resource
    private MultiPlatformOrderBatchService multiPlatformOrderBatchService;

    @PostMapping("/create")
    @Operation(summary = "创建多平台订单处理批次")
    @PreAuthorize("@ss.hasPermission('gift:multi-platform-order-batch:create')")
    public CommonResult<Long> createMultiPlatformOrderBatch(@Valid @RequestBody MultiPlatformOrderBatchSaveReqVO createReqVO) {
        return success(multiPlatformOrderBatchService.createMultiPlatformOrderBatch(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新多平台订单处理批次")
    @PreAuthorize("@ss.hasPermission('gift:multi-platform-order-batch:update')")
    public CommonResult<Boolean> updateMultiPlatformOrderBatch(@Valid @RequestBody MultiPlatformOrderBatchSaveReqVO updateReqVO) {
        multiPlatformOrderBatchService.updateMultiPlatformOrderBatch(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除多平台订单处理批次")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('gift:multi-platform-order-batch:delete')")
    public CommonResult<Boolean> deleteMultiPlatformOrderBatch(@RequestParam("id") Long id) {
        multiPlatformOrderBatchService.deleteMultiPlatformOrderBatch(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得多平台订单处理批次")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('gift:multi-platform-order-batch:query')")
    public CommonResult<MultiPlatformOrderBatchRespVO> getMultiPlatformOrderBatch(@RequestParam("id") Long id) {
        MultiPlatformOrderBatchDO multiPlatformOrderBatch = multiPlatformOrderBatchService.getMultiPlatformOrderBatch(id);
        return success(BeanUtils.toBean(multiPlatformOrderBatch, MultiPlatformOrderBatchRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得多平台订单处理批次分页")
    @PreAuthorize("@ss.hasPermission('gift:multi-platform-order-batch:query')")
    public CommonResult<PageResult<MultiPlatformOrderBatchRespVO>> getMultiPlatformOrderBatchPage(@Valid MultiPlatformOrderBatchPageReqVO pageReqVO) {
        PageResult<MultiPlatformOrderBatchDO> pageResult = multiPlatformOrderBatchService.getMultiPlatformOrderBatchPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MultiPlatformOrderBatchRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出多平台订单处理批次 Excel")
    @PreAuthorize("@ss.hasPermission('gift:multi-platform-order-batch:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMultiPlatformOrderBatchExcel(@Valid MultiPlatformOrderBatchPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MultiPlatformOrderBatchDO> list = multiPlatformOrderBatchService.getMultiPlatformOrderBatchPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "多平台订单处理批次.xls", "数据", MultiPlatformOrderBatchRespVO.class,
                        BeanUtils.toBean(list, MultiPlatformOrderBatchRespVO.class));
    }

    // ==================== 子表（多平台订单） ====================

    @GetMapping("/multi-platform-order/page")
    @Operation(summary = "获得多平台订单分页")
    @Parameter(name = "orderBatchId", description = "订单处理批次号")
    @PreAuthorize("@ss.hasPermission('gift:multi-platform-order-batch:query')")
    public CommonResult<PageResult<MultiPlatformOrderRespVO>> getMultiPlatformOrderPage(@Valid MultiPlatformOrderPageReqVO orderPageReqVO) {
        PageResult<MultiPlatformOrderDO> pageResult= multiPlatformOrderBatchService.getMultiPlatformOrderPage(orderPageReqVO);
        return success(BeanUtils.toBean(pageResult,MultiPlatformOrderRespVO.class));
    }

    @PostMapping("/multi-platform-order/create")
    @Operation(summary = "创建多平台订单")
    @PreAuthorize("@ss.hasPermission('gift:multi-platform-order-batch:create')")
    public CommonResult<Long> createMultiPlatformOrder(@Valid @RequestBody MultiPlatformOrderDO multiPlatformOrder) {
        return success(multiPlatformOrderBatchService.createMultiPlatformOrder(multiPlatformOrder));
    }

    @PutMapping("/multi-platform-order/update")
    @Operation(summary = "更新多平台订单")
    @PreAuthorize("@ss.hasPermission('gift:multi-platform-order-batch:update')")
    public CommonResult<Boolean> updateMultiPlatformOrder(@Valid @RequestBody MultiPlatformOrderDO multiPlatformOrder) {
        multiPlatformOrderBatchService.updateMultiPlatformOrder(multiPlatformOrder);
        return success(true);
    }

    @PutMapping("/multi-platform-order/batchUpdate")
    @Operation(summary = "批量更新多平台订单")
    @PreAuthorize("@ss.hasPermission('gift:multi-platform-order-batch:update')")
    public CommonResult<Boolean> batchUpdateMultiPlatformOrder(@Validated(MultiPlatformOrderPageReqVO.BatchUpdate.class)
                                                                   @RequestBody MultiPlatformOrderPageReqVO multiPlatformOrder) {
        multiPlatformOrderBatchService.batchUpdateMultiPlatformOrder(multiPlatformOrder);
        return success(true);
    }

    @DeleteMapping("/multi-platform-order/delete")
    @Parameter(name = "id", description = "编号", required = true)
    @Operation(summary = "删除多平台订单")
    @PreAuthorize("@ss.hasPermission('gift:multi-platform-order-batch:delete')")
    public CommonResult<Boolean> deleteMultiPlatformOrder(@RequestParam("id") Long id) {
        multiPlatformOrderBatchService.deleteMultiPlatformOrder(id);
        return success(true);
    }

	@GetMapping("/multi-platform-order/get")
	@Operation(summary = "获得多平台订单")
	@Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('gift:multi-platform-order-batch:query')")
	public CommonResult<MultiPlatformOrderDO> getMultiPlatformOrder(@RequestParam("id") Long id) {
	    return success(multiPlatformOrderBatchService.getMultiPlatformOrder(id));
	}

    @GetMapping("/multi-platform-order/export-excel")
    @Operation(summary = "导出多平台订单处理批次对应的明细 Excel")
    @PreAuthorize("@ss.hasPermission('gift:multi-platform-order-batch:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMultiPlatformOrderBatchDetailExcel(@Valid MultiPlatformOrderPageReqVO pageReqVO,
                                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MultiPlatformOrderDO> list = multiPlatformOrderBatchService.getMultiPlatformOrderPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "转礼卡系统-订单批次.xls", "sheet1", MultiPlatformOrderRespVO.class,
                BeanUtils.toBean(list, MultiPlatformOrderRespVO.class));
    }
}