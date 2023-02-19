package com.project.sinabro.sideBarMenu.settings;


import android.os.Bundle;
import android.view.View;

import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.project.sinabro.R;


public class NotificationActivity extends AppCompatActivity {

    private ImageButton back_iBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_notification);

        // 뒤로가기 버튼 기능
        back_iBtn = findViewById(R.id.back_iBtn);
        back_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });



    }
}


