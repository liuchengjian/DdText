package com.lchj.ddtexts.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.lchj.ddtexts.di.module.MainModule;
import com.lchj.ddtexts.mvp.contract.MainContract;

import com.jess.arms.di.scope.ActivityScope;
import com.lchj.ddtexts.mvp.ui.activity.MainActivity;


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
@Component(modules = MainModule.class, dependencies = AppComponent.class)
public interface MainComponent {
    void inject(MainActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        MainComponent.Builder view(MainContract.View view);

        MainComponent.Builder appComponent(AppComponent appComponent);

        MainComponent build();
    }
}