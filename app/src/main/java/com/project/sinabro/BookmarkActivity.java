package com.project.sinabro;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private ArrayList<Dictionary> mArrayList;
    private BookmarkAdapter mAdapter;
    private int count = -1;
    AppCompatActivity app = new AppCompatActivity();
    ViewGroup viewGroup;

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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewGroup =(ViewGroup)inflater.inflate(R.layout.fragment_bookmark_activity, container, false);
        /**
         * view는 단일 객체를 표한하기 위한 것이고
         * viewGroup은 여러 view들을 묶어서 표현 view 와 viewgroup들을 표현할때 사용
         * */

        // 리사이클 뷰 생성
        RecyclerView mRecyclerView = (RecyclerView)viewGroup.findViewById(R.id.recyclerview_main_list);     //리사이클 뷰에 넣어줄 뷰어 생성
                                                                       // 크기 고정
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());                  // !-- 중요- Fragment 클래스를 상속 받은 경우 this 사용이 어려워 getActivity()로 받기
        mRecyclerView.setLayoutManager(mLinearLayoutManager);                                               // 레이어 설정
        mAdapter = new BookmarkAdapter( mArrayList);                                                        // 설정한 어뎁터 연동 -> 어뎁터는 데이터 값을 맵핑하여 생성시 데이터를 입력하는 역활

        mArrayList = new ArrayList<>();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);                                                                  // 어뎁터 맵핑핑

        // 뒤로가기 버튼 기능
        back_ibtn = (ImageButton) viewGroup.findViewById(R.id.back_ibtn);
        back_ibtn.setOnClickListener(new ViewGroup.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        /*객체 별로 떨어트리는 코드 */
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        /*장소 추가*/
        add_ibtn = (ImageButton) viewGroup.findViewById(R.id.add_ibtn);
        add_ibtn.setOnClickListener(new ViewGroup.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                Dictionary data = new Dictionary(count+"번째 즐겨찾기 목록입니다.");
                mArrayList.add(data); // RecyclerView의 마지막 줄에 삽입

                mAdapter.notifyDataSetChanged();
                Log.d("test", "onClick: 테스트중입니다.");
            }
        });

        return viewGroup;
    }
}