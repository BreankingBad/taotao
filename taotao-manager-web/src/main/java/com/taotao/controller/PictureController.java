package com.taotao.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

@Controller
public class PictureController {

	@Value("${qiniu_bucket}")
	private String QINIU_BUCKET;
	@Value("${qiniu_base_url}")
	private String QINIU_BASE_URL;
	@Value("${qiniu_accessKey}")
	private String QINIU_ACCESS_KEY;
	@Value("${qiniu_secretKey}")
	private String QINIU_SECRET_KEY;
	
	@RequestMapping("/pic/upload")
	@ResponseBody
	public Map uploadPic(MultipartFile uploadFile) {
		
		//构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone2());
		//...其他参数参考类注释
		UploadManager uploadManager = new UploadManager(cfg);
		
		
		Auth auth = Auth.create(QINIU_ACCESS_KEY, QINIU_SECRET_KEY);
		String upToken = auth.uploadToken(QINIU_BUCKET);
		
		//默认不指定key的情况下，以文件内容的hash值作为文件名
		String key = null;
		try {
			Response response = uploadManager.put(uploadFile.getBytes(), key, upToken);
			// 解析上传成功的结果
		    DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
		    System.out.println(putRet.key);
		    System.out.println(putRet.hash);
		    
		    Map result = new HashMap<>();
		    result.put("error", 0);
		    result.put("url", QINIU_BASE_URL + putRet.key);
		    return result;
		} catch (QiniuException ex) {
			Response r = ex.response;
		    System.err.println(r.toString());
		    try {
		        System.err.println(r.bodyString());
			    Map result = new HashMap<>();
			    result.put("error", 1);
			    result.put("message", r.bodyString());
			    return result;
		    } catch (QiniuException ex2) {
		        //ignore
		    		Map result = new HashMap<>();
			    result.put("error", 1);
			    result.put("message", "图片上传失败");
			    return result;
		    }
		    
		}catch (IOException e) {
		    Map result = new HashMap<>();
		    result.put("error", 1);
		    result.put("message", "图片上传失败");
		    return result;
		}
		
	}
}
