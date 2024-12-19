package cn.iocoder.yudao.module.gift.service.multiplatformorder;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.validation.ValidationUtilPlus;
import cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo.MultiPlatformOrderBatchPageReqVO;
import cn.iocoder.yudao.module.gift.controller.admin.multiplatformorder.vo.MultiPlatformOrderBatchSaveReqVO;
import cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder.MultiPlatformOrderBatchDO;
import cn.iocoder.yudao.module.gift.dal.dataobject.multiplatformorder.MultiPlatformOrderDO;
import cn.iocoder.yudao.module.gift.dal.mysql.multiplatformorder.MultiPlatformOrderBatchMapper;
import cn.iocoder.yudao.module.gift.dal.mysql.multiplatformorder.MultiPlatformOrderMapper;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
    public Long createMultiPlatformOrderBatch(MultiPlatformOrderBatchSaveReqVO createReqVO) {
        // 插入
        MultiPlatformOrderBatchDO multiPlatformOrderBatch = BeanUtils.toBean(createReqVO, MultiPlatformOrderBatchDO.class);
        multiPlatformOrderBatchMapper.insert(multiPlatformOrderBatch);

        // TODO 生成批次订单
        String fileUrl = multiPlatformOrderBatch.getFileUrl();
        String[] split = fileUrl.split("/");
        int length = split.length;
        try {
            byte[] fileContent = fileService.getFileContent(Long.valueOf(split[length - 3]), split[length - 1]);
            ByteArrayInputStream stream = new ByteArrayInputStream(fileContent);
            List<List<String>> dataList = ExcelConverter.getDataList(stream, ExcelConverter.DELIVERY_SYS_HEADERS);

            // todo 转成 MultiPlatformOrderDO

            ArrayList<MultiPlatformOrderDO> multiPlatformOrderDOS = new ArrayList<>();

            for (List<String> data : dataList) {
                MultiPlatformOrderDO multiPlatformOrderDO = convert2Order(multiPlatformOrderBatch, data);
                multiPlatformOrderDOS.add(multiPlatformOrderDO);
            }

            multiPlatformOrderMapper.insertBatch(multiPlatformOrderDOS);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 返回
        return multiPlatformOrderBatch.getId();
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
        multiPlatformOrderDO.setOrderCreateTime(LocalDateTimeUtils.parseDate(data.get(7)));
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
        String[] heads = ExcelConverter.DELIVERY_SYS_HEADERS;
        for (int i = 0; i < heads.length; i++) {
            originConfig.put(heads[i],data.get(i));
        }
        multiPlatformOrderDO.setOriginConfig(originConfig.toJSONString()); // 首次新增的时候添加原始数据，用于后期如果修改的话可以追溯对比

        // 统一校验
        ValidationUtilPlus.validateBeanAndThrow(multiPlatformOrderDO, MULTI_PLATFORM_ORDER_VALID_ERROR);

        return multiPlatformOrderDO;
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

    static class ExcelConverter {

        public static final String[] DELIVERY_SYS_HEADERS = new String[]{
                "订单号", "姓名", "别名", "电话", "地址", "备注说明",
                "订单金额", "下单时间", "销售渠道SKU", "SKU", "Item_ID",
                "产品名称", "单价", "数量", "小计", "快递单号", "销售渠道"
        };
        public static byte[] convertExcelToXls(InputStream inputFile) throws IOException {
            // 定义新的表头

            List<List<String>> dataList = getDataList(inputFile, DELIVERY_SYS_HEADERS);

            return getBytes(DELIVERY_SYS_HEADERS, dataList);
        }

        private static byte[] getBytes(String[] newHeaders, List<List<String>> dataList) throws IOException {
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
            byte[] result = convertExcelToXls(new FileInputStream(inputFile));
            // 这里可以将 result 写入到 .xls 文件或进行其他操作
        }
    }

    


}