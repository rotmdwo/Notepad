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
    ModifyBoxFragment frg_modifybox;
    DeleteBoxFragment frg_deletebox;

    public static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_modify);

        mContext = this;

        manager= getSupportFragmentManager();
        frg_view = new ViewFragment();
        frg_modify = new ModifyFragment();
        frg_modifybox = new ModifyBoxFragment();
        frg_deletebox = new DeleteBoxFragment();

        manager.beginTransaction().add(R.id.frameLayout,frg_view).add(R.id.frameLayout,frg_modifybox).hide(frg_modifybox).add(R.id.frameLayout,frg_modify)
                .hide(frg_modify).add(R.id.frameLayout,frg_deletebox).hide(frg_deletebox).commit();
    }
}
