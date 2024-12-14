package cn.iocoder.yudao.module.gift.service.multiplatformorder;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo.*;
import cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder.MultiPlatformOrderBatchDO;
import cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder.MultiPlatformOrderDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.gift.dal.mysql.multiplatformorder.MultiPlatformOrderBatchMapper;
import cn.iocoder.yudao.module.gift.dal.mysql.multiplatformorder.MultiPlatformOrderMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.gift.enums.ErrorCodeConstants.*;

/**
 * 多平台订单处理批次 Service 实现类
 *
 * @author reprejudicer1024
 */
@Service
@Validated
public class MultiPlatformOrderBatchServiceImpl implements MultiPlatformOrderBatchService {

    @Resource
    private MultiPlatformOrderBatchMapper multiPlatformOrderBatchMapper;
    @Resource
    private MultiPlatformOrderMapper multiPlatformOrderMapper;

    @Override
    public Long createMultiPlatformOrderBatch(MultiPlatformOrderBatchSaveReqVO createReqVO) {
        // 插入
        MultiPlatformOrderBatchDO multiPlatformOrderBatch = BeanUtils.toBean(createReqVO, MultiPlatformOrderBatchDO.class);
        multiPlatformOrderBatchMapper.insert(multiPlatformOrderBatch);
        // 返回
        return multiPlatformOrderBatch.getId();
    }

    @Override
    public void updateMultiPlatformOrderBatch(MultiPlatformOrderBatchSaveReqVO updateReqVO) {
        // 校验存在
        validateMultiPlatformOrderBatchExists(updateReqVO.getId());
        // 更新
        MultiPlatformOrderBatchDO updateObj = BeanUtils.toBean(updateReqVO, MultiPlatformOrderBatchDO.class);
        multiPlatformOrderBatchMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMultiPlatformOrderBatch(Long id) {
        // 校验存在
        validateMultiPlatformOrderBatchExists(id);
        // 删除
        multiPlatformOrderBatchMapper.deleteById(id);

        // 删除子表
        deleteMultiPlatformOrderByOrderBatchId(id);
    }

    private void validateMultiPlatformOrderBatchExists(Long id) {
        if (multiPlatformOrderBatchMapper.selectById(id) == null) {
            throw exception(MULTI_PLATFORM_ORDER_BATCH_NOT_EXISTS);
        }
    }

    @Override
    public MultiPlatformOrderBatchDO getMultiPlatformOrderBatch(Long id) {
        return multiPlatformOrderBatchMapper.selectById(id);
    }

    @Override
    public PageResult<MultiPlatformOrderBatchDO> getMultiPlatformOrderBatchPage(MultiPlatformOrderBatchPageReqVO pageReqVO) {
        return multiPlatformOrderBatchMapper.selectPage(pageReqVO);
    }

    // ==================== 子表（多平台订单） ====================

    @Override
    public PageResult<MultiPlatformOrderDO> getMultiPlatformOrderPage(PageParam pageReqVO, Long orderBatchId) {
        return multiPlatformOrderMapper.selectPage(pageReqVO, orderBatchId);
    }

    @Override
    public Long createMultiPlatformOrder(MultiPlatformOrderDO multiPlatformOrder) {
        multiPlatformOrderMapper.insert(multiPlatformOrder);
        return multiPlatformOrder.getId();
    }

    @Override
    public void updateMultiPlatformOrder(MultiPlatformOrderDO multiPlatformOrder) {
        // 校验存在
        validateMultiPlatformOrderExists(multiPlatformOrder.getId());
        // 更新
        multiPlatformOrder.setUpdater(null).setUpdateTime(null); // 解决更新情况下：updateTime 不更新
        multiPlatformOrderMapper.updateById(multiPlatformOrder);
    }

    @Override
    public void deleteMultiPlatformOrder(Long id) {
        // 校验存在
        validateMultiPlatformOrderExists(id);
        // 删除
        multiPlatformOrderMapper.deleteById(id);
    }

    @Override
    public MultiPlatformOrderDO getMultiPlatformOrder(Long id) {
        return multiPlatformOrderMapper.selectById(id);
    }

    private void validateMultiPlatformOrderExists(Long id) {
        if (multiPlatformOrderMapper.selectById(id) == null) {
            throw exception(MULTI_PLATFORM_ORDER_NOT_EXISTS);
        }
    }

    private void deleteMultiPlatformOrderByOrderBatchId(Long orderBatchId) {
        multiPlatformOrderMapper.deleteByOrderBatchId(orderBatchId);
    }

}