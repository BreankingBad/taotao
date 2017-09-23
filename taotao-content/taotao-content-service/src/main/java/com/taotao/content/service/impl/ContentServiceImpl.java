package com.taotao.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.content.service.ContentService;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	TbContentMapper tbcontentMapper;
	
	@Autowired
	JedisClient jedisClient;
	
	@Value("${INDEX_CONTENT}")
	String INDEX_CONTENT;
	
	@Override
	public TaotaoResult addContent(TbContent tbContent) {
		tbContent.setCreated(new Date());
		tbContent.setUpdated(new Date());
		
		tbcontentMapper.insert(tbContent);
		
		// 同步缓存，把对应的缓存删掉即可
		jedisClient.hdel(INDEX_CONTENT, tbContent.getCategoryId().toString());
		return TaotaoResult.ok();
	}

	@Override
	public List<TbContent> getContentByCategoryId(long categoryId) {
		
		try {
			// 读取缓存
			String json = jedisClient.hget(INDEX_CONTENT, categoryId + "");
			if (StringUtils.isNotBlank(json)) {
				List<TbContent> result = JsonUtils.jsonToList(json, TbContent.class);
				System.out.println("get cache from redis success!  categoryId:"+categoryId);
				return result;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		
		List<TbContent> contentList = tbcontentMapper.selectByExample(example);
		
		System.out.println("get list from db success!  categoryId:"+categoryId);

		
		try {
			// 添加缓存
			jedisClient.hset(INDEX_CONTENT, categoryId + "", JsonUtils.objectToJson(contentList));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return contentList;
	}

}
