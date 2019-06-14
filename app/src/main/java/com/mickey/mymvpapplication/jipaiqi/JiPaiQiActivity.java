package com.mickey.mymvpapplication.jipaiqi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.mickey.mymvpapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Created by Ming on 2019-6-11.
 */
public class JiPaiQiActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private JiPaiQiAdapter adapter;
    private List<String> mList;
    private String[] mData;
    private Button jokerB, jokerS, two, one, king, queen, jack, ten, nine, eight, seven, six;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jipai_view);
        initData();
        initView();
    }

    private void initData() {
        mList = new ArrayList<>();
        mData = new String[]{"dawang", "xiaowang", "2", "A", "K", "Q", "2", "2", "8", "8", "8", "8", "J", "10", "9", "8", "7", "6", "8", "8", "8", "8", "8", "8"};
    }

    private void initView() {

        jokerB = findViewById(R.id.joker_b);
        jokerB.setOnClickListener(this);
        jokerS = findViewById(R.id.joker_s);
        jokerS.setOnClickListener(this);
        two = findViewById(R.id.two);
        two.setOnClickListener(this);
        one = findViewById(R.id.one);
        one.setOnClickListener(this);
        king = findViewById(R.id.king);
        king.setOnClickListener(this);
        queen = findViewById(R.id.queen);
        queen.setOnClickListener(this);
        jack = findViewById(R.id.jack);
        jack.setOnClickListener(this);
        ten = findViewById(R.id.ten);
        ten.setOnClickListener(this);
        nine = findViewById(R.id.nine);
        nine.setOnClickListener(this);
        eight = findViewById(R.id.eight);
        eight.setOnClickListener(this);
        seven = findViewById(R.id.seven);
        seven.setOnClickListener(this);
        six = findViewById(R.id.six);
        six.setOnClickListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new JiPaiQiAdapter(this, mData);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.joker_b:
                changeState(6,jokerB);
                break;
            case R.id.joker_s:
                changeState(7,jokerS);
                break;
            case R.id.two:
                changeState(8,two);
                break;
            case R.id.one:
                changeState(9,one);
                break;
            case R.id.king:
                changeState(10,king);
                break;
            case R.id.queen:
                changeState(11,queen);
                break;
            case R.id.jack:
                changeState(18,jack);
                break;
            case R.id.ten:
                changeState(19,ten);
                break;
            case R.id.nine:
                changeState(20,nine);
                break;
            case R.id.eight:
                changeState(21,eight);
                break;
            case R.id.seven:
                changeState(22,seven);
                break;
            case R.id.six:
                changeState(23,six);
                break;
        }
    }

    private void changeState(int position,Button button){
        int i = Integer.valueOf(mData[position]) - 1;
        if (i == 0){
            button.setClickable(false);
        }
        mData[position] = i + "";
        adapter.notifyDataSetChanged();
    }
}
