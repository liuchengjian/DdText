package com.lchj.ddtexts.mvp.presenter;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

import javax.inject.Inject;

import com.jess.arms.utils.PermissionUtil;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lchj.ddtexts.R;
import com.lchj.ddtexts.common.libs.LocationInfo;
import com.lchj.ddtexts.mvp.contract.MainContract;
import com.lchj.ddtexts.mvp.model.entity.BaseJson;
import com.lchj.ddtexts.mvp.model.entity.Drivier;

import java.util.List;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 02/15/2019 09:52
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class MainPresenter extends BasePresenter<MainContract.Model, MainContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public MainPresenter(MainContract.Model model, MainContract.View rootView) {
        super(model, rootView);
    }
    public void requestPermissions() {
        PermissionUtil.requestPermission(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                //request permission success, do something.
            }
            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                mRootView.showMessage(mApplication.getString(R.string.get_permissions_fail));

            }
            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
            }
        }, mRootView.getRxPermissions(), mErrorHandler);
    }

    /**
     * 获取附近司机
     */
    public void fetchNearDrivers(double latitude, double longitude) {
        requestPermissions();
        mModel.fetchNearDrivers(latitude, longitude)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 1))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<BaseJson<List<LocationInfo>>>(mErrorHandler) {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        mRootView.hideLoading();
                        Log.e("Throwable","Throwable:"+e);
//                        mRootView.Failed(XJUtils.getThrowsMsg(e));
                    }

                    @Override
                    public void onNext(BaseJson<List<LocationInfo>> baseJson) {
                        if (baseJson == null) {
                            Log.e("Throwable","baseJson is null");
                            return;
                        }
                        if (baseJson.isSuccess()){
                            mRootView.hideLoading();
                            mRootView.fetchSuccess(baseJson.getData());
                        }
                    }
                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}
