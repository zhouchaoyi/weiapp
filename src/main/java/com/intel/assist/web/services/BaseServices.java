package com.intel.assist.web.services;

import com.intel.assist.web.dao.BaseDao;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by kunpeng on 2015/7/7.
 */
public class BaseServices {


    public BaseDao getDao(){
        return null;
    }


    public List<JSONObject> listSubregion(String wwId,String roleId) {
        try {
            return this.getDao().listSubregion(wwId,roleId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<JSONObject> listCityBySubregionCode(String code,String wwId,String roleId) {
        try {
            return this.getDao().listCityBySubregionCode(code,wwId,roleId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String statusMapping(String status){
        if(StringUtils.equals("1",status)){
            return "已发送";
        }else if(StringUtils.equals("-1",status)){
            return "已关闭";
        }else if(StringUtils.equals("0",status)){
            return "未发送";
        }else{
            return "未知";
        }
    }
}
