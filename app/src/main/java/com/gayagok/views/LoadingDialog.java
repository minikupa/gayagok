package com.gayagok.views;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.gayagok.R;
import com.gayagok.databinding.DialogLoadingBinding;

public class LoadingDialog extends Dialog {

    private  DialogLoadingBinding binding;

    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_loading, null, false);
        setContentView(binding.getRoot());

        setCancelable(false);
        binding.animationView.setAnimation("loading.json");
        binding.animationView.setRepeatCount(ValueAnimator.INFINITE);
        binding.animationView.playAnimation();
    }

    public void setText(String text) {
        binding.progress.setText(text);
    }

}
