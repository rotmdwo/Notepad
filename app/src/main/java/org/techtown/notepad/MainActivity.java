package org.techtown.notepad;

/*
 * 사용된 라이브러리:
 * https://github.com/pedroSG94/AutoPermissions/tree/master/app/src/main/java/com/pedro/autopermissions
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.techtown.notepad.classes_for_methods.ArraySort;
import org.techtown.notepad.classes_for_methods.DataProcess;
import org.techtown.notepad.list.ListItem;
import org.techtown.notepad.list.ListItemAdapter;
import org.techtown.notepad.new_memo.NewMemoActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {
    public RecyclerView recyclerView;
    public ListItemAdapter adapter;

    final int AUTO_PERMISSION = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoPermissions.Companion.loadAllPermissions(this, AUTO_PERMISSION);

        // 어댑터와 리사이클러뷰 설정
        adapter = new ListItemAdapter(this);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        manager.setItemPrefetchEnabled(true);  // 리사이클러뷰 정보 미리 불러오기
        recyclerView.setLayoutManager(manager);

        // 새 메모 작성하기 버튼
        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewMemoActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_not_move);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }

    // 권한요구 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this,requestCode,permissions,this);
    }
    @Override
    public void onDenied(int i, String[] strings) {
    }

    @Override
    public void onGranted(int i, String[] strings) {

    }

    public void loadList(){
        // 기존에 있던 리사이클러뷰 아이템 제거
        while (adapter.getItemCount() >= 1) {
            adapter.removeListItem(0);
        }
        adapter.notifyDataSetChanged();

        // 최근 저장한 노트를 가장 위로 올라오게 하기 위해 Set -> Array로 만든 후 시간순으로 정렬 후 array의 뒤에서부터 가져온다.
        Set<String> allNames = DataProcess.restoreNames(this);
        String allNamesArray[] = ArraySort.setToArray(allNames);

        for (int l = allNamesArray.length - 1 ; l >= 0 ; l--) {
            String title = null;
            String content = null;
            ArrayList<String> pics = new ArrayList<>();
            ArrayList<String> urls = new ArrayList<>();

            String name = allNamesArray[l];
            Set<String> note = DataProcess.restoreNote(name, getApplicationContext());
            Iterator<String> iterator_note = note.iterator();
            while (iterator_note.hasNext()) {
                String temp = iterator_note.next();
                if (temp.substring(0,5).equals("title")) {
                    title = temp.substring(6);
                } else if (temp.substring(0,7).equals("content")) {
                    content = temp.substring(8);
                } else if (temp.substring(0,3).equals("pic")) {
                    pics.add(temp);
                } else if (temp.substring(0,3).equals("URL")) {
                    urls.add(temp);
                }
            }

            String picsArray[] = ArraySort.arrayListToArrayForPic(pics);
            String urlsArray[] = ArraySort.arrayListToArrayForPic(urls);

            if (urlsArray.length != 0 && urlsArray[0].substring(3, 5).equals("1_")) {  // URL 이미지가 1번일 때
                int location_ = urlsArray[0].indexOf('_');
                adapter.addListItem(new ListItem(true,urlsArray[0].substring(location_ + 1), title, content, name));
            } else if (picsArray.length != 0 && picsArray[0].substring(3, 5).equals("1_")) { // 로컬 이미지가 1번일 때
                int _location = picsArray[0].indexOf('_');
                adapter.addListItem(new ListItem(false, picsArray[0].substring(_location+1), title, content, name));
            } else if (urlsArray.length == 0 && picsArray.length == 0) {  // 첨부된 이미지가 없을 때
                adapter.addListItem(new ListItem(false, "NO", title, content, name));
            }

            recyclerView.setAdapter(adapter);
        }
    }
}
