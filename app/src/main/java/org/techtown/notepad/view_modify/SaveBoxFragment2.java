package org.techtown.notepad.view_modify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.techtown.notepad.classes_for_methods.DataProcess;
import org.techtown.notepad.R;

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
                    DataProcess.saveNote(getContext(), title, content, (ModifyFragment.mFragment).pics, (ModifyFragment.mFragment).urls);
                    Toast.makeText(getActivity(), "저장 되었습니다.", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        });

        return rootView;
    }

    private String deleteName() {  // 노트 리스트에서 기존 작성했던 노트이름(마지막 수정시간)을 삭제
        Intent intent = getActivity().getIntent();
        String name = intent.getStringExtra("name");
        Set<String> allNoteNmaes = DataProcess.restoreNames(getContext());

        allNoteNmaes.remove(name);
        DataProcess.saveNames(allNoteNmaes, getContext());
        return name;
    }
}