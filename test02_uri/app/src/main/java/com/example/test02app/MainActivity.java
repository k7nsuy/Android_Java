package com.example.test02app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //onCreate => activity객체를 생성.
        setContentView(R.layout.activity_main);
    }

    public void btnOnClicked(View view) {
        Intent intent = null;
//        Intent => 현재앱에서 다른 앱으로 넘어 갈 때 정보를 싣는 명령어
        switch (view.getId()) {

            case R.id.button1:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.naver.com"));
                startActivity(intent);
                break;
            case R.id.button2:
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("tel:010-0000-0000"));
                startActivity(intent);
                break;
        }
    }
}