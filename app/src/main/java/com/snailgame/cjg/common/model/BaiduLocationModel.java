package com.snailgame.cjg.common.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lic on 2016/3/16.
 * 百度ip定位返回的json对象
 */
public class BaiduLocationModel {
    private String address;
    private int status;
    private Content content;

    @JSONField(name = "address")
    public String getAddress() {
        return address;
    }

    @JSONField(name = "content")
    public Content getContent() {
        return content;
    }

    @JSONField(name = "status")
    public int getStatus() {
        return status;
    }

    @JSONField(name = "address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JSONField(name = "status")
    public void setStatus(int status) {
        this.status = status;
    }

    @JSONField(name = "content")
    public void setContent(Content content) {
        this.content = content;
    }


    public static class Content {
        private String address;
        private AddressDetail addressDetail;
        private Point point;

        @JSONField(name = "address")
        public String getAddress() {
            return address;
        }

        @JSONField(name = "address_detail")
        public AddressDetail getAddressDetail() {
            return addressDetail;
        }

        @JSONField(name = "point")
        public Point getPoint() {
            return point;
        }

        @JSONField(name = "address")
        public void setAddress(String address) {
            this.address = address;
        }

        @JSONField(name = "address_detail")
        public void setAddressDetail(AddressDetail addressDetail) {
            this.addressDetail = addressDetail;
        }

        @JSONField(name = "point")
        public void setPoint(Point point) {
            this.point = point;
        }
    }


    public static class AddressDetail {
        private String city;
        private String cityCode;
        private String district;
        private String province;
        private String street;
        private String streetNumber;

        @JSONField(name = "city")
        public String getCity() {
            return city;
        }

        @JSONField(name = "city_code")
        public String getCityCode() {
            return cityCode;
        }

        @JSONField(name = "district")
        public String getDistrict() {
            return district;
        }

        @JSONField(name = "province")
        public String getProvince() {
            return province;
        }

        @JSONField(name = "street")
        public String getStreet() {
            return street;
        }

        @JSONField(name = "street_number")
        public String getStreetNumber() {
            return streetNumber;
        }

        @JSONField(name = "city")
        public void setCity(String city) {
            this.city = city;
        }

        @JSONField(name = "city_code")
        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        @JSONField(name = "district")
        public void setDistrict(String district) {
            this.district = district;
        }

        @JSONField(name = "province")
        public void setProvince(String province) {
            this.province = province;
        }

        @JSONField(name = "street")
        public void setStreet(String street) {
            this.street = street;
        }

        @JSONField(name = "street_number")
        public void setStreetNumber(String streetNumber) {
            this.streetNumber = streetNumber;
        }
    }

    public static class Point {
        private String x;
        private String y;

        @JSONField(name = "x")
        public String getX() {
            return x;
        }

        @JSONField(name = "y")
        public String getY() {
            return y;
        }

        @JSONField(name = "x")
        public void setX(String x) {
            this.x = x;
        }

        @JSONField(name = "y")
        public void setY(String y) {
            this.y = y;
        }
    }
}
