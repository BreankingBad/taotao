package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		
		List<TbContentCategory> categories = contentCategoryMapper.selectByExample(example);
		
		List<EasyUITreeNode> nodeList = new ArrayList<>();
		for (TbContentCategory category:categories) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(category.getId());
			node.setText(category.getName());
			node.setState(category.getIsParent()?"closed":"open");
			
			nodeList.add(node);
		}
		return nodeList;
	}

	@Override
	public TaotaoResult addContentCategory(long parentId, String name) {
		TbContentCategory category = new TbContentCategory();
		category.setParentId(parentId);
		category.setName(name);
		category.setIsParent(false);
		// 排序，默认为1
		category.setSortOrder(1);
		// 状态 1正常 2删除
		category.setStatus(1);
		category.setCreated(new Date());
		category.setUpdated(new Date());
		
		contentCategoryMapper.insert(category);
		
		// 把父节点的isParent改为true
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if (!parent.getIsParent()) {
			parent.setIsParent(true);
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		return TaotaoResult.ok(category);
	}

	@Override
	public TaotaoResult updateContentCategory(long id,String name) {
		
		TbContentCategory category = new TbContentCategory();
		category.setName(name);
		category.setId(id);
		contentCategoryMapper.updateByPrimaryKeySelective(category);
		
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult deleteContentCategory(long id) {
		
		TbContentCategory category = contentCategoryMapper.selectByPrimaryKey(id);
		
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(category.getParentId());
		
		int siblingCount = contentCategoryMapper.countByExample(example);
		// 如果父节点只有一个子节点，那么删除子节点后，就变成叶子节点。
		if(siblingCount == 1) {
			TbContentCategory newParentCategory = new TbContentCategory();
			newParentCategory.setIsParent(false);
			newParentCategory.setId(category.getParentId());
			contentCategoryMapper.updateByPrimaryKeySelective(newParentCategory);
		}
		
		
		TbContentCategoryExample childExample = new TbContentCategoryExample();
		Criteria childCriteria = childExample.createCriteria();
		childCriteria.andParentIdEqualTo(id);
		List<TbContentCategory> childList = contentCategoryMapper.selectByExample(childExample);
		if(childList != null && childList.size() > 0) {
			for (Iterator iterator = childList.iterator(); iterator.hasNext();) {
				TbContentCategory childContentCategory = (TbContentCategory) iterator.next();
				deleteContentCategory(childContentCategory.getId());
			}
		}
		
		contentCategoryMapper.deleteByPrimaryKey(id);
	
		return TaotaoResult.ok();
	}

}
