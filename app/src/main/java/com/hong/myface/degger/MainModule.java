package com.hong.myface.degger;

import com.hong.myface.MainActivity;
import com.hong.myface.presenter.MainPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    private MainActivity mainActivity;

    public MainModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Provides
    public MainPresenter provideMainPresenter() {
        return new MainPresenter();
    }
}
