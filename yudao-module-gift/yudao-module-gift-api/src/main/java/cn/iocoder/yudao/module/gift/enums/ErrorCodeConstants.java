package cn.iocoder.yudao.module.gift.enums;


import cn.iocoder.yudao.framework.common.exception.ErrorCode;


/**
 * GIFT系统 错误码枚举类
 * <p>
 * gift 系统 错误码区间 [1-092-000-000 ~ 1-092-999-999)
 */
public interface ErrorCodeConstants {

    // ========== 蟹云系统旧卡查询 在蟹云系统查询不到可以这里查询 ==========
    ErrorCode HISTORY_CARD_RECORD_NOT_EXISTS = new ErrorCode(1_092_000_000, "该卡号不存在");

    // ========== 多平台订单处理批次 ==========
    ErrorCode MULTI_PLATFORM_ORDER_BATCH_NOT_EXISTS = new ErrorCode(1_092_001_000, "多平台订单处理批次不存在");
    ErrorCode MULTI_PLATFORM_ORDER_NOT_EXISTS = new ErrorCode(1_092_001_001, "多平台订单不存在");
    ErrorCode MULTI_PLATFORM_ORDER_VALID_ERROR = new ErrorCode(1_092_001_002, "多平台订单解析错误:【{}】");
}