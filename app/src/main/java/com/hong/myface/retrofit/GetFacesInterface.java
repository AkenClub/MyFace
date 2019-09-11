package com.hong.myface.retrofit;


import com.hong.myface.bean.FaceppBean;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetFacesInterface {

    @POST("facepp/v3/detect")
    @FormUrlEncoded
    Observable<FaceppBean> getFaces(@Field("api_key") String apiKey,
                                    @Field("api_secret") String apiSecret,
                                    @Field("image_base64") String imageBase64,
                                    @Field("return_landmark") int returnLandmark,
                                    @Field("return_attributes") String returnAttributes
                                    );
}
