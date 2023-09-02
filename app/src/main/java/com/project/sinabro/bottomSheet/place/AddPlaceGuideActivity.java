package com.project.sinabro.bottomSheet.place;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.project.sinabro.R;
import com.project.sinabro.models.Place;

public class AddPlaceGuideActivity extends AppCompatActivity {

    private ImageButton back_iBtn;
    private Button goAddPlace_btn;

    private Intent intent;

    private String markerId, placeId, address, placeName, detailAddress;
    private Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place_guide);

        final Intent intent = getIntent();
        markerId = intent.getStringExtra("markerId_value");
        placeId = intent.getStringExtra("placeId_value");
        address = intent.getStringExtra("address_value");
        latitude = intent.getDoubleExtra("latitude_value", 0);
        longitude = intent.getDoubleExtra("longitude_value", 0);
        placeName = intent.getStringExtra("placeName_value");
        detailAddress = intent.getStringExtra("detailAddress_value");

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
                intent.putExtra("markerId_value", markerId);
                intent.putExtra("latitude_value", latitude);
                intent.putExtra("longitude_value", longitude);
                intent.putExtra("placeName_value", placeName);
                intent.putExtra("detailAddress_value", detailAddress);
                intent.putExtra("placeId_value", placeId);
                intent.putExtra("markerId_value", markerId);
                intent.putExtra("address_value", address);
                startActivity(intent);
            }
        });
    }
}