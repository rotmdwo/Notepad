package org.techtown.notepad.view_modify;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.techtown.notepad.R;
import org.techtown.notepad.new_memo.BackBoxFragment;
import org.techtown.notepad.new_memo.NewMemoActivity;


public class BackBoxFragment2 extends Fragment {
    BackBoxFragment2 mFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_back_box_fragment2, container, false);
        mFragment = this;

        Button exit = rootView.findViewById(R.id.exit);
        Button keep_writing = rootView.findViewById(R.id.keep_writing);
        exit.setOnClickListener(new View.OnClickListener() {  // 정말로 나가기
            @Override
            public void onClick(View v) {
                ((view_modifyActivity) view_modifyActivity.mContext).manager.beginTransaction().show(((view_modifyActivity) view_modifyActivity.mContext).frg_view).hide(mFragment).hide(((view_modifyActivity) view_modifyActivity.mContext).frg_modify).commit();
            }
        });

        keep_writing.setOnClickListener(new View.OnClickListener() {  // 나가지 않고 계속 쓰기
            @Override
            public void onClick(View v) {
                ((view_modifyActivity) view_modifyActivity.mContext).manager.beginTransaction().hide(mFragment).commit();
            }
        });


        return  rootView;
    }

}
