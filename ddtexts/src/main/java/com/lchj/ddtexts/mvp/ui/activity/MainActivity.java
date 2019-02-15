package com.lchj.ddtexts.mvp.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jess.arms.base.App;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import com.lchj.ddtexts.common.libs.GaodeLbsLayerImpl;
import com.lchj.ddtexts.common.libs.ILbsLayer;
import com.lchj.ddtexts.common.libs.LocationInfo;
import com.lchj.ddtexts.common.libs.RouteInfo;
import com.lchj.ddtexts.common.utils.DevUtil;
import com.lchj.ddtexts.di.component.DaggerMainComponent;
import com.lchj.ddtexts.mvp.contract.MainContract;
import com.lchj.ddtexts.mvp.model.entity.Drivier;
import com.lchj.ddtexts.mvp.presenter.MainPresenter;

import com.lchj.ddtexts.R;
import com.lchj.ddtexts.mvp.ui.adapter.PoiAdapter;
import com.tbruyelle.rxpermissions2.RxPermissions;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jess.arms.utils.Preconditions.checkNotNull;

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
public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {
    private static final String LOCATION_END = "10000end";
    private RxPermissions mRxPermissions;
    private long exitTime = 0; ////记录第一次点击的时间
    // 添加地图到容器
    @BindView(R.id.map_container)
    ViewGroup mapViewContainer;
    //定位城市
    @BindView(R.id.city)
    TextView mCity;


    @BindView(R.id.select_area)
    LinearLayout mSelectArea;
    //  起点与终点
    @BindView(R.id.start)
    AutoCompleteTextView mStartEdit;
    @BindView(R.id.end)
    AutoCompleteTextView mEndEdit;

    //  操作状态相关元素
    @BindView(R.id.optArea)
    LinearLayout mOptArea;
    @BindView(R.id.loading_area)
    LinearLayout mLoadingArea;
    @BindView(R.id.tips_info)
    TextView mTips;
    @BindView(R.id.loading_text)
    TextView mLoadingText;
    @BindView(R.id.btn_call_driver)
    Button mBtnCall;
    @BindView(R.id.btn_cancel)
    Button mBtnCancel;
    @BindView(R.id.btn_pay)
    Button mBtnPay;
    private float mCost;


    private ILbsLayer mLbsLayer;
    //用户定位的Bitmap
    private Bitmap mLocationBit;
    //司机的Bitmap
    private Bitmap mDriverBit;

    private Bitmap mStartBit;
    private Bitmap mEndBit;

    // 记录起点和终点
    private LocationInfo mStartLocation;
    private LocationInfo mEndLocation;

    private PoiAdapter mEndAdapter;

    /**
     * 点击事件
     * @param v
     */
    @OnClick({R.id.btn_call_driver,R.id.btn_cancel,R.id.btn_pay})
    void Onclick(View v){
        switch (v.getId()){
            case R.id.btn_call_driver:
                showCalling();
                /**
                 * 延时 3 秒然取消成功
                 */
                new Handler().postDelayed(() -> {
                    showCallingSucOrFil(true);
                    restoreUI();
                }, 3000) ;
                break;
            case R.id.btn_cancel:
                //取消
                if (!mBtnCall.isEnabled()) {
                    // 说明已经点了呼叫
                    showCanceling();
                    /**
                     * 延时 3 秒然取消成功
                     */
                    new Handler().postDelayed(() -> {
                        showCallingSucOrFil(true);
                        restoreUI();
                    }, 3000) ;
//                    mPresenter.cancel();
                } else {
                    // 知识显示了路径信息，还没点击呼叫，恢复 UI 即可
                    restoreUI();
                }
                break;
            case R.id.btn_pay:
                break;
        }

    }


    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);
        mRxPermissions = new RxPermissions(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        // 地图服务
        mLbsLayer = new GaodeLbsLayerImpl(this);
        mLbsLayer.onCreate(savedInstanceState);
        // 添加地图到容器
        mapViewContainer.addView(mLbsLayer.getMapView());

        mLbsLayer.setLocationChangeListener(new ILbsLayer.CommonLocationChangeListener() {
            @Override
            public void onLocationChanged(LocationInfo locationInfo) {
            }
            @Override
            public void onLocation(LocationInfo locationInfo) {
                // 记录起点
                mStartLocation = locationInfo;
                //  设置标题
                mCity.setText(mLbsLayer.getCity());
                // 设置起点
                mStartEdit.setText(locationInfo.getName());
                // 获取附近司机
                getNearDrivers(locationInfo.getLatitude(),locationInfo.getLongitude());
                // 上报当前位置
//                updateLocationToServer(locationInfo);
                // 首次定位，添加当前位置的标记
                addLocationMarker();
//                mIsLocate = true;
                //  获取进行中的订单
//                getProcessingOrder();
            }

        });
        mEndEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //  关键搜索推荐地点
                mLbsLayer.poiSearch(s.toString(), new ILbsLayer.OnSearchedListener() {
                    @Override
                    public void onSearched(List<LocationInfo> results) {
                        Log.e("results","results:"+results);
                        // 更新列表
                        updatePoiList(results);
                    }
                    @Override
                    public void onError(int rCode) {

                    }
                });
            }


        });



    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }


    /**
     * 添加定位图标
     */
    private void addLocationMarker() {
        if (mLocationBit == null || mLocationBit.isRecycled()) {
            mLocationBit = BitmapFactory.decodeResource(getResources(),
                    R.drawable.navi_map_gps_locked);
        }
        mLbsLayer.addOrUpdateMarker(mStartLocation, mLocationBit);
    }
    /**
     * 获取附近司机
     *
     * @param latitude
     * @param longitude
     */
    private void getNearDrivers(double latitude, double longitude) {
        mPresenter.fetchNearDrivers(latitude, longitude);
    }


    /**
     * 更新 输入终点地址的 POI 列表
     * @param results
     */
    private void updatePoiList(List<LocationInfo> results) {
        List<String> listString = new ArrayList<String>();
        for (int i = 0; i < results.size(); i++) {
            listString.add(results.get(i).getName());
        }
        if (mEndAdapter == null) {
            mEndAdapter = new PoiAdapter(getApplicationContext(), listString);
            mEndEdit.setAdapter(mEndAdapter);
        } else {
            mEndAdapter.setData(listString);
        }
        mEndEdit.setOnItemClickListener((parent, view, position, id) -> {

            ArmsUtils.makeText(MainActivity.this, results.get(position).getName());
            DevUtil.closeInputMethod(MainActivity.this);
            //  记录终点
            mEndLocation = results.get(position);
            mEndLocation.setKey(LOCATION_END);
            // 绘制路径
            showRoute(mStartLocation, mEndLocation, result -> {
                Log.d(TAG, "driverRoute: " + result);

                mLbsLayer.moveCamera(mStartLocation, mEndLocation);
                // 显示操作区
                showOptArea();
                mCost = result.getTaxiCost();
                String infoString = getString(R.string.route_info);
                infoString = String.format(infoString,
                        new Float(result.getDistance()).intValue(),
                        mCost,
                        result.getDuration());
                mTips.setVisibility(View.VISIBLE);
                mTips.setText(infoString);
            });
        });

        mEndAdapter.notifyDataSetChanged();
    }

    /**
     * 恢复 UI
     */

    private void restoreUI() {
        // 清楚地图上所有标记：路径信息、起点、终点
        mLbsLayer.clearAllMarkers();
        // 添加定位标记
        addLocationMarker();
        // 恢复地图视野
        mLbsLayer.moveCameraToPoint(mStartLocation, 17);
        //  获取附近司机
        getNearDrivers(mStartLocation.getLatitude(), mStartLocation.getLongitude());
        // 隐藏操作栏
        hideOptArea();

    }

    /**
     * 隐藏操作区
     */
    private void hideOptArea() {
        //显示选择起终点输入框
        mSelectArea.setVisibility(View.VISIBLE);
        mOptArea.setVisibility(View.GONE);

    }

    /**
     * 显示操作区
     */
    private void showOptArea() {
        //隐藏选择起终点输入框
        mSelectArea.setVisibility(View.GONE);
        //显示叫车按钮界面
        mOptArea.setVisibility(View.VISIBLE);
        mLoadingArea.setVisibility(View.GONE);
        mTips.setVisibility(View.VISIBLE);
        mBtnCall.setEnabled(true);
        mBtnCancel.setEnabled(true);
        mBtnCancel.setVisibility(View.VISIBLE);
        mBtnCall.setVisibility(View.VISIBLE);
        mBtnPay.setVisibility(View.GONE);
    }

    /**
     * 绘制起点终点路径
     *
     */
    private void showRoute(final LocationInfo mStartLocation,
                           final LocationInfo mEndLocation,
                           ILbsLayer.OnRouteCompleteListener listener) {

        mLbsLayer.clearAllMarkers();
        addStartMarker();
        addEndMarker();
        //绘制线路
        mLbsLayer.driverRoute(mStartLocation,
                mEndLocation,
                Color.GREEN,
                listener
        );
    }

    /**
     * 添加开始图标
     */
    private void addStartMarker() {
        if (mStartBit == null || mStartBit.isRecycled()) {
            mStartBit = BitmapFactory.decodeResource(getResources(),
                    R.drawable.start);
        }
        mLbsLayer.addOrUpdateMarker(mStartLocation, mStartBit);
    }
    /**
     * 添加结束图标
     */
    private void addEndMarker() {
        if (mEndBit == null || mEndBit.isRecycled()) {
            mEndBit = BitmapFactory.decodeResource(getResources(),
                    R.drawable.end);
        }
        mLbsLayer.addOrUpdateMarker(mEndLocation, mEndBit);
    }

    /**
     * 显示呼叫中
     */
    private void showCalling() {
        mTips.setVisibility(View.GONE);
        mLoadingArea.setVisibility(View.VISIBLE);
        mLoadingText.setText(getString(R.string.calling_driver));
        mBtnCancel.setEnabled(true);
        mBtnCall.setEnabled(false);
    }
    /**
     * 显示取消中
     */
    private void showCanceling() {
        mTips.setVisibility(View.GONE);
        mLoadingArea.setVisibility(View.VISIBLE);
        mLoadingText.setText(getString(R.string.canceling));
        mBtnCancel.setEnabled(false);
    }

    /**
     * 呼叫成功或失败
     * @param isSuc
     */
    private void showCallingSucOrFil(boolean isSuc) {
        if(isSuc){
            //呼叫成功
            mLoadingArea.setVisibility(View.GONE);
            mTips.setVisibility(View.VISIBLE);
            mTips.setText(getString(R.string.show_call_suc));
            // 显示操作区
            showOptArea();
            mBtnCall.setEnabled(false);
        }else {
            //呼叫失败
            mLoadingArea.setVisibility(View.GONE);
            mTips.setVisibility(View.VISIBLE);
            mTips.setText(getString(R.string.show_call_fail));
            mBtnCall.setEnabled(true);
        }

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mLbsLayer.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mLbsLayer.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mLbsLayer.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLbsLayer.onDestroy();
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }

    /**
     * 获取附近司机回调
     * @param data
     */
    @Override
    public void fetchSuccess(List<LocationInfo> data) {
        for (LocationInfo locationInfo : data) {
            showLocationChange(locationInfo);
        }
    }

    /**
     * 显示司机更新位置
     * @param locationInfo
     */
    private void showLocationChange(LocationInfo locationInfo) {
        if (mDriverBit == null || mDriverBit.isRecycled()) {
            mDriverBit = BitmapFactory.decodeResource(getResources(), R.drawable.car);
        }
        mLbsLayer.addOrUpdateMarker(locationInfo, mDriverBit);
    }

    /**
     * 双击退出
     */
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ArmsUtils.makeText(this,getString(R.string.go_on_exit));
            exitTime = System.currentTimeMillis();
        } else {
            //killMyself();
            ((App) getApplication()).getAppComponent().appManager().appExit();
            //System.exit(0);//正常退出App
            //android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

}
