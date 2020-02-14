package org.techtown.notepad.view_modify;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.techtown.notepad.MainActivity;
import org.techtown.notepad.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DeleteBoxFragment extends Fragment {
    DeleteBoxFragment mFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_delete_box, container, false);
        mFragment = this;

        Button delete = rootView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {  // 삭제 버튼
            @Override
            public void onClick(View v) {
                Intent intent = getActivity().getIntent();
                String name = intent.getStringExtra("name");
                Set<String> allNoteNmaes = restoreNames();
                allNoteNmaes.remove(name);

                //SharedPreferences 삭제 및 갱신
                deleteNote(name);
                saveNames(allNoteNmaes);

                // 리스트 화면으로 돌아감
                Intent intent2 = new Intent(getContext(), MainActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
            }
        });

        Button cancel = rootView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() { // 취소 버튼
            @Override
            public void onClick(View v) {
                ((view_modifyActivity)view_modifyActivity.mContext).manager.beginTransaction().hide(mFragment).commit();
            }
        });
        return rootView;
    }

    protected void deleteNote(String name) {  // 저장되어 있는 노트 삭제
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(name);
        editor.commit();
    }

    private Set<String> restoreNames(){  // 저장되어 있는 메모명(실제 타이틀이 아닌 마지막으로 수정된 시간으로 저장됨) 가져오기
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Set<String> defValues = new HashSet<>();
        return pref.getStringSet("names",defValues);
    }

    private void saveNames(Set<String> names){
        // Shared Prefrences 저장
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet("names",names);
        editor.commit();
    }
}
