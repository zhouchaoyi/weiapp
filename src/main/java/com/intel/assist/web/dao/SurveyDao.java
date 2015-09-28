package com.intel.assist.web.dao;

import org.codehaus.jettison.json.JSONObject;

import java.util.List;

/**
 * Created by kunpeng on 2015/6/29.
 */
public interface SurveyDao extends BaseDao{

    public List<JSONObject> listSurveyByPagination(int pageIndex,int pageSize,String wwid) throws Exception;
    public int getSurveyTotalSize(String wwid) throws Exception;
    public JSONObject getExtendSurveyInfo(String surveyId) throws Exception;
    public JSONObject buildSurveyInfo(String surveyId) throws Exception;
    public void updateSurveyInfo(String surveyId,String name,String question) throws Exception;
    public void delectSurveyInfo(String surveyId) throws Exception;
    public void delectSurveysInfo(List<Object[]> params) throws Exception;
    public void updataAnswer(List<Object[]> params) throws Exception;
    public void insertSurveyInfo(String name,String question,String wwid,String createDate) throws Exception;
    public int getSendRSPUserTotal(String surveyId,String isSend,String position,String subregions,String citys,String storeType)throws Exception;
    public int getSendChannelUserTotal(String mrType,String subregion,String city,String isSend,String surveyId)throws Exception;
    public List<JSONObject> listSendRSPUser(String surveyId,String isSend,String position,String subregions,String citys,String storeType,int page,int size)throws Exception;
    public List<JSONObject> listSendChannelUser(String surveyId,String isSend,String mrType,String subregions,String citys,int page,int size)throws Exception;
    public void sendAllRSPUser(String surveyId,String isSend,String position,String subregions,String citys,String storeType)throws Exception;
    public void sendAllChannelUser(String surveyId,String isSend,String mrType,String subregions,String citys)throws Exception;
    public void unsendAllRSPUser(String surveyId,String isSend,String position,String subregions,String citys,String storeType)throws Exception;
    public void unsendAllChannelUser(String surveyId,String isSend,String mrType,String subregions,String citys)throws Exception;
    public void sendSelectUser(List<Object[]> params) throws Exception;
    public void unsendSelectUser(List<Object[]> params) throws Exception;
    public void updateSurveyStatus(String surveyId,String status) throws Exception;
    public void updateLinkUserStatus(String repId,String userType,String surveyId,String storeId,String status) throws Exception;
    public void updateSurveyStatus(String surveyId) throws Exception;
    public List<JSONObject> buildExcelContent(String id) throws Exception;
    public List<String> buildQuestionAnswer(String storeId,String userId,String userType,String surveyId) throws Exception;
    public List<JSONObject> querySurveyList(String repid,String usertype) throws Exception;
    public List<JSONObject> querySurveyList(String repid,String usertype,String storeId) throws Exception;
    public String isHasAnswer(String userId,String surveryId,String userType)throws Exception;
}
