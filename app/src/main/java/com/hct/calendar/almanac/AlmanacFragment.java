package com.hct.calendar.almanac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.calendar.R;
import com.apkfuns.logutils.LogUtils;
import com.google.gson.Gson;
import com.hct.calendar.domain.AlmanacBody;
import com.hct.calendar.domain.AlmanacBody.DataBean.JiBean;
import com.hct.calendar.domain.AlmanacBody.DataBean.YiBean;
import com.hct.calendar.ui.FlowLayout;
import com.hct.calendar.ui.TagAdapter;
import com.hct.calendar.ui.TagFlowLayout;
import com.hct.calendar.utils.DensityUtils;

public class AlmanacFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.almanac_details_layout, container,
                false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String json = getArguments().getString("almanacInfo");
        AlmanacBody body = new Gson().fromJson(json, AlmanacBody.class);
        final TextView tvAlmanac = (TextView) view
                .findViewById(R.id.almanac_tv_date);
        tvAlmanac.setText(body.data.nongli);
        final TextView tvTitleDetail = (TextView) view
                .findViewById(R.id.tv_title_detail);
        tvTitleDetail.setText(body.data.nongli + " "
                + body.data.tgdz.replace(",", " "));
        List<YiBean> yi = yiiList(body);
        final TagFlowLayout yiiLayout = (TagFlowLayout) view
                .findViewById(R.id.almanac_yii_layout);
        yiiLayout.setAdapter(new TagAdapter<YiBean>(yi) {
            @Override
            public View getView(FlowLayout parent, int position, YiBean t) {
                TextView tv = itemTextView();
                tv.setText(t.old);
                return tv;
            }
        });
        List<JiBean> jiiList = jiiList(body);
        LogUtils.e(jiiList + " 1024 1024");
        LogUtils.e(body);
        LogUtils.e(jiiList);
        final TagFlowLayout jiiLayout = (TagFlowLayout) view
                .findViewById(R.id.almanac_jii_layout);
        jiiLayout.setAdapter(new TagAdapter<JiBean>(jiiList) {
            @Override
            public View getView(FlowLayout parent, int position, JiBean t) {
                TextView tv = itemTextView();
                tv.setText(t.old);
                return tv;
            }
        });
        String fanwei = body.data.fanwei;
        String[] split = fanwei.split(" ");
        LogUtils.e(split);
        List<String> fangweiList = new ArrayList<>(Arrays.asList(split));
        final TextView tvCaishen = (TextView) view
                .findViewById(R.id.caishen_fangwei);
        tvCaishen.setText(fangweiList.remove(0) + fangweiList.remove(0));
        final TextView tvXishen = (TextView) view
                .findViewById(R.id.xishen_fangwei);
        tvXishen.setText(fangweiList.remove(0) + fangweiList.remove(0));
        final TextView tvFushen = (TextView) view
                .findViewById(R.id.fushen_fangwei);
        tvFushen.setText(fangweiList.remove(0) + fangweiList.remove(0));
        List<String> shichen = body.data.shichen;
        final LinearLayout shichenLayout = (LinearLayout) view
                .findViewById(R.id.almanac_shichen_layout);
        addShenChenData(shichenLayout, shichen);

        final TextView tvWuxingTextView = (TextView) view
                .findViewById(R.id.almanac_wuxing);
        tvWuxingTextView.setText(body.data.wuxing);
        final TextView tvtv_congsha = (TextView) view
                .findViewById(R.id.tv_congsha);
        tvtv_congsha.setText(body.data.cong);
        final TextView tvXingxiu = (TextView) view
                .findViewById(R.id.tv_xingxiu);
        tvXingxiu.setText(body.data.xingxiu);
        final TextView tvTaishen = (TextView) view
                .findViewById(R.id.tv_taishen);
        tvTaishen.setText(body.data.taishen);

        String[] penzuArr = body.data.pengzu.split(" ");
        LogUtils.e("pen zu ------ " + Arrays.toString(penzuArr));
        String penFirst = penzuArr[0];
        String penSecond = penzuArr[1];
        String ss = penFirst.substring(0, 4) + "\n" + penFirst.substring(4);
        String sss = penSecond.substring(0, 4) + "\n" + penSecond.substring(4);
        final TextView tvPenzu1 = (TextView) view
                .findViewById(R.id.tv_pen_zu_1);
        tvPenzu1.setText(ss);
        final TextView tvPenzu2 = (TextView) view
                .findViewById(R.id.tv_pen_zu_2);
        tvPenzu2.setText(sss);
    }

    private void addShenChenData(LinearLayout shichen,
            List<String> shichenStrings) {
        shichen.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = DensityUtils.dp2px(getActivity(), 10.5f);
        params.gravity = Gravity.CENTER_VERTICAL;
        int i = 0;
        for (String sc : shichenStrings) {
            TextView tv = new TextView(getActivity());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            tv.setEms(1);
            tv.setLayoutParams(params);
            tv.setTextColor(Color.parseColor(i == 8 ? "#1157ae" : "#e5000000"));
            tv.setText(sc);
            shichen.addView(tv, params);
            i++;
        }
    }

    private List<YiBean> yiiList(AlmanacBody body) {
        List<YiBean> yi = body.data.yi;
        if (yi.size() > 14) {
            ArrayList<YiBean> temp = new ArrayList<YiBean>();
            for (int i = 0; i < 14; i++) {
                temp.add(yi.get(i));
            }
            yi = temp;
        }
        return yi;
    }

    private List<JiBean> jiiList(AlmanacBody body) {
        List<JiBean> ji = body.data.ji;
        if (ji.size() > 14) {
            ArrayList<JiBean> temp = new ArrayList<JiBean>();
            for (int i = 0; i < 14; i++) {
                temp.add(ji.get(i));
            }
            ji = temp;
        }
        return ji;
    }

    private TextView itemTextView() {
        TextView tView = new TextView(getActivity());
        MarginLayoutParams params = new MarginLayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.width = MarginLayoutParams.WRAP_CONTENT;
        params.height = MarginLayoutParams.WRAP_CONTENT;
        params.leftMargin = DensityUtils.dp2px(getContext(), 11);
        tView.setLayoutParams(params);
        tView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        // #ffXXXXXX
        tView.setTextColor(Color.parseColor("#e5000000"));
        return tView;
    }

}
