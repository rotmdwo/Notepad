package org.techtown.notepad.new_memo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import org.techtown.notepad.R;

public class NewMemoActivity extends AppCompatActivity {
    EditText editText, editText2;
    FragmentManager manager;
    BackBoxFragment frg_backbox;
    SaveBoxFragment frg_savebox;
    NewMemoFragment frg_newmemo;
    public static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_memo);
        mContext = this;

        editText = findViewById(R.id.title);
        editText2 = findViewById(R.id.content);

        manager= getSupportFragmentManager();
        frg_newmemo = new NewMemoFragment();
        frg_backbox = new BackBoxFragment();
        frg_savebox = new SaveBoxFragment();
        manager.beginTransaction().add(R.id.frameLayout,frg_newmemo).add(R.id.frameLayout,frg_backbox).hide(frg_backbox).add(R.id.frameLayout,frg_savebox).hide(frg_savebox).commit();
    }
}
