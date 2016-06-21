package com.snailgame.cjg.detail;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.snailgame.cjg.common.model.PageInfo;
import com.snailgame.cjg.detail.model.ForumModel;

/**
 * 解析论坛JSON数据
 * Created by taj on 2014/11/20.
 */
public class ForumJsonUtil {


    public static ForumModel parseJsonToFormModel(String json) {
        JSONObject plainJsonObject = new JSONObject();
        try {
            JSONObject object = JSON.parseObject(json);
            JSONObject val = object.getJSONObject("Variables");
            JSONArray formList = val.getJSONArray("forum_threadlist");
            plainJsonObject.put("list", formList);


            int totalCount = Integer.valueOf(val.getJSONObject("forum").getString("threads"));
            int currPage = Integer.valueOf(val.getString("page"));
            int pageCount = Integer.valueOf(val.getString("tpp"));

            int totalPage = totalCount / pageCount + ((totalCount % pageCount == 0) ? 0 : 1);

            PageInfo pageInfo = new PageInfo();
            pageInfo.setTotalRowCount(totalCount);
            pageInfo.setTotalPageCount(totalPage);
            pageInfo.setRequestPageNum(currPage);

            ForumModel model = JSON.parseObject(plainJsonObject.toString(), ForumModel.class);
            model.setPageInfo(pageInfo);

            return model;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
