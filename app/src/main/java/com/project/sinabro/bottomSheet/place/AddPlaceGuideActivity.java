package com.project.sinabro.bottomSheet.place;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.project.sinabro.R;

public class AddPlaceGuideActivity extends AppCompatActivity {

    private ImageButton back_iBtn;
    private Button goAddPlace_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place_guide);

        // 뒤로가기 버튼 기능
        back_iBtn = findViewById(R.id.back_iBtn);
        back_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        goAddPlace_btn = findViewById(R.id.goAddPlace_btn);
        goAddPlace_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent (getApplicationContext(), AddLocationInfoActivity.class);
                startActivity(intent);
            }
        });
    }
}