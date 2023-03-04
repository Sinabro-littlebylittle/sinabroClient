package com.project.sinabro.sideBarMenu.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.project.sinabro.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton back_iBtn;

    private RelativeLayout modify_my_info_relativeLayout,
            setting_notifications_relativeLayout, our_policies_relativeLayout,
            withdrawal_relativeLayout;


    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings_activity, container, false);

        /** 뒤로가기 버튼 기능 */
        back_iBtn = (ImageButton) view.findViewById(R.id.back_iBtn);
        back_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        modify_my_info_relativeLayout = (RelativeLayout) view.findViewById(R.id.modify_my_info_relativeLayout);
        modify_my_info_relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // "회원정보확인" 액티비티로 이동
                // fragment이기 때문에 activity intent와는 다른 방식
                Intent intent = new Intent(getActivity(), CheckPasswordActivity.class);
                intent.putExtra("destActivityName", "MyPageActivity");
                startActivity(intent);
            }
        });

        setting_notifications_relativeLayout = (RelativeLayout) view.findViewById(R.id.setting_notifications_relativeLayout);
        setting_notifications_relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // "알림 설정" 액티비티로 이동
                // fragment이기 때문에 activity intent와는 다른 방식
                Intent intent = new Intent(getActivity(), SettingNotificationsActivity.class);
                startActivity(intent);
            }
        });

        our_policies_relativeLayout = (RelativeLayout) view.findViewById(R.id.our_policies_relativeLayout);
        our_policies_relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // "약관 및 개인정보 처리 동의" 액티비티로 이동
                // fragment이기 때문에 activity intent와는 다른 방식
                Intent intent = new Intent(getActivity(), OurPoliciesActivity.class);
                startActivity(intent);
            }
        });

        withdrawal_relativeLayout = (RelativeLayout) view.findViewById(R.id.withdrawal_relativeLayout);
        withdrawal_relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // "회원정보확인" 액티비티로 이동
                // fragment이기 때문에 activity intent와는 다른 방식
                Intent intent = new Intent(getActivity(), CheckPasswordActivity.class);
                intent.putExtra("destActivityName", "WithdrawalStep1Activity");
                startActivity(intent);
            }
        });

        return view;
    }
}