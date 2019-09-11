package com.hong.myface.degger;

import com.hong.myface.MainActivity;

import dagger.Component;
import dagger.Module;

@Component(modules = MainModule.class)
public interface MainComponent {
    void inject(MainActivity mainActivity);
}
