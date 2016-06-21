package com.snailgame.cjg.news.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by TAJ_C on 2016/4/14.
 */
@Getter
@Setter
public class NewsIgnoreModel {
    private String cSource;
    private int nTagId;
    private String sTagName;

    private boolean isSelected;

}
