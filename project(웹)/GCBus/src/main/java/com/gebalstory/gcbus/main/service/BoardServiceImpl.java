package com.gebalstory.gcbus.main.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.gebalstory.gcbus.common.util.file.FileUtils;
import com.gebalstory.gcbus.main.dao.BoardDAO;

@Service("boardService")
public class BoardServiceImpl implements BoardService {

	@Resource(name="boardDAO")
	private BoardDAO boardDAO;
	
	@Resource(name="fileUtils")
	private FileUtils fileUtils;
	
	@Override
	public List<Map<String, Object>> selectBoardList(Map<String, Object> map) throws Exception {
		
		return  boardDAO.selectBoardList(map);
	}

	@Override
	public int insertBoardInfo(Map<String, Object> map, HttpServletRequest req) throws Exception {
		
		int idx = boardDAO.insertBoardInfo(map);
		
		Map<String,Object> fileMap = fileUtils.parseInsertFileInfo(map,req);
		
		if(fileMap.size()>0) {
			boardDAO.insertFileInfo(fileMap);
		}
		
		return idx;
	}

	@Override
	public Map<String, Object> selectBoardInfo(Map<String, Object> map) throws Exception {
		if(map.get("type").equals("detail")) {
			boardDAO.updateHitCnt(map);
		}
		return boardDAO.selectBoardInfo(map);
	}

	@Override
	public void updateBoardInfo(Map<String, Object> map, HttpServletRequest req) throws Exception {
		boardDAO.updateBoard(map);
		boardDAO.deleteFileInfo(map);
		Map<String,Object> tempMap = fileUtils.parseUpdateFileInfo(map,req);
		
		if(tempMap.size()>0) {
			if(tempMap.get("IS_NEW").equals("O")) {
				boardDAO.insertFileInfo(tempMap);
			}else {
				boardDAO.recoveryFileInfo(tempMap);
			}
		}
		
	}

	@Override
	public void deleteBoardInfo(Map<String, Object> map) throws Exception {
		boardDAO.deleteBoard(map);
	}

	@Override
	public Map<String, Object> selectFileInfo(Map<String, Object> map) throws Exception {
		return boardDAO.selectFileInfo(map);
	}
}
