package com.lchj.ddtexts.mvp.model.api.service;

import com.lchj.ddtexts.common.libs.LocationInfo;
import com.lchj.ddtexts.mvp.model.entity.BaseJson;
import com.lchj.ddtexts.mvp.model.entity.Drivier;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CommonService {


    /**
     * 获取附近司机
     * @param latitude
     * @param longitude
     * @return
     */
    @GET("/f34e28da5816433d/getNearDrivers")
    Observable<BaseJson<List<LocationInfo>>>fetchNearDrivers(@Query("latitude") String latitude,
                                                             @Query("longitude") String longitude);

}
