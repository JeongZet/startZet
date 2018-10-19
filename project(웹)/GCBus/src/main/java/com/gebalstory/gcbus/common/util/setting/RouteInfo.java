package com.gebalstory.gcbus.common.util.setting;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

@Component("routeInfo")
public class RouteInfo {
	public final static String URL = "http://openapi.tago.go.kr/openapi/service/BusRouteInfoInqireService/getRouteInfoIem";
	public final static String KEY = "PbHTKBFJcR%2BUrD78ElcgIcA4VihHSnfuMQ%2FN%2BHnOquMc57DcWVkPp9hjGsRGceN74yGN03OzQ%2BiHdHqluIMBdw%3D%3D";
	
    public List<Map<String,Object>> insertInfo(Map<String,Object> map) throws Exception {
    	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    	
    	for(int i = Integer.parseInt((String) map.get("MIN"));i<= Integer.parseInt((String)map.get("MAX"));i++) {
	        URL url = new URL(URL+"?ServiceKey="+KEY+"&cityCode=37030&routeId=GCB4720"+i);
	    	
	        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	        BufferedInputStream bis = new BufferedInputStream(url.openStream());
	        xpp.setInput(bis, "utf-8");
	        
	        String tag = null;
	        int event_type = xpp.getEventType();
	        
	        Map<String,Object> tempMap = null;
	        while (event_type != XmlPullParser.END_DOCUMENT) {
	            if (event_type == XmlPullParser.START_TAG) {
	                tag = xpp.getName();
	                if(tag.equals("item")) {
	                	tempMap=new HashMap<String,Object>();
	                }
	            } else if (event_type == XmlPullParser.TEXT) {
	                if(tag.equals("routeid")){
	                    tempMap.put("ROUTEID", xpp.getText());
	                }
	                else if(tag.equals("routeno")) {
	                	tempMap.put("ROUTENO", xpp.getText());
	                }
	                else if(tag.equals("startnodenm")) {
	                	tempMap.put("STARTNODENM", xpp.getText());
	                }
	                else if(tag.equals("endnodenm")){
	                	tempMap.put("ENDNODENM", xpp.getText());
	                }
	                else if(tag.equals("startvehicletime")) {
	                	tempMap.put("STARTVEHICLETIME", xpp.getText());
	                }
	                else if(tag.equals("endvehicletime")) {
	                	tempMap.put("ENDVEHICLETIME", xpp.getText());
	                }
	            } else if (event_type == XmlPullParser.END_TAG) {
	                tag = xpp.getName();
	                if (tag.equals("item")) {
	                    list.add(tempMap);
	                }
	            }
	            event_type = xpp.next();
	        }
	        bis.close();
    	}
        return list;
    }
}