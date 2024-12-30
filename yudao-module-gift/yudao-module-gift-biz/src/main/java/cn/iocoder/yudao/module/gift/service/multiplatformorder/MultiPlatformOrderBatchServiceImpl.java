package cn.iocoder.yudao.module.gift.service.multiplatformorder;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.validation.ValidationUtilPlus;
import cn.iocoder.yudao.framework.dict.core.DictFrameworkUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo.MultiPlatformOrderBatchPageReqVO;
import cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo.MultiPlatformOrderBatchSaveReqVO;
import cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo.MultiPlatformOrderPageReqVO;
import cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder.MultiPlatformOrderBatchDO;
import cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder.MultiPlatformOrderDO;
import cn.iocoder.yudao.module.gift.dal.mysql.multiplatformorder.MultiPlatformOrderBatchMapper;
import cn.iocoder.yudao.module.gift.dal.mysql.multiplatformorder.MultiPlatformOrderMapper;
import cn.iocoder.yudao.module.gift.dal.redis.no.GiftNoRedisDAO;
import cn.iocoder.yudao.module.gift.enums.DictTypeConstants;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    @Resource
    private FileService fileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMultiPlatformOrderBatch(MultiPlatformOrderBatchSaveReqVO createReqVO) {
        // 插入
        MultiPlatformOrderBatchDO multiPlatformOrderBatch = BeanUtils.toBean(createReqVO, MultiPlatformOrderBatchDO.class);
        multiPlatformOrderBatchMapper.insert(multiPlatformOrderBatch);

        // TODO 生成批次订单
        String fileUrl = multiPlatformOrderBatch.getFileUrl();
        if (StringUtils.isNotBlank(fileUrl)) { // 如果系统是直接创建订单批次 则不需要上传订单文件
            String[] split = fileUrl.split("/");
            int length = split.length;
            try {
                byte[] fileContent = fileService.getFileContent(Long.valueOf(split[length - 3]), split[length - 1]);
                ByteArrayInputStream stream = new ByteArrayInputStream(fileContent);
                List<MultiPlatformOrderDO> multiPlatformOrderDOS = new ArrayList<>();
                // 如果是工行平台
                if (createReqVO.getPlatformType().equals(1)){
                    List<JSONObject> excelJsonArray = getExcelJsonArray(stream);
                    multiPlatformOrderDOS = convert2GHOrderList(multiPlatformOrderBatch, excelJsonArray);
                }else{
                    List<List<String>> dataList = DownstreamSystemExcelConverter.getDataList(stream);
                    // todo 转成 MultiPlatformOrderDO
                    for (List<String> data : dataList) {
                        MultiPlatformOrderDO multiPlatformOrderDO = convert2Order(multiPlatformOrderBatch, data);
                        multiPlatformOrderDOS.add(multiPlatformOrderDO);
                    }
                }
                multiPlatformOrderMapper.insertBatch(multiPlatformOrderDOS);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // 返回
        return multiPlatformOrderBatch.getId();
    }

    public static List<JSONObject> getExcelJsonArray(ByteArrayInputStream stream) {
            List<JSONObject> jsonObjectList = new ArrayList<>();
            EasyExcel.read(stream, new AnalysisEventListener<Map<Integer, String>>() {
                List<String> headerList;

                @Override
                public void invoke(Map<Integer, String> dataMap, AnalysisContext context) {
//                    if (context.readRowHolder().getRowIndex() >= 2) {
                        JSONObject jsonObject = new JSONObject();
                        for (int i = 0; i < headerList.size(); i++) {
                            jsonObject.put(headerList.get(i), dataMap.get(i));
                        }
                        jsonObjectList.add(jsonObject);
//                    }
                }

                @Override
                public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                    if (context.readRowHolder().getRowIndex() == 1) {
                        headerList = new ArrayList<>(headMap.values());
                    }
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                }
            }).headRowNumber(2).sheet().doRead();
            return jsonObjectList;
        }

    private static MultiPlatformOrderDO convert2Order(MultiPlatformOrderBatchDO orderBatchDO, List<String> data) {
        MultiPlatformOrderDO multiPlatformOrderDO = new MultiPlatformOrderDO();
//                "订单号", "姓名", "别名", "电话", "地址", "备注说明",
//                        "订单金额", "下单时间", "销售渠道SKU", "SKU", "Item_ID",
//                        "产品名称", "单价", "数量", "小计", "快递单号", "销售渠道"
        multiPlatformOrderDO.setOrderBatchId(orderBatchDO.getId());
        multiPlatformOrderDO.setOrderNo(data.get(0));
        multiPlatformOrderDO.setRecipientName(data.get(1));
        multiPlatformOrderDO.setRecipientNickname(data.get(2));
        multiPlatformOrderDO.setRecipientPhone(data.get(3));
        multiPlatformOrderDO.setRecipientAddress(data.get(4));
        multiPlatformOrderDO.setRemark(data.get(5));
        multiPlatformOrderDO.setOrderAmount(new BigDecimal(data.get(6)));
        multiPlatformOrderDO.setOrderCreateTime(StringUtils.isNotBlank(data.get(7)) ? LocalDateTimeUtils.parseDate(data.get(7)) : null);
        multiPlatformOrderDO.setSalesChannelSku(data.get(8));
        multiPlatformOrderDO.setProductSpecification(data.get(9));// 品规最终不得为空,但是不用平台的品规确定时间不一致,因此,此处不校验
        multiPlatformOrderDO.setItemId(data.get(10));
        multiPlatformOrderDO.setProductName(data.get(11));
        multiPlatformOrderDO.setPrice(new BigDecimal(data.get(12)));
        multiPlatformOrderDO.setQuantity(Integer.valueOf(data.get(13)));
        multiPlatformOrderDO.setTotalAmount(new BigDecimal(data.get(14)));
        multiPlatformOrderDO.setExpressTrackingNumber(data.get(15));
        // 优先上传时填写的商城
//        String salesChannel = StringUtils.isNotBlank(data.get(16))? data.get(16) : orderBatchDO.getStoreName();
        multiPlatformOrderDO.setSalesChannel(orderBatchDO.getStoreName());
        multiPlatformOrderDO.setStoreName(orderBatchDO.getStoreName());
        multiPlatformOrderDO.setPlatformType(orderBatchDO.getPlatformType());
        JSONObject originConfig = new JSONObject();
        String[] heads = DownstreamSystemExcelConverter.DELIVERY_SYS_HEADERS;
        for (int i = 0; i < heads.length; i++) {
            originConfig.put(heads[i],data.get(i));
        }
        multiPlatformOrderDO.setOriginConfig(originConfig.toJSONString()); // 首次新增的时候添加原始数据，用于后期如果修改的话可以追溯对比

        // 统一校验
        ValidationUtilPlus.validateBeanAndThrow(multiPlatformOrderDO, MULTI_PLATFORM_ORDER_VALID_ERROR);

        return multiPlatformOrderDO;
    }


    /**
     * 转GH 订单
     * @param orderBatchDO
     * @param excelJsonArray
     * @return
     */
    private List<MultiPlatformOrderDO> convert2GHOrderList(MultiPlatformOrderBatchDO orderBatchDO, List<JSONObject> excelJsonArray) {
        return excelJsonArray.parallelStream().map(data -> {
            MultiPlatformOrderDO multiPlatformOrderDO = new MultiPlatformOrderDO();
            multiPlatformOrderDO.setOrderBatchId(orderBatchDO.getId());
//            "订单号", "姓名", "别名", "电话", "地址", "备注说明",
//                        "订单金额", "下单时间", "销售渠道SKU", "SKU", "Item_ID",
//                        "产品名称", "单价", "数量", "小计", "快递单号", "销售渠道"

            multiPlatformOrderDO.setOrderNo(data.getString("订单编号"));
            multiPlatformOrderDO.setRecipientName(data.getString("收货人姓名"));
            multiPlatformOrderDO.setRecipientPhone(data.getString("手机号码"));
            multiPlatformOrderDO.setRecipientAddress(new StringJoiner("")
                    .add(data.getString("省市"))
                    .add(data.getString("地址"))
                    .toString());
            multiPlatformOrderDO.setRemark(new StringJoiner("")
                    .add("- 买家备注:")
                    .add(data.getString("买家备注"))
                    .add("- 商户备注:")
                    .add(data.getString("商户备注"))
                    .add("- 给买家的备注:")
                    .add(data.getString("给买家的备注"))
                    .toString());
            multiPlatformOrderDO.setOrderAmount(new BigDecimal(data.getString("订单金额(含运费)")));
            String payTime = data.getString("支付时间");
            multiPlatformOrderDO.setOrderCreateTime(StringUtils.isNotBlank(payTime) ? LocalDateTimeUtils.parseDate(payTime) : null);
            multiPlatformOrderDO.setSalesChannelSku(data.getString("商品SKU编号"));
            multiPlatformOrderDO.setProductSpecification(data.getString("商户商品编号"));// 品规最终不得为空,但是不用平台的品规确定时间不一致,因此,此处不校验
            multiPlatformOrderDO.setItemId(data.getString("商品编号"));
            multiPlatformOrderDO.setProductName(data.getString("商品名称"));
            multiPlatformOrderDO.setPrice(new BigDecimal(data.getString("商品单价")));
            multiPlatformOrderDO.setQuantity(Integer.valueOf(data.getString("购买数量")));
            multiPlatformOrderDO.setTotalAmount(new BigDecimal(data.getString("商品金额")));
            multiPlatformOrderDO.setExpressTrackingNumber(data.getString("物流发货单"));
            // 优先上传时填写的商城
//        String salesChannel = StringUtils.isNotBlank(data.get(16))? data.get(16) : orderBatchDO.getStoreName();
            multiPlatformOrderDO.setSalesChannel(orderBatchDO.getStoreName());
            multiPlatformOrderDO.setStoreName(orderBatchDO.getStoreName());
            multiPlatformOrderDO.setPlatformType(orderBatchDO.getPlatformType());

            multiPlatformOrderDO.setOriginConfig(data.toJSONString()); // 首次新增的时候添加原始数据，用于后期如果修改的话可以追溯对比

            // 统一校验
            ValidationUtilPlus.validateBeanAndThrow(multiPlatformOrderDO, MULTI_PLATFORM_ORDER_VALID_ERROR);
            return multiPlatformOrderDO;
        }).toList();

//

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
    public PageResult<MultiPlatformOrderDO> getMultiPlatformOrderPage(MultiPlatformOrderPageReqVO pageReqVO) {
        return multiPlatformOrderMapper.selectPage(pageReqVO);
    }
    @Override
    public PageResult<MultiPlatformOrderDO> getMultiPlatformOrderPageByContent(MultiPlatformOrderPageReqVO pageReqVO) {
        pageReqVO.setOffset((pageReqVO.getPageNo()-1) * pageReqVO.getPageSize());
        List<MultiPlatformOrderDO> multiPlatformOrderDOS = multiPlatformOrderMapper.selectPageByContent(pageReqVO);
        return new PageResult<>(multiPlatformOrderDOS,multiPlatformOrderMapper.selectTotalByContent(pageReqVO));
    }

    @Resource
    GiftNoRedisDAO giftNoRedisDAO;
    @Override
    public Long createMultiPlatformOrder(MultiPlatformOrderDO multiPlatformOrder) {
//        如果没有订单号，则由系统生成
        if (StringUtils.isBlank(multiPlatformOrder.getOrderNo())) {
            // 1.3 生成入库单号，并校验唯一性
            String no = giftNoRedisDAO.generate(GiftNoRedisDAO.BATCH_ORDER_NO_PREFIX);
            multiPlatformOrder.setOrderNo(no);
        }

        // 销售渠道取店铺名称
        multiPlatformOrder.setSalesChannel(multiPlatformOrder.getStoreName());
        multiPlatformOrderMapper.insert(multiPlatformOrder);
        return multiPlatformOrder.getId();
    }

    @Override
    public void updateMultiPlatformOrder(MultiPlatformOrderDO multiPlatformOrder) {
        // 校验存在
        validateMultiPlatformOrderExists(multiPlatformOrder.getId());
        // 更新
        multiPlatformOrder.setUpdater(null).setUpdateTime(null); // 解决更新情况下：updateTime 不更新
        // 销售渠道取店铺名称
        multiPlatformOrder.setSalesChannel(multiPlatformOrder.getStoreName());
        multiPlatformOrderMapper.updateById(multiPlatformOrder);
    }


    /**
     * 目前开放的批量修改字段只有品规
     * @param multiPlatformOrder
     */
    @Override
    public void batchUpdateMultiPlatformOrder(MultiPlatformOrderPageReqVO multiPlatformOrder) {

        LambdaUpdateWrapper<MultiPlatformOrderDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(MultiPlatformOrderDO::getProductSpecification,multiPlatformOrder.getProductSpecification())
                .eq(MultiPlatformOrderDO::getOrderBatchId,multiPlatformOrder.getOrderBatchId())
                .in(MultiPlatformOrderDO::getId,multiPlatformOrder.getIdList());

        multiPlatformOrderMapper.update(updateWrapper);
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

    @Override
    public void exportMultiPlatformOrderBatchDetailExcel(MultiPlatformOrderPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        List<MultiPlatformOrderDO> list = getMultiPlatformOrderPage(pageReqVO).getList();
        // 导出 Excel   - easyExcel的通用方法不适合下游需要的老系统，出现莫名其妙的问题，因此单独写
//        ExcelUtils.write(response, "转礼卡系统-订单批次.xls", "Sheet1", MultiPlatformOrderRespVO.class,
//                BeanUtils.toBean(list, MultiPlatformOrderRespVO.class));

        List<List<String>> dataList = list.stream().map(s -> DownstreamSystemExcelConverter.getdownStreamExceFieldlList(s)).collect(Collectors.toList());

        ExcelUtils.download("转礼卡系统-订单批次.xls",DownstreamSystemExcelConverter.getBytes(dataList),response);
    }

    private void validateMultiPlatformOrderExists(Long id) {
        if (multiPlatformOrderMapper.selectById(id) == null) {
            throw exception(MULTI_PLATFORM_ORDER_NOT_EXISTS);
        }
    }

    private void deleteMultiPlatformOrderByOrderBatchId(Long orderBatchId) {
        multiPlatformOrderMapper.deleteByOrderBatchId(orderBatchId);
    }

    /**
     * 下游系统数据转化类
     */
    static class DownstreamSystemExcelConverter {

        public static final String[] DELIVERY_SYS_HEADERS = new String[]{
                "订单号", "姓名", "别名", "电话", "地址", "备注说明",
                "订单金额", "下单时间", "销售渠道SKU", "SKU", "Item_ID",
                "产品名称", "单价", "数量", "小计", "快递单号", "销售渠道"
        };


        /**
         * 获取下游系统excel字段
         * @param s
         * @return
         */
        private static List<String> getdownStreamExceFieldlList(MultiPlatformOrderDO s) {
            List<String> data = new ArrayList<>();
            data.add(0, s.getOrderNo());
            data.add(1, s.getRecipientName());
            data.add(2, s.getRecipientNickname());
            data.add(3, s.getRecipientPhone());
            data.add(4, s.getRecipientAddress());
            data.add(5, s.getRemark());
            data.add(6, s.getOrderAmount() != null ? s.getOrderAmount().toPlainString() : null);
            data.add(7, LocalDateTimeUtil.format(s.getOrderCreateTime(), LocalDateTimeUtils.YYYY_MM_DD_HH_MM_SS));
            data.add(8, s.getSalesChannelSku());
            data.add(9, s.getProductSpecification());
            data.add(10, s.getItemId());
            data.add(11, s.getProductName());
            data.add(12, s.getPrice() != null ? s.getPrice().toPlainString() : null);
            data.add(13, s.getQuantity() != null ? s.getQuantity().toString() : null);
            data.add(14, s.getTotalAmount() != null ? s.getTotalAmount().toPlainString() : null);
            data.add(15, s.getExpressTrackingNumber());
            // 根据dict的类型获取对应的名称
            data.add(16, DictFrameworkUtils.getDictDataLabel(DictTypeConstants.GIFT_STORE_NAME, String.valueOf(s.getSalesChannel())));
            return data;
        }

        /**
         * 将tmall woda的excel格式数据转成下游
         * @param inputFile
         * @return
         * @throws IOException
         */
        public static byte[] convertTMallWoDaExcelToXls(InputStream inputFile) throws IOException {
            // 定义新的表头

            List<List<String>> dataList = getDataList(inputFile);

            return getBytes(dataList, DELIVERY_SYS_HEADERS);
        }

//        private static byte[] getBytes(String[] newHeaders, List<List<String>> dataList) throws IOException {
//
//        }
        protected static byte[] getBytes(List<List<String>> dataList) throws IOException {
            return getBytes(dataList,DELIVERY_SYS_HEADERS);
        }
        private static byte[] getBytes(List<List<String>> dataList, String[] newHeaders) throws IOException {
            // 使用 Apache POI 写入 .xls 文件
            try (Workbook workbook = new HSSFWorkbook();
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                Sheet sheet = workbook.createSheet("Sheet1");
                dataList.add(0, Arrays.stream(newHeaders).toList());
                for (int i = 0; i < dataList.size(); i++) {
                    List<String> rowData = dataList.get(i);
                    Row row = sheet.createRow(i);

                    for (int j = 0; j < rowData.size(); j++) {
                        Cell cell = row.createCell(j);
                        cell.setCellValue(rowData.get(j));
                    }
                }

                workbook.write(bos);
                return bos.toByteArray();
            }
        }

        /**
         * 默认使用DELIVERY_SYS_HEADERS作为表头
         * @param inputFile
         * @return
         */
        private static List<List<String>> getDataList(InputStream inputFile) {
            return getDataList(inputFile, DELIVERY_SYS_HEADERS);
        }
        private static List<List<String>> getDataList(InputStream inputFile, String[] newHeaders) {
            // 读取 Excel 数据
            List<List<String>> dataList = new ArrayList<>(17);
            EasyExcel.read(inputFile, new AnalysisEventListener<LinkedHashMap<String, String>>() {
                @Override
                public void invoke(LinkedHashMap<String, String> data, AnalysisContext context) {
//                     添加表头
//                     dataList.set(0, new ArrayList<>(List.of(newHeaders)));

                    // 将 LinkedHashMap 转换为 List
                    List<String> rowData = data.entrySet().stream()
                            .map(entry -> entry.getValue())
                            .collect(Collectors.toList());
                    dataList.add(rowData);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    // 替换表头
//                    List<String> headerList = Arrays.asList(newHeaders);
//                    dataList.set(0, headerList);

                    // 处理数据，删除 R 列之后的列
                    int lastColumnIndex = newHeaders.length - 1;
                    for (List<String> rowData : dataList) {
                        if (rowData.size() > lastColumnIndex) {
                            rowData.subList(lastColumnIndex + 1, rowData.size()).clear();
                        }
                    }

                    // 判断是否是多订单行【4000461302892059123,4000603287825059123】 天猫我打导出的订单存在该问题
                    for (int i = 0; i < dataList.size(); i++) {
                        List<String> rowList = dataList.get(i);//获得列数据
                        String orderNumStr = rowList.get(0);
                        if (orderNumStr == null) {//证明之前的逻辑没有补足订单号字段 需要重新补充一下 向上找订单
                            List<String> beforeRowList = dataList.get(i - 1);
                            fixTMallRow(beforeRowList,rowList, beforeRowList.get(0));
                            dataList.set(i,rowList);
                        }else {
                            String[] orderNums = orderNumStr.split(",");
                            rowList.set(0,orderNums[0]);// 自旋纠偏
                            dataList.set(i,rowList);
                            for (int o = 1; o < orderNums.length; o++) {// 迭代赋值给合并的订单
                                // 将i行的数据赋值给 i+1 行的【A列(0):订单编号	B列(1):联系人	C列(2)：买家昵称	D列(3):联系电话
                                // E列(4)：收货人省/市/区/详细地址，
                                // G列(6)：实付金额 H列(7)：付款时间 O列(14)：应付金额 P列(15)：快递单号 Q列(16)：店铺】
                                List<String> nextRowList = dataList.get(i + o);
                                // 防止逻辑错误 保护一下 只要是有订单号的都不处理
                                if (StringUtils.isNotBlank(nextRowList.get(0))) {
                                    continue;
                                }

                                fixTMallRow(rowList, nextRowList, orderNums[o]);
                                dataList.set(i+o,nextRowList);
                            }
                        }
                    }

                    // 合并 F 列和 I 列的内容到 F 列，并处理 O 列的值
                    for (List<String> rowData : dataList) {
                        if (rowData.size() > 5) {
                            String fValue = rowData.get(5); // F 列的值
                            String remarkMsg = "";
                            if (StringUtils.isNotBlank(fValue)) {
                                remarkMsg += "-卖家备注："+ fValue;
                            }

                            if (StringUtils.isNotBlank(rowData.get(8))) {
                                remarkMsg += "\n-买家备注："+ rowData.get(8);
                            }
                            rowData.set(5, remarkMsg);
                            rowData.set(8,"");

                            // 如果卖家备注为空，则不做解析
                            if (StringUtils.isBlank(fValue)) {
                                continue;
                            }
                            // 假设 F 列的值是 "3388--10/210" 这样的格式  "\\d+-+\\d+/\\d+"
                            Matcher matcher = Pattern.compile("(\\d+)-+(\\d+)(?:/+(\\d+)|-+(\\d+))").matcher(fValue);

                            int totalNum = 0;//总数量
                            String firstUnitPrice = null; // 单价
                            BigDecimal totalAmount = BigDecimal.ZERO;// 总计
                            while (matcher.find()){//                            改成使用while
                                // 循环处理数量 单价 总价
                                String num = matcher.group(2); // 本次循环数量
                                totalNum += Integer.valueOf(num);// 累加总数量
                                String unitPrice = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);// 本次循环单价
                                if (firstUnitPrice == null) {
                                    firstUnitPrice = unitPrice;//单价取第一次赋值的
                                }
                                BigDecimal amount = new BigDecimal(num).multiply(new BigDecimal(unitPrice));// 本次循序金额小计
                                totalAmount = totalAmount.add(amount);// 金额累加
                            }
                            if (totalNum>1) {// 如果匹配到的 则修改参数 如果没有匹配到原来的数据不动
                                rowData.set(13,String.valueOf(totalNum));// 数量赋值
                                rowData.set(12, firstUnitPrice);// 单价赋值
                                rowData.set(14,String.valueOf(totalAmount));// 总金额赋值
                            }
                        }
                    }
                }
            }).sheet().doRead();
            return dataList;
        }

        private static void fixTMallRow(List<String> fromRowList, List<String> toRowList, String fixedOrderNum) {
            toRowList.set(0, fixedOrderNum);
            toRowList.set(1, fromRowList.get(1));
            toRowList.set(2, fromRowList.get(2));
            toRowList.set(3, fromRowList.get(3));
            toRowList.set(4, fromRowList.get(4));
            toRowList.set(6, fromRowList.get(6));
            toRowList.set(7, fromRowList.get(7));
            toRowList.set(14, fromRowList.get(14));
            toRowList.set(15, fromRowList.get(15));
            toRowList.set(16, fromRowList.get(16));
        }
        
        public static void main(String[] args) throws IOException {
            File inputFile = new File("your_input_file.xlsx");
            byte[] result = convertTMallWoDaExcelToXls(new FileInputStream(inputFile));
            // 这里可以将 result 写入到 .xls 文件或进行其他操作
        }
    }

    


}