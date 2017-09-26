package com.taotao.search.dao;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.SearchResult;

@Repository
public class SearchDao {

	@Value("${SOLR_BASE_URL}")
	private String SOLR_BASE_URL;
	
	public SearchResult search(SolrQuery solrQuery) throws Exception {
		
		SearchResult searchResult = new SearchResult();
		
		SolrClient client = new HttpSolrClient.Builder(SOLR_BASE_URL).build();
		QueryResponse response = client.query(solrQuery);
		
		SolrDocumentList solrDocumentList = response.getResults();
		searchResult.setRecordCount(solrDocumentList.getNumFound());
		
		List<SearchItem> itemList = new ArrayList<>();
		for (SolrDocument solrDocument : solrDocumentList) {
			SearchItem searchItem = new SearchItem();
			
			searchItem.setId((String) solrDocument.get("id"));
			searchItem.setCategory_name((String) solrDocument.get("item_category_name"));
			searchItem.setImage((String) solrDocument.get("item_image"));
			searchItem.setItem_desc((String) solrDocument.get("item_desc"));
			searchItem.setSell_point((String) solrDocument.get("item_sell_point"));
			searchItem.setPrice((long) solrDocument.get("item_price"));
			
			Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			if(list != null && list.size() > 0) {
				// 如果标题有高亮显示，则取高亮显示的标题，否则取原来的标题
				searchItem.setTitle(list.get(0));
			}else {
				searchItem.setTitle((String) solrDocument.get("item_title"));
			}
			
			itemList.add(searchItem);
		}
		searchResult.setItemList(itemList);
		
		return searchResult;
	}
}
