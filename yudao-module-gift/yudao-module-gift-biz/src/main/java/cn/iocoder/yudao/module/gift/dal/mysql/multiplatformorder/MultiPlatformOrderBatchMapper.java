package cn.iocoder.yudao.module.gift.dal.mysql.multiplatformorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo.MultiPlatformOrderBatchPageReqVO;
import cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder.MultiPlatformOrderBatchDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 多平台订单处理批次 Mapper
 *
 * @author reprejudicer1024
 */
@Mapper
public interface MultiPlatformOrderBatchMapper extends BaseMapperX<MultiPlatformOrderBatchDO> {

    default PageResult<MultiPlatformOrderBatchDO> selectPage(MultiPlatformOrderBatchPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MultiPlatformOrderBatchDO>()
                .eqIfPresent(MultiPlatformOrderBatchDO::getPlatformType, reqVO.getPlatformType())
                .likeIfPresent(MultiPlatformOrderBatchDO::getPlatformName, reqVO.getPlatformName())
                .eqIfPresent(MultiPlatformOrderBatchDO::getStoreName, reqVO.getStoreName())
                .eqIfPresent(MultiPlatformOrderBatchDO::getFileUrl, reqVO.getFileUrl())
                .likeIfPresent(MultiPlatformOrderBatchDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(MultiPlatformOrderBatchDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MultiPlatformOrderBatchDO::getId));
    }

}