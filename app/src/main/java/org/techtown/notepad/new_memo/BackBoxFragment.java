package org.techtown.notepad.new_memo;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.techtown.notepad.R;


public class BackBoxFragment extends Fragment {
    private BackBoxFragment backBoxFragment;
    private Context newMemoContext;

    public BackBoxFragment(Context newMemoContext) {
        this.newMemoContext = newMemoContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_back_box, container, false);

        backBoxFragment = this;

        Button exit = rootView.findViewById(R.id.exit);
        Button keepWriting = rootView.findViewById(R.id.keep_writing);
        exit.setOnClickListener(new View.OnClickListener() {  // 정말로 나가기
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        keepWriting.setOnClickListener(new View.OnClickListener() {  // 나가지 않고 계속 쓰기
            @Override
            public void onClick(View v) {
                ((NewMemoActivity) newMemoContext)
                        .manager.beginTransaction()
                        .hide(backBoxFragment)
                        .commit();
            }
        });

        return rootView;
    }



}
