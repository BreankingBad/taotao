package com.taotao.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ItemAddMessageListener implements MessageListener {

	@Autowired
	ItemService itemService;
	
	@Autowired
	FreeMarkerConfigurer freeMarkerConfigurer;
	
	@Value("${HTML_OUT_PATH}")
	String HTML_OUT_PATH;
	
	@Override
	public void onMessage(Message message) {
		
		
		try {
			TextMessage textMessage = (TextMessage) message;
			long itemId = Long.parseLong(textMessage.getText());
			
			System.out.println("get message:"+itemId);
			// 等待事务提交
			Thread.sleep(1000);
			
			TbItem tbItem = itemService.getItemById(itemId);
			Item item = new Item(tbItem);
			
			TbItemDesc tbItemDesc = itemService.getIemDescById(itemId);
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			
			Template template = configuration.getTemplate("item.ftl");
			Map data = new HashMap<>();
			data.put("item", item);
			data.put("itemDesc", tbItemDesc);
			
			Writer writer = new FileWriter(new File(HTML_OUT_PATH + itemId + ".html"));
			template.process(data, writer);
			
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
