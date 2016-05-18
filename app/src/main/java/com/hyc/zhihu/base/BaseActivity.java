package com.hyc.zhihu.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hyc.zhihu.R;

/**
 * Created by Administrator on 2016/5/13.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected TextView mTitleView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        if (getIntent()!=null) {
            handleIntent();
        }
        initView();
        initActionBar();

    }

    protected abstract void handleIntent();
    protected abstract void initView();

    protected abstract int getLayoutID();

    private void initActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setElevation(0);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        View mCustomView = getLayoutInflater().inflate(R.layout.layout_actionbar, null);
        mTitleView= (TextView) mCustomView.findViewById(R.id.title);
        mTitleView.setText(getTitleString());
        mActionBar.setCustomView(mCustomView, new
                ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT));
        ActionBar.LayoutParams mP = (ActionBar.LayoutParams) mCustomView.getLayoutParams();
        mP.gravity = mP.gravity & ~Gravity.HORIZONTAL_GRAVITY_MASK | Gravity.CENTER_HORIZONTAL;
        mActionBar.setCustomView(mCustomView, mP);
    }
    protected String getTitleString(){
        return "一个";
    }

}
