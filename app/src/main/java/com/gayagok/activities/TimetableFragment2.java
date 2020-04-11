package com.gayagok.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.gayagok.R;
import com.gayagok.databinding.DialogFindshareBinding;
import com.gayagok.databinding.FragmentTimetable2Binding;
import com.gayagok.models.TimetableItem;
import com.gayagok.utils.SharedPreferenceUtil;
import com.gayagok.utils.Timetable2Util;
import com.gayagok.views.LoadingDialog;
import com.gayagok.views.TimetableListAdapter;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;

public class TimetableFragment2 extends Fragment {

    private FragmentTimetable2Binding binding;
    private final Calendar calendar = Calendar.getInstance();

    private Timetable2Util timetable2Util;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private RequestOptions requestOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timetable2, container, false);
        View view = binding.getRoot();
        binding.setFragment(this);

        timetable2Util = new Timetable2Util(getActivity());
        sharedPreferenceUtil = new SharedPreferenceUtil(getActivity());

        requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(Target.SIZE_ORIGINAL)
                .format(DecodeFormat.PREFER_ARGB_8888);

        if (timetable2Util.loadImage().exists()) {
            Glide.with(getActivity()).load(timetable2Util.loadImage()).apply(requestOptions).into(binding.imageView);

            binding.textView.setVisibility(View.INVISIBLE);
            binding.delete.setVisibility(View.VISIBLE);
        } else {
            binding.imageView.setImageResource(R.drawable.ic_noimage);
        }


        return view;
    }

    public void findButtonPress(View view) {
        binding.floatingMenu.collapse();
        findShareDialog("시간표 검색");
    }

    public void shareButtonPress(View view) {
        findShareDialog("시간표를 공유하시겠습니까?");
    }

    public void uploadButtonPress(View view) {
        TedPermission.with(getActivity())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        TedBottomPicker.with(getActivity()).show(uri -> {
                            try {
                                binding.textView.setVisibility(View.INVISIBLE);
                                binding.delete.setVisibility(View.VISIBLE);

                                Glide.with(getActivity()).load(uri).apply(requestOptions).into(binding.imageView);
                                timetable2Util.saveImage(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                    }
                })
                .setRationaleMessage("사진을 가져오기 위해 권한이 필요합니다.")
                .setDeniedMessage("다시 권한을 허용하시려면 [설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    public void deleteButtonPress(View view) {
        binding.imageView.setImageResource(R.drawable.ic_noimage);
        binding.delete.setVisibility(View.GONE);
        binding.textView.setVisibility(View.VISIBLE);
        timetable2Util.deleteImage();
    }

    private void findShareDialog(String title) {
        View innerView = getLayoutInflater().inflate(R.layout.dialog_findshare, null);
        DialogFindshareBinding binding2 = DataBindingUtil.bind(innerView);
        AlertDialog ab = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(innerView)
                .setPositiveButton("확인", null)
                .create();

        ArrayAdapter elementaryAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.share_elementary_grade, android.R.layout.simple_spinner_item);
        ArrayAdapter middleAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.share_middle_grade, android.R.layout.simple_spinner_item);

        elementaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        middleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 2019; i <= calendar.get(Calendar.YEAR); i++) {
            list.add(i + "년");
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                list
        );
        binding2.year.setAdapter(yearAdapter);

        binding2.school.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int i = (int) binding2.grade.getSelectedItemId();
                if (position == 0) {
                    binding2.grade.setAdapter(elementaryAdapter); //학년을 6학년까지 나오게
                    binding2.grade.setSelection(i);
                } else {
                    binding2.grade.setAdapter(middleAdapter); //학년을 3학년까지 나오게
                    if (i < 3) {
                        binding2.grade.setSelection(i);
                    } else {
                        binding2.grade.setSelection(2);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ab.setButton(AlertDialog.BUTTON_NEGATIVE, "취소", (DialogInterface.OnClickListener) null);
        ab.setOnShowListener((DialogInterface dialog) -> //확인버튼 눌러도 다이얼로그 안나가짐
                ab.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((View view) -> {

                    String schoolString = binding2.school.getSelectedItem().toString();
                    String yearString = binding2.year.getSelectedItem().toString();
                    String gradeString = binding2.grade.getSelectedItem().toString();
                    String semesterString = binding2.semester.getSelectedItem().toString();

                    if (title.equals("시간표를 공유하시겠습니까?")) {
                        timetable2Util.uploadImage(((BitmapDrawable) binding.imageView.getDrawable()).getBitmap(), schoolString, yearString, gradeString, semesterString);
                        binding.floatingMenu.collapse();
                        dialog.dismiss();
                    } else {
                        TimetableListAdapter adapter = new TimetableListAdapter();
                        timetable2Util.findTimetable(schoolString, yearString, gradeString, semesterString, binding2.listView, adapter);

                        binding2.listView.setOnItemClickListener((AdapterView<?> parent, View viewX, int positionX, long id) -> {
                            TimetableItem item = adapter.getItem(positionX);
                            LoadingDialog myDialog = new LoadingDialog(getActivity());
                            myDialog.show();
                            myDialog.setText("사진을 불러오는중...");

                            sharedPreferenceUtil.putString("image_title", item.title).putString("image_people", item.people);
                            Glide.with(getActivity()).asBitmap().load(item.content).apply(requestOptions).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                    binding.imageView.setImageBitmap(resource);
                                    timetable2Util.saveImage(resource);

                                    binding.textView.setVisibility(View.INVISIBLE);
                                    binding.delete.setVisibility(View.VISIBLE);
                                    myDialog.dismiss();
                                }
                            });

                            binding.floatingMenu.collapse();
                            dialog.dismiss();
                        });
                    }
                }));
        ab.show();
    }

}
