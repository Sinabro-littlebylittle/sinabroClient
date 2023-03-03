package com.project.sinabro.sideBarMenu.devInfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.project.sinabro.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DevInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DevInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageButton back_ibtn;

    private RoundedImageView projectOpenSourceSinabroClientRoundedImageView;

    private RelativeLayout project_opensource_detail_relativeLayout;

    private TextView opensource_license_tv;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DevInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutUsActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static DevInfoFragment newInstance(String param1, String param2) {
        DevInfoFragment fragment = new DevInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_dev_info_activity, container, false);

        /** 뒤로가기 버튼 기능 */
        back_ibtn = (ImageButton) view.findViewById(R.id.back_iBtn);
        back_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        /** sinabroClient repo 정보 이미지 불러오기 */
        projectOpenSourceSinabroClientRoundedImageView = view.findViewById(R.id.project_opensource_sinabroClient_roundedImageView);
        Glide.with(view).load(getResources().getString(R.string.sinabro_client_repo_url)).into(projectOpenSourceSinabroClientRoundedImageView);

        /** sinabroClient 저장소 이미지 클릭 시 시나브로 저장소 목록 인터넷 페이지 로드 */
        projectOpenSourceSinabroClientRoundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.sinabro_repos)));
                startActivity(browserIntent);
            }
        });

        /** (자세히 보기) 클릭 시 시나브로 organization overview 인터넷 페이지 로드 */
        project_opensource_detail_relativeLayout = (RelativeLayout) view.findViewById(R.id.project_opensource_detail_relativeLayout);
        project_opensource_detail_relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.sinabro_overview)));
                startActivity(browserIntent);
            }
        });

        /** (오픈소스 라이선스) TextView 클릭 시 해당 액티비티로 이동 */
        opensource_license_tv = (TextView) view.findViewById(R.id.opensource_license_tv);
        opensource_license_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // "오픈소스 라이선스" 액티비티로 이동
                // fragment이기 때문에 activity intent와는 다른 방식
                Intent intent = new Intent(getActivity(), OpenSourceLicense.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        return view;
    }
}