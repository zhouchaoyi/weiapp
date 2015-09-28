package com.intel.assist.web.services;

import com.intel.assist.model.entity.Survey;
import com.intel.assist.utils.Consts;
import com.intel.assist.utils.DateUtil;
import com.intel.assist.web.dao.BaseDao;
import com.intel.assist.web.dao.SurveyDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by kunpeng on 2015/6/29.
 */
@Service
public class SurveyService extends BaseServices{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SurveyDao surveyDao;
    public BaseDao getDao(){
        return surveyDao;
    }

    public List<JSONObject> listSurveyByPagination(int pageIndex,int pageSize,String wwid) {
        try {

            List<JSONObject> surveyList = surveyDao.listSurveyByPagination(pageIndex,pageSize,wwid);
            if(null == surveyList || surveyList.size() <= 0){
                return new ArrayList<JSONObject>();
            }

            addExtendSurveyInfo(surveyList);

            return surveyList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public int getSurveyTotalSize(String wwid){
        try {
            return surveyDao.getSurveyTotalSize(wwid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void addExtendSurveyInfo(List<JSONObject> surveyJsonList) throws Exception{
        if(null == surveyJsonList || surveyJsonList.size() <= 0){
            return ;
        }
        for (JSONObject surveyInfo : surveyJsonList) {
            String surveyName = surveyInfo.getString("name");
            surveyInfo.put("name", surveyName.length() > 20 ? surveyName.substring(0, 17) + "..." : surveyName);


            Date createDate = DateUtil.string2Date(surveyInfo.getString("createdate"));
            surveyInfo.put("createdate",DateUtil.formatDateString(createDate));


            JSONObject extendSurveyInfo = surveyDao.getExtendSurveyInfo(surveyInfo.getString("id"));
            if (null != extendSurveyInfo) {
                surveyInfo.put("rspCount", StringUtils.isBlank(extendSurveyInfo.getString("rep_num")) ? 0 : extendSurveyInfo.getString("rep_num"));
                surveyInfo.put("rspJoin", StringUtils.isBlank(extendSurveyInfo.getString("rep_join")) ? 0 : extendSurveyInfo.getString("rep_join"));
                surveyInfo.put("channelCount", StringUtils.isBlank(extendSurveyInfo.getString("channel_num")) ? 0 : extendSurveyInfo.getString("channel_num"));
                surveyInfo.put("channelJoin", StringUtils.isBlank(extendSurveyInfo.getString("channel_join")) ? 0 : extendSurveyInfo.getString("channel_join"));
            } else {
                surveyInfo.put("rspCount", 0);
                surveyInfo.put("rspJoin", 0);
                surveyInfo.put("channelCount", 0);
                surveyInfo.put("channelJoin", 0);
            }
        }
    }

    public JSONObject buildSurvey(String surveyId){

        JSONObject surveyInfo = new JSONObject();

        if(StringUtils.isBlank(surveyId)){
           return surveyInfo;
        }

        try {
            surveyInfo =surveyDao.buildSurveyInfo(surveyId);
            cureSurvey(surveyInfo);
            return  surveyInfo;
        } catch (Exception e) {
            logger.error("buildSurvey error-> "+e.getMessage());
        }

        return surveyInfo;
    }

    private void cureSurvey(JSONObject surveyInfo){
        if(null == surveyInfo ){
            return;
        }

        try {

            Date createDate = DateUtil.string2Date(surveyInfo.getString("createdate"));
            surveyInfo.put("createdate",DateUtil.formatDateString(createDate));

            String status = surveyInfo.getString("status");
            surveyInfo.put("status",statusMapping(status));

            surveyInfo.put("rep_num", StringUtils.equals(surveyInfo.getString("rep_num"),"-1") ?0:surveyInfo.getString("rep_num"));
            surveyInfo.put("rep_join", StringUtils.equals(surveyInfo.getString("rep_join"),"-1")?0:surveyInfo.getString("rep_join"));
            surveyInfo.put("channel_num", StringUtils.equals(surveyInfo.getString("channel_num"),"-1")?0:surveyInfo.getString("channel_num"));
            surveyInfo.put("channel_join", StringUtils.equals(surveyInfo.getString("channel_join"),"-1")?0:surveyInfo.getString("channel_join"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSurvey(JSONObject survey){


        try {
            String id = survey.getString("id");
            if(StringUtils.isBlank(id)){
                return;
            }
            String name = survey.getString("name");
            String question = survey.getString("question");
            question = StringEscapeUtils.unescapeJson(question);
            this.surveyDao.updateSurveyInfo(id,name,question);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void delectSuerveyById(String surveyId){

        if(StringUtils.isBlank(surveyId)){
            return ;
        }

        try {
            this.surveyDao.delectSurveyInfo(surveyId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeSuerveyById(String surveyId){
        if(StringUtils.isBlank(surveyId)){
            return ;
        }

        try {
            this.surveyDao.updateSurveyStatus(surveyId,"-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delectSuerveyByIds(JSONArray ids){

        if(null == ids || ids.length() < 1){
            return;
        }

        List<Object[]> params = new ArrayList<Object[]>();
        try {
            for (int i = 0; i <ids.length() ; i++) {
                params.add(new Object[]{ids.getString(i)});
            }
            this.surveyDao.delectSurveysInfo(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertSuervey(String name,String question,String wwid){
        try {
            SimpleDateFormat df = new SimpleDateFormat(Consts.SIMPLE_DATE_FORMAT_STRING);
            this.surveyDao.insertSurveyInfo(name,question,wwid,df.format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSendUserTotal(JSONObject paramsJson){
        int size = 0;

        try {
            String userType = StringUtils.upperCase(paramsJson.getString("userType"));
            String surveyId = paramsJson.getString("surveyId");
            JSONArray subregions =   paramsJson.getJSONArray("subregions");
            JSONArray citys =   paramsJson.getJSONArray("citys");
            String isSend =  paramsJson.getString("isSend");

            if (StringUtils.equalsIgnoreCase("RSP",userType)){
               String position =   paramsJson.getString("position");
               JSONArray storeType =   paramsJson.getJSONArray("storeType");
               return this.surveyDao.getSendRSPUserTotal(surveyId,isSend,position,subregions.join(",").replace("\"","\'"),citys.join(",").replace("\"", "\'"),storeType.join(",").replace("\"","\'"));
            }else if(StringUtils.equalsIgnoreCase("CHANNEL",userType)){
               String mrType =  paramsJson.getString("mrType");
               return this.surveyDao.getSendChannelUserTotal(mrType,subregions.join(",").replace("\"","\'"),citys.join(",").replace("\"", "\'"),isSend,surveyId);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public List<JSONObject> getsendUserList(JSONObject paramsJson){

        List<JSONObject> result = new ArrayList<JSONObject>();

        try {
            String userType = StringUtils.upperCase(paramsJson.getString("userType"));
            String surveyId = paramsJson.getString("surveyId");
            int page = paramsJson.getInt("page");
            int size = paramsJson.getInt("size");
            String isSend =  paramsJson.getString("isSend");
            JSONArray subregions =   paramsJson.getJSONArray("subregions");
            JSONArray citys =   paramsJson.getJSONArray("citys");

            if (StringUtils.equalsIgnoreCase("RSP",userType)){
                String position =   paramsJson.getString("position");
                JSONArray storeType =   paramsJson.getJSONArray("storeType");
                return this.surveyDao.listSendRSPUser(surveyId, isSend, position, subregions.join(",").replace("\"", "\'"),
                        citys.join(",").replace("\"", "\'"), storeType.join(",").replace("\"", "\'"), page, size);
            }else if(StringUtils.equalsIgnoreCase("CHANNEL",userType)){
                String mrType =  paramsJson.getString("mrType");
                return this.surveyDao.listSendChannelUser(surveyId, isSend, mrType, subregions.join(",").replace("\"", "\'"),
                        citys.join(",").replace("\"", "\'"), page, size);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void sendSelectUser(JSONArray jsonArray,String surveyId,String userType){
        if(jsonArray != null && jsonArray.length() > 0){
                try {
                    List<Object[]> paramsList = new ArrayList<Object[]>();
                    for(int i=0;i<jsonArray.length();i++){
                    Object[] params = new Object[11];
                        params[0] =jsonArray.getJSONObject(i).get("rep_id");
                        params[1] =surveyId;
                        params[2] =0;
                        params[3] =userType;
                        params[4] =jsonArray.getJSONObject(i).get("subregionname");
                        params[5] =jsonArray.getJSONObject(i).get("cityname");
                        params[6] =jsonArray.getJSONObject(i).get("rep_nm");
                        params[7] =jsonArray.getJSONObject(i).get("storeid");
                        params[8] =jsonArray.getJSONObject(i).get("storename");
                        params[9] =jsonArray.getJSONObject(i).get("rep_tel");
                        params[10] =1;
                        paramsList.add(params);
                    }
                    this.surveyDao.sendSelectUser(paramsList);
                    this.surveyDao.updateSurveyStatus(surveyId);

                } catch (Exception e) {
                    logger.error("sendSelectUser error-> " + e.getMessage());
                }
        }
    }

    public void unsendSelectUser(JSONArray ids,String surveyId){

        if(null == ids || ids.length() < 1){
            return;
        }

        List<Object[]> params = new ArrayList<Object[]>();
        try {
            for (int i = 0; i <ids.length() ; i++) {
                params.add(new Object[]{ids.getString(i)});
            }
            this.surveyDao.unsendSelectUser(params);
            this.surveyDao.updateSurveyStatus(surveyId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUserAll(JSONObject paramsJson){

        try {
            String userType = StringUtils.upperCase(paramsJson.getString("userType"));
            String surveyId = paramsJson.getString("surveyId");
            JSONArray subregions =   paramsJson.getJSONArray("subregions");
            JSONArray citys =   paramsJson.getJSONArray("citys");
            String isSend =  paramsJson.getString("isSend");

            if (StringUtils.equalsIgnoreCase("RSP",userType)){
                String position =   paramsJson.getString("position");
                JSONArray storeType =   paramsJson.getJSONArray("storeType");
                this.surveyDao.sendAllRSPUser(surveyId, isSend, position, subregions.join(",").replace("\"", "\'"),
                        citys.join(",").replace("\"", "\'"), storeType.join(",").replace("\"", "\'"));

            } else if (StringUtils.equalsIgnoreCase("CHANNEL", userType)) {
                String mrType =  paramsJson.getString("mrType");
                this.surveyDao.sendAllChannelUser(surveyId, isSend, mrType, subregions.join(",").replace("\"", "\'"),
                        citys.join(",").replace("\"", "\'"));
            }
            this.surveyDao.updateSurveyStatus(surveyId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unsendUserAll(JSONObject paramsJson){

        try {
            String userType =StringUtils.upperCase(paramsJson.getString("userType"));
            String surveyId = paramsJson.getString("surveyId");
            String isSend =  paramsJson.getString("isSend");
            JSONArray subregions =   paramsJson.getJSONArray("subregions");
            JSONArray citys =   paramsJson.getJSONArray("citys");
            if (StringUtils.equalsIgnoreCase("RSP",userType)){
                String position =   paramsJson.getString("position");
                JSONArray storeType =   paramsJson.getJSONArray("storeType");
                this.surveyDao.unsendAllRSPUser(surveyId, isSend, position, subregions.join(",").replace("\"", "\'"),
                        citys.join(",").replace("\"", "\'"), storeType.join(",").replace("\"", "\'"));

            }else if(StringUtils.equalsIgnoreCase("CHANNEL",userType)){
                String mrType =  paramsJson.getString("mrType");
                this.surveyDao.unsendAllChannelUser(surveyId, isSend, mrType, subregions.join(",").replace("\"", "\'"),
                        citys.join(",").replace("\"", "\'"));
            }
            this.surveyDao.updateSurveyStatus(surveyId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void download(OutputStream output, String surveyId){
        List<JSONObject> list = new ArrayList<JSONObject>();
        try {
            list = this.surveyDao.buildExcelContent(surveyId);
            for (JSONObject p :list){
                buildQuestionAnswer(p);
            }
            xlsDto2Excel(list, output);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void buildQuestionAnswer(JSONObject p)throws Exception{
        List<String> answers = this.surveyDao.buildQuestionAnswer(p.getString("store_id"),p.getString("userid"),StringUtils.upperCase(p.getString("usertype")),p.getString("surveyid"));
        p.put("answers",new JSONArray(answers));
    }

    public  void xlsDto2Excel(List<JSONObject> xls, OutputStream out) throws Exception {
        // 获取总列数
        int countColumnNum = 0;

        if (null != xls && xls.size() > 0) {
            if (null != xls.get(0).getJSONArray("answers")) {
                countColumnNum = xls.get(0).getJSONArray("answers").length();
            }
        }

        if (countColumnNum == 0) {
            return;
        }
        countColumnNum += 6;


        // 创建Excel文档
        HSSFWorkbook hwb = new HSSFWorkbook();
        JSONObject xlsDto = null;
        // sheet 对应一个工作页
        HSSFSheet sheet = hwb.createSheet("report");
        HSSFRow firstrow = sheet.createRow(0); // 下标为0的行开始
        HSSFCell[] firstcell = new HSSFCell[countColumnNum];
        // 设置表格列名
        String[] names = new String[countColumnNum];
        names[0] = "地区";
        names[1] = "城市";
        names[2] = "门店编号";
        names[3] = "门店名称";
        names[4] = "用户名称";
        names[5] = "手机";

        for (int i = 6; i < countColumnNum; i++) {
            names[i] = "问题 " + (i - 5);
        }
        for (int j = 0; j < countColumnNum; j++) {
            firstcell[j] = firstrow.createCell(j);
            firstcell[j].setCellValue(new HSSFRichTextString(names[j]));
        }
        for (int i = 0; i < xls.size(); i++) {
            // 创建一行
            HSSFRow row = sheet.createRow(i + 1);
            // 得到要插入的每一条记录
            xlsDto = xls.get(i);
            row.createCell(0).setCellValue(xlsDto.getString("region"));
            row.createCell(1).setCellValue(xlsDto.getString("city"));
            row.createCell(2).setCellValue(xlsDto.getString("store_id"));
            row.createCell(3).setCellValue(xlsDto.getString("store_name"));
            row.createCell(4).setCellValue(xlsDto.getString("user_name"));
            row.createCell(5).setCellValue(xlsDto.getString("user_phone"));

            for (int m = 6; m < countColumnNum; m++) {
                row.createCell(m).setCellValue(xlsDto.getJSONArray("answers").getString(m-6));
            }
        }
        hwb.write(out);
        out.close();
    }

    public List<JSONObject> querySurveyList(String repid,String usertype){

        try {
            return this.surveyDao.querySurveyList(repid,usertype);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return  null;
    }

    public List<JSONObject> querySurveyList(String repId,String userIype,String storeId){

        try {
            return this.surveyDao.querySurveyList(repId,userIype,storeId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return  null;
    }

    public String isHasAnswer(String userId,String surveryId,String userType){
        try {
            return this.surveyDao.isHasAnswer(userId,surveryId,userType);
        }catch (Exception e){
            logger.error("isHasAnswer error-> " + e.getMessage());
        }
        return null;
    }

    public void updataAnswer(String surveyid,String userid,String usertype, JSONArray answerdata,String storeId) throws Exception{

        if(null != answerdata && answerdata.length() > 0){

            List<Object[]> paramList = new ArrayList<Object[]>();
            for(int i = 0 ; i < answerdata.length() ;i++ ){
                Object[] params= new Object[6];
                params[0] = userid;
                params[1] = surveyid;
                params[2] = usertype;
                params[3] = answerdata.getJSONObject(i).get("question_id");
                params[4] = answerdata.getJSONObject(i).get("answer_id");
                System.out.println("answer_id -> " + answerdata.getJSONObject(i).get("answer_id"));
                params[5] = storeId;

                paramList.add(params);
            }
            this.surveyDao.updataAnswer(paramList);
            this.surveyDao.updateLinkUserStatus(userid,usertype,surveyid,storeId,"1");
        }
    }
}
