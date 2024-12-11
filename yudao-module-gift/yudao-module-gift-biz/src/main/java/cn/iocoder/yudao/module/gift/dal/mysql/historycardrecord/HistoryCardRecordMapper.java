package cn.iocoder.yudao.module.gift.dal.mysql.historycardrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.gift.controller.admin.historycardrecord.vo.HistoryCardRecordPageReqVO;
import cn.iocoder.yudao.module.gift.dal.dataobject.historycardrecord.HistoryCardRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询 Mapper
 *
 * @author reprejudicer1024
 */
@Mapper
public interface HistoryCardRecordMapper extends BaseMapperX<HistoryCardRecordDO> {

    default PageResult<HistoryCardRecordDO> selectPage(HistoryCardRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HistoryCardRecordDO>()
                .eqIfPresent(HistoryCardRecordDO::getDataSource, reqVO.getDataSource())
                .likeIfPresent(HistoryCardRecordDO::getCardNo, reqVO.getCardNo())
                .likeIfPresent(HistoryCardRecordDO::getProductSpecName, reqVO.getProductSpecName())
                .likeIfPresent(HistoryCardRecordDO::getInSpecInternalAllocation, reqVO.getInSpecInternalAllocation())
                .betweenIfPresent(HistoryCardRecordDO::getValidityPeriodOfCoupon, reqVO.getValidityPeriodOfCoupon())
                .likeIfPresent(HistoryCardRecordDO::getProductSpecValue, reqVO.getProductSpecValue())
                .betweenIfPresent(HistoryCardRecordDO::getCardCreateTime, reqVO.getCardCreateTime())
                .betweenIfPresent(HistoryCardRecordDO::getCardActivationTime, reqVO.getCardActivationTime())
                .eqIfPresent(HistoryCardRecordDO::getCardSalesStatus, reqVO.getCardSalesStatus())
                .eqIfPresent(HistoryCardRecordDO::getAppointmentStatus, reqVO.getAppointmentStatus())
                .eqIfPresent(HistoryCardRecordDO::getCardStatus, reqVO.getCardStatus())
                .likeIfPresent(HistoryCardRecordDO::getCardSalesOrderNo, reqVO.getCardSalesOrderNo())
                .likeIfPresent(HistoryCardRecordDO::getCardSalesOrderUserName, reqVO.getCardSalesOrderUserName())
                .likeIfPresent(HistoryCardRecordDO::getCardSalesOrderRemark, reqVO.getCardSalesOrderRemark())
                .likeIfPresent(HistoryCardRecordDO::getConsigneeOrderId, reqVO.getConsigneeOrderId())
                .likeIfPresent(HistoryCardRecordDO::getConsigneeOrderUserName, reqVO.getConsigneeOrderUserName())
                .betweenIfPresent(HistoryCardRecordDO::getConsigneeTime, reqVO.getConsigneeTime())
                .betweenIfPresent(HistoryCardRecordDO::getAppointmentSendingTime, reqVO.getAppointmentSendingTime())
                .likeIfPresent(HistoryCardRecordDO::getConsignedItemContent, reqVO.getConsignedItemContent())
                .likeIfPresent(HistoryCardRecordDO::getExpressTrackingNo, reqVO.getExpressTrackingNo())
                .likeIfPresent(HistoryCardRecordDO::getExpressCompany, reqVO.getExpressCompany())
                .likeIfPresent(HistoryCardRecordDO::getConsigneePhoneNumber, reqVO.getConsigneePhoneNumber())
                .likeIfPresent(HistoryCardRecordDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(HistoryCardRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(HistoryCardRecordDO::getId));
    }

}