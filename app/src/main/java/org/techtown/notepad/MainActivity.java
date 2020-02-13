package org.techtown.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.techtown.notepad.list.list_item;
import org.techtown.notepad.list.list_item_adapter;
import org.techtown.notepad.new_memo.NewMemoActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener{
    public static Context mContext;
    RecyclerView recyclerView;
    list_item_adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this; // 어댑터에서 이 액티비티 사용하기 위해 컨텍스트 저장

        AutoPermissions.Companion.loadAllPermissions(this,102); // 자동권한요구

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
            }
        });

        // 최근 저장한 노트를 가장 위로 올라오게 하기 위해 Set -> Array로 만든 후 시간순으로 정렬 후 array의 뒤에서부터 가져온다.
        Set<String> allNames = restoreNames();
        String allNames_array[] = new String[allNames.size()];
        int k = 0;
        for(String s : allNames){
            allNames_array[k++] = s;
        }
        java.util.Arrays.sort(allNames_array);

        for(int l = allNames_array.length - 1; l >= 0 ; l--){
            String title = null;
            String content = null;
            ArrayList<String> pics = new ArrayList<>();
            ArrayList<String> URLs = new ArrayList<>();

            String name = allNames_array[l];
            Set<String> note = restoreNote(name);
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

            // 사진의 순서를 정렬하기 위해 Set -> Array 형태로 변환
            String pics_array[] = new String[pics.size()];
            int i = 0;
            for(String s : pics){
                pics_array[i++] = s;
            }
            java.util.Arrays.sort(pics_array);
            String URLs_array[] = new String [URLs.size()];
            int j = 0;
            for(String s : URLs){
                URLs_array[j++] = s;
            }
            java.util.Arrays.sort(URLs_array);

            if(URLs_array.length != 0 && URLs_array[0].substring(3,5).equals("1_")){  // URL 이미지가 1번일 때
                int _location = URLs_array[0].indexOf('_');
                adapter.addListItem(new list_item(true,URLs_array[0].substring(_location+1),title,content));
            } else if(pics_array.length != 0 && pics_array[0].substring(3,5).equals("1_")){ // 로컬 이미지가 1번일 때
                int _location = pics_array[0].indexOf('_');
                adapter.addListItem(new list_item(false,pics_array[0].substring(_location+1),title,content));
            } else if(URLs_array.length == 0 && pics_array.length == 0){  // 첨부된 이미지가 없을 때
                adapter.addListItem(new list_item(false,"NO",title,content));
            }

            recyclerView.setAdapter(adapter);
        }
    }

    private Set<String> restoreNames(){  // 저장되어 있는 메모명(실제 타이틀이 아닌 마지막으로 수정된 시간으로 저장됨) 가져오기
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Set<String> defValues = new HashSet<>();
        return pref.getStringSet("names",defValues);
    }

    private Set<String> restoreNote(String name){  // 노트 불러오기
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Set<String> defValues = new HashSet<>();
        return pref.getStringSet(name,defValues);
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
}
