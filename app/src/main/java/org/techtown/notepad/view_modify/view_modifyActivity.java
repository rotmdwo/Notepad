package org.techtown.notepad.view_modify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import org.techtown.notepad.R;

public class view_modifyActivity extends AppCompatActivity {
    FragmentManager manager;
    ViewFragment frg_view;
    ModifyFragment frg_modify;
    ModifyBoxFragment frg_modifybox;
    DeleteBoxFragment frg_deletebox;
    BackBoxFragment2 frg_backbox2;
    SaveBoxFragment2 frg_savebox2;
    Context mainContext;

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
        frg_deletebox = new DeleteBoxFragment(mainContext);
        frg_backbox2 = new BackBoxFragment2(mainContext);
        frg_savebox2 = new SaveBoxFragment2(mainContext);

        manager.beginTransaction().add(R.id.frameLayout,frg_view).add(R.id.frameLayout,frg_modifybox).hide(frg_modifybox).add(R.id.frameLayout,frg_modify)
                .hide(frg_modify).add(R.id.frameLayout,frg_deletebox).hide(frg_deletebox).add(R.id.frameLayout,frg_backbox2).hide(frg_backbox2).add(R.id.frameLayout,frg_savebox2).hide(frg_savebox2).commit();

        Toast.makeText(getApplicationContext(),"첨부된 사진을 눌러 확대보기 할 수 있습니다.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_not_move, R.anim.anim_slide_out_right);
    }
}
