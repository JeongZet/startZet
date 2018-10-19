package com.gebalstory.gcbus.common.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gebalstory.gcbus.common.common.CommandMap;
import com.gebalstory.gcbus.common.service.CommonService;

@Controller
public class CommonController {

	@Resource(name="commonService")
	private CommonService commonService;
	
	@RequestMapping(value="/downloadFile.do")
	public void downloadFile(CommandMap commandMap, HttpServletResponse res) throws Exception{
		Map<String,Object> tempMap = commonService.selectFileInfo(commandMap.getMap());
		
		String original_File_Name = tempMap.get("ORIGINAL_FILE_NAME").toString();
		String stored_File_Name = tempMap.get("STORED_FILE_NAME").toString();
		
		byte[] fileByte = FileUtils.readFileToByteArray(new File("C:\\Users\\JEONG\\Desktop\\dev\\file\\"+stored_File_Name));
		
        res.setContentType("application/octet-stream");
        res.setContentLength(fileByte.length);
        res.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(original_File_Name,"UTF-8")+"\";");
        res.setHeader("Content-Transfer-Encoding", "binary");
        res.getOutputStream().write(fileByte);
          
        res.getOutputStream().flush();
        res.getOutputStream().close();
		
	}
	
}
