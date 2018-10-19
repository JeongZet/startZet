package com.gebalstory.gcbus.main.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gebalstory.gcbus.common.util.setting.NodeInfo;
import com.gebalstory.gcbus.common.util.setting.NodeToRoute;
import com.gebalstory.gcbus.common.util.setting.RouteInfo;
import com.gebalstory.gcbus.common.util.setting.RouteToOrder;
import com.gebalstory.gcbus.main.dao.GCBusDAO;

@Service("gcBusServ")
public class GCBusServiceImpl implements GCBusService {

	@Resource(name="gcBusDAO")
	private GCBusDAO gcBusDAO;
	
	@Resource(name="nodeToRoute")
	private NodeToRoute nodeToRoute;
	
	@Resource(name="routeToOrder")
	private RouteToOrder routeToOrder;

	@Resource(name="routeInfo")
	private RouteInfo routeInfo;
	
	@Resource(name="nodeInfo")
	private NodeInfo nodeInfo;
	
	@Override
	public Integer regNodeToRoute() throws Exception {
		List<Map<String,Object>> list = nodeToRoute.insertInfo();
		Iterator<Map<String,Object>> iterator = list.iterator();
		Map<String,Object> map = null;
	    while(iterator.hasNext()) {
        	map=iterator.next();
        	gcBusDAO.insertNodeRouteInfo(map);
	    }
	    
	    return Integer.valueOf(list.size());
	}
	
	@Override
	public Integer regRouteToOrder(Map<String,Object> map) throws Exception {
		List<Map<String,Object>> list = routeToOrder.insertInfo(map);
		Iterator<Map<String,Object>> iterator = list.iterator();
		Map<String,Object> temp = null;
		while(iterator.hasNext()) {
			temp=iterator.next();
			gcBusDAO.insertRouteOrder(temp);
		}
		
		return Integer.valueOf(list.size());
	}
	
	@Override
	public Integer regRouteInfo(Map<String,Object> map) throws Exception {
		List<Map<String,Object>> list = routeInfo.insertInfo(map);
		Iterator<Map<String,Object>> iterator = list.iterator();
		Map<String,Object> temp = null;
		while(iterator.hasNext()) {
			temp=iterator.next();
			gcBusDAO.insertRouteInfo(temp);
		}
		return Integer.valueOf(list.size());
	}

	@Override
	public Integer regNodeInfo(Map<String,Object> map) throws Exception {
		List<Map<String,Object>> list = nodeInfo.insertInfo(map);
		Iterator<Map<String,Object>> iterator = list.iterator();
		Map<String,Object> temp = null;
		while(iterator.hasNext()) {
			temp=iterator.next();
			gcBusDAO.insertNodeInfo(temp);
		}
		return Integer.valueOf(list.size());
	}
	
	@Override
	public List<Map<String,Object>> nodeToRouteList(Map<String, Object> map) throws Exception {
		return gcBusDAO.selectNodeId(map);
	}

	@Override
	public List<Map<String, Object>> routeList(Map<String, Object> map) throws Exception {
		return gcBusDAO.selectRouteNo(map);
	}
	
	@Override
	public Map<String, Object> routeInfo(Map<String, Object> map) throws Exception {
		Map<String,Object> temp = new HashMap<String,Object>();
		temp.put("info", gcBusDAO.selectRouteId(map));
		temp.put("path",gcBusDAO.selectRoutePath(map));
		
		return temp;
	}

	@Override
	public List<Map<String, Object>> nodeList(Map<String, Object> map) throws Exception {
		
		return gcBusDAO.selectNodeList(map);
	}

}
