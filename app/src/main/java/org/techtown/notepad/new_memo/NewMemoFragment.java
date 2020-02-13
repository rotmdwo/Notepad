package org.techtown.notepad.new_memo;


import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.techtown.notepad.MainActivity;
import org.techtown.notepad.R;

import java.util.ArrayList;


public class NewMemoFragment extends Fragment {
    EditText title, content, URL;
    static NewMemoFragment mFragment;
    LinearLayout image_preview;

    // 현재 노트에 첨부한 로컬사진의 byte to string 형식과 url 링크 저장
    ArrayList<String> pics = new ArrayList<>();
    ArrayList<String> URLs = new ArrayList<>();

    // 현재 노트에 로컬사진과 url 사진이 몇 개 있는지 저장
    int num_of_pics = 0;
    int num_of_urls = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_new_memo, container, false);
        mFragment = this;
        image_preview = rootView.findViewById(R.id.image_preview);

        title = rootView.findViewById(R.id.title);
        content = rootView.findViewById(R.id.content);
        URL = rootView.findViewById(R.id.URL);

        ImageButton imageButton = rootView.findViewById(R.id.back);  // 뒤로가기 버튼
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewMemoActivity) NewMemoActivity.mContext).manager.beginTransaction().show(((NewMemoActivity) NewMemoActivity.mContext).frg_backbox).commit();
            }
        });

        ImageButton imageButton2 = rootView.findViewById(R.id.save);  // 저장 버튼
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewMemoActivity) NewMemoActivity.mContext).manager.beginTransaction().show(((NewMemoActivity) NewMemoActivity.mContext).frg_savebox).commit();
            }
        });


        Button button = rootView.findViewById(R.id.fromAlbum);  // 앨범에서 사진 불러오기
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button button2 = rootView.findViewById(R.id.takePhoto);  // 직접 사진 찍어 첨부하기
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button button3 = rootView.findViewById(R.id.URL_Button);  // URL 주소로 첨부하기
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = URL.getText().toString();
                final ImageView imageView = new ImageView(getContext());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width,height);  // 가로,세로 100dp
                imageView.setLayoutParams(params);

                Glide.with(getContext()).load(url).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(getContext(),"잘못된 이미지 URL입니다.\n다시 한 번 확인해 주세요.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        image_preview.addView(imageView);

                        // URLn_https:.. 형식으로 어레이리스트에 저장
                        num_of_urls++;
                        URLs.add("URL"+(num_of_pics+num_of_urls)+"_"+url);

                        return false;
                    }
                }).into(imageView); // 리스너 쓰려면 into()를 꼭 써야함.

            }
        });


        return rootView;
    }

}
