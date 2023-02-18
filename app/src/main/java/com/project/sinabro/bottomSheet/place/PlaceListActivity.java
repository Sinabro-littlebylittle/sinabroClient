package com.project.sinabro.bottomSheet.place;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.project.sinabro.R;

import java.util.ArrayList;

public class PlaceListActivity extends AppCompatActivity {

    private ImageButton back_iBtn, dialogClose_iBtn;
    private Button goAddPlace_btn, peopleScan_btn, editLocation_btn, bookmarkEmpty_btn;
    private ListView listview;
    private ListViewAdapter adapter;
    private Dialog placeInfo_dialog;
    private TextView roadNameAddress_tv, placeName_tv_inDialog, peopleCount_tv_inDialog, roadNameAddress_tv_inDialog, detailAddress_tv_inDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        // 뒤로가기 버튼 기능
        back_iBtn = findViewById(R.id.back_iBtn);
        back_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        roadNameAddress_tv = findViewById(R.id.roadNameAddress_tv);

        placeInfo_dialog = new Dialog(PlaceListActivity.this);  // Dialog 초기화
        placeInfo_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        placeInfo_dialog.setContentView(R.layout.dialog_place_info); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        placeInfo_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        goAddPlace_btn = findViewById(R.id.goAddPlace_btn);
        goAddPlace_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), AddLocationInfoActivity.class);
                startActivity(intent);
            }
        });

        listview = findViewById(R.id.placeList_listView);
        adapter = new ListViewAdapter();

        //Adapter 안에 아이템의 정보 담기
        adapter.addItem(new PlaceItem("충북대학교 중앙도서관", "3층", 5));
        adapter.addItem(new PlaceItem("양성재 학생생활관", "식당", 1));
        adapter.addItem(new PlaceItem("충북대학교가나다라마바사아자", "205호", 10));
        adapter.addItem(new PlaceItem("가나다라마바사 아자차카타하가", "205호", 100));
        adapter.addItem(new PlaceItem("가나다라마바사아자차카 타하가", "205호", -1));
        adapter.addItem(new PlaceItem("가나다라마바  사아자차카타하", "205호", -1));
        adapter.addItem(new PlaceItem("샘마루(SAMMaru)", "113호", -1));
        adapter.addItem(new PlaceItem("충북대학교 소프트웨어학부", "205호", -1));
        adapter.addItem(new PlaceItem("충북대학교 소프트웨어학부", "203호", -1));

        final Intent intent = getIntent();
        Boolean input_value = intent.getBooleanExtra("input_value", false);

        if (input_value) {
            String placeName_value = intent.getStringExtra("placeName_value");
            String detailAddress_value = intent.getStringExtra("detailAddress_value");

            adapter.addItem(new PlaceItem(placeName_value, detailAddress_value, -1));
        }

        // 리스트뷰에 Adapter 설정
        listview.setAdapter(adapter);
    }

    /* 리스트뷰 어댑터 */
    public class ListViewAdapter extends BaseAdapter {
        ArrayList<PlaceItem> items = new ArrayList<PlaceItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(PlaceItem item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final PlaceItem placeItem = items.get(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.place_list_view_item, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            TextView placeName_tv = convertView.findViewById(R.id.placeName_tv);
            TextView placeInfo_tv = convertView.findViewById(R.id.placeInfo_tv);
            TextView peopleCount_tv = convertView.findViewById(R.id.peopleCount_tv);

            placeName_tv.setText(placeItem.getPlaceName());
            placeInfo_tv.setText(placeItem.getDetailAddress());
            if (placeItem.getPeopleCnt() > 0) {
                peopleCount_tv.setText("" + placeItem.getPeopleCnt());
            } else {
                peopleCount_tv.setText("?");
            }

            //각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog_placeInfo(placeItem);
                }
            });

            return convertView;  //뷰 객체 반환
        }
    }

    // (dialog_place_remove) 다이얼로그를 디자인하는 함수
    public void showDialog_placeInfo(PlaceItem placeItem) {
        placeInfo_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        placeInfo_dialog.getWindow().setDimAmount(0.35f);

        placeName_tv_inDialog = placeInfo_dialog.findViewById(R.id.placeName_tv_inDialog);
        peopleCount_tv_inDialog = placeInfo_dialog.findViewById(R.id.peopleCount_tv_inDialog);
        roadNameAddress_tv_inDialog = placeInfo_dialog.findViewById(R.id.roadNameAddress_tv_inDialog);
        detailAddress_tv_inDialog = placeInfo_dialog.findViewById(R.id.detailAddress_tv_inDialog);

        placeName_tv_inDialog.setText(placeItem.getPlaceName());
        if (placeItem.getPeopleCnt() > 0) {
            peopleCount_tv_inDialog.setText("" + placeItem.getPeopleCnt());
        } else {
            peopleCount_tv_inDialog.setText("?");
        }
        roadNameAddress_tv_inDialog.setText(roadNameAddress_tv.getText().toString());
        detailAddress_tv_inDialog.setText(placeItem.getDetailAddress());

        // Dialog 닫기 버튼
        dialogClose_iBtn = placeInfo_dialog.findViewById(R.id.dialogClose_iBtn);
        dialogClose_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeInfo_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // 카메라 스캔 버튼
        peopleScan_btn = placeInfo_dialog.findViewById(R.id.peopleScan_btn);
        peopleScan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                placeInfo_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // 장소 수정 버튼
        editLocation_btn = placeInfo_dialog.findViewById(R.id.editLocation_btn);
        editLocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeInfo_dialog.dismiss(); // 다이얼로그 닫기
                final Intent intent = new Intent(getApplicationContext(), AddLocationInfoActivity.class);

                intent.putExtra("modify_clicked", true);
                intent.putExtra("placeName_value", placeName_tv_inDialog.getText().toString());
                intent.putExtra("detailAddress_value", detailAddress_tv_inDialog.getText().toString());

                startActivity(intent);
            }
        });

        // 즐겨찾기 버튼
        bookmarkEmpty_btn = placeInfo_dialog.findViewById(R.id.bookmarkEmpty_btn);
        bookmarkEmpty_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                placeInfo_dialog.dismiss(); // 다이얼로그 닫기
            }
        });
    }
}