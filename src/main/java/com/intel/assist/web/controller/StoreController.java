package com.intel.assist.web.controller;

import com.intel.assist.utils.CommonUtils;
import com.intel.assist.utils.WebUtils;
import com.intel.assist.web.services.StoreService;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by zhouchaoyi on 2015/8/31.
 */

@Controller
@RequestMapping("/storeMgmt")
public class StoreController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StoreService storeService;


    /**
     * 获取物品类别
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/listGoodsType", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String listGoodsType(HttpServletRequest request, HttpServletResponse response)throws Exception{
        JSONObject json=new JSONObject();
        List<JSONObject> list=storeService.listGoodsType();
        //List<JSONObject> list=new ArrayList<JSONObject>();
        json.put("data",list);
        json.put("status", CommonUtils.getSubStatus("获取数据成功！"));
        return json.toString();
    }

    /**
     * 添加物品
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/addGoods", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addGoods(HttpServletRequest request, HttpServletResponse response)throws Exception{
        JSONObject json=new JSONObject();
        String goodsNm = WebUtils.getStringValue(request, "goodsNm", true);
        goodsNm= URLDecoder.decode(goodsNm,"UTF-8");
        String goodsType = WebUtils.getStringValue(request, "goodsType", true);
        String goodsPrice = WebUtils.getStringValue(request, "goodsPrice", true);
        String lowPrice = WebUtils.getStringValue(request, "lowPrice",false);
        if(null==lowPrice||lowPrice.length()==0) {
            lowPrice=null;
        }
        String remark = WebUtils.getStringValue(request, "remark",false);
        if(StringUtils.isNotEmpty(remark)) {
            remark= URLDecoder.decode(remark,"UTF-8");
        }

        /*System.out.println("goodsNm="+goodsNm);
        System.out.println("goodsType="+goodsType);
        System.out.println("goodsPrice="+goodsPrice);
        System.out.println("lowPrice="+lowPrice);*/

        storeService.addGoods(goodsNm,goodsType,goodsPrice,lowPrice,remark);
        json.put("data","");
        json.put("status", CommonUtils.getSubStatus("获取数据成功！"));
        return json.toString();
    }

    /**
     * 修改物品
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/modifyGoods", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String modifyGoods(HttpServletRequest request, HttpServletResponse response)throws Exception{
        JSONObject json=new JSONObject();
        String goodsNm = WebUtils.getStringValue(request, "goodsNm", true);
        goodsNm= URLDecoder.decode(goodsNm,"UTF-8");
        String goodsType = WebUtils.getStringValue(request, "goodsType", true);
        String goodsPrice = WebUtils.getStringValue(request, "goodsPrice", true);
        String lowPrice = WebUtils.getStringValue(request, "lowPrice",false);
        if(null==lowPrice||lowPrice.length()==0) {
            lowPrice=null;
        }
        String remark = WebUtils.getStringValue(request, "remark",false);
        if(StringUtils.isNotEmpty(remark)) {
            remark= URLDecoder.decode(remark,"UTF-8");
        }
        String goodsId = WebUtils.getStringValue(request, "goodsId",true);

        storeService.modifyGoods(goodsNm, goodsType, goodsPrice, lowPrice, remark, goodsId);
        json.put("data","");
        json.put("status", CommonUtils.getSubStatus("获取数据成功！"));
        return json.toString();
    }

    /**
     * 删除物品
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/deleteGoods", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteGoods(HttpServletRequest request, HttpServletResponse response)throws Exception{
        JSONObject json=new JSONObject();
        String goodsId = WebUtils.getStringValue(request, "goodsId",true);
        storeService.deleteGoods(goodsId);
        json.put("data","");
        json.put("status", CommonUtils.getSubStatus("获取数据成功！"));
        return json.toString();
    }

    /**
     * 获取物品列表信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/listGoods", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String listGoods(HttpServletRequest request, HttpServletResponse response)throws Exception{
        JSONObject json=new JSONObject();
        int pageSize = WebUtils.getIntValue(request, "pageSize",true);
        int currentPage = WebUtils.getIntValue(request, "currentPage",true);
        String type_id = WebUtils.getStringValue(request, "type_id", false);
        String keyword = WebUtils.getStringValue(request, "keyword", false);
        keyword=URLDecoder.decode(keyword,"UTF-8");
        //System.out.println("keyword="+keyword);
        JSONObject result=storeService.listGoods(pageSize,currentPage,type_id,keyword);
        json.put("data",result);
        json.put("status", CommonUtils.getSubStatus("获取数据成功！"));
        return json.toString();
    }

}
