package cn.iocoder.yudao.module.gift.dal.mysql.multiplatformorder;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder.MultiPlatformOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 多平台订单 Mapper
 *
 * @author reprejudicer1024
 */
@Mapper
public interface MultiPlatformOrderMapper extends BaseMapperX<MultiPlatformOrderDO> {

    default PageResult<MultiPlatformOrderDO> selectPage(PageParam reqVO, Long orderBatchId) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MultiPlatformOrderDO>()
            .eq(MultiPlatformOrderDO::getOrderBatchId, orderBatchId)
            .orderByDesc(MultiPlatformOrderDO::getId));
    }

    default int deleteByOrderBatchId(Long orderBatchId) {
        return delete(MultiPlatformOrderDO::getOrderBatchId, orderBatchId);
    }

}