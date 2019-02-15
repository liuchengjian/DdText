package com.lchj.ddtexts.mvp.contract;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;
import com.lchj.ddtexts.common.libs.LocationInfo;
import com.lchj.ddtexts.mvp.model.entity.BaseJson;
import com.lchj.ddtexts.mvp.model.entity.Drivier;
import com.tbruyelle.rxpermissions2.RxPermissions;

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
public interface MainContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        RxPermissions getRxPermissions();
        void fetchSuccess(List<LocationInfo> drivierList);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        /**
         * 获取附近司机
         * @param latitude
         * @param longitude
         */
        Observable<BaseJson<List<LocationInfo>>> fetchNearDrivers(double latitude, double longitude);
    }
}
