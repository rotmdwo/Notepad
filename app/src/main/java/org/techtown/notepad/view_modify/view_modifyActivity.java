package org.techtown.notepad.view_modify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.techtown.notepad.R;

public class view_modifyActivity extends AppCompatActivity {
    FragmentManager manager;
    ViewFragment frg_view;
    ModifyFragment frg_modify;

    public static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_modify);

        mContext = this;

        manager= getSupportFragmentManager();
        frg_view = new ViewFragment();
        frg_modify = new ModifyFragment();

        manager.beginTransaction().add(R.id.frameLayout,frg_view).add(R.id.frameLayout,frg_modify).hide(frg_modify).commit();
    }
}
