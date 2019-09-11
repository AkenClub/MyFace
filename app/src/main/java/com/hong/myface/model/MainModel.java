package com.hong.myface.model;

import com.hong.myface.BuildConfig;
import com.hong.myface.bean.FaceppBean;
import com.hong.myface.contract.Contract;
import com.hong.myface.retrofit.GetFacesInterface;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainModel implements Contract.IModel {
    @Override
    public Observable<FaceppBean> getResultFromServer(String s) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-cn.faceplusplus.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        GetFacesInterface facesInterface = retrofit.create(GetFacesInterface.class);

        Observable<FaceppBean> observable = facesInterface.getFaces(BuildConfig.API_KEY, BuildConfig.API_SECRET, s, 1,
                "gender,age,smiling,emotion,beauty");

        return observable;
    }
}
