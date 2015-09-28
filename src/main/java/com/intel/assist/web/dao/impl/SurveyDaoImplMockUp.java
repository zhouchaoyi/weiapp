package com.intel.assist.web.dao.impl;

import com.intel.assist.model.entity.FileLibrary;
import com.intel.assist.web.dao.FileLibraryDao;
import com.intel.assist.web.dao.SurveyDao;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by kunpeng on 2015/6/24.
 */
//@Repository
public abstract class SurveyDaoImplMockUp implements SurveyDao {
    @Override
    public void delectSurveysInfo(List<Object[]> params) throws Exception {

    }

    @Override
    public List<JSONObject> listSubregion(String wwid, String roleId) throws Exception {
        return subregions;
    }

    @Override
    public List<JSONObject> listCityBySubregionCode(String code, String wwId, String roleId) throws Exception {
        cityMaps =new ArrayList<JSONObject>();

        for (int i = 0; i < 100; i++) {

            JSONObject json = new JSONObject();
            try {
                json.put("cityid","city_code-"+code+i);
                json.put("citynm","city_name-"+code+i*8);

                cityMaps.add(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return cityMaps;
    }

    @Override
    public void updateSurveyInfo(String surveyId, String name, String question) throws Exception {
        return;
    }

    @Override
    public void delectSurveyInfo(String surveyId) throws Exception {

    }

    @Override
    public void insertSurveyInfo(String name, String question, String wwid, String createDate) throws Exception {

    }

    public List<JSONObject> jsonList = null;
    public List<JSONObject> subregions=null;
    public List<JSONObject> cityMaps=null;

    public SurveyDaoImplMockUp() {
        jsonList  = new ArrayList<JSONObject>();
        subregions =new ArrayList<JSONObject>();


        for (int i = 0; i < 100; i++) {

            JSONObject json = new JSONObject();
            try {
                json.put("id",i);
                json.put("name","name" + i%5);
                json.put("createdate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                json.put("rspCount",0);
                json.put("rspJoin",0);
                json.put("channelCount",0);
                json.put("channelJoin",0);

                jsonList.add(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 7; i++) {

            JSONObject json = new JSONObject();
            try {
                json.put("subrgncd","subregion_code-"+i);
                json.put("subrgnnm","subregion_name-"+i*8);

                subregions.add(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<JSONObject> listSurveyByPagination(int pageIndex, int pageSize,String wwid) throws Exception {
        return jsonList.subList(pageSize*(pageIndex-1)+1,pageSize*pageIndex);
    }

    @Override
    public int getSurveyTotalSize(String wwid) throws Exception {
        return jsonList.size();
    }

    @Override
    public JSONObject getExtendSurveyInfo(String id) throws Exception {
        return null;
    }

    @Override
    public JSONObject buildSurveyInfo(String surveyId) throws Exception {
        JSONObject json = new JSONObject();
        json.put("rep_num",0);
        json.put("channel_num",0);
        json.put("status",1);
        json.put("rep_join",0);
        json.put("channel_join",0);
        json.put("createdate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        json.put("name","name");
        return json;
    }

    public List<JSONObject> listSubregion() throws Exception {
        return subregions;
    }

    public List<JSONObject> listCityBySubregionCode(String code) throws Exception {

        cityMaps =new ArrayList<JSONObject>();

        for (int i = 0; i < 100; i++) {

            JSONObject json = new JSONObject();
            try {
                json.put("cityid","city_code-"+code+i);
                json.put("citynm","city_name-"+code+i*8);

                cityMaps.add(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return cityMaps;
    }
}
