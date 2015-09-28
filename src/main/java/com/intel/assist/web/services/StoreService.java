package com.intel.assist.web.services;

import com.intel.assist.web.dao.impl.BaseDaoUtil;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kunpeng on 2015/6/24.
 */
@Service
public class StoreService extends BaseServices{
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaseDaoUtil baseDaoUtil;

    /**
     * 获取物品类别
     *
     * @return
     */
    public List<JSONObject> listGoodsType() {
        String sql = new StringBuilder("select type_id,type_nm from goods_type ")
                .toString();
        return this.baseDaoUtil.executeQueryList(sql,new Object[]{});
    }

    /**
     * 添加物品
     *
     * @return
     */
    public void addGoods(String goodsNm,String goodsType,String goodsPrice,String lowPrice,String remark) {
        String sql=new StringBuilder("insert into goods_info (goods_name,type_id,goods_price,low_price,remark) values (?,?,?,?,?)")
                .toString();
        baseDaoUtil.executeUpdate(sql,new Object[]{goodsNm,goodsType,goodsPrice,lowPrice,remark});
    }

    /**
     * 修改物品
     *
     * @return
     */
    public void modifyGoods(String goodsNm,String goodsType,String goodsPrice,String lowPrice,String remark,String goodsId) {
        String sql=new StringBuilder("update goods_info set goods_name=?,type_id=?,goods_price=?,low_price=?,remark=? where goods_id=?")
                .toString();
        baseDaoUtil.executeUpdate(sql,new Object[]{goodsNm,goodsType,goodsPrice,lowPrice,remark,goodsId});
    }

    /**
     * 删除物品
     *
     * @return
     */
    public void deleteGoods(String goodsId) {
        String sql=new StringBuilder("delete from goods_info where goods_id=?")
                .toString();
        baseDaoUtil.executeUpdate(sql,new Object[]{goodsId});
    }

    /**
     * 获取物品列表信息
     *
     * @return
     */
    public JSONObject listGoods(int pageSize,int currentPage,String type_id,String keyword) throws Exception {
        JSONObject result=new JSONObject();
        int index=(currentPage -1 ) * pageSize;
        List<Object> objectList = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder("select a.goods_id,a.type_id,b.type_nm,a.goods_name,a.goods_price,b.type_nm,a.low_price,a.remark from goods_info a ")
                .append(" left join goods_type b on a.type_id=b.type_id where 1=1 ");
        if(StringUtils.length(type_id)>0) {
            sql.append(" and a.type_id=? ");
            objectList.add(type_id);
        }
        if(StringUtils.length(keyword)>0) {
            sql.append(" and a.goods_name like ? ");
            objectList.add("%"+keyword+"%");
        }
        sql.append(" order by a.type_id,a.goods_id desc")
                .append(" limit ?,?");
        objectList.add(index);
        objectList.add(pageSize);
        List<JSONObject> list=this.baseDaoUtil.executeQueryList(sql.toString(),objectList.toArray());

        objectList.clear();
        sql = new StringBuilder("select count(1) as allNum from goods_info a ")
                .append(" left join goods_type b on a.type_id=b.type_id where 1=1 ");
        if(StringUtils.length(type_id)>0) {
            sql.append(" and a.type_id=? ");
            objectList.add(type_id);
        }
        if(StringUtils.length(keyword)>0) {
            sql.append(" and a.goods_name like ? ");
            objectList.add("%"+keyword+"%");
        }
        sql.append(" order by a.type_id,a.goods_id desc");
        String allNum=this.baseDaoUtil.sqlQueryList(sql.toString(),objectList.toArray(),"allNum").get(0);

        int totalPageNum = (int) Math.ceil(Double.valueOf(allNum) / pageSize);
        result.put("allNum",allNum);
        result.put("totalPageNum",totalPageNum);
        result.put("list",list);
        return result;
    }

}
