package cn.iocoder.yudao.module.gift.service.historycardrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.gift.controller.admin.historycardrecord.vo.HistoryCardRecordPageReqVO;
import cn.iocoder.yudao.module.gift.controller.admin.historycardrecord.vo.HistoryCardRecordSaveReqVO;
import cn.iocoder.yudao.module.gift.dal.dataobject.historycardrecord.HistoryCardRecordDO;
import jakarta.validation.Valid;

/**
 * 蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询 Service 接口
 *
 * @author reprejudicer1024
 */
public interface HistoryCardRecordService {

    /**
     * 创建蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createHistoryCardRecord(@Valid HistoryCardRecordSaveReqVO createReqVO);

    /**
     * 更新蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询
     *
     * @param updateReqVO 更新信息
     */
    void updateHistoryCardRecord(@Valid HistoryCardRecordSaveReqVO updateReqVO);

    /**
     * 删除蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询
     *
     * @param id 编号
     */
    void deleteHistoryCardRecord(Integer id);

    /**
     * 获得蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询
     *
     * @param id 编号
     * @return 蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询
     */
    HistoryCardRecordDO getHistoryCardRecord(Integer id);

    /**
     * 获得蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询分页
     *
     * @param pageReqVO 分页查询
     * @return 蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询分页
     */
    PageResult<HistoryCardRecordDO> getHistoryCardRecordPage(HistoryCardRecordPageReqVO pageReqVO);

}