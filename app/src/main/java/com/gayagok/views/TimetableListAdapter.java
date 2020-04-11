package com.gayagok.views;

import android.content.Context;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gayagok.R;
import com.gayagok.databinding.ListTimetableBinding;
import com.gayagok.models.TimetableItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimetableListAdapter extends BaseAdapter {

    private final List<TimetableItem> itemList = new ArrayList<>();

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public TimetableItem getItem(int position) {
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
            convertView = inflater.inflate(R.layout.list_timetable, parent, false);
        }
        ListTimetableBinding binding = DataBindingUtil.bind(convertView);
        binding.setItem(itemList.get(position));


        return convertView;
    }

    @BindingAdapter({"imageUrl"})
    public static void setImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }

    public void addItem(String title, String content, String people, String profile) {
        itemList.add(new TimetableItem(title, content, people, profile));
        Collections.shuffle(itemList);
    }

}
