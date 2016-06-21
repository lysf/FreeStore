package com.snailgame.cjg.communication.model;

import java.util.ArrayList;

/**
 * Created by lic on 2015/1/22.
 * 通讯页数据格式
 */
public class CommunicationModel {

    private ArrayList<CommunicationChildModel> list;

    public ArrayList<CommunicationChildModel> getList() {
        return list;
    }

    public void setList(ArrayList<CommunicationChildModel> list) {
        this.list = list;
    }
}
