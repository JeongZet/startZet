package com.gebalstory.gcbus.common.util.file;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Component("fileUtils")
public class FileUtils {

	private static final String FILE_PATH = "C:\\Users\\JEONG\\Desktop\\dev\\file\\"; 
	
	public Map<String,Object> parseInsertFileInfo(Map<String,Object> map, HttpServletRequest req) throws Exception{
		
		MultipartHttpServletRequest mulReq = (MultipartHttpServletRequest)req;
		Iterator<String> iterator = mulReq.getFileNames();
		File fold = new File(FILE_PATH);
		if(!fold.exists()) {
			fold.mkdir();
		}
		
		Map<String,Object> tempMap = new HashMap<String,Object>();
		
		if(iterator.hasNext()) {
			MultipartFile mulFile = mulReq.getFile(mulReq.getFileNames().next());
			if(!mulFile.isEmpty()) {
				String original_File_Name = mulFile.getOriginalFilename();
				String original_Extension = original_File_Name.substring(original_File_Name.lastIndexOf("."));
				
				String stored_File_Name = getRandomString()+original_Extension;
				
				File file = new File(FILE_PATH+stored_File_Name);
				
				mulFile.transferTo(file);
				
				tempMap.put("BOARD_IDX", map.get("BOARD_IDX"));
				tempMap.put("ORIGINAL_FILE_NAME", original_File_Name);
				tempMap.put("STORED_FILE_NAME", stored_File_Name);
				tempMap.put("FILE_SIZE", mulFile.getSize());
				tempMap.put("CREA_ID", map.get("CREATOR"));
			}
		}
		return tempMap;
	}
	
	public Map<String,Object> parseUpdateFileInfo(Map<String,Object> map, HttpServletRequest req) throws Exception{
		
		MultipartHttpServletRequest mulReq = (MultipartHttpServletRequest)req;
		
		Map<String,Object> tempMap = new HashMap<String,Object>();
		
		if(mulReq.getFileNames().hasNext()) {
			MultipartFile mulFile = mulReq.getFile(mulReq.getFileNames().next());
			
			if(!mulFile.isEmpty()) {
			
				String original_File_Name = mulFile.getOriginalFilename();
				String original_Extension = original_File_Name.substring(original_File_Name.lastIndexOf("."));
				
				String stored_File_Name = getRandomString()+original_Extension; 
				
				mulFile.transferTo(new File(FILE_PATH+stored_File_Name));
				
				tempMap.put("BOARD_IDX", map.get("BOARD_IDX"));
				tempMap.put("ORIGINAL_FILE_NAME", original_File_Name);
				tempMap.put("STORED_FILE_NAME", stored_File_Name);
				tempMap.put("FILE_SIZE", mulFile.getSize());
				tempMap.put("CREA_ID", map.get("CREATOR"));
				tempMap.put("IS_NEW", "O");

			}else {
				if(map.containsKey("FILE_IDX")==true&& map.get("FILE_IDX")!=null) {
					tempMap.put("FILE_IDX", map.get("FILE_IDX"));
					tempMap.put("IS_NEW", "X");
				}
			}
		}
		return tempMap;
	}
	
	public static String getRandomString() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
}
