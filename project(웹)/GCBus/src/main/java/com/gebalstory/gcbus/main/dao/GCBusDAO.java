package com.gebalstory.gcbus.main.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.gebalstory.gcbus.common.dao.AbstractDAO;

@Repository("gcBusDAO")
public class GCBusDAO extends AbstractDAO {

	public void insertNodeRouteInfo(Map<String,Object> map) throws Exception{
		insert("common.insertNodeSetting",map);
	}

	public void insertRouteOrder(Map<String,Object> map) throws Exception{
		insert("common.insertRouteOrderSetting",map);
	}
	
	public void insertRouteInfo(Map<String,Object> map) throws Exception{
		insert("common.insertRouteInfoSetting",map);
	}

	public void insertNodeInfo(Map<String,Object> map) throws Exception{
		insert("common.insertNodeInfoSetting",map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectNodeList(Map<String, Object> map) {
		return (List<Map<String,Object>>)selectList("gcbus.selectNodeList",map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> selectNodeId(Map<String, Object> map) throws Exception {

		return (List<Map<String,Object>>)selectPagingList("gcbus.selectNodeId",map);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectRouteNo(Map<String, Object> map) {

		return (List<Map<String,Object>>)selectList("gcbus.selectRouteNo",map);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> selectRouteId(Map<String, Object> map) {

		return (Map<String,Object>)selectOne("gcbus.selectRouteId",map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> selectRoutePath(Map<String,Object> map){
		return (List<Map<String,Object>>)selectList("gcbus.selectRoutePath",map);
	}
	

}
