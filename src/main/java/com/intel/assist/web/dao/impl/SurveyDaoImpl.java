package com.intel.assist.web.dao.impl;

import com.intel.assist.web.dao.SurveyDao;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by kunpeng on 2015/6/29.
 */
@Repository
public class SurveyDaoImpl extends BaseDaoImpl implements SurveyDao {
    @Override
    public void updateSurveyInfo(String surveyId, String name, String question) throws Exception {
        if(StringUtils.isBlank(surveyId)){
            return;
        }
        String sql = " UPDATE prc_mbl_survey set name=? , question=? where id =?  ";
        this.daoUtil.executeUpdate(sql,new Object[]{name,question,surveyId});

    }

    @Override
    public void delectSurveyInfo(String surveyId) throws Exception {
        String sql = " UPDATE prc_mbl_survey set is_valid = 0 where id = ?   ";
        this.daoUtil.executeUpdate(sql,new Object[]{surveyId});
    }

    @Override
    public void delectSurveysInfo(List<Object[]> params) throws Exception {
        String sql = " UPDATE prc_mbl_survey set is_valid = 0 where id = ?   ";
        this.daoUtil.executeBatchUpdate(sql, params);
    }

    @Override
    public void updataAnswer(List<Object[]> params) throws Exception {
        String sql = " insert into prc_mbl_survey_question_answer(user_id,survey_id,user_type,question_id,answer,store_id) VALUES(?,?,?,?,?,?) ";
        this.daoUtil.executeBatchUpdate(sql,params);
    }

    @Override
    public void insertSurveyInfo(String name, String question, String wwid,String createDate) throws Exception {

        String sql = " insert into prc_mbl_survey(name,createdate,createby,question,status,is_valid) VALUES(?,?,?,?,0,1) ";
        this.daoUtil.executeUpdate(sql,new Object[]{name,createDate,wwid,question});
    }

    @Override
    public int getSendRSPUserTotal(String surveyId,String isSend, String position, String subregions, String citys, String storeType) throws Exception {
        String sql = " select count(1) from  (" + buildRspSql(subregions,citys,storeType,position,isSend,surveyId)+") a ";
        return this.daoUtil.executeQueryNum(sql);
    }

    @Override
    public int getSendChannelUserTotal(String mrType,String subregion,String city,String isSend,String surveyId) throws Exception {
        String sql = " select count(1) from  (" + buildSQL(mrType,subregion,city,isSend,surveyId)+") a ";
        return this.daoUtil.executeQueryNum(sql);
    }

    @Override
    public List<JSONObject> listSendRSPUser(String surveyId, String isSend, String position, String subregions, String citys, String storeType, int page, int size) throws Exception {
        int begin = size*(page -1) + 1;
        int end = page*size;
        String sql = " select * from  (" + buildRspSql(subregions,citys,storeType,position,isSend,surveyId)+") t where t.rank between ? and ?  ";
        return this.daoUtil.executeQueryList(sql,new Object[]{begin,end});
    }

    @Override
    public List<JSONObject> listSendChannelUser(String surveyId, String isSend, String mrType, String subregions, String citys,int page, int size) throws Exception {
        int begin = size*(page -1) + 1;
        int end = page*size;
        String sql = " select * from  (" + buildSQL(mrType, subregions, citys, isSend, surveyId)+") t where t.rank between ? and ?  ";
        return this.daoUtil.executeQueryList(sql,new Object[]{begin,end});
    }

    @Override
    public void sendAllRSPUser(String surveyId, String isSend, String position, String subregions, String citys, String storeType) throws Exception {
        String insertSql =" insert into prc_mbl_survey_linkuser(userid,surveyid,status,usertype,region,city,user_name,store_id,store_name,user_phone,is_valid) " +
                " SELECT t.rep_id,?,0,'RSP',t.subregionname,t.cityname,t.rep_nm,t.storeid,t.storename,t.rep_tel,1 from  (" + buildRspSql(subregions,citys,storeType,position,isSend,surveyId)+") t ";
        this.daoUtil.executeUpdate(insertSql,new Object[]{surveyId});
    }

    @Override
    public void sendAllChannelUser(String surveyId, String isSend,String mrType, String subregions, String citys) throws Exception {
        String insertSql =" insert into prc_mbl_survey_linkuser(userid,surveyid,status,usertype,region,city,user_name,store_id,store_name,user_phone,is_valid) " +
                " SELECT t.rep_id,?,0,'CHANNEL',t.subregionname,t.cityname,t.rep_nm,t.storeid,t.storename,t.rep_tel,1 from  (" + buildSQL(mrType, subregions, citys, isSend, surveyId)+") t ";
        this.daoUtil.executeUpdate(insertSql,new Object[]{surveyId});
    }

    @Override
    public void unsendAllRSPUser(String surveyId, String isSend, String position, String subregions, String citys, String storeType) throws Exception {
        String insertSql =" update prc_mbl_survey_linkuser set is_valid = 0 where id in ( " +
                " SELECT sendUserId from  (" + buildRspSql(subregions,citys,storeType,position,isSend,surveyId)+") t ) ";
        this.daoUtil.executeUpdate(insertSql);
    }

    @Override
    public void unsendAllChannelUser(String surveyId, String isSend, String mrType, String subregions, String citys) throws Exception {
        String insertSql =" update prc_mbl_survey_linkuser set is_valid = 0 where id in ( " +
                " SELECT sendUserId from  (" + buildSQL(mrType, subregions, citys, isSend, surveyId)+") t ) ";
        this.daoUtil.executeUpdate(insertSql);
    }

    @Override
    public void sendSelectUser(List<Object[]> params) throws Exception {
        String insertSql = " insert into prc_mbl_survey_linkuser(userid,surveyid,status,usertype,region,city,user_name,store_id,store_name,user_phone,is_valid) values(?,?,?,?,?,?,?,?,?,?,?) ";
        this.daoUtil.executeBatchUpdate(insertSql,params);
    }

    @Override
    public void unsendSelectUser(List<Object[]> params) throws Exception {
        String sql = " update prc_mbl_survey_linkuser set is_valid = 0 where id=?  ";
        this.daoUtil.executeBatchUpdate(sql,params);
    }

    @Override
    public void updateSurveyStatus(String surveyId, String status) throws Exception {
        String sql = "  update prc_mbl_survey  set status =?  where id = ?  ";
        this.daoUtil.executeUpdate(sql,new Object[]{status,surveyId});
    }

    @Override
    public void updateLinkUserStatus(String repId, String userType, String surveyId, String storeId, String status) throws Exception {
        String updateSurveyStatusSql = " update prc_mbl_survey_linkuser set status = ? where userid=? and surveyid=? and usertype=? and store_id = ?  ";
        this.daoUtil.executeUpdate(updateSurveyStatusSql,new Object[]{status,repId,surveyId,userType,storeId});
    }

    @Override
    public void updateSurveyStatus(String surveyId) throws Exception {
        String sql = "  update prc_mbl_survey  set status =(case when exists (select * from prc_mbl_survey_linkuser b where b.surveyid = id and is_valid = 1) then 1 else 0 end )  where id = ?  ";
        this.daoUtil.executeUpdate(sql,new Object[]{surveyId});
    }

    @Override
    public List<JSONObject> buildExcelContent(String id) throws Exception {
        String sql = " select region,city,store_id,store_name,user_phone,user_name ,userid,surveyid,usertype " +
                " from prc_mbl_survey_linkuser a where status =1 and a.is_valid =1 and a.surveyid =?  ";
       return this.daoUtil.executeQueryList(sql,new Object[]{id});
    }

    @Override
    public List<String> buildQuestionAnswer(String storeId,String userId,String userType,String surveyId) throws Exception {
        String sql = " select answer from prc_mbl_survey_question_answer where is_valid=1 and user_id =? and store_id = ? and survey_id =? and upper(user_type) = ?  ";
        return this.daoUtil.sqlQueryList(sql,new Object[]{userId,storeId,surveyId,userType},"answer");
    }

    @Override
    public List<JSONObject> querySurveyList(String repid, String usertype) throws Exception {
        String sql = " SELECT a.id as surveyid,a.name as surveytitle,a.status as surveystatus,a.question as question ,b.status as ishasanswer,b.store_id as storeId,b.store_name as storeName  " +
                " FROM prc_mbl_survey a,prc_mbl_survey_linkuser b where a.is_valid=1 and b.is_valid=1 and a.id = b.surveyid and b.userid=? and b.usertype=? ";

        List<JSONObject> jsonList = this.daoUtil.executeQueryList(sql,new Object[]{repid,usertype});
        if (jsonList == null || jsonList.size() == 0) {
            return null;
        }
        return  jsonList;
    }

    @Override
    public List<JSONObject> querySurveyList(String repid, String usertype, String storeId) throws Exception {
            String sql = " SELECT a.id as surveyid,a.name as surveytitle,a.status as surveystatus,a.question as question ,b.status as ishasanswer,b.store_id as storeId,b.store_name as storeName " +
                    " FROM prc_mbl_survey a,prc_mbl_survey_linkuser b where a.is_valid=1 and b.is_valid=1 and  a.id = b.surveyid and b.userid=? and b.usertype=? and b.store_id=? ";

            List<JSONObject> jsonList = this.daoUtil.executeQueryList(sql, new Object[]{repid,usertype,storeId});
            if (jsonList == null || jsonList.size() == 0) {
                return null;
            }
            return jsonList;
    }

    @Override
    public String isHasAnswer(String userId, String surveryId, String userType) throws Exception {
        String sql=" SELECT status from prc_mbl_survey_linkuser where userid=? and surveyid =? and usertype=?  ";
        List<String> result = this.daoUtil.sqlQueryList(sql,new Object[]{userId,surveryId,userType},"status");

        if(null == result || result.size() < 1 ){
            return null;
        }
        return result.get(1);
    }

    @Override
    public List<JSONObject> listSurveyByPagination(int pageIndex, int pageSize,String wwid) throws Exception {
        int begin = pageSize*(pageIndex -1) + 1;
        int end = pageIndex*pageSize;
        String sql="SELECT * FROM (SELECT * , ROW_NUMBER() OVER (ORDER BY a.id) as rank  from prc_mbl_survey a where a.createby = ? and is_valid = 1 ) as t where t.rank between ? and ? ";
        return this.daoUtil.executeQueryList(sql,new Object[]{wwid,begin,end});
    }

    @Override
    public int getSurveyTotalSize(String wwid) throws Exception {
        String sql = " SELECT count(1) from prc_mbl_survey where createby = ?  and is_valid = 1 ";
        return this.daoUtil.executeQueryNum(sql,new Object[]{wwid});
    }

    @Override
    public JSONObject getExtendSurveyInfo(String id) throws Exception {

        if (StringUtils.isBlank(id)){return new JSONObject();}

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT surveyid, ");
        sql.append(" SUM (CASE WHEN upper(usertype) = 'RSP' THEN 1 ELSE 0 END ) AS rep_num, ");
        sql.append(" SUM (CASE WHEN upper(usertype) = 'RSP' AND status = 1 THEN 1 ELSE 0 END ) AS rep_join , ");
        sql.append(" SUM (CASE	WHEN upper(usertype) = 'SR' THEN 1 ELSE 0 END ) AS sr_num, ");
        sql.append(" SUM (CASE WHEN upper(usertype) = 'SR' AND status = 1 THEN 1 ELSE 0 END ) AS sr_join , ");
        sql.append(" SUM (CASE	WHEN upper(usertype) = 'CHANNEL' THEN 1 ELSE 0 END ) AS channel_num, ");
        sql.append(" SUM (CASE WHEN upper(usertype) = 'CHANNEL' AND status = 1 THEN 1 ELSE 0 END ) AS channel_join  ");
        sql.append("from prc_mbl_survey_linkuser where surveyid = ? and is_valid =1  group by surveyid   ");

        List<JSONObject> extendSurveyinfos = this.daoUtil.executeQueryList(sql.toString(),new Object[]{id});
        if(null == extendSurveyinfos || extendSurveyinfos.size()<=0 ){
            return null;
        }else {
            return extendSurveyinfos.get(0);
        }
    }

    @Override
    public JSONObject buildSurveyInfo(String surveyId) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM prc_mbl_survey a LEFT JOIN ( ");
        sql.append("SELECT surveyid, ");
        sql.append(" SUM (CASE WHEN upper(usertype) = 'RSP' THEN 1 ELSE 0 END ) AS rep_num, ");
        sql.append(" SUM (CASE WHEN upper(usertype) = 'RSP' AND status = 1 THEN 1 ELSE 0 END ) AS rep_join , ");
        sql.append(" SUM (CASE	WHEN upper(usertype) = 'SR' THEN 1 ELSE 0 END ) AS sr_num, ");
        sql.append(" SUM (CASE WHEN upper(usertype) = 'SR' AND status = 1 THEN 1 ELSE 0 END ) AS sr_join , ");
        sql.append(" SUM (CASE	WHEN upper(usertype) = 'CHANNEL' THEN 1 ELSE 0 END ) AS channel_num, ");
        sql.append(" SUM (CASE WHEN upper(usertype) = 'CHANNEL' AND status = 1 THEN 1 ELSE 0 END ) AS channel_join  ");
        sql.append("from prc_mbl_survey_linkuser where is_valid = 1 group by surveyid ) b  ");
        sql.append(" ON a.id= b.surveyid where a.id = ? and is_valid = 1 ");

        List<JSONObject> surveyinfos = this.daoUtil.executeQueryList(sql.toString(),new Object[]{surveyId});
        if(null == surveyinfos || surveyinfos.size()<=0 ){
            return null;
        }else {
            return surveyinfos.get(0);
        }
    }

    /**
     * 根绝页面条件build SQL
     * @param mrType MR Type
     * @param subregion 分区
     * @param city 城市
     * @return build后的SQL
     */
    public String buildSQL(String mrType,String subregion,String city,String isSend,String surveyId){
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT ");
        sql.append("    a.rep_id,a.rep_nm ,a.rep_tel ,a.rep_email,a.city_id as cityid ,b.city_std_loc_nm as cityname ,b.grid_cd as gridcd,b.grid_nm as gridnm,b.subrgn_nm as subregionname ,a.co_id as storeid,a.co_nm as storename,a4.id as sendUserId , " );
        sql.append("    ROW_NUMBER() OVER (ORDER BY a.rep_id) as rank  ");
        sql.append(" FROM ");
        sql.append("    external_v_chnl_rep_lst a ");
        sql.append("    LEFT JOIN prc_mbl_survey_linkuser a4 on a4.userid = a.rep_id AND a4.store_id = a.co_id AND a4.is_valid = 1 AND a4.surveyid = "+surveyId+ " AND upper(a4.usertype) = 'CHANNEL', ");
        sql.append("    external_prc_v_rgn_subrgn_grid_pref_city b ");
        sql.append(" WHERE ");
        sql.append("    a.rec_sts_id = 1 ");
        //if mrtype = 1 then MR , if mrType = 2 then T5 plus, if mrType = 3 then mr + T5plus
        if(StringUtils.equals("1",mrType)){
            sql.append(" AND a.co_city_type NOT IN ('县镇') ");
        }else if(StringUtils.equals("2",mrType)){
            sql.append(" AND a.co_city_type IN ('县镇') ");
        }
        sql.append(" AND a.city_id = b.city_id ");

        if(StringUtils.isNotBlank(subregion)){
            sql.append(" AND b.subrgn_cd in ("+subregion+") ");
        }

        if(StringUtils.isNotBlank(city)){
            sql.append(" AND a.city_id IN ("+city+") ");
        }

        if(StringUtils.equalsIgnoreCase("1",isSend)){
            sql.append(" and a4.id is not null ");
        }else if(StringUtils.equalsIgnoreCase("0",isSend)){
            sql.append(" and a4.id is null ");
        }

        return sql.toString();
    }

    /**
     * 生成分页临时表
     *
     * @param cityId,storeType,postion
     * @return String
     * @throws org.codehaus.jettison.json.JSONException
     */
    public String buildRspSql(String subregion,String cityId,String storeType,String position,String isSend,String surveyId) {

        StringBuilder sql = new StringBuilder("");
        if(storeType.contains("DIY")){
            sql = new StringBuilder("SELECT DISTINCT a.stor_id AS stor_id FROM external_v_diy_stor_lst_mbl_2 a")
                    .append(" WHERE a.city_id in(").append(cityId).append(") ");
        }
        if(storeType.contains("MSR")){
            if(sql.toString().equals("")){
                sql = new StringBuilder(" SELECT DISTINCT a.stor_id AS stor_id FROM ");
            }else{
                sql.append(" union ");
                sql.append(" SELECT DISTINCT a.stor_id AS stor_id FROM ");
            }
            sql.append(" external_v_msr_stor_lst_mbl a LEFT JOIN stdb_stor b ON a.stor_id = b.stor_id AND b.rec_sts_id = 1 ")
                    .append(" LEFT JOIN stdb_stor_sku_assrtm_reltn c ON a.stor_id = c.stor_id ,")
                    .append(" stdb_stor_sku_assrtm d WHERE a.city_id in(").append(cityId).append(") AND c.sku_assrtm_type_id = d.sku_assrtm_type_id ")
                    .append(" AND d.sku_assrtm_type_id IN (1, 2, 4, 7)");
        }
        if(storeType.contains("MNC")){
            if(sql.toString().equals("")){
                sql = new StringBuilder("SELECT DISTINCT a.[Intel Store ID] AS stor_id ");
            }else{
                sql.append(" union ");
                sql.append("SELECT DISTINCT a.[Intel Store ID] AS stor_id ");
            }
            sql.append("FROM external_v_xoem_stor_lst_mbl a , external_v_stor_rep_lst b  ")
                    .append(" where a.[City ID] in(").append(cityId).append(") AND a.[Intel Store ID] = b.stor_id AND b.rec_sts_id = 1 ");
        }
        StringBuilder listRspSql = new StringBuilder("SELECT ROW_NUMBER() over(order by subregionname,gridcd,cityid,rep_id) as rank,* FROM (")
                .append("select distinct a1.rep_id as rep_id,a1.rep_nm as rep_nm,a1.rep_tel as rep_tel,a1.rep_email as rep_email, ")
                .append(" a1.city_id as cityid,a2.city_std_loc_nm as cityname,a2.grid_cd as gridcd,a2.grid_nm as gridnm,a2.subrgn_nm as subregionname,a1.stor_id as storeid,a3.stor_nm as storename,a4.id as sendUserId ")
                .append(" from  external_v_stor_rep_lst a1 LEFT JOIN prc_mbl_survey_linkuser a4 on a4.userid = a1.rep_id AND a4.store_id = a1.stor_id AND a4.is_valid = 1 AND a4.surveyid = "+surveyId+ " AND a4.usertype = 'RSP'  ,external_prc_v_rgn_subrgn_grid_pref_city a2,stdb_stor a3 where  a1.rec_sts_id = 1 ")
                .append(" and a1.stor_id in (").append(sql).append(") and a1.city_id = a2.city_id and a2.subrgn_cd  in (")
                .append(subregion).append(") ");
        if(position.equals("3")){
            listRspSql.append(" and (a1.rep_asgn_role_id = '1' or a1.rep_asgn_role_id = '2')");
        }else{
            listRspSql.append(" and a1.rep_asgn_role_id = ").append(position);
        }

        if(StringUtils.equalsIgnoreCase("1",isSend)){
            listRspSql.append(" and a4.id is not null ");
        }else if(StringUtils.equalsIgnoreCase("0",isSend)){
            listRspSql.append(" and a4.id is null ");
        }
        listRspSql.append(" and a1.stor_id = a3.stor_id ");
        listRspSql.append(") as tt");

        return listRspSql.toString();
    }
}
