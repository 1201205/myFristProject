package com.hyc.one.presenter;

import com.hyc.one.base.BasePresenter;
import com.hyc.one.base.DefaultTransformer;
import com.hyc.one.base.ExceptionAction;
import com.hyc.one.beans.BaseBean;
import com.hyc.one.beans.Other;
import com.hyc.one.beans.OtherCenter;
import com.hyc.one.net.Requests;
import com.hyc.one.presenter.base.IOtherDetailPresenter;
import com.hyc.one.view.OtherDetailView;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;

/**
 * Created by Administrator on 2016/7/7.
 */
public class OtherDetailPresenter extends BasePresenter<OtherDetailView> implements IOtherDetailPresenter {
    public OtherDetailPresenter(OtherDetailView view) {
        super(view);
    }

    @Override
    public void getAndShowContent(String id) {
        mView.showLoading();
        mCompositeSubscription.add(
                Observable.zip(Requests.getApi()
                        .getOtherInfoByID(id)
                        .compose(new DefaultTransformer<BaseBean<Other>, Other>()), Requests.getApi()
                        .getOtherCenterByID(id)
                        .compose(new DefaultTransformer<BaseBean<OtherCenter>, OtherCenter>()),
                    new Func2<Other, OtherCenter, Void>() {
                        @Override public Void call(Other other, OtherCenter otherCenter) {
                            mView.showDairyAndMusic(otherCenter);
                            mView.showContent(other);
                            return null;
                        }
                    }).subscribe(new Action1<Void>() {
                    @Override public void call(Void aVoid) {
                        mView.dismissLoading();
                    }
                }, new ExceptionAction() {
                    @Override public void onNothingGet() {
                        mView.onNothingGet();
                        mView.dismissLoading();
                    }
                }));
                //Observable.just(Requests.getApi().getOtherInfoByID(id).compose(new DefaultTransformer<BaseBean<Other>, Other>()).subscribe(new Action1<Other>() {
                //    @Override
                //    public void call(Other other) {
                //        mView.showContent(other);
                //    }
                //}), Requests.getApi().getOtherCenterByID(id).compose(new DefaultTransformer<BaseBean<OtherCenter>, OtherCenter>()).subscribe(new Action1<OtherCenter>() {
                //    @Override
                //    public void call(OtherCenter otherCenter) {
                //        mView.showDairyAndMusic(otherCenter);
                //    }
                //})).subscribe(new Action1<Subscription>() {
                //    @Override
                //    public void call(Subscription subscription) {
                //        mView.dismissLoading();
                //    }
                //}, new ExceptionAction() {
                //    @Override
                //    public void onNothingGet() {
                //        mView.onNothingGet();
                //        mView.dismissLoading();
                //    }
                //}));
    }
}
