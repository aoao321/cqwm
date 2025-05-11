package com.sky.service.impl;

import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author aoao
 * @create 2025-05-09-22:34
 */
@Service
@Transactional
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //获取日期
        List<String> dateList = getDateList(begin, end);
        //查询营业额
        List<Long> turnover = orderMapper.countTurnoverByDate(dateList);
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnover, ","))
                .build();
        return turnoverReportVO;
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //获取日期
        List<String> dateList = getDateList(begin, end);
        //查询总用户数量
        List<Long> countUser = userMapper.countByDate(dateList);
        //新增用户量
        List<Long> countNewUser = userMapper.countNewByDate(dateList);

        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(countUser,","))
                .newUserList(StringUtils.join(countNewUser,","))
                .build();

        return userReportVO;
    }

    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //获取日期
        List<String> dateList = getDateList(begin, end);
        //根据日期查询总订单数
        List<Long> countOrderList = orderMapper.countByDate(dateList);
        //计算出订单总数
        long totalOrderCount = 0;
        for (Long count : countOrderList) {
            totalOrderCount += count;
        }
        //有效订单列表
        List<Long> validOrderCountList = orderMapper.countTurnoverByDate(dateList);
        long validOrderCount =0;
        for (Long count : validOrderCountList) {
            validOrderCount += count;
        }
        //订单完成率    有效/总数
        double orderCompletionRate = (double)validOrderCount / totalOrderCount;

        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .totalOrderCount((int)totalOrderCount)
                .validOrderCount((int)validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(StringUtils.join(countOrderList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .build();
        return orderReportVO;
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        //获取日期
        List<String> dateList = getDateList(begin, end);
        List<Map<String,Object>> top10 = orderDetailMapper.selectTop10(begin,end);
        List<String> nameList = new ArrayList<>();
        List<Long> numberList = new ArrayList<>();
        if (top10!=null && top10.size()>0) {
            for (Map<String,Object> map : top10) {
                String name = (String) map.get("name");
                BigDecimal totalNumberBD = (BigDecimal) map.get("total_number");
                Long totalNumber = totalNumberBD.longValue(); // 或 longValueExact()
                map.put("total_number", totalNumber);
                nameList.add(name);
                numberList.add(totalNumber);
            }
        }
        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();

        return salesTop10ReportVO;
    }

    private List<String> getDateList(LocalDate begin, LocalDate end) {
        //具体的区间时间字符串
        List<LocalDate> date = new ArrayList<>();
        for (LocalDate day = begin ; !day.isAfter(end) ; day = day.plusDays(1)) {
            date.add(day);
        }
        //用stream流把LocalDate映射成String
        List<String> formattedDates = date.stream()
                .map(dt -> dt.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .collect(Collectors.toList());
        return formattedDates;
    }
}
