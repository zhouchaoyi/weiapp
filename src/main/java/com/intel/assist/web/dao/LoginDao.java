package com.intel.assist.web.dao;

import com.intel.assist.model.entity.LoginUser;
import org.codehaus.jettison.json.JSONObject;

/**
 * Created by Ecic Chen on 2015/8/5.
 */
public interface LoginDao extends BaseDao{

    public JSONObject login(String userName, String password) throws Exception;
    public boolean isExistSession(String sessionId) throws Exception;

}
