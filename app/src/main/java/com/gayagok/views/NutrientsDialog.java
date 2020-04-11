package com.gayagok.views;

import android.app.Dialog;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.gayagok.R;
import com.gayagok.databinding.DialogNutrientsBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NutrientsDialog extends Dialog {

    private final String nutrientStr;

    public NutrientsDialog(Context context, String nutrient) {
        super(context);
        this.nutrientStr = nutrient;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogNutrientsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_nutrients, null, false);
        setContentView(binding.getRoot());

        TextView[] nutrients = new TextView[]{binding.nutrient0, binding.nutrient1, binding.nutrient2, binding.nutrient3, binding.nutrient4, binding.nutrient5, binding.nutrient6, binding.nutrient7, binding.nutrient8, binding.nutrient9};
        String[] nutrientsStr = new Gson().fromJson(nutrientStr, new TypeToken<String[]>() {
        }.getType());

        for (int i = 0; i < 10; i++) {
            nutrients[i].setText(nutrientsStr[i]);
        }

    }

}
