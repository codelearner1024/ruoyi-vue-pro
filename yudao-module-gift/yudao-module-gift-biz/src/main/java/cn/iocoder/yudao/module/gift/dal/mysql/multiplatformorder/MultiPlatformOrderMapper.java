package cn.iocoder.yudao.module.gift.dal.mysql.multiplatformorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo.MultiPlatformOrderPageReqVO;
import cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder.MultiPlatformOrderDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 多平台订单 Mapper
 *
 * @author reprejudicer1024
 */
@Mapper
public interface MultiPlatformOrderMapper extends BaseMapperX<MultiPlatformOrderDO> {

    default PageResult<MultiPlatformOrderDO> selectPage(MultiPlatformOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MultiPlatformOrderDO>()
            .eq(MultiPlatformOrderDO::getOrderBatchId, reqVO.getOrderBatchId())
            .likeIfPresent(MultiPlatformOrderDO::getProductName, reqVO.getProductName())
            .orderByDesc(MultiPlatformOrderDO::getId));
    }

    List<MultiPlatformOrderDO> selectPageByContent(@Param("reqVO") MultiPlatformOrderPageReqVO reqVO);
    Long selectTotalByContent(@Param("reqVO") MultiPlatformOrderPageReqVO reqVO);

    default int deleteByOrderBatchId(Long orderBatchId) {
        return delete(MultiPlatformOrderDO::getOrderBatchId, orderBatchId);
    }

    int deleteByPrimaryKey(Long id);

    int insert(MultiPlatformOrderDO record);

    int insertSelective(MultiPlatformOrderDO record);

    MultiPlatformOrderDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MultiPlatformOrderDO record);

    int updateByPrimaryKey(MultiPlatformOrderDO record);



}