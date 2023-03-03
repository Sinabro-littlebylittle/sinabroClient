package com.project.sinabro.sideBarMenu.bookmark;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.project.sinabro.R;

import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddBookmarkPlaceActivity_setting extends AppCompatActivity {

    private ImageButton back_iBtn;
    private Button colorPicker_button;
    private AppCompatButton addlistbtn;
    private Dialog addbookmarklist_dialog;
    private TextView colortextview;
    private EditText ListName;
    private int Get_color;

    private RadioButton radioButton_open, radioButton_close;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bookmark_place_activity);

        // 취소 버튼
        back_iBtn = findViewById(R.id.back_iBtn);
        back_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_place_remove();
            }
        });

        Intent intent = new Intent();
        ListName = findViewById(R.id.ListName);     //



        addbookmarklist_dialog = new Dialog(AddBookmarkPlaceActivity_setting.this);     // dialog 초기화
        addbookmarklist_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);          // 타이틀 제거
        addbookmarklist_dialog.setContentView(R.layout.dialog_bookmarklist_remove);    // 커스텀 다이로그 레이아웃 연결
        addbookmarklist_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final Intent ColorIntent = getIntent();
        colortextview = findViewById(R.id.displayColor);

        /*컬러 버튼*/
        colorPicker_button = findViewById(R.id.colorPicker_button);
        colorPicker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ColorPicker colorPicker = new ColorPicker(AddBookmarkPlaceActivity_setting.this);
                ArrayList<String> colors = new ArrayList<>();
                //색상 추가는 colors.add("#xxxxxxxx");
                colors.add("#e8472e");
                colors.add("#ff8c8c");
                colors.add("#f77f23");
                colors.add("#e38436");
                colors.add("#f0e090");
                colors.add("#98b84d");
                colors.add("#a3d17b");
                colors.add("#6ecf69");
                colors.add("#6ec48c");
                colors.add("#5aa392");
                colors.add("#4b8bbf");
                colors.add("#6a88d4");
                colors.add("#736dc9");
                colors.add("#864fbd");
                colors.add("#d169ca");

                colorPicker
                        .setDefaultColorButton(Color.parseColor("#f84c44"))
                        .setColors(colors)
                        .setColumns(5)                      // 열의 크기 설정
                        .setRoundColorButton(true)          // true : 둥근 모양, false : 사각형
                        .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int color ) {                // 여기다가 데이터 전달하는 코드 삽입
                                Log.d("color", "test"+ color);// will be fired only when OK button was tapped
                                colortextview.setBackgroundTintList(ColorStateList.valueOf(color));     // 선택한 색상으로 변경
                                /*추후 데이터 연결시 여기다가 작성하시면 됩니다.*/
                                Get_color = color;
                                // 색깔을 넣어야겠네
                            }

                            @Override                                           // 취소시 어떻게 할지 정하는 코든데 취소하면 할게 없으니 그냥 빈칸으로 뒀습니다.
                            public void onCancel() {

                            }
                        }).show();

            }
        });
        /*확인 버튼*/
        addlistbtn = findViewById(R.id.addlistbtn);
        addlistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*여기다가 확인후 기능을 추가하시면 됩니다.*/
                // 엑티비티에서 엑티비티 데이터 전송
                intent.putExtra("listname",ListName.getText().toString() );
                intent.putExtra("ColorPicker", Get_color);

                // 프레그먼트로 데이터 전송
                Bundle bundle = new Bundle();
                bundle.putString("listname",ListName.getText().toString());
                bundle.putInt("ColorPicker",Get_color);
                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetDialogFragment();
                bottomSheetDialogFragment.setArguments(bundle);

                onBackPressed();                    // 뒤로가기 기능 수행
                finish();                           // 현재 액티비티 종료



            }
        });
        /*공개 비공개*/

        /*세부 내용*/
    }



    /*다이어로그 내용 */
    public void showDialog_place_remove() {
        addbookmarklist_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        addbookmarklist_dialog.getWindow().setDimAmount(0.35f);

        // 아니오 버튼
        Button noBtn = addbookmarklist_dialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addbookmarklist_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // 네 버튼
        addbookmarklist_dialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addbookmarklist_dialog.dismiss();   // 다이얼로그 닫기
                onBackPressed();                    // 뒤로가기 기능 수행
                finish();                           // 현재 액티비티 종료
            }
        });
    }
}
