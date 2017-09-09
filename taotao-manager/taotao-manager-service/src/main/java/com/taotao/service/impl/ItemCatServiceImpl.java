package com.taotao.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.mapper.TbItemCatMapper;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.pojo.TbItemCatExample.Criteria;
import com.taotao.service.ItemCatService;

@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Override
	public List<EasyUITreeNode> getItemCatList(long parentId) {
		
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		
		List<TbItemCat> catList = itemCatMapper.selectByExample(example);
		
		List<EasyUITreeNode> gridResultList = new ArrayList<>();
		for(TbItemCat itemCat:catList) {
			EasyUITreeNode result = new EasyUITreeNode();
			result.setId(itemCat.getId());
			result.setText(itemCat.getName());
			result.setState(itemCat.getIsParent()?"closed":"open");
			
			gridResultList.add(result);
		}
		
		return gridResultList;
	}

}
