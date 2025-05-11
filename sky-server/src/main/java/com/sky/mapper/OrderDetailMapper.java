package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author aoao
 * @create 2025-04-27-18:24
 */
@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单详细表
     * @param orderDetails
     */
    void insertBatch(@Param("orderDetails") List<OrderDetail> orderDetails);

    /**
     * 根据订单id查询订单明细
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);


    /**
     * 查询销量前10
     * @param begin
     * @param end
     * @return
     */
    List<Map<String, Object>> selectTop10(@Param("begin") LocalDate begin,@Param("end") LocalDate end);
}
