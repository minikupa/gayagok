package com.gayagok.activities;

import androidx.annotation.NonNull;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.gayagok.R;

public class AboutActivity extends MaterialAboutActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_MaterialAboutActivity_Light_DarkActionBar);
        super.onCreate(savedInstanceState);
    }

    @Override
    @NonNull
    protected MaterialAboutList getMaterialAboutList(@NonNull Context context) {
        MaterialAboutCard.Builder aboutCardBuilder = new MaterialAboutCard.Builder();

        aboutCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .icon(R.mipmap.ic_launcher)
                .text("가야곡초중학교")
                .desc("© 2018-2019 MiniKupa")
                .build());

        aboutCardBuilder.addItem(ConvenienceBuilder.createVersionActionItem(AboutActivity.this,
                getDrawable(R.drawable.ic_info),
                "버젼",
                true));

        aboutCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_license)
                .text("라이선스")
                .setOnClickAction(ConvenienceBuilder.createWebsiteOnClickAction(AboutActivity.this, Uri.parse("https://minikupa-dev.tistory.com/4")))
                .build());

        MaterialAboutCard.Builder mkCardBuilder = new MaterialAboutCard.Builder();
        mkCardBuilder.title("개발자");

        mkCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_kakao)
                .text("카카오톡(문의사항)")
                .setOnClickAction(ConvenienceBuilder.createWebsiteOnClickAction(AboutActivity.this, Uri.parse("https://open.kakao.com/o/scqGKetb")))
                .build());

        mkCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_youtube)
                .text("유튜브")
                .setOnClickAction(ConvenienceBuilder.createWebsiteOnClickAction(AboutActivity.this, Uri.parse("https://www.youtube.com/c/%EB%AF%B8%EB%8B%88%EC%BF%A0%ED%8C%8C")))
                .build());

        mkCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_blog)
                .text("블로그")
                .setOnClickAction(ConvenienceBuilder.createWebsiteOnClickAction(AboutActivity.this, Uri.parse("https://minikupa-dev.tistory.com/")))
                .build());


        return new MaterialAboutList(aboutCardBuilder.build(), mkCardBuilder.build());
    }

    @Override
    protected CharSequence getActivityTitle() {
        return "about";
    }

}
