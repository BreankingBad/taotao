package com.taotao.search.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.taotao.common.pojo.SearchItem;
import com.taotao.search.mapper.SearchItemMapper;

/**
 * 监听商品添加事件，同步索引库
 * @author mxm
 *
 */
public class ItemAddMessageListener implements MessageListener {

	@Autowired
	SearchItemMapper searchItemMapper;
	
	@Value("${SOLR_BASE_URL}")
	private String SOLR_BASE_URL;
	
	@Override
	public void onMessage(Message message) {
		
		try {
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			long itemId = Long.parseLong(text);
			
			Thread.sleep(1000);
			
			SearchItem item = searchItemMapper.getItemById(itemId);
			
			SolrClient client = new HttpSolrClient.Builder(SOLR_BASE_URL).build();
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", item.getId());
			document.addField("item_title", item.getTitle());
			document.addField("item_sell_point", item.getSell_point());
			document.addField("item_price", item.getPrice());
			document.addField("item_image", item.getImage());
			document.addField("item_category_name", item.getCategory_name());
			document.addField("item_desc", item.getItem_desc());
			client.add(document);
			
			client.commit(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
