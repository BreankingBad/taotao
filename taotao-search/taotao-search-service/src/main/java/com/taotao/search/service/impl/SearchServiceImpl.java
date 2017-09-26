package com.taotao.search.service.impl;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.SearchResult;
import com.taotao.search.dao.SearchDao;
import com.taotao.search.service.SearchService;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SearchDao searchDao;

	@Override
	public SearchResult search(String keyword, int page, int rows) throws Exception {

		SolrQuery solrQuery = new SolrQuery();

		if (page < 1) {
			page = 1;
		}
		if (rows < 1) {
			rows = 10;
		}

		solrQuery.setQuery(keyword);

		// start是开始查询的第几条记录
		solrQuery.setStart((page - 1) * rows);
		solrQuery.setRows(rows);

		solrQuery.setHighlight(true);
		solrQuery.addHighlightField("item_title");
		solrQuery.setHighlightSimplePre("<em>");
		solrQuery.setHighlightSimplePost("</em>");

		SearchResult searchResult = searchDao.search(solrQuery);
		long recordCount = searchResult.getRecordCount();

		int totalPages = (int) (recordCount / rows);
		if (recordCount % rows > 0) {
			totalPages++;
		}
		searchResult.setTotalPages(totalPages);

		return searchResult;
	}

}
