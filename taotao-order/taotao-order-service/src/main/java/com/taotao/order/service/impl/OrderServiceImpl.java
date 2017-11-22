package com.taotao.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbOrderShippingMapper;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	TbOrderItemMapper tbOrderItemMapper;
	
	@Autowired
	TbOrderShippingMapper tbOrderShippingMapper;
	
	@Autowired
	TbOrderMapper tbOrderMapper;
	
	@Autowired
	JedisClient jedisClient;
	
	@Value("${ORDER_ID_GEN_KEY}")
	String ORDER_ID_GEN_KEY;
	@Value("${ORDER_ITEM_ID_GEN_KEY}")
	String ORDER_ITEM_ID_GEN_KEY;
	@Value("${ORDER_ID_GEN_BEGIN}")
	String ORDER_ID_GEN_BEGIN;
	
	@Override
	public TaotaoResult createOrder(OrderInfo orderInfo) {
		if(!jedisClient.exists(ORDER_ID_GEN_KEY)) {
			jedisClient.set(ORDER_ID_GEN_KEY, ORDER_ID_GEN_BEGIN);
		}
		String orderId = jedisClient.incr(ORDER_ID_GEN_KEY).toString();
		
		orderInfo.setOrderId(orderId);
		orderInfo.setPostFee("0");
		// 1 未付款
		orderInfo.setStatus(1);
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		
		// 向订单表插入订单
		tbOrderMapper.insert(orderInfo);
		
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		for(TbOrderItem orderItem:orderItems) {
			String orderItemId = jedisClient.incr(ORDER_ITEM_ID_GEN_KEY).toString();
			orderItem.setId(orderItemId);
			orderItem.setOrderId(orderId);
			tbOrderItemMapper.insert(orderItem);
		}
		
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		tbOrderShippingMapper.insert(orderShipping);
		return TaotaoResult.ok(orderId);
	}

}
