package com.intel.assist.web.dao;

import org.codehaus.jettison.json.JSONObject;

import java.util.List;

/**
 * Created by kunpeng on 2015/6/24.
 */
public interface FileLibraryDao extends BaseDao{

    public List<JSONObject> listFilesByPagination(int pageIndex,int pageSize,String wwid) throws Exception;
    public int getFilesTotalSize(String wwid) throws Exception;
    public JSONObject getExtendFileInfo(String fileId) throws Exception;
    public void addFile(String s3ReturnUrl,String file_name,String file_cat,String file_desc,String file_size,String bucket_name,String object_key,String wwid) throws Exception;
    public List<JSONObject> listFileCate() throws Exception;
    public List<JSONObject> listAll(String rep_id,String stor_id,String file_cate) throws Exception;
    public List<JSONObject> getSignedUrl(String file_id) throws Exception;
    public void recordDownload(String send_id,String status) throws Exception;
    public JSONObject getLibraryInfoById(String fileId) throws Exception;
    public void saveFile(String fileId,String fileName,String fileCate,String fileDesc) throws Exception;
    public void deleteById(int id) throws Exception;
    public void deleteItems(String ids) throws Exception;
    public int getSendRSPUserTotal(String fileId,String isSend,String position,String subregions,String citys,String storeType)throws Exception;
    public int getSendChannelUserTotal(String mrType,String subregion,String city,String isSend,String fileId)throws Exception;
    public List<JSONObject> listSendRSPUser(String fileId,String isSend,String position,String subregions,String citys,String storeType,int page,int size)throws Exception;
    public List<JSONObject> listSendChannelUser(String fileId,String isSend,String mrType,String subregions,String citys,int page,int size)throws Exception;
    public void sendAllRSPUser(String fileId,String isSend,String position,String subregions,String citys,String storeType)throws Exception;
    public void sendAllChannelUser(String fileId,String isSend,String mrType,String subregions,String citys)throws Exception;
    public void unsendAllRSPUser(String fileId,String isSend,String position,String subregions,String citys,String storeType)throws Exception;
    public void unsendAllChannelUser(String fileId,String isSend,String mrType,String subregions,String citys)throws Exception;
    public void sendSelectUser(List<Object[]> params) throws Exception;
    public void unsendSelectUser(List<Object[]> params) throws Exception;
    public void updateFileStatus(String fileId,String status) throws Exception;
    public void updateFileStatus(String fileId) throws Exception;

}
