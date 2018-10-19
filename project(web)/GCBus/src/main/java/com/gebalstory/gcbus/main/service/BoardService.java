package com.gebalstory.gcbus.main.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface BoardService {
	public List<Map<String,Object>> selectBoardList(Map<String,Object> map) throws Exception;
	public int insertBoardInfo(Map<String,Object> map, HttpServletRequest req) throws Exception;
	public Map<String,Object> selectBoardInfo(Map<String,Object> map) throws Exception;
	public void updateBoardInfo(Map<String,Object> map, HttpServletRequest req) throws Exception;
	public void deleteBoardInfo(Map<String,Object> map) throws Exception;
	public Map<String,Object> selectFileInfo(Map<String,Object> map) throws Exception;
}
