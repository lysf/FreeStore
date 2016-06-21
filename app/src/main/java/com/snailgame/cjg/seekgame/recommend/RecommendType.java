package com.snailgame.cjg.seekgame.recommend;

public enum RecommendType {
    RECOMMEND_NONE(0),
    RECOMMEND_DETAIL(1),
    RECOMMEND_COLLECTION(2),
    RECOMMEND_HTML(3);

    private int typeValue;

    RecommendType(int type) {
        this.typeValue = type;
    }

    public int value(RecommendType type) {

        return type.typeValue;
    }

    public static RecommendType valueOf(int type) {

        switch (type) {
            case 1:
                return RECOMMEND_DETAIL;
            case 2:
                return RECOMMEND_COLLECTION;
            case 3:
                return RECOMMEND_HTML;
            default:
                return RECOMMEND_NONE;
        }
    }

}
