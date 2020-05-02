package org.techtown.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageButton;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.techtown.notepad.classes_for_methods.Array_sort;
import org.techtown.notepad.classes_for_methods.DataProcess;
import org.techtown.notepad.list.list_item;
import org.techtown.notepad.list.list_item_adapter;
import org.techtown.notepad.new_memo.NewMemoActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener{ // 라이브러리: https://github.com/pedroSG94/AutoPermissions/tree/master/app/src/main/java/com/pedro/autopermissions
    public RecyclerView recyclerView;
    public list_item_adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoPermissions.Companion.loadAllPermissions(this,102); // 자동권한요구 라이브러리: https://github.com/pedroSG94/AutoPermissions/tree/master/app/src/main/java/com/pedro/autopermissions

        // 어댑터와 리사이클러뷰 설정
        adapter = new list_item_adapter(this);
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
                overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_not_move);
            }
        });

        // 최근 저장한 노트를 가장 위로 올라오게 하기 위해 Set -> Array로 만든 후 시간순으로 정렬 후 array의 뒤에서부터 가져온다.
        Set<String> allNames = DataProcess.restoreNames(this);
        Array_sort array_sort = new Array_sort();
        String allNames_array[] = array_sort.setToArray(allNames);

        for(int l = allNames_array.length - 1; l >= 0 ; l--){
            String title = null;
            String content = null;
            ArrayList<String> pics = new ArrayList<>();
            ArrayList<String> URLs = new ArrayList<>();

            String name = allNames_array[l];
            Set<String> note = DataProcess.restoreNote(name,getApplicationContext());
            Iterator<String> iterator_note = note.iterator();
            while(iterator_note.hasNext()){
                String temp = iterator_note.next();
                if(temp.substring(0,5).equals("title")){
                    title = temp.substring(6);
                } else if(temp.substring(0,7).equals("content")){
                    content = temp.substring(8);
                } else if(temp.substring(0,3).equals("pic")){
                    pics.add(temp);
                } else if(temp.substring(0,3).equals("URL")){
                    URLs.add(temp);
                }
            }

            String pics_array[] = array_sort.arrayListToArrayForPic(pics);
            String URLs_array[] = array_sort.arrayListToArrayForPic(URLs);

            if(URLs_array.length != 0 && URLs_array[0].substring(3,5).equals("1_")){  // URL 이미지가 1번일 때
                int _location = URLs_array[0].indexOf('_');
                adapter.addListItem(new list_item(true,URLs_array[0].substring(_location+1),title,content,name));
            } else if(pics_array.length != 0 && pics_array[0].substring(3,5).equals("1_")){ // 로컬 이미지가 1번일 때
                int _location = pics_array[0].indexOf('_');
                adapter.addListItem(new list_item(false,pics_array[0].substring(_location+1),title,content,name));
            } else if(URLs_array.length == 0 && pics_array.length == 0){  // 첨부된 이미지가 없을 때
                adapter.addListItem(new list_item(false,"NO",title,content,name));
            }

            recyclerView.setAdapter(adapter);
        }
    }

    // 권한요구 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this,requestCode,permissions,this); // 라이브러리: https://github.com/pedroSG94/AutoPermissions/tree/master/app/src/main/java/com/pedro/autopermissions
    }
    @Override
    public void onDenied(int i, String[] strings) {
    }

    @Override
    public void onGranted(int i, String[] strings) {

    }
}
