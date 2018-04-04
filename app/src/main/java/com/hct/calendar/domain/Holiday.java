package com.hct.calendar.domain;

import java.util.List;

/**
 * Created by cat on 2017/6/9.
 */

public class Holiday {

    public DataBean data;

    public DataBean getData() {
        return data;
    }

    /**
     * data : {"hk":[{"date":20161231,"status":0},{"date":20170101,"status":0},{
     * "date"
     * :20170102,"status":0},{"date":20170128,"status":0},{"date":20170129,
     * "status"
     * :0},{"date":20170130,"status":0},{"date":20170131,"status":0},{"date"
     * :20170404
     * ,"status":0},{"date":20170414,"status":0},{"date":20170415,"status"
     * :0},{"date"
     * :20170417,"status":0},{"date":20170501,"status":0},{"date":20170503
     * ,"status"
     * :0},{"date":20170530,"status":0},{"date":20170701,"status":0},{"date"
     * :20171001
     * ,"status":0},{"date":20171002,"status":0},{"date":20171005,"status"
     * :0},{"date"
     * :20171028,"status":0},{"date":20171225,"status":0},{"date":20171226
     * ,"status"
     * :0}],"tw":[{"date":20170101,"status":0},{"date":20170102,"status"
     * :0},{"date"
     * :20170127,"status":0},{"date":20170128,"status":0},{"date":20170129
     * ,"status"
     * :0},{"date":20170130,"status":0},{"date":20170131,"status":0},{"date"
     * :20170201
     * ,"status":0},{"date":20170227,"status":0},{"date":20170228,"status"
     * :0},{"date"
     * :20170403,"status":0},{"date":20170404,"status":0},{"date":20170529
     * ,"status"
     * :0},{"date":20170530,"status":0},{"date":20171004,"status":0},{"date"
     * :20171009
     * ,"status":0},{"date":20171010,"status":0}],"ma":[{"date":20160101
     * ,"status"
     * :0},{"date":20160208,"status":0},{"date":20160209,"status":0},{"date"
     * :20160210
     * ,"status":0},{"date":20160325,"status":0},{"date":20160326,"status"
     * :0},{"date"
     * :20160404,"status":0},{"date":20160501,"status":0},{"date":20160514
     * ,"status"
     * :0},{"date":20160609,"status":0},{"date":20160916,"status":0},{"date"
     * :20161001
     * ,"status":0},{"date":20161002,"status":0},{"date":20161009,"status"
     * :0},{"date"
     * :20161102,"status":0},{"date":20161208,"status":0},{"date":20161220
     * ,"status"
     * :0},{"date":20161221,"status":0},{"date":20161224,"status":0},{"date"
     * :20161225
     * ,"status":0}],"cn":[{"date":20161231,"status":0},{"date":20170101
     * ,"status"
     * :0},{"date":20170102,"status":0},{"date":20170122,"status":1},{"date"
     * :20170127
     * ,"status":0},{"date":20170128,"status":0},{"date":20170129,"status"
     * :0},{"date"
     * :20170130,"status":0},{"date":20170131,"status":0},{"date":20170201
     * ,"status"
     * :0},{"date":20170202,"status":0},{"date":20170204,"status":1},{"date"
     * :20170401
     * ,"status":1},{"date":20170402,"status":0},{"date":20170403,"status"
     * :0},{"date"
     * :20170404,"status":0},{"date":20170429,"status":0},{"date":20170430
     * ,"status"
     * :0},{"date":20170501,"status":0},{"date":20170527,"status":1},{"date"
     * :20170528
     * ,"status":0},{"date":20170529,"status":0},{"date":20170530,"status"
     * :0},{"date"
     * :20170930,"status":1},{"date":20171001,"status":0},{"date":20171002
     * ,"status"
     * :0},{"date":20171003,"status":0},{"date":20171004,"status":0},{"date"
     * :20171005
     * ,"status":0},{"date":20171006,"status":0},{"date":20171007,"status"
     * :0},{"date":20171008,"status":0}]} status : 1000 desc : OK
     */

    public int status;
    public String desc;

    public static class DataBean {
        /**
         * date : 20161231 status : 0
         */

        public List<InnerBean> hk;
        /**
         * date : 20170101 status : 0
         */

        public List<InnerBean> tw;
        /**
         * date : 20160101 status : 0
         */

        public List<InnerBean> ma;
        /**
         * date : 20161231 status : 0
         */

        public List<InnerBean> cn;

        public List<InnerBean> getCn() {
            return cn;
        }

        public static class InnerBean {
            public int date;
            public int status;

            public int getDate() {
                return date;
            }

            public void setDate(int date) {
                this.date = date;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }
        }

    }

}
