package com.gayagok.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

import com.gayagok.models.LunchItem;
import com.gayagok.R;
import com.google.android.material.snackbar.Snackbar;
import com.hendraanggrian.recyclerview.widget.ExpandableItem;
import com.hendraanggrian.recyclerview.widget.ExpandableRecyclerView;

public class LunchAdapter extends ExpandableRecyclerView.Adapter<LunchAdapter.ViewHolder> {
  private Context context;
  private final List<LunchItem> items;
  private final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
  private final Calendar cal;

  public LunchAdapter(LinearLayoutManager layout, List<LunchItem> items) {
    super(layout);
    this.items = items;
    cal = Calendar.getInstance();
    numberFormat.setMinimumIntegerDigits(2);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    this.context = parent.getContext();
    return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_lunch, parent, false));
  }

  public void onBindViewHolder(final ViewHolder holder, int position) {
    super.onBindViewHolder(holder, position);
    if (position == 5) {
      holder.date.setText("알레르기 정보");
      holder.menu.setText("1.난류 2.우유 3.메밀 4.땅콩 5.대두\n6.밀 7.고등어 8.게 9.새우 10.돼지고기\n11.복숭아 12.토마토 13.아황산류 14.호두\n15.닭고기16.쇠고기 17.오징어 18.조개류(굴,전복,홍합 포함)");
      holder.menu.setTextColor(Color.parseColor("#212121"));
      holder.expandLayout.setVisibility(View.GONE);
      LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.headerLayout.getLayoutParams();
      params.weight = 6;
      holder.headerLayout.setLayoutParams(params);
    } else {
      final LunchItem item = items.get(position);
      holder.date.setText(item.date.replaceAll(cal.get(Calendar.YEAR) + ".", ""));
      holder.menu.setText(Html.fromHtml(item.menu.substring(0, item.menu.lastIndexOf("<br>"))));

      if (item.date.contains(numberFormat.format(cal.get(Calendar.MONTH) + 1) + "." + numberFormat.format(cal.get(Calendar.DAY_OF_MONTH)))) {
        holder.animationView.setAnimation("today.json");
        holder.animationView.setRepeatCount(ValueAnimator.INFINITE);
        holder.animationView.playAnimation();
      }

      holder.imageView.setOnClickListener((View view) -> {
        if (holder.expandableItem.isOpened()) {
          holder.expandableItem.hide();
        } else {
          holder.expandableItem.show();
        }
      });
      holder.nutritionLayout.setOnClickListener((View view)-> {
        if (!item.nutrient.isEmpty()) {
          new NutrientsDialog(context, item.nutrient).show();
        }
      });
      holder.personnelLayout.setOnClickListener((View view) -> {
        if (!item.personnel.isEmpty()) {
          Snackbar.make(view, item.personnel, Snackbar.LENGTH_LONG)
                  .setAction("확인", v -> {
                  })
                  .setActionTextColor(context.getResources().getColor(R.color.colorPrimary))
                  .show();
        }
      });
    }

    holder.expandableItem.setOnClickListener(null);
    Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
    holder.cardview.setAnimation(animation);
  }

  @Override
  public int getItemCount() {
    return 6;
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    final TextView date;
    final TextView menu;
   final LottieAnimationView animationView;
    final ImageView imageView;
    final LinearLayout nutritionLayout;
    final LinearLayout personnelLayout;
    final LinearLayout expandLayout;
    final LinearLayout headerLayout;
    final ExpandableItem expandableItem;
    final CardView cardview;

    private ViewHolder(View itemView) {
      super(itemView);

      expandableItem = itemView.findViewById(R.id.row);
      cardview = itemView.findViewById(R.id.cardview);
      date = expandableItem.findViewById(R.id.date);
      menu = expandableItem.findViewById(R.id.menu);
      animationView = expandableItem.findViewById(R.id.today);
      imageView = expandableItem.findViewById(R.id.expand);
      nutritionLayout = expandableItem.findViewById(R.id.nutrition);
      personnelLayout = expandableItem.findViewById(R.id.personnel);
      expandLayout = expandableItem.findViewById(R.id.expandLayout);
      headerLayout = expandableItem.findViewById(R.id.headerLayout);
    }
  }
}