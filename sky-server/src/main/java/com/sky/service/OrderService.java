package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.util.List;

/**
 * @author aoao
 * @create 2025-04-27-17:11
 */
public interface OrderService {
    /**
     * 用户提交订单业务接口
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult page(int page, int pageSize, Integer status);

    /**
     * 再来一单
     * @param id
     */
    void repetition(Long id);

    /**
     * 获取订单详情
     * @param id
     * @return
     */
    OrderVO detail(Long id);

    /**
     * 取消订单
     * @param id
     */
    void cancel(Long id);

    /**
     * 商家查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 商家拒单
     * @param ordersRejectionDTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 商家接单
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 商家派送
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成订单
     * @param id
     */
    void complete(Long id);

    /**
     * 商家取消订单
     * @param ordersCancelDTO
     */
    void adminCancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 查询待接单、待派送、派送中的订单数量
     * @return
     */
    OrderStatisticsVO statistics();

}
