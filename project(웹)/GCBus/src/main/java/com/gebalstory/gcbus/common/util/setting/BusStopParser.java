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

@Component("busStopParser")
public class BusStopParser {
    public final static String KEY = "PbHTKBFJcR%2BUrD78ElcgIcA4VihHSnfuMQ%2FN%2BHnOquMc57DcWVkPp9hjGsRGceN74yGN03OzQ%2BiHdHqluIMBdw%3D%3D";
    
    public List<Map<String,Object>> apiParserNodeRealTime(Map<String,Object> map) throws Exception {
    	String apiUrl = "http://openapi.tago.go.kr/openapi/service/ArvlInfoInqireService/getSttnAcctoArvlPrearngeInfoList";
        URL url = new URL(apiUrl+"?ServiceKey="+KEY+"&nodeId="+map.get("NODEID")+"&cityCode=37030");
 
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        xpp.setInput(bis, "utf-8");
        
        String tag = null;
        int event_type = xpp.getEventType();
        
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        Map<String,Object> tempMap = null;
        while (event_type != XmlPullParser.END_DOCUMENT) {
            if (event_type == XmlPullParser.START_TAG) {
                tag = xpp.getName();
                if(tag.equals("item")) {
                	tempMap=new HashMap<String,Object>();
                }
            } else if (event_type == XmlPullParser.TEXT) {
                if(tag.equals("nodeid")){
                    tempMap.put("NODEID", xpp.getText());
                }
                else if(tag.equals("routeid")){
                	tempMap.put("ROUTEID", xpp.getText());
                }
                else if(tag.equals("routeno")) {
                	tempMap.put("ROUTENO", xpp.getText());
                }
                else if(tag.equals("vehicletp")) {
                	tempMap.put("VEHICLETP", xpp.getText());
                }
                else if(tag.equals("arrprevstationcnt")) {
                	tempMap.put("ARRPREV", xpp.getText());
                }
                else if(tag.equals("arrtime")) {
                	tempMap.put("ARRTIME", xpp.getText());
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
        return list;
    }

    public Map<String,Object> apiParserRouteRealTime(Map<String,Object> map) throws Exception {
    	String apiUrl = "http://openapi.tago.go.kr/openapi/service/BusLcInfoInqireService/getRouteAcctoBusLcList";
        URL url = new URL(apiUrl+"?ServiceKey="+KEY+"&routeId="+map.get("ROUTEID")+"&cityCode=37030");
 
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        xpp.setInput(bis, "utf-8");
        
        String tag = null;
        int event_type = xpp.getEventType();
        
        Map<String,Object> tempMap=new HashMap<String,Object>();
        while (event_type != XmlPullParser.END_DOCUMENT) {
            if (event_type == XmlPullParser.START_TAG) {
                tag = xpp.getName();
            } else if (event_type == XmlPullParser.TEXT) {
                if(tag.equals("nodeid")){
                    tempMap.put("NODEID", xpp.getText());
                }
                else if(tag.equals("nodenm")){
                	tempMap.put("NODENM", xpp.getText());
                }
                else if(tag.equals("routetp")) {
                	tempMap.put("ROUTETP", xpp.getText());
                }
                else if(tag.equals("gpslati")) {
                	tempMap.put("LAT", xpp.getText());
                	System.out.println(xpp.getText());
                }
                else if(tag.equals("gpslong")) {
                	tempMap.put("LNG", xpp.getText());
                }
            } else if (event_type == XmlPullParser.END_TAG) {
                tag = xpp.getName();
            }
 
            event_type = xpp.next();
        }
        bis.close();
        return tempMap;
    }
    
    public List<Map<String,Object>> apiParserNodeList(Map<String,Object> map) throws Exception {
    	String apiUrl = "http://openapi.tago.go.kr/openapi/service/BusSttnInfoInqireService/getCrdntPrxmtSttnList";
        URL url = new URL(apiUrl+"?ServiceKey="+KEY+"&gpsLati="+map.get("LAT")+"&gpsLong="+map.get("LNG"));
 
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        xpp.setInput(bis, "utf-8");
        
        String tag = null;
        int event_type = xpp.getEventType();
        
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        Map<String,Object> tempMap = null;
        while (event_type != XmlPullParser.END_DOCUMENT) {
            if (event_type == XmlPullParser.START_TAG) {
                tag = xpp.getName();
                if(tag.equals("item")) {
                	tempMap=new HashMap<String,Object>();
                }
            } else if (event_type == XmlPullParser.TEXT) {
                if(tag.equals("nodenm")){
                    tempMap.put("NODENAME", xpp.getText());
                }
                else if(tag.equals("nodeid")){
                	tempMap.put("NODEID", xpp.getText());
                }
                else if(tag.equals("gpslati")) {
                	tempMap.put("LAT", xpp.getText());
                }
                else if(tag.equals("gpslong")) {
                	tempMap.put("LNG", xpp.getText());
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
        return list;
    }
    
    
}
