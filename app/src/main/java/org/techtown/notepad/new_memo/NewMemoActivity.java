package org.techtown.notepad.new_memo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.widget.Toast;

import org.techtown.notepad.R;

import java.util.ArrayList;

public class NewMemoActivity extends AppCompatActivity {
    FragmentManager manager;
    BackBoxFragment frg_backbox;
    SaveBoxFragment frg_savebox;
    NewMemoFragment frg_newmemo;
    String title;
    String content;
    ArrayList<String> pics = new ArrayList<>();
    ArrayList<String> urls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_memo);

        manager = getSupportFragmentManager();
        frg_newmemo = new NewMemoFragment(this);
        frg_backbox = new BackBoxFragment(this);
        frg_savebox = new SaveBoxFragment(this);
        manager.beginTransaction()
                .add(R.id.frameLayout, frg_newmemo)
                .add(R.id.frameLayout, frg_backbox)
                .hide(frg_backbox)
                .add(R.id.frameLayout, frg_savebox)
                .hide(frg_savebox)
                .commit();

        Toast.makeText(getApplicationContext(),
                "첨부된 사진을 꾹 눌러 첨부를 취소할 수 있습니다.",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_not_move, R.anim.anim_slide_out_right);
    }
}
