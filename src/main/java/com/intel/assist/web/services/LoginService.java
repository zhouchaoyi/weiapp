package com.intel.assist.web.services;

import com.intel.assist.model.entity.LoginUser;
import com.intel.assist.web.dao.LoginDao;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService extends BaseServices{
	
    private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private LoginDao loginDao;

    public LoginUser login(String userName, String password) {


		try {
			JSONObject jsonObject = loginDao.login(userName,password);
			if(null != jsonObject){
			    LoginUser user = new LoginUser();
				user.setRoleId(jsonObject.getString("roleId"));
				user.setUserName(jsonObject.getString("userName"));
				user.setWwid(jsonObject.getString("wwid"));
				return user;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

    public boolean isExistSession(String sessionId){
		try {
			return this.loginDao.isExistSession(sessionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
