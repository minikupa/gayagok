package com.gayagok.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.databinding.DataBindingUtil;

import com.gayagok.R;
import com.gayagok.databinding.ListScheduleBinding;
import com.gayagok.models.ScheduleItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduleListAdapter extends BaseAdapter {

  private final List<ScheduleItem> itemList = new ArrayList<>();

  @Override
  public int getCount() {
    return itemList.size();
  }

  @Override
  public ScheduleItem getItem(int position) {
    return itemList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Context context = parent.getContext();

    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.list_schedule, parent, false);
    }
    ListScheduleBinding binding = DataBindingUtil.bind(convertView);
    binding.setItem(itemList.get(position));

    return convertView;
  }

  public void addItem(String title, String date) {
    itemList.add( new ScheduleItem(title, date));
    Collections.sort(itemList);
  }

}
