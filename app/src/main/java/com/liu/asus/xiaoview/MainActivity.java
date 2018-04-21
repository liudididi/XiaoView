package com.liu.asus.xiaoview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


    @BindView(R.id.bt_wuziqi)
    Button btWuziqi;
    @BindView(R.id.bt_more)
    Button btMore;

    @Override
    public int getId() {
        return R.layout.activity_main;
    }

    @Override
    public void InIt() {

    }

    @OnClick({R.id.bt_wuziqi, R.id.bt_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_wuziqi:
                Intent intent=new Intent(this,WuziqiActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_more:
                break;
        }
    }
}
