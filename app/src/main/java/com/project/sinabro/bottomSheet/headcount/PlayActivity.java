package com.project.sinabro.bottomSheet.headcount;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.sinabro.R;

public class PlayActivity extends AppCompatActivity {
    //인원수 확인 및 수정 (
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifiy_register_headcount);

        Intent intent = getIntent();
        int serverValue = intent.getIntExtra("인원수", 0);

        TextView textView = findViewById(R.id.textView);
        textView.setText("Server Value: " + serverValue);
    }

    //영상 재생
    /*private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.videoView);
        Intent secondIntent = getIntent();
        Toast.makeText(this, "저에요", Toast.LENGTH_SHORT).show();
        // 경로 설정
        String videoPath = secondIntent.getStringExtra("path");
        Toast.makeText(this, "저wj에요", Toast.LENGTH_SHORT).show();
        Log.d("여기를 보시와요.55", videoPath);

        Uri uri = Uri.parse(videoPath);
        Log.d("여기라구여65", String.valueOf(uri));

        videoView.setVideoURI(uri);

        // 미디어 컨트롤러 추가
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);

        // 비디오 재생 시작
        videoView.start();
    }

    public void onPlayButtonClick(android.view.View view) {
        if (videoView.isPlaying()) {
            videoView.pause();
            ((Button)view).setText("Play");
        } else {
            videoView.start();
            ((Button)view).setText("Pause");
        }
    }*/
}
