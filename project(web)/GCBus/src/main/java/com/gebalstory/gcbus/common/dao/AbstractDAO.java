package com.gebalstory.gcbus.common.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class AbstractDAO {
    protected Log log = LogFactory.getLog(AbstractDAO.class);
      
    @Autowired
    private SqlSessionTemplate sqlSession;
      
    protected void printQueryId(String queryId) {
        if(log.isDebugEnabled()){
            log.debug("\t QueryId  \t:  " + queryId);
        }
    }
      
    public Object insert(String queryId, Object params){
        printQueryId(queryId);
        return sqlSession.insert(queryId, params);
    }
      
    public Object update(String queryId, Object params){
        printQueryId(queryId);
        return sqlSession.update(queryId, params);
    }
      
    public Object delete(String queryId, Object params){
        printQueryId(queryId);
        return sqlSession.delete(queryId, params);
    }
      
    public Object selectOne(String queryId){
        printQueryId(queryId);
        return sqlSession.selectOne(queryId);
    }
      
    public Object selectOne(String queryId, Object params){
        printQueryId(queryId);
        return sqlSession.selectOne(queryId, params);
    }
      
    @SuppressWarnings("rawtypes")
    public List selectList(String queryId){
        printQueryId(queryId);
        return sqlSession.selectList(queryId);
    }
      
    @SuppressWarnings("rawtypes")
    public List selectList(String queryId, Object params){
        printQueryId(queryId);
        return sqlSession.selectList(queryId,params);
    }
    
    @SuppressWarnings("unchecked")
    public Object selectPagingList(String queryId, Object params) {
    	printQueryId(queryId);
    	Map<String,Object> map = (Map<String,Object>)params;
    	
    	String strPage_No = map.get("PAGE_NO").toString();
    	String strRow_Count = (String)map.get("ROW_COUNT");
    	int nPage_No = 0;
    	int nRow_Count = 10;
    	
    	if(StringUtils.isEmpty(strPage_No)==false) {
    		nPage_No = (Integer.parseInt(strPage_No)-1)*10;
    	}
    	if(StringUtils.isEmpty(strRow_Count)==false) {
    		nRow_Count = Integer.parseInt(strRow_Count);
    	}
    	
    	map.put("PAGE_NO", nPage_No);
    	map.put("ROW_COUNT", nRow_Count);
    	
    	return sqlSession.selectList(queryId, map);
    }
}