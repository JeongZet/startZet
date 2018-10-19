package com.gebalstory.gcbus.main.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.gebalstory.gcbus.common.dao.AbstractDAO;

@Repository("boardDAO")
public class BoardDAO extends AbstractDAO {
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> selectBoardList(Map<String,Object> map) throws Exception{
		return (List<Map<String,Object>>)selectList("board.selectBoardList",map);
	}
	
	public int insertBoardInfo(Map<String,Object> map) throws Exception{
		insert("board.insertBoardInfo", map);
		return Integer.parseInt(map.get("BOARD_IDX").toString());
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> selectBoardInfo(Map<String,Object> map) throws Exception{
		return (Map<String,Object>)selectOne("board.selectBoardInfo",map);
	}
	
	public void updateHitCnt(Map<String,Object> map) throws Exception{
		update("board.updateHitCnt", map);
	}
	
	public void updateBoard(Map<String,Object> map) throws Exception{
		update("board.updateBoard",map);
	}
	
	public void deleteBoard(Map<String,Object> map) throws Exception{
		update("board.deleteBoard",map);
	}
	
	public void insertFileInfo(Map<String,Object> map) throws Exception{
		insert("board.insertFileInfo",map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> selectFileInfo(Map<String,Object> map) throws Exception{
		return (Map<String,Object>)selectOne("board.selectFileInfo", map);
	}
	
	public void deleteFileInfo(Map<String,Object> map) throws Exception{
		update("board.deleteFileInfo",map);
	}
	
	public void recoveryFileInfo(Map<String,Object> map) throws Exception{
		update("board.recoveryFileInfo",map);
	}
}
