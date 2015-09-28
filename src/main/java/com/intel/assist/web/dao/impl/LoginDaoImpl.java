package com.intel.assist.web.dao.impl;

import com.intel.assist.utils.RC2Encryptor;
import com.intel.assist.utils.SecuritySHAUtils;
import com.intel.assist.web.dao.LoginDao;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resources;
import java.util.List;

/**
 * Created by Ecic Chen on 2015/8/5.
 */

@Repository
public class LoginDaoImpl extends BaseDaoImpl implements LoginDao{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public JSONObject login(String userName, String password) throws Exception {

        String sql = new StringBuilder("SELECT a.*, b.rl_id, b.rl_nm FROM view_internal_user_login a, ")
                .append("vw_mip_role_user b WHERE a.slsprs_id = b.slsprs_id AND a.wwid = b.wwid")
                .append(" AND b.rl_id IN (40, 72, 73, 272, 273, 274, 300, 400, 402)")
                .append(" AND a.email = ? ").toString();

        List<JSONObject> loginUserList = this.daoUtil.executeQueryList(sql,new Object[]{userName});
        if (loginUserList == null || loginUserList.size() == 0) {
            return null;
        }
        String newSql = "select log_pwd from view_internal_user_login where email = ? ";
        List<String> pwdList = this.daoUtil.sqlQueryList(newSql,new Object[]{userName},"log_pwd");
        //比对密码
        //String pppp = SecuritySHAUtils.bin2hex(RC2Encryptor.decrypt(pwdList.get(0)));
        String ppppp = RC2Encryptor.decrypt(pwdList.get(0));
        if (!password.equalsIgnoreCase(ppppp)) {
            return null;
        }
        try {

            JSONObject json = new JSONObject();
            json.put("userName",loginUserList.get(0).getString("full_name"));
            json.put("wwid",loginUserList.get(0).getString("wwid"));

            String rl_id = null;
            for(int i=0;i<loginUserList.size();i++){
                String rlid = loginUserList.get(i).getString("rl_id");
                if(i==0){
                    rl_id = rlid;
                }else{
                    rl_id = rl_id + "," + rlid;
                }
            }
            json.put("roleId",rl_id);

            return json;
        } catch (JSONException e) {
            logger.error("jason getValue failed", e);
        }

        return  null;
    }

    public boolean isExistSession(String sessionId) throws Exception {
        if(StringUtils.isBlank(sessionId)){return false;}
        String sql = " select id from prc_mbl_session where session_id = ? ";

        List list = this.daoUtil.sqlQueryList(sql,new Object[]{sessionId},"id");
        if(null != list && list.size() > 0){
            return true;
        }else{
            return  false;
        }
    }
}
