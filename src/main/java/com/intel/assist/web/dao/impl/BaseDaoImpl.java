package com.intel.assist.web.dao.impl;

import com.intel.assist.web.dao.BaseDao;
import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kunpeng on 2015/6/29.
 */
public abstract class BaseDaoImpl implements BaseDao{

    @Autowired
    protected BaseDaoUtil daoUtil;

    @Override
    public List<JSONObject> listSubregion(String wwid,String roleId) throws Exception {

        String[] str = roleId.split(",");
        if(null == str){
            str = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List<Object> params = new ArrayList<Object>();



        for (int i = 0; i < str.length; i++) {
            if (Integer.valueOf(str[i]) == 400 || Integer.valueOf(str[i]) == 402 || Integer.valueOf(str[i]) == 40) {
                String sql = new StringBuilder("SELECT DISTINCT b.subrgn_cd as subrgncd,b.subrgn_nm as subrgnnm,b.grid_cd ")
                        .append(" FROM external_prc_v_rgn_subrgn_grid_pref_city b ")
                        .append(" where b.city_id IS NOT NULL AND b.city_alt_nm_1 is not null ORDER BY subrgncd").toString();
                return this.daoUtil.executeQueryList(sql,null);
            }
        }


        StringBuilder sqlStr = new StringBuilder("SELECT DISTINCT b.subrgn_cd as subrgncd,b.subrgn_nm as subrgnnm ");
        sqlStr.append(" FROM mip_usr_rpt_loc a,external_prc_v_rgn_subrgn_grid_pref_city b ");
        sqlStr.append(" WHERE a.rl_id in( ''" );
           for(int i=0 ;i < str.length ; i++ ){
               sqlStr.append(" , ? ");
               params.add(str[i]);
           }
        sqlStr.append( ") AND a.wwid = ?  ");

        params.add(wwid);

        sqlStr.append("  AND a.grid_cd = b.grid_cd AND b.city_id IS NOT NULL AND b.city_alt_nm_1 is not null ");
        sqlStr.append(" union ");
        sqlStr.append("SELECT DISTINCT b.subrgn_cd as subrgncd,b.subrgn_nm as subrgnnm ");
        sqlStr.append(" FROM mip_usr_rpt_loc a,external_prc_v_rgn_subrgn_grid_pref_city b  ");
        sqlStr.append(" WHERE a.rl_id in(' ' " );
        for(int i=0 ;i < str.length ; i++ ){
            sqlStr.append(" , ? ");
            params.add(str[i]);
        }
        sqlStr.append( ") AND a.wwid = ?  ");

        params.add(wwid);

        sqlStr.append("  AND a.subrgn_cd= b.subrgn_cd AND b.city_id IS NOT NULL AND b.city_alt_nm_1 is not null ORDER BY subrgncd");
        return this.daoUtil.executeQueryList(sqlStr.toString(),params.toArray());
    }

    @Override
    public List<JSONObject> listCityBySubregionCode(String code,String wwId,String roleId) throws Exception {

        String[] str = roleId.split(",");
        if(null == str){
            str = ArrayUtils.EMPTY_STRING_ARRAY;
        }

        List<Object> params = new ArrayList<Object>();

        for (int i = 0; i < str.length; i++) {
            if (Integer.valueOf(str[i]) == 400 || Integer.valueOf(str[i]) == 402 || Integer.valueOf(str[i]) == 40) {
                String sql = new StringBuilder("SELECT DISTINCT b.subrgn_cd as subrgncd,b.subrgn_nm as subrgnnm,b.grid_cd, ")
                        .append(" b.city_id as cityid,REPLACE(b.city_alt_nm_1, CHAR(13) + CHAR(10), '')  as citynm")
                        .append(" FROM external_prc_v_rgn_subrgn_grid_pref_city b LEFT OUTER JOIN prc_v_citytype_new c ")
                        .append(" on b.city_id = c.city_id ")
                        .append(" where b.city_id IS NOT NULL AND b.city_alt_nm_1 is not null and b.subrgn_cd=? ").toString();
                return this.daoUtil.executeQueryList(sql,new Object[]{code});
            }
        }

        StringBuilder sqlStr = new StringBuilder("SELECT DISTINCT b.subrgn_cd as subrgncd,b.subrgn_nm as subrgnnm, ")
                .append(" b.city_id as cityid,REPLACE(b.city_alt_nm_1, CHAR(13) + CHAR(10), '')  as citynm")
                .append(" FROM mip_usr_rpt_loc a,external_prc_v_rgn_subrgn_grid_pref_city b LEFT OUTER JOIN prc_v_citytype_new c ")
                .append(" on b.city_id = c.city_id ");
        sqlStr.append(" WHERE a.rl_id in(''" );
        for(int i=0 ;i < str.length ; i++ ){
            sqlStr.append(" , ? ");
            params.add(str[i]);
        }
        sqlStr.append( ") AND a.wwid = ?  ");
        params.add(wwId);

        sqlStr.append("  AND a.grid_cd = b.grid_cd AND b.subrgn_cd=? and  b.city_id IS NOT NULL AND b.city_alt_nm_1 is not null ");
        params.add(code);



        sqlStr.append(" union ");
        sqlStr.append("SELECT DISTINCT b.subrgn_cd as subrgncd,b.subrgn_nm as subrgnnm, ")
                .append(" b.city_id as cityid,REPLACE(b.city_alt_nm_1, CHAR(13) + CHAR(10), '')  as citynm")
                .append(" FROM mip_usr_rpt_loc a,external_prc_v_rgn_subrgn_grid_pref_city b LEFT OUTER JOIN prc_v_citytype_new c ")
                .append(" on b.city_id = c.city_id ");
        sqlStr.append(" WHERE a.rl_id in(''" );
        for(int i=0 ;i < str.length ; i++ ){
            sqlStr.append(" , ? ");
            params.add(str[i]);
        }
        sqlStr.append( ") AND a.wwid = ?  ");

        params.add(wwId);

        sqlStr.append("  AND a.subrgn_cd= b.subrgn_cd AND b.subrgn_cd=? and  b.city_id IS NOT NULL AND b.city_alt_nm_1 is not null ");
        params.add(code);

        return this.daoUtil.executeQueryList(sqlStr.toString(),params.toArray());
    }
}
