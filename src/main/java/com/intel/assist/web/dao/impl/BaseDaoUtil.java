package com.intel.assist.web.dao.impl;

import net.sf.xsshtmlfilter.HTMLFilter;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kunpeng on 2015/6/29.
 */

@Repository
public class BaseDaoUtil {

    private static Logger logger = LoggerFactory.getLogger(BaseDaoUtil.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void executeBatchUpdate(String sql,List<Object[]> params){

        jdbcTemplate.batchUpdate(sql,params);

    }

    /**
     * 执行insert update insert 语句
     * @param sql
     * @param paramsArray
     * @return 影响行数
     */
    public int executeUpdate(String sql, Object[] paramsArray){

        this.logInfo(sql,null,paramsArray);
        return jdbcTemplate.update(sql, paramsArray);

    }

    /**
     * 执行insert update insert 语句
     * @param sql
     * @return 影响行数
     */
    public int executeUpdate(String sql) {
        return jdbcTemplate.update(sql);
    }

    /**
     *  返回Json结果集
     * @param sql
     * @param paramsArray
     * @return
     */
    public List<JSONObject> executeQueryList(final String sql, final Object[] paramsArray) {

        this.logInfo(sql,null,paramsArray);
        return jdbcTemplate.execute(sql, new PreparedStatementCallback<List<JSONObject>>() {
            public List<JSONObject> doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {
                if(null != paramsArray && paramsArray.length > 0){
                    for (int i = 0; i < paramsArray.length; i++) {
                        pstmt.setObject(i + 1, paramsArray[i]);
                    }
                }
                ResultSet rs = pstmt.executeQuery();
                return resultSet2Json(rs);
            }
        });
    }

    /**
     * 根据传入的列名 返回对应的结果集
     * @param sql
     * @param paramsArray
     * @param key
     * @return
     */
    public List<String> sqlQueryList(String sql,final Object[] paramsArray, final String key){

        this.logInfo(sql,null,paramsArray);
        return jdbcTemplate.execute(sql, new PreparedStatementCallback<List<String>>() {
            public List<String> doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {
                for (int i = 0; i < paramsArray.length; i++) {
                    pstmt.setObject(i + 1, paramsArray[i]);
                }
                ResultSet rs = pstmt.executeQuery();
                List<String> result = new ArrayList<String>();
                while (rs.next()) {
                    result.add(rs.getString(key));
                }
                return result;
            }
        });
    }

    /**
     * 返回 count(1) 用于统计行数
     * @param sql
     * @param paramsArray
     * @return
     */
    public int executeQueryNum(final String sql, final Object[] paramsArray) {

        this.logInfo(sql,null,paramsArray);
        return jdbcTemplate.execute(sql, new PreparedStatementCallback<Integer>() {
            public Integer doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {
                if(null != paramsArray && paramsArray.length > 0){
                    for (int i = 0; i < paramsArray.length; i++) {
                        pstmt.setObject(i + 1, paramsArray[i]);
                    }
                }
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return null;
            }
        });
    }

    /**
     * 返回 count(1) 用于统计行数
     * @param sql
     * @return
     */
    public int executeQueryNum(String sql) {
        this.logInfo(sql,null,null);
        return this.executeQueryNum(sql,null);
    }


    private void logInfo(String sql,String sqlKey,Object[] paramsArray){
        try{
            JSONObject o = new JSONObject();
            o.put("sql",sql);
            if(null != sqlKey && sqlKey.length() > 0){
                o.put("sqlKey",sqlKey);
            }
            if(null != paramsArray && paramsArray.length > 0){
                o.put("paramsArray", Arrays.toString(paramsArray));
            }
            logger.info(o.toString());
        }catch (JSONException jsonE){
            logger.error("sqlUtils log error sql-> " + sql);
            if(null != sqlKey && sqlKey.length() > 0){
                logger.error("sqlUtils log error sqlKey- > " + sqlKey);
            }
            if(null != paramsArray && paramsArray.length > 0){
                logger.error("sqlUtils log error paramsArray-> " + Arrays.toString(paramsArray));
            }
            logger.error("sqlUtils log error info-> " + jsonE.getMessage());
        }
    }

    public List<JSONObject> resultSet2Json(ResultSet rs) throws SQLException {
        if(rs == null){return null;}
        List<JSONObject> retList = new ArrayList<JSONObject>();
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();
        while (rs.next()) {
            JSONObject subData = new JSONObject();

            //HTMLFilter htmlFilter = new HTMLFilter();

            for (int i = 1; i <= colCount; i++) {
                String name = meta.getColumnName(i);
                Object value = rs.getObject(i);
                try {
                    if (value == null) {
                        if (meta.getColumnType(i) == Types.INTEGER) {
                            value = new Integer(-1);
                        } else {
                            value = "";
                        }
                    }
                   // String sValue=htmlFilter.filter(String.valueOf(value));
                   // subData.put(name, sValue);
                    subData.put(name, value.toString().trim());
                } catch (JSONException e) {
                    logger.error("jason parseToMap failed", e);
                }
            }
            retList.add(subData);
        }
        return retList;
    }
}
