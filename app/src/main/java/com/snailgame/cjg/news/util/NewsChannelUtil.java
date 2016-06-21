package com.snailgame.cjg.news.util;

import android.content.Context;

import com.snailgame.cjg.common.db.dao.NewsChannel;
import com.snailgame.cjg.common.db.daoHelper.NewsChannelDaoHelper;
import com.snailgame.cjg.event.NewsChannelGetEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.news.model.ChannelListModel;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TAJ_C on 2016/4/19.
 */
public class NewsChannelUtil {

    private static final String TAG = NewsChannelUtil.class.getSimpleName();

    private static NewsChannelUtil newsChannelUtil;

    public static NewsChannelUtil getInstance() {
        if (newsChannelUtil == null) {
            newsChannelUtil = new NewsChannelUtil();

        }
        return newsChannelUtil;
    }


    public void getNewsChannelData(final Context context) {
        final List<NewsChannel> dbnewsChannelList = NewsChannelDaoHelper.getInstance(context).queryAllChannel();

        final boolean isInit = (ListUtils.isEmpty(dbnewsChannelList));

        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_NEWS_CHANNEL_LIST, TAG,
                ChannelListModel.class, new IFDResponse<ChannelListModel>() {
                    @Override
                    public void onSuccess(ChannelListModel channelListModel) {
                        if (channelListModel != null && channelListModel.getItem() != null) {


                            //频道解析
                            List<NewsChannel> netChannelList = new ArrayList<NewsChannel>();
                            for (int i = 0; i < channelListModel.getItem().size(); i++) {
                                ChannelListModel.ModelItem item = channelListModel.getItem().get(i);

                                NewsChannel channel = new NewsChannel();
                                channel.setChannelId(item.getChannelId());
                                channel.setChannelName(item.getSChannelName());
                                if (isInit && i < 6) {
                                    channel.setShow(true);
                                } else {
                                    channel.setShow(false);
                                }
                                netChannelList.add(channel);
                            }


                            if (!isInit) { //如果本地保存有频道数据 则处理相关逻辑
                                //输出outList
                                final List<NewsChannel> outList = new ArrayList<NewsChannel>();
                                //拷贝net channel
                                final List<NewsChannel> tempList = new ArrayList<>();
                                tempList.addAll(netChannelList);


                                for (NewsChannel dbChannel : dbnewsChannelList) {

                                    for (NewsChannel netChannel : netChannelList) {

                                        if (dbChannel.getChannelId() == netChannel.getChannelId()) {
                                            outList.add(dbChannel);
                                            tempList.remove(netChannel);
                                        }
                                    }
                                }
                                outList.addAll(tempList);
                                NewsChannelDaoHelper.getInstance(context).saveChannelToDb(outList);
                                MainThreadBus.getInstance().post(new NewsChannelGetEvent(outList));
                            } else {
                                NewsChannelDaoHelper.getInstance(context).saveChannelToDb(netChannelList);
                                MainThreadBus.getInstance().post(new NewsChannelGetEvent(netChannelList));
                            }


                        }
                    }

                    @Override
                    public void onNetWorkError() {
                        MainThreadBus.getInstance().post(new NewsChannelGetEvent(null));
                    }

                    @Override
                    public void onServerError() {
                        MainThreadBus.getInstance().post(new NewsChannelGetEvent(null));
                    }


                }, false);
    }

    public void onDestroy() {
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
        newsChannelUtil = null;
    }

}
