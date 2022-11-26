package com.project.sinabro;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

// Bookmark 추가를 위한 라이브러리
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookmarkActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookmarkActivity extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageButton back_ibtn;
    private ImageButton add_ibtn;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BookmarkActivity() {
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


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookmarkActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static BookmarkActivity newInstance(String param1, String param2) {
        BookmarkActivity fragment = new BookmarkActivity();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_bookmark_activity, container, false);

        // 리사이클 뷰 생성
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerview_bookmark);     //리사이클 뷰에 넣어줄 뷰어 생성
        mRecyclerView.setHasFixedSize(true);                           // 크기 고정

        mAdapter = new BookmarkAdapter(mArrayList);                                                        // 설정한 어뎁터 연동 -> 어뎁터는 데이터 값을 맵핑하여 생성시 데이터를 입력하는 역활

        // !-- 중요- Fragment 클래스를 상속 받은 경우 this 사용이 어려워 getActivity()로 받기
        RecyclerView.LayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());


        //LinearLayoutManager  =
        mRecyclerView.setLayoutManager(mLinearLayoutManager);                                               // 레이어 설정
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);                                                                  // 어뎁터 맵핑핑



        // 뒤로가기 버튼 기능
        back_ibtn = (ImageButton) view.findViewById(R.id.back_ibtn);
        back_ibtn.setOnClickListener(new ViewGroup.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        /*객체 별로 떨어트리는 코드 */
       // DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        //mRecyclerView.addItemDecoration(dividerItemDecoration);

        /*장소 추가*/
        add_ibtn = (ImageButton) view.findViewById(R.id.add_ibtn);
        add_ibtn.setOnClickListener(new ViewGroup.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                Dictionary data = new Dictionary(count+"번째 즐겨찾기 목록입니다.");
                mArrayList.add(data); // RecyclerView의 마지막 줄에 삽입
                mAdapter.notifyDataSetChanged();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.favorite_edit_box,null,false);
                builder.setView(v);

                final Button ButtonSubmit = (Button) v.findViewById(R.id.button_bookmark_submit);
                final EditText editTextNAME = (EditText) v.findViewById(R.id.edittext_bookmark_name);
                //final EditText editTextEnglish = (EditText) view.findViewById(R.id.edittext_dialog_endlish);
                //final EditText editTextKorean = (EditText) view.findViewById(R.id.edittext_dialog_korean);

                ButtonSubmit.setText("삽입");


                final AlertDialog dialog = builder.create();
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {                                   // 변경
                        String strNAME = editTextNAME.getText().toString();
                        //String strEnglish = editTextEnglish.getText().toString();
                        //String strKorean = editTextKorean.getText().toString();

                        Dictionary dict = new Dictionary(strNAME);

                        mArrayList.add(0, dict); //첫 줄에 삽입
                        //mArrayList.add(dict); //마지막 줄에 삽입
                        mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영

                        dialog.dismiss();
                    }
                });

                dialog.show();
                Log.d("test", "onClick: 테스트중입니다.");
            }


        });

        return view;
    }

    private void preparDate(){
        mArrayList.add(new Dictionary("MY PLACE"));
        mArrayList.add(new Dictionary("Good"));
    }
}