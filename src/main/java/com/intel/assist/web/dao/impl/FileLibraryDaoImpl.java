package com.intel.assist.web.dao.impl;

import com.intel.assist.web.dao.FileLibraryDao;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by kunpeng on 2015/6/24.
 */
@Repository
public class FileLibraryDaoImpl extends BaseDaoImpl implements FileLibraryDao {
    @Override
    public List<JSONObject> listFilesByPagination(int pageIndex, int pageSize,String wwid) throws Exception {
        int begin = pageSize*(pageIndex -1)+1;
        int end = pageIndex*pageSize;
        StringBuilder sql=new StringBuilder("SELECT * FROM (")
                .append(" SELECT a.id,a.file_name,b.cate_name as file_cate,a.create_date, ROW_NUMBER() OVER (ORDER BY a.id) as rank ")
                .append(" from prc_mbl_file_library a ")
                .append(" left join prc_mbl_file_library_file_cate b on b.id=a.file_cate ")
                .append(" where a.create_by = ? and a.is_valid = 1 ")
                .append(") as t where t.rank between ? and ? ");
        return this.daoUtil.executeQueryList(sql.toString(),new Object[]{wwid,begin,end});
    }

    @Override
    public int getFilesTotalSize(String wwid) throws Exception {
        int size=0;
        String sql="select count(1) from prc_mbl_file_library where is_valid=1  and create_by = ? "; //where create_by=?
        size=this.daoUtil.executeQueryNum(sql,new Object[]{wwid});
        return size;
    }

    @Override
    public JSONObject getExtendFileInfo(String fileId) throws Exception {
        if (StringUtils.isBlank(fileId)){return new JSONObject();}

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT file_id, ");
        sql.append(" ");
        sql.append(" SUM (CASE WHEN upper(user_type) = 'RSP' THEN 1 ELSE 0 END ) AS rep_num, ");
        sql.append(" SUM (CASE WHEN upper(user_type) = 'RSP' AND status = 1 THEN 1 ELSE 0 END ) AS rep_join , ");
        sql.append(" ");
        sql.append(" SUM (CASE	WHEN upper(user_type) = 'SR' THEN 1 ELSE 0 END ) AS sr_num, ");
        sql.append(" SUM (CASE WHEN upper(user_type) = 'SR' AND status = 1 THEN 1 ELSE 0 END ) AS sr_join , ");
        sql.append(" ");
        sql.append(" SUM (CASE	WHEN upper(user_type) = 'CHANNEL' THEN 1 ELSE 0 END ) AS channel_num, ");
        sql.append(" SUM (CASE WHEN upper(user_type) = 'CHANNEL' AND status = 1 THEN 1 ELSE 0 END ) AS channel_join  ");
        sql.append(" ");
        sql.append("from prc_mbl_file_library_send_user where file_id = ? and is_valid =1  group by file_id   ");

        List<JSONObject> extendFileinfos = this.daoUtil.executeQueryList(sql.toString(),new Object[]{fileId});
        if(null == extendFileinfos || extendFileinfos.size()<=0 ){
            return null;
        }else {
            return extendFileinfos.get(0);
        }
    }

    @Override
    public void addFile(String s3ReturnUrl, String file_name, String file_cat, String file_desc, String file_size, String bucket_name, String object_key,String wwid) throws Exception {
        StringBuilder sql=new StringBuilder("insert into prc_mbl_file_library (s3_path,size,file_cate,file_name,file_desc,create_by,create_date,status,bucket_name,object_key,is_valid) ")
                .append(" values (?,?,?,?,?,?,getdate(),0,?,?,1)");
        this.daoUtil.executeUpdate(sql.toString(), new Object[]{s3ReturnUrl, file_size, file_cat, file_name, file_desc,wwid, bucket_name, object_key});
    }

    @Override
    public List<JSONObject> listFileCate() throws Exception {
        StringBuilder sql=new StringBuilder("select id,cate_name from prc_mbl_file_library_file_cate ");
        return this.daoUtil.executeQueryList(sql.toString(),new Object[]{});
    }

    @Override
    public List<JSONObject> listAll(String rep_id, String stor_id, String file_cate) throws Exception {
        StringBuilder sql=new StringBuilder("select a.id as send_id,a.file_id,b.file_name,b.size,c.cate_name as file_cate,b.file_desc,a.send_date,b.object_key from prc_mbl_file_library_send_user a ")
                .append(" left join prc_mbl_file_library b on b.id=a.file_id ")
                .append(" left join prc_mbl_file_library_file_cate c on c.id=b.file_cate ")
                .append(" where a.user_id=? and a.store_id=? and a.status=1 and c.id=?");
        List<JSONObject> list=this.daoUtil.executeQueryList(sql.toString(),new Object[]{rep_id,stor_id,file_cate});
        for(int i=0;null!=list&&i<list.size();i++) {
            String sFileType=list.get(i).getString("object_key").split("\\.")[1];
            list.get(i).remove("object_key");
            list.get(i).put("file_type",sFileType);
        }
        return list;
    }

    @Override
    public List<JSONObject> getSignedUrl(String file_id) throws Exception {
        StringBuilder sql=new StringBuilder("select a.bucket_name,a.object_key from prc_mbl_file_library a ")
                .append(" where a.id=?");
         return this.daoUtil.executeQueryList(sql.toString(),new Object[]{file_id});
    }

    @Override
    public void recordDownload(String send_id, String status) throws Exception {
        StringBuilder sql=new StringBuilder("insert into prc_mbl_file_library_download (send_id,download_date,status) values (?,getdate(),?)");
        this.daoUtil.executeUpdate(sql.toString(), new Object[]{send_id, status});
    }

    @Override
    public JSONObject getLibraryInfoById(String fileId) throws Exception {
        StringBuilder sql=new StringBuilder();

        sql.append(" SELECT * FROM prc_mbl_file_library a LEFT JOIN ( ");
        sql.append("SELECT file_id, ");
        sql.append(" SUM (CASE WHEN upper(user_type) = 'RSP' THEN 1 ELSE 0 END ) AS rep_num, ");
        sql.append(" SUM (CASE WHEN upper(user_type) = 'RSP' AND status = 1 THEN 1 ELSE 0 END ) AS rep_join , ");
        sql.append(" SUM (CASE	WHEN upper(user_type)= 'SR' THEN 1 ELSE 0 END ) AS sr_num, ");
        sql.append(" SUM (CASE WHEN upper(user_type) = 'SR' AND status = 1 THEN 1 ELSE 0 END ) AS sr_join , ");
        sql.append(" SUM (CASE	WHEN upper(user_type) = 'CHANNEL' THEN 1 ELSE 0 END ) AS channel_num, ");
        sql.append(" SUM (CASE WHEN upper(user_type) = 'CHANNEL' AND status = 1 THEN 1 ELSE 0 END ) AS channel_join  ");
        sql.append("from prc_mbl_file_library_send_user where is_valid = 1 group by file_id ) b  ");
        sql.append(" ON a.id= b.file_id where a.id = ? and is_valid = 1 ");
        return this.daoUtil.executeQueryList(sql.toString(), new Object[]{fileId}).get(0);
    }

    @Override
    public void saveFile(String fileId,String fileName,String fileCate,String fileDesc) throws Exception {
        StringBuilder sql=new StringBuilder("update prc_mbl_file_library set file_name=?,file_cate=?,file_desc=? where id=?");
        this.daoUtil.executeUpdate(sql.toString(), new Object[]{fileName,fileCate,fileDesc,fileId});
    }

    @Override
    public void deleteById(int id) throws Exception {
        StringBuilder sql=new StringBuilder("update prc_mbl_file_library set is_valid=-1 where id=?");
        this.daoUtil.executeUpdate(sql.toString(), new Object[]{id});
    }

    @Override
    public void deleteItems(String ids) throws Exception {
        StringBuilder sql=new StringBuilder("update prc_mbl_file_library set is_valid=-1 where id in ("+ids+")");
        this.daoUtil.executeUpdate(sql.toString(), new Object[]{});
    }

    @Override
    public int getSendRSPUserTotal(String fileId, String isSend, String position, String subregions, String citys, String storeType) throws Exception {
        String sql = " select count(1) from  (" + buildRspSql(subregions,citys,storeType,position,isSend,fileId)+") a ";
        return this.daoUtil.executeQueryNum(sql);
    }

    @Override
    public int getSendChannelUserTotal(String mrType, String subregion, String city, String isSend, String fileId) throws Exception {
        String sql = " select count(1) from  (" + buildSQL(mrType,subregion,city,isSend,fileId)+") a ";
        return this.daoUtil.executeQueryNum(sql);
    }

    @Override
    public List<JSONObject> listSendRSPUser(String fileId, String isSend, String position, String subregions, String citys, String storeType, int page, int size) throws Exception {
        int begin = size*(page -1) + 1;
        int end = page*size;
        String sql = " select * from  (" + buildRspSql(subregions,citys,storeType,position,isSend,fileId)+") t where t.rank between ? and ?  ";
        return this.daoUtil.executeQueryList(sql,new Object[]{begin,end});
    }

    @Override
    public List<JSONObject> listSendChannelUser(String fileId, String isSend, String mrType, String subregions, String citys, int page, int size) throws Exception {
        int begin = size*(page -1) + 1;
        int end = page*size;
        String sql = " select * from  (" + buildSQL(mrType, subregions, citys, isSend, fileId)+") t where t.rank between ? and ?  ";
        return this.daoUtil.executeQueryList(sql,new Object[]{begin,end});
    }

    @Override
    public void sendAllRSPUser(String fileId, String isSend, String position, String subregions, String citys, String storeType) throws Exception {
        String insertSql =" insert into prc_mbl_file_library_send_user(user_id,file_id,status,user_type,region,city,user_name,store_id,store_name,user_phone,is_valid) " +
                " SELECT t.rep_id,?,0,'RSP',t.subregionname,t.cityname,t.rep_nm,t.storeid,t.storename,t.rep_tel,1 from  (" + buildRspSql(subregions,citys,storeType,position,isSend,fileId)+") t ";
        this.daoUtil.executeUpdate(insertSql,new Object[]{fileId});
    }

    @Override
    public void sendAllChannelUser(String fileId, String isSend, String mrType, String subregions, String citys) throws Exception {
        String insertSql =" insert into prc_mbl_file_library_send_user(user_id,file_id,status,user_type,region,city,user_name,store_id,store_name,user_phone,is_valid) " +
                " SELECT t.rep_id,?,0,'CHANNEL',t.subregionname,t.cityname,t.rep_nm,t.storeid,t.storename,t.rep_tel,1 from  (" + buildSQL(mrType, subregions, citys, isSend, fileId)+") t ";
        this.daoUtil.executeUpdate(insertSql,new Object[]{fileId});
    }

    @Override
    public void unsendAllRSPUser(String fileId, String isSend, String position, String subregions, String citys, String storeType) throws Exception {
        String insertSql =" update prc_mbl_file_library_send_user set is_valid = 0 where id in ( " +
                " SELECT sendUserId from  (" + buildRspSql(subregions,citys,storeType,position,isSend,fileId)+") t ) ";
        this.daoUtil.executeUpdate(insertSql);
    }

    @Override
    public void unsendAllChannelUser(String fileId, String isSend, String mrType, String subregions, String citys) throws Exception {
        String insertSql =" update prc_mbl_file_library_send_user set is_valid = 0 where id in ( " +
                " SELECT sendUserId from  (" + buildSQL(mrType, subregions, citys, isSend, fileId)+") t ) ";
        this.daoUtil.executeUpdate(insertSql);
    }

    @Override
    public void sendSelectUser(List<Object[]> params) throws Exception {
        String insertSql = " insert into prc_mbl_file_library_send_user(user_id,file_id,status,user_type,region,city,user_name,store_id,store_name,user_phone,is_valid) values(?,?,?,?,?,?,?,?,?,?,?) ";
        this.daoUtil.executeBatchUpdate(insertSql,params);
    }

    @Override
    public void unsendSelectUser(List<Object[]> params) throws Exception {
        String sql = " update prc_mbl_file_library_send_user set is_valid = 0 where id=?  ";
        this.daoUtil.executeBatchUpdate(sql,params);
    }

    @Override
    public void updateFileStatus(String fileId, String status) throws Exception {
        String sql = "  update prc_mbl_file_library  set status =?  where id = ?  ";
        this.daoUtil.executeUpdate(sql,new Object[]{status,fileId});
    }

    @Override
    public void updateFileStatus(String fileId) throws Exception {
        String sql = "  update prc_mbl_file_library  set status =(case when exists (select * from prc_mbl_file_library_send_user b where b.file_id = id and is_valid = 1) then 1 else 0 end )  where id = ?  ";
        this.daoUtil.executeUpdate(sql,new Object[]{fileId});
    }

    /**
     * 根绝页面条件build SQL
     * @param mrType MR Type
     * @param subregion 分区
     * @param city 城市
     * @return build后的SQL
     */
    public String buildSQL(String mrType,String subregion,String city,String isSend,String fileId){
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT ");
        sql.append("    a.rep_id,a.rep_nm ,a.rep_tel ,a.rep_email,a.city_id as cityid ,b.city_std_loc_nm as cityname ,b.grid_cd as gridcd,b.grid_nm as gridnm,b.subrgn_nm as subregionname ,a.co_id as storeid,a.co_nm as storename,a4.id as sendUserId , " );
        sql.append("    ROW_NUMBER() OVER (ORDER BY a.rep_id) as rank  ");
        sql.append(" FROM ");
        sql.append("    external_v_chnl_rep_lst a ");
        sql.append("    LEFT JOIN prc_mbl_file_library_send_user a4 on a4.user_id = a.rep_id AND a4.store_id = a.co_id AND a4.is_valid = 1 AND a4.file_id = "+fileId+ " AND upper(a4.user_type) = 'CHANNEL', ");
        sql.append("    external_prc_v_rgn_subrgn_grid_pref_city b ");
        sql.append(" WHERE ");
        sql.append("    a.rec_sts_id = 1 ");
        //if mrtype = 1 then MR , if mrType = 2 then T5 plus, if mrType = 3 then mr + T5plus
        if(StringUtils.equals("1", mrType)){
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
    public String buildRspSql(String subregion,String cityId,String storeType,String position,String isSend,String fileId) {

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
                .append(" from  external_v_stor_rep_lst a1 LEFT JOIN prc_mbl_file_library_send_user a4 on a4.user_id = a1.rep_id AND a4.store_id = a1.stor_id AND a4.is_valid = 1 AND a4.file_id = "+fileId+ " AND upper(a4.user_type) = 'RSP'  ,external_prc_v_rgn_subrgn_grid_pref_city a2,stdb_stor a3 where  a1.rec_sts_id = 1 ")
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
