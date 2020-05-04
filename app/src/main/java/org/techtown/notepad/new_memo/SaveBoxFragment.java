package org.techtown.notepad.new_memo;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.techtown.notepad.classes_for_methods.DataProcess;
import org.techtown.notepad.R;


public class SaveBoxFragment extends Fragment {
    SaveBoxFragment saveBoxFragment;
    Boolean save_already_clicked = false; // 저장버튼 두 번 눌러서 저장 2번 되는 걸 방지
    Context newMemoContext;

    public SaveBoxFragment(Context newMemoContext) {
        this.newMemoContext = newMemoContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_save_box, container, false);

        saveBoxFragment = this;

        Button cancel = rootView.findViewById(R.id.cancel);
        final Button confirm = rootView.findViewById(R.id.confirm);
        cancel.setOnClickListener(new View.OnClickListener() {  // 저장 취소
            @Override
            public void onClick(View v) {
                ((NewMemoActivity) newMemoContext)
                        .manager.beginTransaction()
                        .hide(saveBoxFragment)
                        .commit();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {  // 내용 저장
            @Override
            public void onClick(View v) {
                if (save_already_clicked == false) { // 저장버튼 두 번 눌러서 저장 2번 되는 걸 방지
                    save_already_clicked = true;
                    String title = (NewMemoFragment.mFragment).title.getText().toString();
                    String content = (NewMemoFragment.mFragment).content.getText().toString();

                    DataProcess.saveNote(getContext(), title,content,(NewMemoFragment.mFragment).pics, (NewMemoFragment.mFragment).urls);
                    Toast.makeText(getActivity(),"저장 되었습니다.", Toast.LENGTH_SHORT).show();
                    getActivity().finish();

                }
            }
        });

        return rootView;
    }

}
