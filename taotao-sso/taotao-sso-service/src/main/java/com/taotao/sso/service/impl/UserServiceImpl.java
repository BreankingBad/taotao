package com.taotao.sso.service.impl;


import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	TbUserMapper tbUserMapper;

	@Autowired
	JedisClient jedisClient;
	
	@Value("${KEY_SESSION}")
	String KEY_SESSION;
	
	@Value("${SESSION_EXPIRE}")
	int SESSION_EXPIRE;
	
	@Override
	public TaotaoResult checkData(String data, int type) {

		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();

		// 检查用户名
		if (type == 1) {
			criteria.andUsernameEqualTo(data);
			// 检查手机号
		} else if (type == 2) {
			criteria.andPhoneEqualTo(data);
			// 检查邮箱
		} else if (type == 3) {
			criteria.andEmailEqualTo(data);
		} else {
			return TaotaoResult.build(400, "参数非法");
		}

		List<TbUser> list = tbUserMapper.selectByExample(example);
		if (list != null && !list.isEmpty()) {
			return TaotaoResult.ok(false);
		}
		return TaotaoResult.ok(true);
	}

	@Override
	public TaotaoResult register(TbUser user) {
		// 检查数据的有效性
		
		if(StringUtils.isBlank(user.getUsername())) {
			return TaotaoResult.build(400, "用户名不能为空");
		}
		
		TaotaoResult nameResult = checkData(user.getUsername(), 1);
		
		if(!((boolean) nameResult.getData())) {
			return TaotaoResult.build(400, "用户名重复");
		}
		
		if(StringUtils.isBlank(user.getPassword())) {
			return TaotaoResult.build(400, "密码不能为空");
		}
		
		if (StringUtils.isNoneBlank(user.getPhone())) {
			TaotaoResult phoneResult = checkData(user.getPhone(), 2);
			if(!((boolean) phoneResult.getData())) {
				return TaotaoResult.build(400, "电话号码重复");
			}
		}
		
		if (StringUtils.isNoneBlank(user.getEmail())) {
			TaotaoResult emailResult = checkData(user.getEmail(), 3);
			if(!((boolean) emailResult.getData())) {
				return TaotaoResult.build(400, "邮箱重复");
			}
		}
		
		user.setCreated(new Date());
		user.setUpdated(new Date());
		
		// 密码要md5加密
		String md5Pwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5Pwd);
		
		tbUserMapper.insert(user);
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult login(String username, String password) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> users = tbUserMapper.selectByExample(example);
		if(users == null || users.isEmpty()) {
			return TaotaoResult.build(400, "用户名或密码有误");
		}
		
		TbUser user = users.get(0);
		if(!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
			return TaotaoResult.build(400, "用户名或密码有误");
		}
		
		String token = UUID.randomUUID().toString();
		
		String tokenRedisKey = KEY_SESSION + ":" + token;
		
		// 不用保存密码
		user.setPassword(null);
		jedisClient.set(tokenRedisKey, JsonUtils.objectToJson(user));
		jedisClient.expire(tokenRedisKey, SESSION_EXPIRE);
		
		return TaotaoResult.ok(token);
	}

	@Override
	public TaotaoResult getUserInfoByToken(String token) {
		String tokenRedisKey = KEY_SESSION + ":" + token;
		String json = jedisClient.get(tokenRedisKey);
		if(StringUtils.isBlank(json)) {
			TaotaoResult.build(400, "登录已失效");
		}
		
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		return TaotaoResult.ok(user);
	}

}
