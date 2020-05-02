package org.techtown.notepad.view_modify;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.techtown.notepad.classes_for_methods.Array_sort;
import org.techtown.notepad.classes_for_methods.DataProcess;
import org.techtown.notepad.MainActivity;
import org.techtown.notepad.R;

import java.util.Set;

public class DeleteBoxFragment extends Fragment {
    DeleteBoxFragment mFragment;
    int location = 0;
    Context mainContext;

    public DeleteBoxFragment(Context mainContext) {
        this.mainContext = mainContext;
    }

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
                Set<String> allNoteNmaes = DataProcess.restoreNames(getContext());

                Array_sort array_sort = new Array_sort();
                String allNames_array[] = array_sort.setToArray(allNoteNmaes);

                for(int i=0;i<allNames_array.length;i++){
                    if(allNames_array[i].equals(name)){
                        location = i;
                    }
                }

                allNoteNmaes.remove(name);

                //SharedPreferences 삭제 및 갱신
                DataProcess.deleteNote(name,getContext());
                DataProcess.saveNames(allNoteNmaes,getContext());

                ((MainActivity)mainContext).adapter.removeListItem(allNoteNmaes.size()-location);
                ((MainActivity)mainContext).recyclerView.setAdapter(((MainActivity)mainContext).adapter);

                getActivity().finish();
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
}
