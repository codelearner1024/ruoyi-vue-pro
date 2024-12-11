package cn.iocoder.yudao.module.gift.controller.admin.historycardrecord;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.gift.controller.admin.historycardrecord.vo.HistoryCardRecordPageReqVO;
import cn.iocoder.yudao.module.gift.controller.admin.historycardrecord.vo.HistoryCardRecordRespVO;
import cn.iocoder.yudao.module.gift.controller.admin.historycardrecord.vo.HistoryCardRecordSaveReqVO;
import cn.iocoder.yudao.module.gift.dal.dataobject.historycardrecord.HistoryCardRecordDO;
import cn.iocoder.yudao.module.gift.service.historycardrecord.HistoryCardRecordService;
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

@Tag(name = "管理后台 - 蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询")
@RestController
@RequestMapping("/gift/history-card-record")
@Validated
public class HistoryCardRecordController {

    @Resource
    private HistoryCardRecordService historyCardRecordService;

    @PostMapping("/create")
    @Operation(summary = "创建蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询")
    @PreAuthorize("@ss.hasPermission('gift:history-card-record:create')")
    public CommonResult<Integer> createHistoryCardRecord(@Valid @RequestBody HistoryCardRecordSaveReqVO createReqVO) {
        return success(historyCardRecordService.createHistoryCardRecord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询")
    @PreAuthorize("@ss.hasPermission('gift:history-card-record:update')")
    public CommonResult<Boolean> updateHistoryCardRecord(@Valid @RequestBody HistoryCardRecordSaveReqVO updateReqVO) {
        historyCardRecordService.updateHistoryCardRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('gift:history-card-record:delete')")
    public CommonResult<Boolean> deleteHistoryCardRecord(@RequestParam("id") Integer id) {
        historyCardRecordService.deleteHistoryCardRecord(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('gift:history-card-record:query')")
    public CommonResult<HistoryCardRecordRespVO> getHistoryCardRecord(@RequestParam("id") Integer id) {
        HistoryCardRecordDO historyCardRecord = historyCardRecordService.getHistoryCardRecord(id);
        return success(BeanUtils.toBean(historyCardRecord, HistoryCardRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询分页")
    @PreAuthorize("@ss.hasPermission('gift:history-card-record:query')")
    public CommonResult<PageResult<HistoryCardRecordRespVO>> getHistoryCardRecordPage(@Valid HistoryCardRecordPageReqVO pageReqVO) {
        PageResult<HistoryCardRecordDO> pageResult = historyCardRecordService.getHistoryCardRecordPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, HistoryCardRecordRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询 Excel")
    @PreAuthorize("@ss.hasPermission('gift:history-card-record:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportHistoryCardRecordExcel(@Valid HistoryCardRecordPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<HistoryCardRecordDO> list = historyCardRecordService.getHistoryCardRecordPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询.xls", "数据", HistoryCardRecordRespVO.class,
                        BeanUtils.toBean(list, HistoryCardRecordRespVO.class));
    }

}