package cn.iocoder.yudao.module.gift.service.multiplatformorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo.MultiPlatformOrderBatchPageReqVO;
import cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo.MultiPlatformOrderBatchSaveReqVO;
import cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo.MultiPlatformOrderPageReqVO;
import cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder.MultiPlatformOrderBatchDO;
import cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder.MultiPlatformOrderDO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.IOException;

/**
 * 多平台订单处理批次 Service 接口
 *
 * @author reprejudicer1024
 */
public interface MultiPlatformOrderBatchService {

    /**
     * 创建多平台订单处理批次
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMultiPlatformOrderBatch(@Valid MultiPlatformOrderBatchSaveReqVO createReqVO);

    /**
     * 更新多平台订单处理批次
     *
     * @param updateReqVO 更新信息
     */
    void updateMultiPlatformOrderBatch(@Valid MultiPlatformOrderBatchSaveReqVO updateReqVO);

    /**
     * 删除多平台订单处理批次
     *
     * @param id 编号
     */
    void deleteMultiPlatformOrderBatch(Long id);

    /**
     * 获得多平台订单处理批次
     *
     * @param id 编号
     * @return 多平台订单处理批次
     */
    MultiPlatformOrderBatchDO getMultiPlatformOrderBatch(Long id);

    /**
     * 获得多平台订单处理批次分页
     *
     * @param pageReqVO 分页查询
     * @return 多平台订单处理批次分页
     */
    PageResult<MultiPlatformOrderBatchDO> getMultiPlatformOrderBatchPage(MultiPlatformOrderBatchPageReqVO pageReqVO);

    // ==================== 子表（多平台订单） ====================

    /**
     * 获得多平台订单分页
     *
     * @param pageReqVO 分页查询
     * @return 多平台订单分页
     */
    PageResult<MultiPlatformOrderDO> getMultiPlatformOrderPage(MultiPlatformOrderPageReqVO pageReqVO);

    PageResult<MultiPlatformOrderDO> getMultiPlatformOrderPageByContent(MultiPlatformOrderPageReqVO pageReqVO);

    /**
     * 创建多平台订单
     *
     * @param multiPlatformOrder 创建信息
     * @return 编号
     */
    Long createMultiPlatformOrder(@Valid MultiPlatformOrderDO multiPlatformOrder);

    /**
     * 更新多平台订单
     *
     * @param multiPlatformOrder 更新信息
     */
    void updateMultiPlatformOrder(@Valid MultiPlatformOrderDO multiPlatformOrder);

    /**
     * 批量更新
     * @param multiPlatformOrder
     */
    void batchUpdateMultiPlatformOrder(MultiPlatformOrderPageReqVO multiPlatformOrder);

    /**
     * 删除多平台订单
     *
     * @param id 编号
     */
    void deleteMultiPlatformOrder(Long id);

	/**
	 * 获得多平台订单
	 *
	 * @param id 编号
     * @return 多平台订单
	 */
    MultiPlatformOrderDO getMultiPlatformOrder(Long id);

    /**
     * 导出礼卡系统能兼容的excel 格式
     *
     * @param pageReqVO
     * @param response
     */
    void exportMultiPlatformOrderBatchDetailExcel(MultiPlatformOrderPageReqVO pageReqVO, HttpServletResponse response) throws IOException;
}