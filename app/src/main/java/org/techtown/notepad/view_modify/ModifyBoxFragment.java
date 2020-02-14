package org.techtown.notepad.view_modify;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.techtown.notepad.R;

public class ModifyBoxFragment extends Fragment {
    ModifyBoxFragment mFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_modify_box, container, false);
        mFragment = this;

        Button background_button = rootView.findViewById(R.id.background);
        background_button.setOnClickListener(new View.OnClickListener() {  // 배경 클릭하면 드롭박스 사라짐
            @Override
            public void onClick(View v) {
                ((view_modifyActivity)view_modifyActivity.mContext).manager.beginTransaction().hide(mFragment).commit();
            }
        });

        Button delete_button = rootView.findViewById(R.id.delete);
        delete_button.setOnClickListener(new View.OnClickListener() {  // 삭제버튼 클릭
            @Override
            public void onClick(View v) {
                ((view_modifyActivity)view_modifyActivity.mContext).manager.beginTransaction().show(((view_modifyActivity)view_modifyActivity.mContext).frg_deletebox).hide(mFragment).commit();
            }
        });

        Button modify_button = rootView.findViewById(R.id.modify);
        modify_button.setOnClickListener(new View.OnClickListener() {  // 수정버튼 클릭
            @Override
            public void onClick(View v) {
                ((view_modifyActivity)view_modifyActivity.mContext).manager.beginTransaction().show(((view_modifyActivity)view_modifyActivity.mContext).frg_modify)
                        .hide(mFragment).hide(((view_modifyActivity)view_modifyActivity.mContext).frg_view).commit();
            }
        });
        return rootView;
    }

}
