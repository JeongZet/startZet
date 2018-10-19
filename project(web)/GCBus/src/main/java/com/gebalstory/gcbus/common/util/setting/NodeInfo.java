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

@Component("nodeInfo")
public class NodeInfo {

	public final static String URL = "http://openapi.tago.go.kr/openapi/service/BusSttnInfoInqireService/getSttnNoList";
	public final static String KEY = "PbHTKBFJcR%2BUrD78ElcgIcA4VihHSnfuMQ%2FN%2BHnOquMc57DcWVkPp9hjGsRGceN74yGN03OzQ%2BiHdHqluIMBdw%3D%3D";
	
    public List<Map<String,Object>> insertInfo(Map<String,Object> map) throws Exception {
    	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
	
        URL url = new URL(URL+"?ServiceKey="+KEY+"&cityCode=37030&numOfRows=1400");
    	
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
                if(tag.equals("nodeid")){
                    tempMap.put("NODEID", xpp.getText());
                }
                else if(tag.equals("nodenm")) {
                	tempMap.put("NODENAME", xpp.getText());
                }
                else if(tag.equals("nodeno")) {
                	tempMap.put("NODENO", xpp.getText());
                }
                else if(tag.equals("gpslati")){
                	tempMap.put("LAT", Double.parseDouble(xpp.getText()));
                }
                else if(tag.equals("gpslong")) {
                	tempMap.put("LNG", Double.parseDouble(xpp.getText()));
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
