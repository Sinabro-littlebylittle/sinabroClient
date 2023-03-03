package com.project.sinabro.bottomSheet.place.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.project.sinabro.R;

import java.util.ArrayList;



public class MyBottomSheetDialog extends BottomSheetDialogFragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageButton back_ibtn;
    private ImageButton add_ibtn;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyBottomSheetDialog() {
        // Required empty public constructor
    }
    /**
     * 이부분은 북마크 추가 부분 코드 입니다.
     */

    // 리사이클러뷰에 필요한 변수 선언
    private ArrayList<Dictionary> mArrayList = new ArrayList<>();
    private BookmarkAdapter mAdapter;
    private int count = 0;                                  // n번째 즐겨찾기 이름을 부여할 count 변수
    private RecyclerView mRecyclerView;

    private int ColorPicker;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookmarkActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static MyBottomSheetDialog newInstance(String param1, String param2) {
        MyBottomSheetDialog fragment = new MyBottomSheetDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_bottom_sheet_bookmark, container, false);

        // 리사이클 뷰 생성
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerview_bookmark);     //리사이클 뷰에 넣어줄 뷰어 생성
        mRecyclerView.setHasFixedSize(true);                           // 크기 고정

        mAdapter = new BookmarkAdapter(getActivity(),mArrayList);                                                        // 설정한 어뎁터 연동 -> 어뎁터는 데이터 값을 맵핑하여 생성시 데이터를 입력하는 역활

        // !-- 중요- Fragment 클래스를 상속 받은 경우 this 사용이 어려워 getActivity()로 받기
        RecyclerView.LayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());


        //LinearLayoutManager  =
        mRecyclerView.setLayoutManager(mLinearLayoutManager);                                                // 레이어 설정
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);                                                                  // 어뎁터 맵핑핑



        // 뒤로가기 버튼 기능
        back_ibtn = (ImageButton) view.findViewById(R.id.back_ibtn);
        back_ibtn.setOnClickListener(new ViewGroup.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();            // 프레그먼트 메니져 호출
                fragmentManager.beginTransaction().remove(MyBottomSheetDialog.this).commit();           // 현재 바텀시트 종료
                fragmentManager.popBackStack();                                                         // 이전 화면 호출

            }
        });




        /*장소 추가*/
        add_ibtn = (ImageButton) view.findViewById(R.id.add_ibtn);
        add_ibtn.setOnClickListener(new ViewGroup.OnClickListener() {
            @Override
            public void onClick(View view) {

               /*
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.add_bookmark_place_activity,null,false);
                builder.setView(v);



                final Button ButtonSubmit = (Button) v.findViewById(R.id.addlistbtn);
                final EditText editTextNAME = (EditText) v.findViewById(R.id.ListName);
                final Button colorPicker_button = (Button) v.findViewById(R.id.colorPicker_button);



                final AlertDialog dialog = builder.create();
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {                                   // 변경
                        String strNAME = editTextNAME.getText().toString();

                        Dictionary dict = new Dictionary(strNAME);
                        mArrayList.add(0, dict); //첫 줄에 삽입

                        //mArrayList.add(dict); //마지막 줄에 삽입

                        mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영
                        dialog.dismiss();
                    }
                });
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = 1800;


                dialog.show();
                Window window = dialog.getWindow();
                window.setAttributes(lp);
                */


                //새로운 리스트와 관련된 추가
                final Intent intent = new Intent(getActivity(), AddBookmarkPlaceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
               // startActivityForResult(getActivity(),AddBookmarkPlaceActivity.class, 555);

            }


        });



        return view;
    }


}