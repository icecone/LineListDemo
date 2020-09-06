package com.example.linelistdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.linelistdemo.bean.ScoreDailyDurationBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 *
 * 用列表实现折线图，之后自己可以进行刷新，加载
 *
 */
public class MainActivity extends AppCompatActivity {

    String json = "[{\"studyTime\": 20,\"showTime\": \"10-01\"}, {\"studyTime\": 40,\"showTime\": \"10-02\"}, {\"studyTime\": 30,\"showTime\": \"10-03\"}, {\"studyTime\": 50,\"showTime\": \"10-04\"}, {\"studyTime\": 10,\"showTime\": \"10-05\"}, {\"studyTime\": 60,\"showTime\": \"10-06\"}, {\"studyTime\": 7,\"showTime\": \"10-07\"}, {\"studyTime\": 40,\"showTime\": \"10-08\"}, {\"studyTime\": 20,\"showTime\": \"10-09\"}, {\"studyTime\": 5,\"showTime\": \"10-10\"}, {\"studyTime\": 40,\"showTime\": \"10-11\"}, {\"studyTime\": 6,\"showTime\": \"10-12\"}, {\"studyTime\": 60,\"showTime\": \"10-13\"}, {\"studyTime\": 6,\"showTime\": \"10-14\"}]";

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        StudyZexianAdapter studyZexianAdapter = new StudyZexianAdapter(R.layout.item_studyzexian_layout);
        recyclerView.setAdapter(studyZexianAdapter);

        Gson gson = new Gson();
        List<ScoreDailyDurationBean> list= gson.fromJson(json,new TypeToken<List<ScoreDailyDurationBean>>(){}.getType());
        studyZexianAdapter.setNewData(list);

    }
}
