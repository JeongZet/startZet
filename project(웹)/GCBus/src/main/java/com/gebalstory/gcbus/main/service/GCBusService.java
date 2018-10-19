package com.gebalstory.gcbus.main.service;

import java.util.List;
import java.util.Map;

public interface GCBusService {
	Integer regNodeToRoute() throws Exception;
	Integer regRouteToOrder(Map<String,Object> map) throws Exception;
	Integer regRouteInfo(Map<String,Object> map) throws Exception;
	Integer regNodeInfo(Map<String, Object> map) throws Exception;
	List<Map<String,Object>> nodeToRouteList(Map<String, Object> map) throws Exception;
	List<Map<String,Object>> routeList(Map<String, Object> map) throws Exception;
	Map<String,Object> routeInfo(Map<String, Object> map) throws Exception;
	List<Map<String,Object>> nodeList(Map<String, Object> map) throws Exception;
	
}
