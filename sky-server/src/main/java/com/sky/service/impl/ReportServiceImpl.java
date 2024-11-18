package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        // 计算开始时间到结束时间的所有日期
        List<LocalDate> dateTimeList = new ArrayList<>();
        dateTimeList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateTimeList.add(begin);
        }
        String dataDate = StringUtils.join(dateTimeList, ",");

        // 查询每天的营业额
        List<Long> data = new ArrayList<>();
        for (LocalDate time : dateTimeList) {
            LocalDateTime dateTimeMin = LocalDateTime.of(time, LocalTime.MIN);
            LocalDateTime dateTimeMax = LocalDateTime.of(time, LocalTime.MAX);
            Long Statistics = orderMapper.StatisticsGetByTime(dateTimeMin,dateTimeMax);
            Statistics= Statistics == null ? 0 : Statistics;
            data.add(Statistics);
        }
        String moneyDate = StringUtils.join(data, ",");

        return TurnoverReportVO
                .builder()
                .turnoverList(moneyDate)
                .dateList(dataDate)
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        // 计算开始时间到结束时间的所有日期
        List<LocalDate> dateTimeList = new ArrayList<>();
        dateTimeList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateTimeList.add(begin);
        }
        String dataDate = StringUtils.join(dateTimeList, ",");

        // 查询所有用户总数
        List<Long> data = new ArrayList<>();
        List<Long> create = new ArrayList<>();

        // 获取每天新增的用户数量，用前缀和再计算总的用户数
        for (LocalDate time : dateTimeList) {
            LocalDateTime dateTimeMin = LocalDateTime.of(time, LocalTime.MIN);
            LocalDateTime dateTimeMax = LocalDateTime.of(time, LocalTime.MAX);
            Long total = orderMapper.total(dateTimeMin,dateTimeMax);
            total= total == null ? 0 : total;
            if (!data.isEmpty()){
                long totalData = data.get(data.size() - 1) + total;
                data.add(totalData);
            } else {
                // 该判断循环第一次必然会跳转到else里面，因为第一次循环集合没数据就为假
                // 第一次就是用来计算在统计时间之前有多少个用户，而这个判断也只会进入一次
                Long aLong = orderMapper.total(LocalDateTime.now().minusYears(100), dateTimeMax);
                data.add(aLong);
            }
            create.add(total);
        }
        String totalDate = StringUtils.join(data, ",");
        // 新增用户数列表
        String newDate = StringUtils.join(create, ",");

        return UserReportVO
                .builder()
                .newUserList(newDate)
                .totalUserList(totalDate)
                .dateList(dataDate)
                .build();


    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        // 计算开始时间到结束时间的所有日期
        List<LocalDate> dateTimeList = new ArrayList<>();
        dateTimeList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateTimeList.add(begin);
        }
        String dataDate = StringUtils.join(dateTimeList, ",");
        // 查询出完成的订单数与总订单数
        List<Integer> total = new ArrayList<>();
        List<Integer> valid = new ArrayList<>();
        for (LocalDate time : dateTimeList) {
            LocalDateTime dateTimeMin = LocalDateTime.of(time, LocalTime.MIN);
            LocalDateTime dateTimeMax = LocalDateTime.of(time, LocalTime.MAX);
            Integer complete = orderMapper.getByStatus(dateTimeMin,dateTimeMax,5);
            complete= complete == null ? 0 : complete;

            valid.add(complete);

            Integer all = orderMapper.getByStatus(dateTimeMin,dateTimeMax,null);
            all= all == null ? 0 : all;

            total.add(all);
        }
        // 订单总列表与完成的订单列表
        String totalDate = StringUtils.join(total, ",");
        String validDate = StringUtils.join(valid, ",");
        // 订单总数与订单完成数
        Integer totalSize = 0;
        Integer validSize = 0;
        for (int i = 0; i < dateTimeList.size(); i++) {
            if (total.get(i) != 0) {
                totalSize += total.get(i);
            }
            if (valid.get(i) != 0) {
                validSize += valid.get(i) ;
            }
        }
        // 完成率,使用BigDecimal精确计算
        BigDecimal orderCompletionRate;
        BigDecimal bigDecimal = new BigDecimal(totalSize.toString());
        BigDecimal bigDecimal1 = new BigDecimal(validSize.toString());
        // 判断非空就不运算
        if (validSize == 0 && totalSize == 0) {
            orderCompletionRate = BigDecimal.valueOf(0.0);
        } else {
            // 运算，保留2位小数，四舍五入
            orderCompletionRate = bigDecimal1.divide(bigDecimal,2, RoundingMode.HALF_UP);
        }
        // 再将BigDecimal对象转换为double对象
        double doubleValue = orderCompletionRate.doubleValue();
        return OrderReportVO
                .builder()
                .orderCompletionRate(doubleValue)
                .dateList(dataDate)
                .orderCountList(totalDate)
                .validOrderCountList(validDate)
                .validOrderCount(validSize)
                .totalOrderCount(totalSize)
                .build();
    }

    /**
     * 查询销量排名top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO RankTop(LocalDate begin, LocalDate end) {

        // 查询已完成订单中销量最好的10款
        LocalDateTime dateTimeMin = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime dateTimeMax = LocalDateTime.of(end, LocalTime.MAX);

        // 名称列表与销售数量列表
        List<GoodsSalesDTO> salesDTOList = orderDetailMapper.getByTop(dateTimeMin,dateTimeMax);

        // 将数据提取到相应列表
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (GoodsSalesDTO salesDTO : salesDTOList) {
            nameList.add(salesDTO.getName());
            numberList.add(salesDTO.getNumber());
        }
        // 将数据转换为字符串
        String nameString = StringUtils.join(nameList, ",");
        String numberString = StringUtils.join(numberList, ",");
        return SalesTop10ReportVO
                .builder()
                .nameList(nameString)
                .numberList(numberString)
                .build();
    }

    /**
     * 导出营业数据
     * @param response
     */
    @Override
    public void export(HttpServletResponse response) {
        // 从数据库获取近30天的运营数据
        LocalDate begin = LocalDate.now().minusDays(31);
        LocalDate end = LocalDate.now().minusDays(1);

        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin,LocalTime.MIN), LocalDateTime.of(end,LocalTime.MAX));

        // 通过POI将数据写入表格
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx"); // 通过反射获取当前项目下的输入流对象
        try {
            // 通过输入的模板文件创建一个excel表
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);

            // 获取工作簿
            XSSFSheet sheet = excel.getSheet("sheet1");

            // 将获取的数据写入到表格中

            // 概览数据，获取行获取列再设置值
            sheet.getRow(1).getCell(1).setCellValue("时间:"+begin+"至"+end);
            sheet.getRow(3).getCell(2).setCellValue(businessData.getTurnover());
            sheet.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            sheet.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());
            sheet.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());
            sheet.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());

            // 明细数据，for循环写入30天里每一天的数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                BusinessDataVO data = workspaceService.getBusinessData(LocalDateTime.of(date,LocalTime.MIN), LocalDateTime.of(date,LocalTime.MAX));
                // 获得行
                XSSFRow row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(data.getTurnover());
                row.getCell(3).setCellValue(data.getValidOrderCount());
                row.getCell(4).setCellValue(data.getOrderCompletionRate());
                row.getCell(5).setCellValue(data.getUnitPrice());
                row.getCell(6).setCellValue(data.getNewUsers());
            }

            // 通过输入流将数据写入到客户端浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            outputStream.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
