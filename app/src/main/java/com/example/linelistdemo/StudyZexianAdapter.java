package com.example.linelistdemo;

import android.view.View;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.linelistdemo.bean.ScoreDailyDurationBean;

public class StudyZexianAdapter extends BaseQuickAdapter<ScoreDailyDurationBean, BaseViewHolder> {

    //一个view行数量
    public static int lineNumber = 6;
    private List<String> times;
    private List<Double> dataList;
    //最大的学习时间
    private int mymaxTime;
    //建议学习时间
    private int mysuggestTime;
    //view的list集合
    private List<ScrollChartView> viewsList;

    public StudyZexianAdapter(int layoutResId) {
        super(layoutResId);
        viewsList = new ArrayList<>();
        times = new ArrayList<>();
        dataList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return getData() == null ? 0 : (getData().size() % lineNumber == 0 ? getData().size() / lineNumber : getData().size() / lineNumber + 1);
    }

    public void setMaxTime(int maxTime, int suggestTime) {
        mymaxTime = maxTime;
        this.mysuggestTime = suggestTime;
    }

    @Override
    public void setNewData(@Nullable List<ScoreDailyDurationBean> data) {
        viewsList.clear();
        times.clear();
        dataList.clear();
        if (null != data)
            for (int i = 0; i < data.size(); i++) {
                times.add(data.get(i).showTime);
                dataList.add(data.get(i).studyTime);
            }
        super.setNewData(data);
    }

    @Override
    protected void convert(final BaseViewHolder holder, ScoreDailyDurationBean item) {

        final ScrollChartView scroll_chart_main = holder.getView(R.id.scroll_chart_main);
        scroll_chart_main.post(new Runnable() {
            @Override
            public void run() {
                try {
                    viewsList.add(scroll_chart_main);
                    int nowposition = (holder.getLayoutPosition() < 0 ? 0 : holder.getLayoutPosition());
                    int startorend = nowposition == 0 ? 0 : (getItemCount() - 1 == nowposition ? 2 : 1);//0 是开始，1是中间  2是最后
                    List<String> listtime = getItemCount() > 1 ? times.subList(nowposition * lineNumber, (nowposition + 1) * lineNumber > times.size() ? times.size() : (nowposition + 1) * lineNumber) : times;
                    List<Double> listdata = getItemCount() > 1 ? dataList.subList(nowposition * lineNumber, (nowposition + 1) * lineNumber > dataList.size() ? dataList.size() : (nowposition + 1) * lineNumber) : dataList;

                    CopyOnWriteArrayList<String> copytimes = new CopyOnWriteArrayList<>();
                    CopyOnWriteArrayList<Double> copydata = new CopyOnWriteArrayList<>();
                    for (int i = 0; i < listtime.size(); i++) {
                        copytimes.add(listtime.get(i));
                        copydata.add(listdata.get(i));
                    }

                    scroll_chart_main.setData(
                            copytimes,
                            copydata,
                            startorend,
                            dataList.get(nowposition == 0 ? 0 : nowposition * lineNumber > dataList.size() ? dataList.size() - 1 : nowposition * lineNumber - 1),
                            dataList.get(dataList.size() > (nowposition + 1) * lineNumber ? (nowposition + 1) * lineNumber : dataList.size() - 1),
                            nowposition,
                            mymaxTime,
                            mysuggestTime);
                } catch (Exception e) {
                }
            }
        });

        scroll_chart_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < viewsList.size(); i++) {
                    if (null != viewsList.get(i))
                        viewsList.get(i).setmSelectedposition(-3);
                }
                scroll_chart_main.setmSelectedposition(holder.getLayoutPosition());
            }
        });
    }


}

