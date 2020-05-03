package org.techtown.notepad.view_modify;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.techtown.notepad.classes_for_methods.Array_sort;
import org.techtown.notepad.classes_for_methods.DataProcess;
import org.techtown.notepad.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class SaveBoxFragment2 extends Fragment {
    SaveBoxFragment2 mFragment;
    Boolean saveAlreadyClicked = false; // 저장버튼 두 번 눌러서 저장 2번 되는 걸 방지
    Context viewModifyContext;

    public SaveBoxFragment2(Context viewModifyContext) {
        this.viewModifyContext = viewModifyContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_save_box_fragment2, container, false);
        mFragment = this;

        Button cancel = rootView.findViewById(R.id.cancel);
        Button confirm = rootView.findViewById(R.id.confirm);
        cancel.setOnClickListener(new View.OnClickListener() {  // 저장 취소
            @Override
            public void onClick(View v) {
                ((view_modifyActivity) viewModifyContext).manager.beginTransaction().hide(mFragment).commit();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {  // 내용 저장
            @Override
            public void onClick(View v) {
                if (saveAlreadyClicked == false) { // 저장버튼 두 번 눌러서 저장 2번 되는 걸 방지
                    saveAlreadyClicked = true;
                    String title = ModifyFragment.mFragment.title.getText().toString();
                    String content = ModifyFragment.mFragment.content.getText().toString();

                    // 기존에 저장되어 있던 내용을 삭제
                    String name = deleteName();
                    DataProcess.deleteNote(name,getContext());

                    // 수정하는 시간을 Key값으로 새로운 내용을 저장
                    saveNote(title,content,(ModifyFragment.mFragment).pics, (ModifyFragment.mFragment).urls);
                    Toast.makeText(getActivity(),"저장 되었습니다.",Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        });

        return rootView;
    }

    private void saveNote(String title, String content, ArrayList<String> pics, ArrayList<String> urls) {
        // 현재 시간을 메모를 구분하는 이름으로 추가
        Set<String> names = DataProcess.restoreNames(getContext());
        Date time = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String current_time = format.format(time);
        names.add(current_time);

        // 노트 내용을 Set으로 담음
        Set<String> note = new HashSet<>();
        note.add("title_" + title);
        note.add("content_" + content);
        for (int i = 0 ; i < urls.size() ; i++) {  // 형식에 맞춘 URL들을 첨부해준다.  --> 형식: URLn_https:// ...
            note.add(urls.get(i));
        }
        for (int j = 0 ; j < pics.size() ; j++) {
            note.add(pics.get(j));
        }

        // Shared Prefrences 저장
        SharedPreferences prefNames = getActivity().getSharedPreferences("names", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editorNames = prefNames.edit();
        editorNames.clear();  // 클리어를 안 해주면 수정이 안 되는 문제 생김
        editorNames.putStringSet("names",names);
        editorNames.commit();

        SharedPreferences pref_note = getActivity().getSharedPreferences(current_time, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor_note = pref_note.edit();
        editor_note.putStringSet(current_time,note);
        editor_note.commit();
    }

    private String deleteName() {  // 노트 리스트에서 기존 작성했던 노트이름(마지막 수정시간)을 삭제
        Intent intent = getActivity().getIntent();
        String name = intent.getStringExtra("name");
        Set<String> allNoteNmaes = DataProcess.restoreNames(getContext());

        Array_sort array_sort = new Array_sort();
        String allNames_array[] = array_sort.setToArray(allNoteNmaes);

        allNoteNmaes.remove(name);
        DataProcess.saveNames(allNoteNmaes,getContext());
        return name;
    }
}