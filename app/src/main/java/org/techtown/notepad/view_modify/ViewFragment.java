package org.techtown.notepad.view_modify;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.techtown.notepad.classes_for_methods.ArraySort;
import org.techtown.notepad.classes_for_methods.DataProcess;
import org.techtown.notepad.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewFragment extends Fragment {
    private EditText titleView, contentView;
    private String name,title,content;
    private ArrayList<String> pics = new ArrayList<>();
    private ArrayList<String> urls = new ArrayList<>();
    private LinearLayout imagePreview;
    private ImageView bigPreview;
    private Context viewModifyContext;

    public ViewFragment(Context viewModifyContext) {
        this.viewModifyContext = viewModifyContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_view, container, false);

        bigPreview = rootView.findViewById(R.id.big_preview);

        final Button close_btn = rootView.findViewById(R.id.close);
        close_btn.setOnClickListener(new View.OnClickListener() { // 큰 사진보기를 닫는 버튼
            @Override
            public void onClick(View v) {
                bigPreview.setVisibility(View.INVISIBLE);
                close_btn.setVisibility(View.INVISIBLE);
            }
        });

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
                ((view_modifyActivity) viewModifyContext).manager.beginTransaction().show(((view_modifyActivity) viewModifyContext).frg_modifybox).commit();
            }
        });

        Intent intent = getActivity().getIntent();
        titleView = rootView.findViewById(R.id.title);
        contentView = rootView.findViewById(R.id.content);
        titleView.setText(intent.getStringExtra("title"));
        contentView.setText(intent.getStringExtra("content"));
        name = intent.getStringExtra("name");  // 각 노트를 구별하는 시간 이름

        Set<String> note = DataProcess.restoreNote(name,getContext());
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

        String pics_array[] = ArraySort.arrayListToArrayForPic(pics);
        String URLs_array[] = ArraySort.arrayListToArrayForPic(urls);


        ArrayList<String> allPics_array = new ArrayList<>();  // 모든 사진을 String으로 담을 어레이 리스트
        int num_of_all_pics = pics_array.length + URLs_array.length; // 전체 사진 수
        int pic_found = 0, url_found = 0; // 현재까지 탐색된 로컬사진, url 사진 개수
        for (int k = 1 ; k <= num_of_all_pics ; k++) {
            if (pic_found < pics_array.length && pics_array[pic_found].substring(3,pics_array[pic_found].indexOf('_')).equals(Integer.toString(k))) {
                allPics_array.add(pics_array[pic_found]);
                pic_found++;
            } else if (url_found < URLs_array.length && URLs_array[url_found].substring(3,URLs_array[url_found].indexOf('_')).equals(Integer.toString(k))) {
                allPics_array.add(URLs_array[url_found]);
                url_found++;
            }
        }

        imagePreview = rootView.findViewById(R.id.image_preview);
        for (String s : allPics_array) {
            // 이미지뷰 생성
            CircleImageView imageView = new CircleImageView(getContext()); // 라이브러리: https://github.com/hdodenhof/CircleImageView
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
            params.rightMargin = margin;
            params.gravity = Gravity.CENTER_VERTICAL;
            imageView.setLayoutParams(params);
            imageView.setBorderWidth(1);
            imageView.setBackgroundColor(0xFFFFFFFF);
            imageView.setBorderColor(0xFF000000);

            // 이미지 String 형식에서 picn_ 또는 URLn_ 부분을 제거
            final String string_image = s.substring(s.indexOf('_')+1);

            if (s.substring(0,3).equals("pic")) { // 로컬 사진이면
                // String to Byte 이미지 변환
                byte[] bytes = Base64.decode(string_image,Base64.DEFAULT);
                final Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                imageView.setOnClickListener(new View.OnClickListener() { // 사진 클릭시 큰 화면으로 볼 수 있음
                    @Override
                    public void onClick(View v) {
                        bigPreview.setVisibility(View.VISIBLE);
                        close_btn.setVisibility(View.VISIBLE);
                        bigPreview.setImageBitmap(image);
                    }
                });

                // 미리보기에 추가
                imageView.setImageBitmap(image);
                imagePreview.addView(imageView);

            } else {  // URL 사진이면
                final RequestOptions options = new RequestOptions().error(R.drawable.wrongurl);
                Glide.with(getContext()).load(string_image).apply(options).into(imageView); // 라이브러리: https://github.com/bumptech/glide
                imagePreview.addView(imageView);

                imageView.setOnClickListener(new View.OnClickListener() { // 사진 클릭시 큰 화면으로 볼 수 있음
                    @Override
                    public void onClick(View v) {
                        bigPreview.setVisibility(View.VISIBLE);
                        close_btn.setVisibility(View.VISIBLE);
                        Glide.with(getContext()).load(string_image).apply(options).into(bigPreview); // 라이브러리: https://github.com/bumptech/glide
                    }
                });
            }
        }

        return rootView;
    }

}
