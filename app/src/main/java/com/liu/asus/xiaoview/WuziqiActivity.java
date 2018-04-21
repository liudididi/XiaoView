package com.liu.asus.xiaoview;

import android.os.Bundle;
import android.widget.TextView;

import View.WuziqiView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WuziqiActivity extends BaseActivity {


    @BindView(R.id.again)
    TextView again;
    @BindView(R.id.wuziqiview)
    WuziqiView wuziqiview;

    @Override
    public int getId() {
        return R.layout.activity_wuziqi;
    }

    @Override
    public void InIt() {
        
    }
    @OnClick(R.id.again)
    public void onViewClicked() {
        wuziqiview.start();
    }

}
