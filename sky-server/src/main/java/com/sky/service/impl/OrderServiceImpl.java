package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.BaseException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;


import java.time.LocalDateTime;
import java.util.*;

import static com.sky.entity.Orders.PENDING_PAYMENT;

/**
 * @author aoao
 * @create 2025-04-27-17:11
 */
@Service
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;


    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, order);
        //判断地址是否为空
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //获取设置下单用户id
        Long userId = BaseContext.getCurrentId();
        order.setUserId(userId);
        //判断购物车是否为空
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectByUserId(userId);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //生成订单号
        String number = String.valueOf(System.currentTimeMillis());
        order.setNumber(number);
        //设置订单状态为  1待付款
        order.setStatus(PENDING_PAYMENT);
        //设置支付状态 0未支付
        order.setPayStatus(Orders.UN_PAID);
        //设置下单时间
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        order.setOrderTime(currentTime);
        //设置用户手机号和名字
        order.setPhone(addressBook.getPhone());
        order.setUserName(addressBook.getConsignee());
        //设置地址和收货人
        String actuallyAddress = addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();
        order.setAddress(actuallyAddress);
        order.setConsignee(addressBook.getConsignee());
        //向订单表插入一条订单数据
        orderMapper.insert(order);
        //遍历购物车
        Iterator<ShoppingCart> iterator = shoppingCarts.iterator();
        List<OrderDetail> orderDetails = new ArrayList<>();
        while (iterator.hasNext()) {
            ShoppingCart shoppingCart = iterator.next();
            OrderDetail orderDetail = OrderDetail.builder()
                                    .name(shoppingCart.getName())
                                    .orderId(order.getId())
                                    .dishId(shoppingCart.getDishId())
                                    .setmealId(shoppingCart.getSetmealId())
                                    .dishFlavor(shoppingCart.getDishFlavor())
                                    .number(shoppingCart.getNumber())
                                    .amount(shoppingCart.getAmount())
                                    .image(shoppingCart.getImage())
                                    .build();
            orderDetails.add(orderDetail);
        }
        //向明细表中插入n条数据
        orderDetailMapper.insertBatch(orderDetails);

        //清空当前用户购物车所有的商品
        shoppingCartMapper.deleteAll(userId);
        //获取订单id
        //Long id = order.getId();
        //返回vo
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .orderAmount(order.getAmount())
                .orderNumber(order.getNumber())
                .orderTime(order.getOrderTime())
                .id(order.getId()).
                build();
        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {

        //SELECT * FROM sky_take_out.orders;
        //update orders set pay_status =1 where id =7
        //update orders set status=2 where id =7

        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    public PageResult page(int pageNum, int pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // 分页条件查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// 订单id
                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    @Override
    public void repetition(Long id) {
        //通过订单号获取详细信息
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        //遍历
        Iterator<OrderDetail> iterator = orderDetails.iterator();
        while (iterator.hasNext()) {
            OrderDetail orderDetail = iterator.next();
            if(orderDetail.getDishId()==null || orderDetail.getSetmealId()==null){
                ShoppingCart shoppingCart = ShoppingCart.builder()
                        .name(orderDetail.getName())
                        .number(orderDetail.getNumber())
                        .dishId(orderDetail.getDishId())
                        .dishFlavor(orderDetail.getDishFlavor())
                        .setmealId(orderDetail.getSetmealId())
                        .image(orderDetail.getImage())
                        .amount(orderDetail.getAmount())
                        .userId(BaseContext.getCurrentId())
                        .createTime(LocalDateTime.now())
                        .build();
                //向购物车中插入获取到的菜品
                shoppingCartMapper.insert(shoppingCart);
            }
        }

    }

    @Override
    public OrderVO detail(Long id) {
        //查询订单表
        Orders orders = orderMapper.selectByOrderId(id);
        //查询详细表
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    @Override
    public void cancel(Long id) {
        // 订单状态为 1待付款 2待接单 可以执行取消订单 6已取消
        Orders orders = orderMapper.selectByOrderId(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Integer status = orders.getStatus();
        if (status == Orders.PENDING_PAYMENT) {
            cancelOrder(orders, "用户取消", null);
        } else if (status == Orders.TO_BE_CONFIRMED) {
            cancelOrder(orders, "用户取消", Orders.REFUND);
            // 直接退款，无需商家审核
            // weChatPayUtil.refund();
        } else {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    private void cancelOrder(Orders orders,String cancelReason, Integer payStatus) {
        // 修改订单状态为已取消
        orders.setStatus(Orders.CANCELLED);
        // 订单取消时间
        orders.setCancelTime(LocalDateTime.now());
        // 订单取消原因
        orders.setCancelReason(cancelReason);
        if (payStatus != null) {
            orders.setPayStatus(payStatus);
        }
        orderMapper.update(orders);
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        //分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        //查询
        Page<Orders> orders = orderMapper.pageQuery(ordersPageQueryDTO);
        //获取分页后的信息
        List<OrderVO> list = new ArrayList();
        //遍历orders
        if (orders != null && orders.getTotal() > 0) {
            for (Orders order : orders) {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(order, orderVO);
                orderVO.setOrderDishes("666");
                list.add(orderVO);
            }
        }
        return new PageResult(orders.getTotal(), list);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = orderMapper.selectByOrderId(ordersRejectionDTO.getId());
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if(orders.getStatus() == Orders.TO_BE_CONFIRMED) {
            Long id = orders.getId();
            String rejectionReason = ordersRejectionDTO.getRejectionReason();
            Integer status = Orders.CANCELLED;
            orderMapper.updateOrderCancelInfoByNumber(id, rejectionReason, status);

            //退款
            //refund()
        }
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        updateStatus(ordersConfirmDTO.getId(),Orders.TO_BE_CONFIRMED,Orders.CONFIRMED);
    }

    @Override
    public void delivery(Long id) {
       updateStatus(id,Orders.CONFIRMED,Orders.DELIVERY_IN_PROGRESS);
    }

    @Override
    public void complete(Long id) {
        updateStatus(id,Orders.DELIVERY_IN_PROGRESS,Orders.COMPLETED);
    }

    @Override
    public void adminCancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = orderMapper.selectByOrderId(ordersCancelDTO.getId());
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if(orders.getStatus() == Orders.CONFIRMED) {
            orders.setCancelReason(ordersCancelDTO.getCancelReason());
            orders.setCancelTime(LocalDateTime.now());
            orders.setStatus(Orders.CANCELLED);
            orderMapper.update(orders);
            //refund()
        }
    }

    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        List<Map<String, Object>> mapList = orderMapper.countByStatus();
        if (mapList != null) {
                for (Map<String, Object> map : mapList) {
                    Long status = (Long) map.get("status");// 获取状态值
                    Long count = (Long) map.get("count_status");// 获取统计值
                    switch (status.intValue()) {
                        case 2:
                            orderStatisticsVO.setToBeConfirmed(count);
                            break;
                        case 3:
                            orderStatisticsVO.setConfirmed(count);
                            break;
                        case 4:
                            orderStatisticsVO.setDeliveryInProgress(count);
                            break;
                        default:
                            break;
                    }
                }
        }
        return orderStatisticsVO;
    }

    private void updateStatus(Long id,Integer preStatus,Integer endStatus) {
        Orders orders = orderMapper.selectByOrderId(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
            Integer status = orders.getStatus();
            if (status == preStatus) {
                orders.setStatus(endStatus);
                orderMapper.update(orders);
            }
    }

}
