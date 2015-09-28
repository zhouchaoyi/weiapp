package com.intel.assist.web.dao;

import org.codehaus.jettison.json.JSONObject;

import java.util.List;

/**
 * Created by kunpeng on 2015/6/29.
 */
public interface BaseDao {

    public List<JSONObject> listSubregion(String wwid,String roleId) throws Exception;
    public List<JSONObject> listCityBySubregionCode(String code,String wwId,String roleId) throws Exception;


}
