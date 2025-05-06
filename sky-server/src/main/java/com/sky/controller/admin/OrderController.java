package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author aoao
 * @create 2025-05-04-12:26
 */
@RestController
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("分页查询订单:{}", ordersPageQueryDTO);
        PageResult orderVO = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(orderVO);
    }

    @GetMapping("/details/{id}")
    @ApiOperation("获取订单详情")
    public Result<OrderVO> detail(@PathVariable Long id) {
        log.info("获取订单详情,id:{}",id);
        OrderVO orderVO = orderService.detail(id);
        return Result.success(orderVO);
    }

    @PutMapping("/rejection")
    @ApiOperation("商家拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("商家拒单:{}", ordersRejectionDTO);
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/confirm")
    @ApiOperation("商家接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("商家接单:{}", ordersConfirmDTO);
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @ApiOperation("商家派送")
    public Result delivery(@PathVariable Long id) {
        log.info("商家派送:{}", id);
        orderService.delivery(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id) {
        log.info("完成订单:{}" ,id);
        orderService.complete(id);
        return Result.success();
    }

    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单:{}", ordersCancelDTO);
        orderService.adminCancel(ordersCancelDTO);
        return Result.success();
    }

    @GetMapping("/statistics")
    @ApiOperation("统计订单")
    public Result<OrderStatisticsVO> statistics() {
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }
}
