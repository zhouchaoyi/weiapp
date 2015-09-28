package com.intel.assist.web.services;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.intel.assist.utils.Consts;
import com.intel.assist.utils.DateUtil;
import com.intel.assist.utils.PropertiesUtil;
import com.intel.assist.web.dao.BaseDao;
import com.intel.assist.web.dao.FileLibraryDao;
import com.intel.assist.web.dao.impl.BaseDaoUtil;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by kunpeng on 2015/6/24.
 */
@Service
public class FileLibraryService extends BaseServices{
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileLibraryDao fileDao;

    public BaseDao getDao(){
        return fileDao;
    }

    public List<JSONObject> listFilesByPagination(int pageIndex, int pageSize,String wwid) {
        try {

            List<JSONObject> fileList =fileDao.listFilesByPagination(pageIndex, pageSize, wwid);
            if(null == fileList || fileList.size() <= 0){
                return new ArrayList<JSONObject>();
            }

            addExtendFileInfo(fileList);

            return fileList;
        } catch (Exception e) {
            logger.error("listFilesByPagination error-> " + e.getMessage());
        }
        return null;
    }

    private void addExtendFileInfo(List<JSONObject> fileJsonList) throws Exception{
        if(null == fileJsonList || fileJsonList.size() <= 0){
            return ;
        }
        for (JSONObject fileInfo : fileJsonList) {
            String fileName = fileInfo.getString("file_name");
            fileInfo.put("file_name", fileName.length() > 20 ? fileName.substring(0, 17) + "..." : fileName);


            Date createDate = DateUtil.string2Date(fileInfo.getString("create_date"));
            fileInfo.put("create_date",DateUtil.formatDateString(createDate));


            JSONObject extendFileInfo = fileDao.getExtendFileInfo(fileInfo.getString("id"));
            if (null != extendFileInfo) {
                fileInfo.put("rspCount", StringUtils.isBlank(extendFileInfo.getString("rep_num")) ? 0 : extendFileInfo.getString("rep_num"));
                fileInfo.put("rspJoin", StringUtils.isBlank(extendFileInfo.getString("rep_join")) ? 0 : extendFileInfo.getString("rep_join"));
                fileInfo.put("channelCount", StringUtils.isBlank(extendFileInfo.getString("channel_num")) ? 0 : extendFileInfo.getString("channel_num"));
                fileInfo.put("channelJoin", StringUtils.isBlank(extendFileInfo.getString("channel_join")) ? 0 : extendFileInfo.getString("channel_join"));
            } else {
                fileInfo.put("rspCount", 0);
                fileInfo.put("rspJoin", 0);
                fileInfo.put("channelCount", 0);
                fileInfo.put("channelJoin", 0);
            }
        }
    }

    public int getFilesTotalSize(String wwid) {
        try {
            return fileDao.getFilesTotalSize(wwid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String[] uploadToS3(File tempFile, String remoteFileName) throws IOException {
        //System.out.println("remoteFileName="+remoteFileName);
        String[] aStr=new String[3];
        PropertiesUtil propertiesUtil = new PropertiesUtil("s3.properties");
        AmazonS3 s3 = new AmazonS3Client(
                new BasicAWSCredentials(propertiesUtil.getKeyValue(Consts.S3_ACCESS_KEY),
                        propertiesUtil.getKeyValue(Consts.S3_SCERET_KEY)));
        Region chinaRegion = Region.getRegion(Regions.CN_NORTH_1);
        s3.setRegion(chinaRegion);
        //设置bucket,key
        String bucketName = Consts.S3_BUCKET_NAME;
        String key = Consts.S3_BUCKTE_FILENAME + "/" + UUID.randomUUID() + "."+remoteFileName.split("\\.")[remoteFileName.split("\\.").length-1];
        //System.out.println("key="+key);
        try {
//            if (!checkBucketExists(s3, bucketName)) {
//                s3.createBucket(bucketName);
//            }

            s3.putObject(new PutObjectRequest(bucketName, key, tempFile));
            s3.setObjectAcl(Consts.S3_BUCKET_NAME,
                    key, CannedAccessControlList.PublicRead);

            String sRetUrl=Consts.S3_BASEPATH_CHINA + Consts.S3_BUCKET_NAME + "/" + key;
            aStr[0]=sRetUrl;
            aStr[1]=bucketName;
            aStr[2]=key;
            return aStr;
        } catch (AmazonServiceException ase) {
            ase.printStackTrace();
            logger.info("====================================AWS S3 UPLOAD ERROR START======================================");
            logger.info("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            logger.info("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            logger.info("Error Message:    " + ase.getMessage());
            logger.info("HTTP Status Code: " + ase.getStatusCode());
            logger.info("AWS Error Code:   " + ase.getErrorCode());
            logger.info("Error Type:       " + ase.getErrorType());
            logger.info("Request ID:       " + ase.getRequestId());
            logger.info(ase.getMessage(), ase);
            logger.info("====================================AWS S3 UPLOAD ERROR END======================================");
            //throw new OperateFailureException("error occurs during upload to s3!");
        } catch (AmazonClientException ace) {
            logger.info("====================================AWS S3 UPLOAD ERROR START======================================");
            logger.info("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            logger.info("Error Message: " + ace.getMessage());
            logger.info("====================================AWS S3 UPLOAD ERROR END======================================");
            //throw new OperateFailureException("error occurs during upload to s3!");
        }
        return aStr;
    }

    public void addFile(String s3ReturnUrl,String file_name,String file_cat,String file_desc,String file_size,String bucket_name,String object_key,String wwid) throws Exception {
        fileDao.addFile(s3ReturnUrl,file_name,file_cat,file_desc,file_size,bucket_name,object_key,wwid);
    }

    /**
     * 获取文件类别
     * @return
     * @throws Exception
     */
    public List<JSONObject> listFileCate() throws Exception {
        try {
            return this.fileDao.listFileCate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<JSONObject>();
    }

    /**
     * 根据参数返回所有的文件数据
     * @return
     * @throws Exception
     */
    public List<JSONObject> listAll(String rep_id,String stor_id,String file_cate) throws Exception {
        try {
           return this.fileDao.listAll(rep_id,stor_id,file_cate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<JSONObject>();
    }

    /**
     * 获取文件的预签名URL(URL有失效时间)
     * @return
     * @throws Exception
     */
    public JSONObject getSignedUrl(String file_id) throws Exception {
        JSONObject json=new JSONObject();
        List<JSONObject> list= this.fileDao.getSignedUrl(file_id);
        if(null!=list&&list.size()>0) {
            String bucket_name=list.get(0).getString("bucket_name");
            String object_key=list.get(0).getString("object_key");
            PropertiesUtil propertiesUtil = new PropertiesUtil("s3.properties");
            AmazonS3 s3 = new AmazonS3Client(
                    new BasicAWSCredentials(propertiesUtil.getKeyValue(Consts.S3_ACCESS_KEY),
                            propertiesUtil.getKeyValue(Consts.S3_SCERET_KEY)));
            Region chinaRegion = Region.getRegion(Regions.CN_NORTH_1);
            s3.setRegion(chinaRegion);
            java.util.Date expiration = new java.util.Date();
            long milliSeconds = expiration.getTime();
            milliSeconds += 1000 * 60 *60* 5; //过期时间为5小时
            expiration.setTime(milliSeconds);
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucket_name, object_key);
            generatePresignedUrlRequest.setMethod(HttpMethod.GET);
            generatePresignedUrlRequest.setExpiration(expiration);
            URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);
            json.put("url",url);
        }
        return json;
    }


    /**
     * 记录用户下载文件的时间
     * @return
     * @throws Exception
     */
    public void recordDownload(String send_id,String status) throws Exception {
        try {
            this.fileDao.recordDownload(send_id,status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看资料的信息
     * @return
     * @throws Exception
     */
    public JSONObject getLibraryInfoById(String fileId) throws Exception {

        JSONObject fileInfo = new JSONObject();

        if(StringUtils.isBlank(fileId)){
            return fileInfo;
        }

        try {
            fileInfo =this.fileDao.getLibraryInfoById(fileId);
            cureFile(fileInfo);
            return  fileInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileInfo;
    }

    private void cureFile(JSONObject fileInfo){
        if(null == fileInfo ){
            return;
        }

        try {

            Date createDate = DateUtil.string2Date(fileInfo.getString("create_date"));
            fileInfo.put("create_date",DateUtil.formatDateString(createDate));

            String status = fileInfo.getString("status");
            fileInfo.put("status",statusMapping(status));

            fileInfo.put("rep_num", StringUtils.equals(fileInfo.getString("rep_num"),"-1") ?0:fileInfo.getString("rep_num"));
            fileInfo.put("rep_join", StringUtils.equals(fileInfo.getString("rep_join"),"-1")?0:fileInfo.getString("rep_join"));
            fileInfo.put("channel_num", StringUtils.equals(fileInfo.getString("channel_num"),"-1")?0:fileInfo.getString("channel_num"));
            fileInfo.put("channel_join", StringUtils.equals(fileInfo.getString("channel_join"),"-1")?0:fileInfo.getString("channel_join"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存资料的信息
     * @return
     * @throws Exception
     */
    public  void saveFile(String fileId,String fileName,String fileCate,String fileDesc) throws Exception {
        this.fileDao.saveFile(fileId,fileName,fileCate,fileDesc);
    }

    /**
     * 根据Id删除资料
     * @return
     * @throws Exception
     */
    public void deleteById(int id) throws Exception {
        this.fileDao.deleteById(id);
    }

    /**
     * 根据Id批量删除资料
     * @return
     * @throws Exception
     */
    public void deleteItems(String ids) throws Exception {
        this.fileDao.deleteItems(ids);
    }


    public int getSendUserTotal(JSONObject paramsJson){
        int size = 0;

        try {
            String userType =StringUtils.upperCase(paramsJson.getString("userType"));
            String fileId = paramsJson.getString("fileId");
            JSONArray subregions =   paramsJson.getJSONArray("subregions");
            JSONArray citys =   paramsJson.getJSONArray("citys");
            String isSend =  paramsJson.getString("isSend");

            if (StringUtils.equalsIgnoreCase("RSP", userType)){
                String position =   paramsJson.getString("position");
                JSONArray storeType =   paramsJson.getJSONArray("storeType");
                return this.fileDao.getSendRSPUserTotal(fileId,isSend,position,subregions.join(",").replace("\"","\'"),citys.join(",").replace("\"", "\'"),storeType.join(",").replace("\"","\'"));
            }else if(StringUtils.equalsIgnoreCase("CHANNEL",userType)){
                String mrType =  paramsJson.getString("mrType");
                return this.fileDao.getSendChannelUserTotal(mrType,subregions.join(",").replace("\"","\'"),citys.join(",").replace("\"", "\'"),isSend,fileId);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public List<JSONObject> getsendUserList(JSONObject paramsJson){

        List<JSONObject> result = new ArrayList<JSONObject>();

        try {
            String userType = StringUtils.upperCase(paramsJson.getString("userType"));
            String fileId = paramsJson.getString("fileId");
            int page = paramsJson.getInt("page");
            int size = paramsJson.getInt("size");
            String isSend =  paramsJson.getString("isSend");
            JSONArray subregions =   paramsJson.getJSONArray("subregions");
            JSONArray citys =   paramsJson.getJSONArray("citys");

            if (StringUtils.equalsIgnoreCase("RSP",userType)){
                String position =   paramsJson.getString("position");
                JSONArray storeType =   paramsJson.getJSONArray("storeType");
                return this.fileDao.listSendRSPUser(fileId, isSend, position, subregions.join(",").replace("\"", "\'"),
                        citys.join(",").replace("\"", "\'"), storeType.join(",").replace("\"", "\'"), page, size);
            }else if(StringUtils.equalsIgnoreCase("CHANNEL",userType)){
                String mrType =  paramsJson.getString("mrType");
                return this.fileDao.listSendChannelUser(fileId, isSend, mrType, subregions.join(",").replace("\"", "\'"),
                        citys.join(",").replace("\"", "\'"), page, size);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void sendSelectUser(JSONArray jsonArray,String fileId,String userType){
        if(jsonArray != null && jsonArray.length() > 0){
            try {
                List<Object[]> paramsList = new ArrayList<Object[]>();
                for(int i=0;i<jsonArray.length();i++){
                    Object[] params = new Object[11];
                    params[0] =jsonArray.getJSONObject(i).get("rep_id");
                    params[1] =fileId;
                    params[2] =0;
                    params[3] =userType;
                    params[4] =jsonArray.getJSONObject(i).get("subregionname");
                    params[5] =jsonArray.getJSONObject(i).get("cityname");
                    params[6] =jsonArray.getJSONObject(i).get("rep_nm");
                    params[7] =jsonArray.getJSONObject(i).get("storeid");
                    params[8] =jsonArray.getJSONObject(i).get("storename");
                    params[9] =jsonArray.getJSONObject(i).get("rep_tel");
                    params[10] =1;
                    paramsList.add(params);
                }
                this.fileDao.sendSelectUser(paramsList);
                this.fileDao.updateFileStatus(fileId);

            } catch (Exception e) {
                logger.error("sendSelectUser error-> " + e.getMessage());
            }
        }
    }

    public void unsendSelectUser(JSONArray ids,String fileId){

        if(null == ids || ids.length() < 1){
            return;
        }

        List<Object[]> params = new ArrayList<Object[]>();
        try {
            for (int i = 0; i <ids.length() ; i++) {
                params.add(new Object[]{ids.getString(i)});
            }
            this.fileDao.unsendSelectUser(params);
            this.fileDao.updateFileStatus(fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUserAll(JSONObject paramsJson){

        try {
            String userType =StringUtils.upperCase(paramsJson.getString("userType"));
            String fileId = paramsJson.getString("fileId");
            JSONArray subregions =   paramsJson.getJSONArray("subregions");
            JSONArray citys =   paramsJson.getJSONArray("citys");
            String isSend =  paramsJson.getString("isSend");

            if (StringUtils.equalsIgnoreCase("RSP",userType)){
                String position =   paramsJson.getString("position");
                JSONArray storeType =   paramsJson.getJSONArray("storeType");
                this.fileDao.sendAllRSPUser(fileId, isSend, position, subregions.join(",").replace("\"", "\'"),
                        citys.join(",").replace("\"", "\'"), storeType.join(",").replace("\"", "\'"));

            } else if (StringUtils.equalsIgnoreCase("CHANNEL", userType)) {
                String mrType =  paramsJson.getString("mrType");
                this.fileDao.sendAllChannelUser(fileId, isSend, mrType, subregions.join(",").replace("\"", "\'"),
                        citys.join(",").replace("\"", "\'"));
            }
            this.fileDao.updateFileStatus(fileId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unsendUserAll(JSONObject paramsJson){

        try {
            String userType =StringUtils.upperCase(paramsJson.getString("userType"));
            String fileId = paramsJson.getString("fileId");
            String isSend =  paramsJson.getString("isSend");
            JSONArray subregions =   paramsJson.getJSONArray("subregions");
            JSONArray citys =   paramsJson.getJSONArray("citys");
            if (StringUtils.equalsIgnoreCase("RSP",userType)){
                String position =   paramsJson.getString("position");
                JSONArray storeType =   paramsJson.getJSONArray("storeType");
                this.fileDao.unsendAllRSPUser(fileId, isSend, position, subregions.join(",").replace("\"", "\'"),
                        citys.join(",").replace("\"", "\'"), storeType.join(",").replace("\"", "\'"));

            }else if(StringUtils.equalsIgnoreCase("CHANNEL",userType)){
                String mrType =  paramsJson.getString("mrType");
                this.fileDao.unsendAllChannelUser(fileId, isSend, mrType, subregions.join(",").replace("\"", "\'"),
                        citys.join(",").replace("\"", "\'"));
            }
            this.fileDao.updateFileStatus(fileId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
