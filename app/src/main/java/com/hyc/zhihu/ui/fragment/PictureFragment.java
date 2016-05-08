package com.hyc.zhihu.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyc.zhihu.R;
import com.hyc.zhihu.base.BasePresenter;
import com.hyc.zhihu.base.PresenterFactory;
import com.hyc.zhihu.base.PresenterLoader;
import com.hyc.zhihu.beans.OnePictureData;
import com.hyc.zhihu.beans.PictureViewBean;
import com.hyc.zhihu.presenter.MainPresenter;
import com.hyc.zhihu.presenter.PicturePresenterImp;
import com.hyc.zhihu.presenter.base.PicturePresenter;
import com.hyc.zhihu.ui.adpter.PictureAdapter;
import com.hyc.zhihu.view.PictureView;

import java.util.List;

/**
 * Created by ray on 16/5/5.
 */
public class PictureFragment extends Fragment implements PictureView,LoaderManager.LoaderCallbacks<PicturePresenter> {
    private PicturePresenter mPresenter;
    private ViewPager mViewPager;
    private PictureAdapter mPictureAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(111111, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.picture_fragment,null);
        mViewPager= (ViewPager) view.findViewById(R.id.pager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPictureAdapter.setCurrentPage(position);
                mPresenter.gotoPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getPictureIdsAndFirstItem();
    }

    @Override
    public void showPicture(String id, OnePictureData data) {
        mPictureAdapter.setCurrentItem(id,data);
    }

    @Override
    public void jumpToDate() {

    }

    @Override
    public void showNetWorkError() {

    }

    @Override
    public void setAdapter(List<PictureViewBean> beans) {
        mPictureAdapter=new PictureAdapter(beans,getActivity());

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setAdapter(mPictureAdapter);

            }
        });
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public Loader<PicturePresenter> onCreateLoader(int id, Bundle args) {
        return new PresenterLoader(getContext(), new PresenterFactory() {
            @Override
            public BasePresenter create() {
                return new PicturePresenterImp(PictureFragment.this);
            }
        });
    }

    @Override
    public void onLoadFinished(Loader<PicturePresenter> loader, PicturePresenter data) {
        mPresenter = data;
    }

    @Override
    public void onLoaderReset(Loader<PicturePresenter> loader) {
        mPresenter = null;
    }
}