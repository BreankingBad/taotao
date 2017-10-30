package com.taotao.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.IDUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	TbItemMapper itemMapper;
	
	@Autowired
	TbItemDescMapper itemDescMapper;
	
	@Autowired
	JmsTemplate jmsTemplate;
	
	@Resource(name="itemAddTopic")
	Destination destination;
	
	@Autowired
	JedisClient jedisClient;
	
	@Value("${ITEM_INFO}")
	String ITEM_INFO;
	
	@Value("${ITEM_EXPIRE_TIME}")
	Integer ITEM_EXPIRE_TIME;
	
	@Override
	public TbItem getItemById(long itemId) {
		String redisKey = ITEM_INFO + ":" + itemId + ":BASE";
		
		try {
			String itemInfo = jedisClient.get(redisKey);
			if(StringUtils.isNoneBlank(itemInfo)) {
				
				TbItem item = JsonUtils.jsonToPojo(itemInfo, TbItem.class);
				System.out.println("get item redis cache: "+itemId);
				return item;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		TbItem	item = itemMapper.selectByPrimaryKey(itemId);
		
		try {
			jedisClient.set(redisKey, JsonUtils.objectToJson(item));
			jedisClient.expire(redisKey, ITEM_EXPIRE_TIME);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return item;
	}

	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		
		PageHelper.startPage(page, rows);
		TbItemExample example = new TbItemExample();
		List list = itemMapper.selectByExample(example);
		
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setTotal(pageInfo.getTotal());
		result.setRows(list);
		return result;
	}

	@Override
	public TaotaoResult addItem(TbItem tbItem, String desc) {
	
		final long itemId = IDUtils.genItemId();
		tbItem.setId(itemId);
		
		// 1正常 2下架 3删除
		tbItem.setStatus((byte) 1);
		
		tbItem.setCreated(new Date());
		tbItem.setUpdated(new Date());
		itemMapper.insert(tbItem);
		
		TbItemDesc itemDesc = new TbItemDesc();
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(new Date());
		itemDesc.setUpdated(new Date());
		itemDescMapper.insert(itemDesc);
		
		// 向activemq发送商品添加消息
		jmsTemplate.send(destination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				// 发送商品ID
				TextMessage textMessage = session.createTextMessage(itemId + "");
				return textMessage;
			}
		});
		
		return TaotaoResult.ok();
	}

	@Override
	public TbItemDesc getIemDescById(long itemId) {
		String redisKey = ITEM_INFO + ":" + itemId + ":DESC";
		
		try {
			String itemInfo = jedisClient.get(redisKey);
			if(StringUtils.isNoneBlank(itemInfo)) {
				TbItemDesc item = JsonUtils.jsonToPojo(itemInfo, TbItemDesc.class);
				System.out.println("get itemdesc redis cache: "+itemId);
				return item;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		
		try {
			jedisClient.set(redisKey, JsonUtils.objectToJson(tbItemDesc));
			jedisClient.expire(redisKey, ITEM_EXPIRE_TIME);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tbItemDesc;
	}

}
