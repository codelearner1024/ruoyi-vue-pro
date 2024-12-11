package cn.iocoder.yudao.module.gift.service.historycardrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.gift.controller.admin.historycardrecord.vo.HistoryCardRecordPageReqVO;
import cn.iocoder.yudao.module.gift.controller.admin.historycardrecord.vo.HistoryCardRecordSaveReqVO;
import cn.iocoder.yudao.module.gift.dal.dataobject.historycardrecord.HistoryCardRecordDO;
import cn.iocoder.yudao.module.gift.dal.mysql.historycardrecord.HistoryCardRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.gift.enums.ErrorCodeConstants.*;

/**
 * 蟹云系统旧卡查询  在蟹云系统查询不到可以这里查询 Service 实现类
 *
 * @author reprejudicer1024
 */
@Service
@Validated
public class HistoryCardRecordServiceImpl implements HistoryCardRecordService {

    @Resource
    private HistoryCardRecordMapper historyCardRecordMapper;

    @Override
    public Integer createHistoryCardRecord(HistoryCardRecordSaveReqVO createReqVO) {
        // 插入
        HistoryCardRecordDO historyCardRecord = BeanUtils.toBean(createReqVO, HistoryCardRecordDO.class);
        historyCardRecordMapper.insert(historyCardRecord);
        // 返回
        return historyCardRecord.getId();
    }

    @Override
    public void updateHistoryCardRecord(HistoryCardRecordSaveReqVO updateReqVO) {
        // 校验存在
        validateHistoryCardRecordExists(updateReqVO.getId());
        // 更新
        HistoryCardRecordDO updateObj = BeanUtils.toBean(updateReqVO, HistoryCardRecordDO.class);
        historyCardRecordMapper.updateById(updateObj);
    }

    @Override
    public void deleteHistoryCardRecord(Integer id) {
        // 校验存在
        validateHistoryCardRecordExists(id);
        // 删除
        historyCardRecordMapper.deleteById(id);
    }

    private void validateHistoryCardRecordExists(Integer id) {
        if (historyCardRecordMapper.selectById(id) == null) {
            throw exception(HISTORY_CARD_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public HistoryCardRecordDO getHistoryCardRecord(Integer id) {
        return historyCardRecordMapper.selectById(id);
    }

    @Override
    public PageResult<HistoryCardRecordDO> getHistoryCardRecordPage(HistoryCardRecordPageReqVO pageReqVO) {
        return historyCardRecordMapper.selectPage(pageReqVO);
    }

}