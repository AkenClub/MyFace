package com.hong.myface.presenter;

import android.graphics.Bitmap;
import android.util.Log;

import com.hong.myface.bean.FaceppBean;
import com.hong.myface.contract.Contract;
import com.hong.myface.contract.Contract.IView;
import com.hong.myface.model.MainModel;
import com.hong.myface.util.BitmapUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter implements Contract.IPresenter {

    private MainModel mModel;
    IView mView;
    private List<FaceppBean.FacesBean> facesList;


    public MainPresenter() {
        mModel = new MainModel();
    }


    @Override
    public void attachView(IView view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        this.mView = null;
    }

    @Override
    public void handleRequest(Bitmap bitmap) {
        String s = BitmapUtil.bitmap2base64(bitmap);
        Observable<FaceppBean> observable = mModel.getResultFromServer(s);
        handleResult(observable, bitmap);
    }

    @Override
    public void handleResult(Observable<FaceppBean> observable, Bitmap bitmap) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FaceppBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mView.clearFaces();
                        mView.showProgress();
                        mView.showPhoto(bitmap);
                    }

                    @Override
                    public void onNext(FaceppBean faceppBean) {
                        handleDetectResult(faceppBean, bitmap);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.hideProgress();
                        mView.showError();
                    }

                    @Override
                    public void onComplete() {
                        mView.hideProgress();
                    }
                });
    }

    @Override
    public void handleDetectResult(FaceppBean faceppBean, Bitmap bitmap) {
        List<FaceppBean.FacesBean> faces = faceppBean.getFaces();
        if (faces.size() > 5) {
            faces = faces.subList(0, 5);
        }
        Bitmap makedPhoto = BitmapUtil.markFacesInPhoto(faces, bitmap);
        mView.showMarkedPhoto(makedPhoto);
        //将每个face裁剪之后再放回List中
        facesList = setFace2Bean(faces, bitmap);
        mView.showFacesInfo(facesList);
    }


    private List<FaceppBean.FacesBean> setFace2Bean(List<FaceppBean.FacesBean> faces, Bitmap bitmap) {
        for (int i = 0; i < faces.size(); i++) {
            FaceppBean.FacesBean facesBean = faces.get(i);
            FaceppBean.FacesBean.FaceRectangleBean rectangle = facesBean.getFace_rectangle();
            int left = rectangle.getLeft();
            int top = rectangle.getTop();
            int width = rectangle.getWidth();
            int height = rectangle.getHeight();

            int widthPadding = width / 5;
            int heightPadding = height / 5;

            int mLeft = left - widthPadding < 0 ? 0 : left - widthPadding;
            int mTop = top - heightPadding < 0 ? 0 : top - heightPadding;
            int mWidth = (mLeft + width + widthPadding) > bitmap.getWidth() ? bitmap.getWidth() - mLeft : width + widthPadding;
            int mHeight = (mTop + height + heightPadding) > bitmap.getHeight() ? bitmap.getHeight() - mTop : height + heightPadding;

            Bitmap faceBitmap = Bitmap.createBitmap(bitmap, mLeft, mTop, mWidth, mHeight);

            facesBean.setBitmap(faceBitmap);
            faces.set(i, facesBean);
        }
        return faces;
    }

}
