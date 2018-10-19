package com.gebalstory.gcbus.main.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gebalstory.gcbus.common.common.CommandMap;
import com.gebalstory.gcbus.common.util.setting.BusStopParser;
import com.gebalstory.gcbus.main.service.GCBusService;

@Controller
public class GCBusController {

	@Resource(name="gcBusServ")
	private GCBusService gcBusServ;
	
	@Resource(name="busStopParser")
	private BusStopParser busStopParser;
	
	
	//�����ͺ��̽� ������ ������ ���� ��Ʈ�ѷ�
	@RequestMapping(value="/regForm.do")
	public ModelAndView regForm() throws Exception{
		ModelAndView mv = new ModelAndView("setting/regForm");
		
		return mv;
	}
	
	@RequestMapping(value="/reg/regNodeInfo.do")
	public ModelAndView regNodeInfo(CommandMap commandMap) throws Exception{
		
		ModelAndView mv = new ModelAndView("setting/regSuc");
		mv.addObject("size", gcBusServ.regNodeInfo(commandMap.getMap()));
		
		return mv;
	}
	
	@RequestMapping(value="/reg/regNodeToRoute.do")
	public ModelAndView regNodeToRoute() throws Exception{
		ModelAndView mv = new ModelAndView("setting/regSuc");
		mv.addObject("size", gcBusServ.regNodeToRoute());
		
		return mv;
	}
	
	@RequestMapping(value="/reg/regRouteToOrder.do")
	public ModelAndView regRouteToOrder(CommandMap commandMap) throws Exception{
		
		ModelAndView mv = new ModelAndView("setting/regSuc");
		mv.addObject("size", gcBusServ.regRouteToOrder(commandMap.getMap()));
		
		return mv;
	}
	
	@RequestMapping(value="/reg/regRouteInfo.do")
	public ModelAndView regRouteInfo(CommandMap commandMap) throws Exception{
		
		ModelAndView mv = new ModelAndView("setting/regSuc");
		mv.addObject("size", gcBusServ.regRouteInfo(commandMap.getMap()));
		
		return mv;
	}

	//��������� �����ͺ��̽� ���� ������ ���� ��Ʈ�ѷ���
	
	//������ ���� ��Ʈ�ѷ�
	@RequestMapping(value="/main.do")
	public ModelAndView nodeFind(CommandMap commandMap) throws Exception{
		
		ModelAndView mv = new ModelAndView("node_find");
		mv.addObject("nodeid",commandMap.get("nodeid"));
		mv.addObject("nodename",commandMap.get("nodename"));
		mv.addObject("lat",commandMap.get("lat"));
		mv.addObject("lng",commandMap.get("lng"));
		
		return mv;
	}

	@RequestMapping(value="/nodeList.do")
	public @ResponseBody ModelAndView busList(@RequestBody Map<String,Object> map) throws Exception{

		map.put("list", gcBusServ.nodeList(map));
		return new ModelAndView("jsonView", map);
	}
	
	@RequestMapping(value="/routeList.do")
	public @ResponseBody ModelAndView routeList(@RequestBody Map<String,Object> map) throws Exception{
		
		map.put("list", gcBusServ.routeList(map));
		return new ModelAndView("jsonView",map);
	}
	
	
	@RequestMapping(value="/nodeRealTime.do")
	public @ResponseBody ModelAndView nodeRealTime(@RequestBody Map<String,Object> map) throws Exception{
		map.put("list", busStopParser.apiParserNodeRealTime(map));
		
		return new ModelAndView("jsonView", map);
	}
	
	@RequestMapping(value="/nodeToRouteList.do")
	public @ResponseBody ModelAndView nodeToRouteList(@RequestBody Map<String,Object> map) throws Exception{
		
		map.put("list", gcBusServ.nodeToRouteList(map));
		
		return new ModelAndView("jsonView", map);
	}

	@RequestMapping(value="/routeInfoPage.do")
	public ModelAndView routeInfoPage(CommandMap commandMap) throws Exception{
		
		ModelAndView mv = new ModelAndView("route_info");
		
		mv.addObject("routeno",commandMap.get("routeno"));
		mv.addObject("routeid",commandMap.get("routeid"));
		
		return mv;
	}
	
	@RequestMapping(value="/routeInfo.do")
	public @ResponseBody ModelAndView routeInfo(@RequestBody Map<String,Object> map) throws Exception{
		Map<String,Object> temp = gcBusServ.routeInfo(map);
		map.put("map", temp.get("info"));
		map.put("path", temp.get("path"));
		
		return new ModelAndView("jsonView",map);
	}
	
	
	@RequestMapping(value="/routeRealTime.do")
	public @ResponseBody ModelAndView routeRealTime(@RequestBody Map<String,Object> map) throws Exception{
		map.put("map", busStopParser.apiParserRouteRealTime(map));
		return new ModelAndView("jsonView", map);
	}
	//��������� ���� �뼱 �� �ǽð� �� ����� ���� ��Ʈ�ѷ���
}
