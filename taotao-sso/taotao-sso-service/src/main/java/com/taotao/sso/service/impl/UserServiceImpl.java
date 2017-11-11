package com.taotao.sso.service.impl;

import static org.hamcrest.CoreMatchers.nullValue;

import java.util.List;

import org.apache.zookeeper.Op.Create;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	TbUserMapper tbUserMapper;
	
	@Override
	public TaotaoResult checkData(String data, int type) {
		
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		// 检查用户名
		if(type == 1) {
			criteria.andUsernameEqualTo(data);
		// 检查手机号
		}else if(type == 2) {
			criteria.andPhoneEqualTo(data);
		// 检查邮箱
		}else if(type == 3) {
			criteria.andEmailEqualTo(data);
		}else {
			return TaotaoResult.build(400, "参数非法")	;
		}
		
		List<TbUser> list = tbUserMapper.selectByExample(example);
		if(list != null && !list.isEmpty()) {
			return TaotaoResult.ok(false);
		}
		return TaotaoResult.ok(true);
	}

}
