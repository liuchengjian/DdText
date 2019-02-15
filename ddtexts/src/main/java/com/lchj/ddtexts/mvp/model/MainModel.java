package com.lchj.ddtexts.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.lchj.ddtexts.common.libs.LocationInfo;
import com.lchj.ddtexts.mvp.contract.MainContract;
import com.lchj.ddtexts.mvp.model.api.service.CommonService;
import com.lchj.ddtexts.mvp.model.entity.BaseJson;
import com.lchj.ddtexts.mvp.model.entity.Drivier;

import java.util.List;

import io.reactivex.Observable;


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
public class MainModel extends BaseModel implements MainContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public MainModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    /**
     * 上传司机
     * @param latitude
     * @param longitude
     */
    @Override
    public Observable<BaseJson<List<LocationInfo>>> fetchNearDrivers(double latitude, double longitude) {
        return mRepositoryManager
                .obtainRetrofitService(CommonService.class)
                .fetchNearDrivers(new Double(latitude).toString(), new Double(longitude).toString());
    }
}