package com.taotao.search.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.mapper.SearchItemMapper;
import com.taotao.search.service.SearchItemService;

@Service
public class SearchItemServiceImpl implements SearchItemService {

	@Autowired
	private SearchItemMapper searchItemMapper;
	
	@Value("${SOLR_BASE_URL}")
	private String SOLR_BASE_URL;
	
	@Override
	public TaotaoResult importItemsToIndex() {
		
		try {
			SolrClient client = new HttpSolrClient.Builder(SOLR_BASE_URL).build();
			
			List<SearchItem> searchItems = searchItemMapper.getItemList();
			for(SearchItem item:searchItems) {
				SolrInputDocument document = new SolrInputDocument();
				
				document.addField("id", item.getId());
				document.addField("item_title", item.getTitle());
				document.addField("item_sell_point", item.getSell_point());
				document.addField("item_price", item.getPrice());
				document.addField("item_image", item.getImage());
				document.addField("item_category_name", item.getCategory_name());
				document.addField("item_desc", item.getItem_desc());
				client.add(document);
			}

			client.commit();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return TaotaoResult.build(500, "数据导入失败");
		}
		return TaotaoResult.ok();
	}

}
