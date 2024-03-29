package com.example.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.chattingapp.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

public class JoinActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 10;
    private EditText email;
    private EditText name;
    private EditText password;
    private Button join;
    private ImageView profile;
    private Uri imageUri;

    private String splash_background;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }

        profile = findViewById(R.id.joinActivity_imageView_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,PICK_FROM_ALBUM);
            }
        });

        email = findViewById(R.id.joinActivity_edittext_email);
        name = findViewById(R.id.joinActivity_edittext_name);
        password = findViewById(R.id.joinActivity_edittext_password);
        join = findViewById(R.id.joinActivity_button_join);
        join.setBackgroundColor(Color.parseColor(splash_background));

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (email.getText().toString() == null || password.getText().toString() == null
                || name.getText().toString() == null || imageUri == null) {
                    return;
                }

                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email.getText().toString(),
                                password.getText().toString())
                        .addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {

                                final String uid = task.getResult().getUser().getUid();
                                final StorageReference profileImageRef =
                                FirebaseStorage.getInstance().getReference().child("userImages").child(uid);
                                profileImageRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw task.getException();
                                        }
                                        return profileImageRef.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri downUri = task.getResult();
                                            String imageUri = downUri.toString();

                                            User user = new User();
                                            user.userName = name.getText().toString();
                                            user.profileImageUrl = imageUri;
                                            user.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                            FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                                                    .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Intent intent = new Intent(JoinActivity.this,LoginActivity.class);
                                                    startActivity(intent);
                                                    JoinActivity.this.finish();
                                                }
                                            });
                                        }
                                    }
                                });
//                                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
//                                        String imageUrl = task.getResult().getUploadSessionUri().toString();
//
//                                        User user = new User();
//                                        user.userName = name.getText().toString();
//                                        user.profileImageUrl = imageUrl;
//
//                                        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
//                                                .setValue(user);
//                                    }
//                                });
                            }
                        });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            profile.setImageURI(data.getData()); // 가운데 뷰를 바꿈
            imageUri = data.getData(); // 이미지 경로 원본
        }
    }
}