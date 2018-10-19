package com.gebalstory.gcbus.common.util.setting;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

@Component("nodeToRoute")
public class NodeToRoute{

	public final static String ROUTE1_URL = "http://openapi.tago.go.kr/openapi/service/BusRouteInfoInqireService/getRouteNoList?serviceKey=PbHTKBFJcR%2BUrD78ElcgIcA4VihHSnfuMQ%2FN%2BHnOquMc57DcWVkPp9hjGsRGceN74yGN03OzQ%2BiHdHqluIMBdw%3D%3D&cityCode=37030&numOfRows=500";
	public final static String ROUTE2_URL = "http://openapi.tago.go.kr/openapi/service/BusRouteInfoInqireService/getRouteAcctoThrghSttnList?serviceKey=PbHTKBFJcR%2BUrD78ElcgIcA4VihHSnfuMQ%2FN%2BHnOquMc57DcWVkPp9hjGsRGceN74yGN03OzQ%2BiHdHqluIMBdw%3D%3D&cityCode=37030&numOfRows=80&routeId=";
	
    public List<Map<String,Object>> insertInfo() throws Exception {
    	
    	URL url = new URL(ROUTE1_URL);
    	 
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        xpp.setInput(bis, "utf-8");
        
        String tag = null;
        int event_type = xpp.getEventType();
        List<Map<String,Object>> routeList = new ArrayList<Map<String,Object>>();
        Map<String,Object> routeMap = null;
        while (event_type != XmlPullParser.END_DOCUMENT) {
            if (event_type == XmlPullParser.START_TAG) {
                tag = xpp.getName();
                if(tag.equals("item")) {
                	routeMap = new HashMap<String,Object>();
                }
            } else if (event_type == XmlPullParser.TEXT) {
                if(tag.equals("routeid")){
                	routeMap.put("routeid",xpp.getText());
                }else if(tag.equals("routeno")) {
                	routeMap.put("routeno",xpp.getText());
                }
            } else if (event_type == XmlPullParser.END_TAG) {
                tag = xpp.getName();
                if(tag.equals("item")) {
                	routeList.add(routeMap);
                }
            }
            event_type = xpp.next();
        }
        bis.close();
        
        Iterator<Map<String,Object>> iterator = routeList.iterator();
        List<Map<String,Object>> info = new ArrayList<Map<String,Object>>();
        Map<String,Object> nodeMap = null;
        Map<String,Object> temp = null;
        while(iterator.hasNext()) {
        	temp = iterator.next();
        	url = new URL(getURLParam((String)temp.get("routeid")));
        	bis = new BufferedInputStream(url.openStream());
            xpp.setInput(bis, "utf-8");
            tag = null;
            event_type = xpp.getEventType();
            
        	while (event_type != XmlPullParser.END_DOCUMENT) {
        		
                if (event_type == XmlPullParser.START_TAG) {
                    tag = xpp.getName();
                    if(tag.equals("item")) {
                    	nodeMap= new HashMap<String,Object>();
                    	nodeMap.put("ROUTENO", temp.get("routeno"));
                    }
                } else if (event_type == XmlPullParser.TEXT) {
                    if(tag.equals("nodeid")){
                    	nodeMap.put("NODEID", xpp.getText());
                    }
                    else if(tag.equals("nodenm")){
                    	nodeMap.put("NODENAME", xpp.getText());
                    }
                    else if(tag.equals("routeid")){
                    	nodeMap.put("ROUTEID", xpp.getText());
                    }
                } else if (event_type == XmlPullParser.END_TAG) {
                    tag = xpp.getName();
                    if (tag.equals("item")) {
                        info.add(nodeMap);
                    }
                }
                event_type = xpp.next();
            }
        }
        bis.close();
        return info;
    }
    
	private String getURLParam(String object) {
		String url = ROUTE2_URL+object;
		return url;
	}
	
}
