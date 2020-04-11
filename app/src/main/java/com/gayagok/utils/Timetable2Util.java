package com.gayagok.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.gayagok.views.TimetableListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class Timetable2Util {

    private final Context context;

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference().child("image").child("timetable");
    private final StorageReference storageRef = storage.getReference().child("timetable");

    public Timetable2Util(Context context) {
        this.context = context;
    }

    public void saveImage(Bitmap bitmap) {
        try {
            FileOutputStream out = context.openFileOutput("timetableImage.png", MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (Exception error) {
        }
    }

    public File loadImage() {
        return context.getFileStreamPath("timetableImage.png");
    }

    public void deleteImage(){
        loadImage().delete();
    }

    public void uploadImage(Bitmap bitmap, String school, String year, String grade, String semester) {
        try {
            String timetableTitle = school + " " + grade + " " + semester + "(" + year + ")";

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            StorageReference image = storageRef.child(school).child(year).child(grade).child(semester).child(timetableTitle + user.getUid() + ".png");
            UploadTask uploadTask = image.putBytes(out.toByteArray());

            uploadTask.addOnSuccessListener(taskSnapshot -> { DatabaseReference position = databaseReference.child(school).child(year).child(grade).child(semester);

                image.getDownloadUrl().addOnSuccessListener(uri -> {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("title", timetableTitle);
                    hashMap.put("content", String.valueOf(uri));
                    hashMap.put("people", user.getDisplayName());
                    hashMap.put("profile", String.valueOf(user.getPhotoUrl()));

                    position.child(user.getUid()).setValue(hashMap).addOnFailureListener(e -> {
                        Toast.makeText(context, "오류가 발생해 사진 업로드에 실패했습니다.", Toast.LENGTH_LONG).show();
                        image.delete();
                    });
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findTimetable(String school, String year, String grade, String semester, ListView listView, TimetableListAdapter adapter) {
        DatabaseReference position = databaseReference.child(school).child(year).child(grade).child(semester);

        position.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<HashMap<String, String>> t = new GenericTypeIndicator<HashMap<String, String>>() {
                    };
                    adapter.addItem(snapshot.getValue(t).get("title"), snapshot.getValue(t).get("content"), snapshot.getValue(t).get("people"), snapshot.getValue(t).get("profile"));
                }

                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
