package com.hong.myface.contract;

import android.graphics.Bitmap;

import com.hong.myface.bean.FaceppBean;

import java.util.List;

import io.reactivex.Observable;

public interface Contract {
    interface IModel {
        Observable<FaceppBean> getResultFromServer(String s);
    }

    interface IView {
        void showError();
        void showProgress();
        void hideProgress();
        void showMarkedPhoto(Bitmap bitmap);
        void clearFaces();

        void showPhoto(Bitmap bitmap);

        void showFacesInfo(List<FaceppBean.FacesBean> facesList);
    }

    interface IPresenter{
        void attachView(IView view);
        void detachView();
        void handleRequest(Bitmap bitmap);
        void handleResult(Observable<FaceppBean> observable, Bitmap bitmap);
        void handleDetectResult(FaceppBean faceppBean, Bitmap bitmap);
    }
}
