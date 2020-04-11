package com.gayagok.views;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gayagok.activities.LunchActivity;
import com.gayagok.activities.ScheduleActivity;
import com.gayagok.activities.TimetableActivity;
import com.gayagok.R;
import com.gayagok.databinding.CardviewMenusBinding;
import com.gayagok.models.MenusItem;

import java.util.List;

public class MenusAdapter extends RecyclerView.Adapter<MenusAdapter.ViewHolder> {

  private final Context context;
  private final List<MenusItem> items;

  public MenusAdapter(Context context, List<MenusItem> items) {
    this.context = context;
    this.items = items;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_menus, null, false);
    return new ViewHolder(v);
  }

  @BindingAdapter({"vectorImage"})
  public static void loadImage(ImageView imageView, int image) {
    imageView.setImageResource(image);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    final MenusItem item = items.get(position);
    holder.binding.setMenus(item);

    holder.binding.cardview.setOnClickListener((View view) -> {

            switch (position){
              case 0:
                context.startActivity(new Intent(context, LunchActivity.class));
                break;
              case 1:
                context.startActivity(new Intent(context, TimetableActivity.class));
                break;
              case 2:
                context.startActivity(new Intent(context, ScheduleActivity.class));
                break;
              case 3:
                break;
            }

    });
  }

  @Override
  public int getItemCount() {
    return this.items.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    final CardviewMenusBinding binding;

    private ViewHolder(View itemView) {
      super(itemView);
      binding = DataBindingUtil.bind(itemView);
    }
  }
}