package com.hong.myface;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hong.myface.bean.FaceppBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import fr.tvbarthel.lib.blurdialogfragment.BlurDialogFragment;

public class DetailDialogFragment extends BlurDialogFragment {

    FaceppBean.FacesBean face;
    Unbinder unbinder;

    @BindView(R.id.sex)
    TextView sex;
    @BindView(R.id.age)
    TextView age;
    @BindView(R.id.beauty)
    TextView beauty;
    @BindView(R.id.person_img)
    ImageView ivPersonImg;
    @BindView(R.id.angery)
    TextView angery;
    @BindView(R.id.surprise)
    TextView surprise;
    @BindView(R.id.fear)
    TextView fear;
    @BindView(R.id.happy)
    TextView happy;
    @BindView(R.id.calm)
    TextView calm;
    @BindView(R.id.sad)
    TextView sad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(FaceEvent event) {
        face = event.getFace();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        ivPersonImg.setImageBitmap(face.getBitmap());

        String gender = face.getAttributes().getGender().getValue();
        sex.setText("Male".equals(gender) ? "男" : "女");
        age.setText(face.getAttributes().getAge().getValue() + "");
        double femaleScore = face.getAttributes().getBeauty().getFemale_score();
        double maleScore = face.getAttributes().getBeauty().getMale_score();
        beauty.setText(String.format("%.2f", "Male".equals(gender) ? maleScore : femaleScore));

        FaceppBean.FacesBean.AttributesBean.EmotionBean emotion = face.getAttributes().getEmotion();
        angery.setText(emotion.getAnger() + "%");
        surprise.setText(emotion.getSurprise() + "%");
        fear.setText(emotion.getFear() + "%");
        happy.setText(emotion.getHappiness() + "%");
        calm.setText(emotion.getNeutral() + "%");
        sad.setText(emotion.getSadness() + "%");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = win.getAttributes();
        params.width = 800;
        params.height = 1000;
        win.setAttributes(params);
    }

    @Override
    protected float getDownScaleFactor() {
        return 5.0f;
    }

    @Override
    protected boolean isDimmingEnable() {
        return false;
    }

    @Override
    protected boolean isRenderScriptEnable() {
        return true;
    }

    @Override
    protected int getBlurRadius() {
        return 7;
    }

}
