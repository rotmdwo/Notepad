package org.techtown.notepad.view_modify;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.techtown.notepad.R;


public class BackBoxFragment2 extends Fragment {
    BackBoxFragment2 mFragment;
    Context mainContext;
    Context viewModifyContext;

    public BackBoxFragment2(Context mainContext, Context viewModifyContext) {
        this.mainContext = mainContext;
        this.viewModifyContext = viewModifyContext;
    }

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
                // 밖으로 나갈 시 수정중이던 내용 삭제하도록 수정.
                // 하지만 수정하는 fragment만 remove하고 재할당하면 뒤로가기와 저장하기 fragment가 동작하지 않는 문제가 있음
                // 이유는 fragment stack 아래로 뒤로가기와 저장하기 UI fragment가 깔리고, 새로 만든 수정 fragment가 위로 올라오기 때문
                // 이것을 해결하기 위해서는 UI fragment도 삭제하고 재할당
                ((view_modifyActivity) viewModifyContext).manager.beginTransaction().show(((view_modifyActivity) viewModifyContext).frg_view).hide(mFragment).remove(((view_modifyActivity) viewModifyContext).frg_modify).remove(((view_modifyActivity) viewModifyContext).frg_backbox2).remove(((view_modifyActivity) viewModifyContext).frg_savebox2).commit();
                ((view_modifyActivity) viewModifyContext).frg_modify = new ModifyFragment(viewModifyContext);
                ((view_modifyActivity) viewModifyContext).frg_backbox2 = new BackBoxFragment2(mainContext, viewModifyContext);
                ((view_modifyActivity) viewModifyContext).frg_savebox2 = new SaveBoxFragment2(mainContext, viewModifyContext);
                ((view_modifyActivity) viewModifyContext).manager.beginTransaction().add(R.id.frameLayout, ((view_modifyActivity) viewModifyContext).frg_modify).hide(((view_modifyActivity) viewModifyContext).frg_modify).add(R.id.frameLayout, ((view_modifyActivity) viewModifyContext).frg_backbox2).hide(((view_modifyActivity) viewModifyContext).frg_backbox2).add(R.id.frameLayout, ((view_modifyActivity) viewModifyContext).frg_savebox2).hide(((view_modifyActivity) viewModifyContext).frg_savebox2).commit();
            }
        });

        keep_writing.setOnClickListener(new View.OnClickListener() {  // 나가지 않고 계속 쓰기
            @Override
            public void onClick(View v) {
                ((view_modifyActivity) viewModifyContext).manager.beginTransaction().hide(mFragment).commit();
            }
        });


        return  rootView;
    }

}
