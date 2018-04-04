package com.hct.calendar.domain;

import java.util.List;

public class AlmanacBody {

    public DataBean data;

    public int status;
    public String desc;

    public static class DataBean {
        public String date;
        public String xingqi;
        public String pengzu;
        public int jujin;
        public String cong;
        public String taishen;
        public String xingxiu;
        public String wuxing;
        public String fanwei;
        public String nongli;
        public String jieqi;
        public String jieri;
        public String tgdz;

        public List<YiBean> yi;
        public List<String> shichen;
        public List<JiBean> ji;

        public static class YiBean {
            public String newX;
            public String old;
        }

        public static class JiBean {
            public String newX;
            public String old;

        }
    }
}
