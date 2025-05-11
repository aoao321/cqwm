package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author aoao
 * @create 2025-04-27-18:22
 */
@Mapper
public interface OrderMapper {
    /**
     * 插入一条订单
     * @param order
     */
    void insert(Orders order);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);


    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单id查询订单
     * @param id
     * @return
     */
    @Select("SELECT * FROM orders WHERE id=#{id}")
    Orders selectByOrderId(Long id);

    /**
     * 根据订单号设置取消订单原因
     * @param number
     * @param cancelReason
     */
    @Update("UPDATE orders SET cancel_reason=#{reason},rejection_reason=#{reason},status=#{status} WHERE id=#{number}")
    void updateOrderCancelInfoByNumber(@Param("number") Long number,@Param("reason") String cancelReason,@Param("status") Integer status);

    /**
     * 统计待接单、待派送、派送中的订单数量
     * @return
     */
    //@MapKey("status")
    List<Map<String, Object>> countByStatus();

    /**
     * 获取未支付的超时订单
     * @param status
     * @param time
     */
    @Select("SELECT * FROM orders WHERE status=#{status} AND order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLT(@Param("status") Integer status,@Param("time") LocalDateTime time);

    /**
     * 根据日期统计营业额
     * @param dateList
     * @return
     */
    List<Long> countTurnoverByDate(@Param("dateList") List<String> dateList);

    /**
     * 统计订单总数
     * @param dateList
     * @return
     */
    List<Long> countByDate(@Param("dateList") List<String> dateList);


}
