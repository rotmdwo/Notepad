package org.techtown.notepad.new_memo;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.techtown.notepad.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class SaveBoxFragment extends Fragment {
    SaveBoxFragment saveBoxFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_save_box, container, false);

        saveBoxFragment = this;

        Button cancel = rootView.findViewById(R.id.cancel);
        Button confirm = rootView.findViewById(R.id.confirm);
        cancel.setOnClickListener(new View.OnClickListener() {  // 저장 취소
            @Override
            public void onClick(View v) {
                ((NewMemoActivity) NewMemoActivity.mContext).manager.beginTransaction().hide(saveBoxFragment).commit();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {  // 내용 저장
            @Override
            public void onClick(View v) {
                String title = ((NewMemoFragment)NewMemoFragment.mFragment).title.getText().toString();
                String content = ((NewMemoFragment)NewMemoFragment.mFragment).content.getText().toString();

                saveNote(title,content,(NewMemoFragment.mFragment).pics, (NewMemoFragment.mFragment).URLs);
                Toast.makeText(getActivity(),"저장 되었습니다.",Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });

        return rootView;
    }

    private void saveNote(String title, String content, ArrayList<String>pics, ArrayList<String>URLs){
        // 현재 시간을 메모를 구분하는 이름으로 추가
        Set<String> names = restoreNames();
        Date time = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String current_time = format.format(time);
        names.add(current_time);

        // 노트 내용을 Set으로 담음
        Set<String> note = new HashSet<>();
        note.add("title_"+title);
        note.add("content_"+content);
        for(int i = 0 ; i < URLs.size() ; i++){  // 형식에 맞춘 URL들을 첨부해준다.  --> 형식: URLn_https:// ...
            note.add(URLs.get(i));
        }
        for(int j = 0; j < pics.size() ; j++){
            note.add(pics.get(j));
        }

        // Shared Prefrences 저장
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet("names",names);
        editor.putStringSet(current_time,note);
        editor.commit();
    }

    private Set<String> restoreNames(){  // 저장되어 있는 메모명(실제 타이틀이 아닌 마지막으로 수정된 시간으로 저장됨) 가져오기
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Set<String> defValues = new HashSet<>();
        return pref.getStringSet("names",defValues);
    }

}
