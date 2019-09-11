package com.hong.myface.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hong.myface.R;
import com.hong.myface.bean.FaceppBean;

import java.util.List;

public class FacesAdapter extends BaseQuickAdapter<FaceppBean.FacesBean, BaseViewHolder> {

    Context mContext;

    public FacesAdapter(int layoutResId, @Nullable List<FaceppBean.FacesBean> data, Context context) {
        super(layoutResId, data);
        this.mContext = context;
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void convert(@NonNull BaseViewHolder helper, FaceppBean.FacesBean item) {
        Glide.with(mContext)
                .asBitmap()
                .load(item.getBitmap())
                .transition(new BitmapTransitionOptions().crossFade(700))
                .apply(RequestOptions.circleCropTransform().transform(new RoundedCorners(8)))
                .into((ImageView) helper.getView(R.id.iv_face_detail));
        String sex = item.getAttributes().getGender().getValue();
        helper.setText(R.id.tv_gender, "Male".equals(sex) ? "男" : "女");
        helper.setText(R.id.tv_age, item.getAttributes().getAge().getValue() + "");
        double femaleScore = item.getAttributes().getBeauty().getFemale_score();
        double maleScore = item.getAttributes().getBeauty().getMale_score();
        helper.setText(R.id.tv_beauty, String.format("%.2f", "Male".equals(sex) ? maleScore : femaleScore));
    }

}
