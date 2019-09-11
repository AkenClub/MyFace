package com.hong.myface;

import com.hong.myface.bean.FaceppBean;

class FaceEvent {

    FaceppBean.FacesBean face;

    public FaceEvent(FaceppBean.FacesBean facesBean) {
        this.face = facesBean;
    }

    public FaceppBean.FacesBean getFace() {
        return face;
    }
}
