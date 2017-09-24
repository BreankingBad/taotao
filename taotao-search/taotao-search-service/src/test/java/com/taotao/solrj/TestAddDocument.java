package com.taotao.solrj;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.aspectj.apache.bcel.generic.InstructionConstants.Clinit;
import org.junit.Test;

public class TestAddDocument {

	@Test
	public void testAddDocument() {
		SolrClient client = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/collection1").build();
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", "test002");
		document.addField("item_title", "测试商品002");
		document.addField("item_desc", "001描述");
		
		try {
			client.add(document);
			client.commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void deleteDocumentById() {
		SolrClient client = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/collection1").build();
		
		try {
			client.deleteById("test001");
			client.commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void deleteDocumentByQuery() {
		SolrClient client = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/collection1").build();
		
		try {
			// 注意，由于分词，如果是其他title为测试商品XXX也会被删除
			client.deleteByQuery("item_title:测试商品001");
			client.commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
