package org.techtown.notepad.view_modify;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.techtown.notepad.MainActivity;
import org.techtown.notepad.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class ViewFragment extends Fragment {
    EditText title_view, content_view;
    String name,title,content;
    ArrayList<String> pics = new ArrayList<>();
    ArrayList<String> URLs = new ArrayList<>();
    LinearLayout image_preview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_view, container, false);

        ImageButton back = rootView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() { // 뒤로가기 눌렀을 때
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        ImageButton modify_delete = rootView.findViewById(R.id.modify_delete);
        modify_delete.setOnClickListener(new View.OnClickListener() { // 우측상단 ... 버튼 눌렀을 때
            @Override
            public void onClick(View v) {

            }
        });

        Intent intent = getActivity().getIntent();
        title_view = rootView.findViewById(R.id.title);
        content_view = rootView.findViewById(R.id.content);
        title_view.setText(intent.getStringExtra("title"));
        content_view.setText(intent.getStringExtra("content"));
        name = intent.getStringExtra("name");  // 각 노트를 구별하는 시간 이름

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

        ArrayList<String> allPics_array = new ArrayList<>();  // 모든 사진을 String으로 담을 어레이 리스트
        int num_of_all_pics = pics_array.length + URLs_array.length; // 전체 사진 수
        int pic_found = 0, url_found = 0; // 현재까지 탐색된 로컬사진, url 사진 개수
        for(int k = 1; k <= num_of_all_pics ; k++){
            if (pic_found < pics_array.length && pics_array[pic_found].substring(3,pics_array[pic_found].indexOf('_')).equals(Integer.toString(k))){
                allPics_array.add(pics_array[pic_found]);
                pic_found++;
            } else if(url_found < URLs_array.length && URLs_array[url_found].substring(3,URLs_array[url_found].indexOf('_')).equals(Integer.toString(k))){
                allPics_array.add(URLs_array[url_found]);
                url_found++;
            }
        }

        image_preview = rootView.findViewById(R.id.image_preview);
        for(String s : allPics_array){
            // 이미지뷰 생성
            ImageView imageView = new ImageView(getContext());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width,height);  // 가로,세로 100dp
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            // 이미지 String 형식에서 picn_ 또는 URLn_ 부분을 제거
            String string_image = s.substring(s.indexOf('_')+1);

            if(s.substring(0,3).equals("pic")){ // 로컬 사진이면
                // String to Byte 이미지 변환
                byte[] bytes = Base64.decode(string_image,Base64.DEFAULT);
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                // 미리보기에 추가
                imageView.setImageBitmap(image);
                image_preview.addView(imageView);

            } else{  // URL 사진이면
                RequestOptions options = new RequestOptions().error(R.drawable.wrongurl);
                Glide.with(getContext()).load(string_image).apply(options).into(imageView);
                image_preview.addView(imageView);
            }
        }

        return rootView;
    }

    private Set<String> restoreNote(String name){  // 노트 불러오기
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Set<String> defValues = new HashSet<>();
        return pref.getStringSet(name,defValues);
    }
}
