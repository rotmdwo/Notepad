package org.techtown.notepad.view_modify;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.techtown.notepad.R;
import org.techtown.notepad.new_memo.NewMemoActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ModifyFragment extends Fragment {
    EditText title, content,URL;
    static ModifyFragment mFragment;
    LinearLayout image_preview;
    File file;

    // 현재 노트에 첨부한 로컬사진의 byte to string 형식과 url 링크 저장
    ArrayList<String> pics = new ArrayList<>();
    ArrayList<String> URLs = new ArrayList<>();

    // 현재 노트에 로컬사진과 url 사진이 몇 개 있는지 저장
    int num_of_pics = 0;
    int num_of_urls = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_modify, container, false);
        mFragment = this;
        image_preview = rootView.findViewById(R.id.image_preview);
        URL = rootView.findViewById(R.id.URL);

        // 리스트로부터 받은 인텐트
        Intent intent = getActivity().getIntent();
        String title_string = intent.getStringExtra("title");
        String content_string = intent.getStringExtra("content");
        String name = intent.getStringExtra("name");

        // 제목과 내용 불러와 설정
        title = rootView.findViewById(R.id.title);
        content = rootView.findViewById(R.id.content);
        title.setText(title_string);
        content.setText(content_string);

        // 노트에 저장된 사진들을 불러옴
        Set<String> note = restoreNote(name);
        Iterator<String> iterator_note = note.iterator();
        while(iterator_note.hasNext()){
            String temp = iterator_note.next();
            if(temp.substring(0,3).equals("pic")){
                pics.add(temp);
            } else if(temp.substring(0,3).equals("URL")){
                URLs.add(temp);
            }
        }

        // 로컬사진과 URL 사진의 개수
        num_of_pics = pics.size();
        num_of_urls = URLs.size();

        // 사진의 순서를 정렬하기 위해 Array 형태로 변환
        String pics_array[] = new String[pics.size()];
        int i = 0;
        for(String s : pics){
            pics_array[i++] = s;
        }
        java.util.Arrays.sort(pics_array,new Comparator<String>(){  // 숫자 부분만 비교해야지 사진이 10개 이상일 때 정렬이 제대로 됨.
            @Override
            public int compare(String s1, String s2) {
                return Integer.parseInt(s1.substring(3,s1.indexOf('_'))) - Integer.parseInt(s2.substring(3,s2.indexOf('_')));  // 오름차순 정렬
            }
        });
        String URLs_array[] = new String [URLs.size()];
        int j = 0;
        for(String s : URLs){
            URLs_array[j++] = s;
        }
        java.util.Arrays.sort(URLs_array,new Comparator<String>(){
            @Override
            public int compare(String s1, String s2) {
                return Integer.parseInt(s1.substring(3,s1.indexOf('_'))) - Integer.parseInt(s2.substring(3,s2.indexOf('_')));
            }
        });

        // 정렬된 Array를 다시 ArrayList로 변환
        for(int k = 0 ; k < num_of_pics ; k++){
            pics.set(k, pics_array[k]);
        }
        for(int k = 0 ; k < num_of_urls ; k++){
            URLs.set(k, URLs_array[k]);
        }

        // 순서대로 ImageView로 미리보기에 넣어줌
        int pic_found = 0, url_found = 0;
        for(int k = 1 ; k <= num_of_urls + num_of_pics ; k++){
            if (pic_found < pics_array.length && pics_array[pic_found].substring(3,pics_array[pic_found].indexOf('_')).equals(Integer.toString(k))){
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

                // 이미지 String 형식에서 picn_ 부분을 제거
                String string_image = pics_array[pic_found].substring(pics_array[pic_found].indexOf('_')+1);

                // String to Byte 이미지 변환
                byte[] bytes = Base64.decode(string_image,Base64.DEFAULT);
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                // 사진을 길게 눌렀을시 첨부를 취소
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int id = v.getId();   // 해당 로컬사진의 ID 받아오기
                        v.setVisibility(View.GONE);  // 해당 로컬사진을 미리보기에서 제거
                        num_of_pics--;

                        for(int i = 0; i < pics.size() ; i++){  // 로컬사진들을 저장한 어레이 리스트에서 해당 로컬사진을 삭제
                            int num = Integer.parseInt(pics.get(i).substring(3,pics.get(i).indexOf('_')));
                            if(num == id){
                                pics.remove(i);
                            }
                        }

                        for(int i = 0 ; i < pics.size() ; i++){  // 로컬 사진들을 저장한 어레이 리스트에서 해당 로컬사진보다 뒤에 있던 사진들의 네이밍 넘버를 하나 씩 떙김
                            int num = Integer.parseInt(pics.get(i).substring(3,pics.get(i).indexOf('_')));
                            String string_image = pics.get(i).substring(pics.get(i).indexOf('_')+1);
                            if(num > id){
                                pics.set(i, "pic"+(num-1)+"_"+string_image);
                            }
                        }
                        for(int i = 0 ; i < URLs.size() ; i++){  // URL을 저장한 어레이 리스트에서 해당 로컬사진보다 뒤에 있던 사진들의 네이밍 넘버를 하나 씩 떙김
                            int num = Integer.parseInt(URLs.get(i).substring(3,URLs.get(i).indexOf('_')));
                            String string_url = URLs.get(i).substring(URLs.get(i).indexOf('_')+1);
                            if(num > id){
                                URLs.set(i, "URL"+(num-1)+"_"+string_url);
                            }
                        }
                        return false;
                    }
                });

                // 미리보기에 추가
                imageView.setImageBitmap(image);
                imageView.setId(pic_found+url_found+1);
                image_preview.addView(imageView);

                pic_found++;

            } else if(url_found < URLs_array.length && URLs_array[url_found].substring(3,URLs_array[url_found].indexOf('_')).equals(Integer.toString(k))){
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

                // 이미지 String 형식에서 URLn_ 부분을 제거
                String string_image = URLs_array[url_found].substring(URLs_array[url_found].indexOf('_')+1);

                imageView.setOnLongClickListener(new View.OnLongClickListener() {  // 사진을 길게 눌렀을시 첨부를 취소
                    @Override
                    public boolean onLongClick(View v) {
                        int id = v.getId();   // 해당 URL의 ID 받아오기
                        v.setVisibility(View.GONE);  // 해당 URL을 미리보기에서 제거
                        num_of_urls--;

                        for(int i = 0; i < URLs.size() ; i++){  // URL들을 저장한 어레이 리스트에서 해당 URL을 삭제
                            int num = Integer.parseInt(URLs.get(i).substring(3,URLs.get(i).indexOf('_')));
                            if(num == id){
                                URLs.remove(i);
                            }
                        }

                        for(int i = 0 ; i < pics.size() ; i++){  // 로컬 사진들을 저장한 어레이 리스트에서 해당 URL보다 뒤에 있던 사진들의 네이밍 넘버를 하나 씩 떙김
                            int num = Integer.parseInt(pics.get(i).substring(3,pics.get(i).indexOf('_')));
                            String string_image = pics.get(i).substring(pics.get(i).indexOf('_')+1);
                            if(num > id){
                                pics.set(i, "pic"+(num-1)+"_"+string_image);
                            }
                        }
                        for(int i = 0 ; i < URLs.size() ; i++){  // URL을 저장한 어레이 리스트에서 해당 URL보다 뒤에 있던 사진들의 네이밍 넘버를 하나 씩 떙김
                            int num = Integer.parseInt(URLs.get(i).substring(3,URLs.get(i).indexOf('_')));
                            String string_url = URLs.get(i).substring(URLs.get(i).indexOf('_')+1);
                            if(num > id){
                                URLs.set(i, "URL"+(num-1)+"_"+string_url);
                            }
                        }
                        return false;
                    }
                });

                RequestOptions options = new RequestOptions().error(R.drawable.wrongurl);
                Glide.with(getContext()).load(string_image).apply(options).into(imageView);  // 라이브러리: https://github.com/bumptech/glide
                imageView.setId(pic_found+url_found+1);
                image_preview.addView(imageView);

                url_found++;
            }
        }

        ImageButton imageButton = rootView.findViewById(R.id.back);  // 뒤로가기 버튼
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((view_modifyActivity) view_modifyActivity.mContext).manager.beginTransaction().show(((view_modifyActivity) view_modifyActivity.mContext).frg_backbox2).commit();
            }
        });

        ImageButton imageButton2 = rootView.findViewById(R.id.save);  // 저장 버튼
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().equals("") || content.getText().toString().equals("")){
                    Toast.makeText(getContext(),"제목과 내용 모두 입력하셔야 합니다.",Toast.LENGTH_SHORT).show();
                } else{
                    ((view_modifyActivity) view_modifyActivity.mContext).manager.beginTransaction().show(((view_modifyActivity) view_modifyActivity.mContext).frg_savebox2).commit();
                }
            }
        });

        Button button = rootView.findViewById(R.id.fromAlbum);  // 앨범에서 사진 불러오기
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,101);
            }
        });

        Button button2 = rootView.findViewById(R.id.takePhoto);  // 직접 사진 찍어 첨부하기
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(file == null){
                    String filename = "temp.jpg";
                    File storageDir = Environment.getExternalStorageDirectory();
                    file = new File(storageDir,filename);
                }

                Uri fileUri = FileProvider.getUriForFile(getContext(),"org.techtown.notepad.intent.fileprovider",file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
                startActivityForResult(intent,103);
            }
        });

        Button button3 = rootView.findViewById(R.id.URL_Button);  // URL 주소로 첨부하기
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = URL.getText().toString();
                final CircleImageView imageView = new CircleImageView(getContext()); // 라이브러리: https://github.com/hdodenhof/CircleImageView
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

                Glide.with(getContext()).load(url).addListener(new RequestListener<Drawable>() { // 라이브러리: https://github.com/bumptech/glide
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(getContext(),"잘못된 이미지 URL입니다.\n다시 한 번 확인해 주세요.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageView.setOnLongClickListener(new View.OnLongClickListener() {  // 사진을 길게 눌렀을시 첨부를 취소
                            @Override
                            public boolean onLongClick(View v) {
                                int id = v.getId();   // 해당 URL의 ID 받아오기
                                v.setVisibility(View.GONE);  // 해당 URL을 미리보기에서 제거
                                num_of_urls--;

                                for(int i = 0; i < URLs.size() ; i++){  // URL들을 저장한 어레이 리스트에서 해당 URL을 삭제
                                    int num = Integer.parseInt(URLs.get(i).substring(3,URLs.get(i).indexOf('_')));
                                    if(num == id){
                                        URLs.remove(i);
                                    }
                                }

                                for(int i = 0 ; i < pics.size() ; i++){  // 로컬 사진들을 저장한 어레이 리스트에서 해당 URL보다 뒤에 있던 사진들의 네이밍 넘버를 하나 씩 떙김
                                    int num = Integer.parseInt(pics.get(i).substring(3,pics.get(i).indexOf('_')));
                                    String string_image = pics.get(i).substring(pics.get(i).indexOf('_')+1);
                                    if(num > id){
                                        pics.set(i, "pic"+(num-1)+"_"+string_image);
                                    }
                                }
                                for(int i = 0 ; i < URLs.size() ; i++){  // URL을 저장한 어레이 리스트에서 해당 URL보다 뒤에 있던 사진들의 네이밍 넘버를 하나 씩 떙김
                                    int num = Integer.parseInt(URLs.get(i).substring(3,URLs.get(i).indexOf('_')));
                                    String string_url = URLs.get(i).substring(URLs.get(i).indexOf('_')+1);
                                    if(num > id){
                                        URLs.set(i, "URL"+(num-1)+"_"+string_url);
                                    }
                                }
                                return false;
                            }
                        });
                        imageView.setId(num_of_pics+num_of_urls+1);
                        image_preview.addView(imageView);

                        // URLn_https:.. 형식으로 어레이리스트에 저장
                        num_of_urls++;
                        URLs.add("URL"+(num_of_pics+num_of_urls)+"_"+url);

                        URL.setText("");  // URL 입력화면 초기화
                        return false;
                    }
                }).into(imageView); // 리스너 쓰려면 into()를 꼭 써야함.

            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String pic = null;
        Bitmap image = null;
        ExifInterface exif = null;
        String imagePath = null;

        if(requestCode == 101 && resultCode == RESULT_OK){  // 사진첨부 처리부분
            Uri file;

            file = data.getData();

            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContext().getContentResolver().query(file, filePath, null, null, null);
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            cursor.close();
        }

        if(requestCode == 103 && resultCode == RESULT_OK){  // 사진찍기 처리부분
            imagePath = file.getAbsolutePath();
        }

        if((requestCode == 101 || requestCode == 103) && resultCode == RESULT_OK){  // 사진첨부와 사진찍기의 공통된 처리부분
            // 사진이 돌아갔는지 확인하기 위해 사진의 정보 가져옴.
            try{
                exif = new ExifInterface(imagePath);
            } catch(IOException e){
                e.printStackTrace();
            }

            // 메모리 초과 문제 해결하기 위해 이미지 압축
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath,options);
            options.inSampleSize = ResizeRatio(options.outWidth,options.outHeight);
            options.inJustDecodeBounds = false;

            // 사진이 회전 되어있다면 정방향으로 돌림
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);
            image = BitmapFactory.decodeFile(imagePath, options);
            image = rotate(image, exifOrientationToDegrees(orientation));

            // 사진을 String 형태로 전환
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] byteArray = bytes.toByteArray();
            pic = Base64.encodeToString(byteArray,Base64.DEFAULT);

            // 이미지 미리보기 띄우기
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

            imageView.setImageBitmap(image);
            imageView.setId(num_of_urls+num_of_pics+1);
            image_preview.addView(imageView);

            // 사진 string을 어레이 리스트에 저장
            num_of_pics++;
            pics.add("pic"+(num_of_pics+num_of_urls)+"_"+pic);

            imageView.setOnLongClickListener(new View.OnLongClickListener() {  // 사진을 길게 눌렀을시 첨부를 취소
                @Override
                public boolean onLongClick(View v) {
                    int id = v.getId();   // 해당 로컬사진의 ID 받아오기
                    v.setVisibility(View.GONE);  // 해당 로컬사진을 미리보기에서 제거
                    num_of_pics--;

                    for(int i = 0; i < pics.size() ; i++){  // 로컬사진들을 저장한 어레이 리스트에서 해당 로컬사진을 삭제
                        int num = Integer.parseInt(pics.get(i).substring(3,pics.get(i).indexOf('_')));
                        if(num == id){
                            pics.remove(i);
                        }
                    }

                    for(int i = 0 ; i < pics.size() ; i++){  // 로컬 사진들을 저장한 어레이 리스트에서 해당 로컬사진보다 뒤에 있던 사진들의 네이밍 넘버를 하나 씩 떙김
                        int num = Integer.parseInt(pics.get(i).substring(3,pics.get(i).indexOf('_')));
                        String string_image = pics.get(i).substring(pics.get(i).indexOf('_')+1);
                        if(num > id){
                            pics.set(i, "pic"+(num-1)+"_"+string_image);
                        }
                    }
                    for(int i = 0 ; i < URLs.size() ; i++){  // URL을 저장한 어레이 리스트에서 해당 로컬사진보다 뒤에 있던 사진들의 네이밍 넘버를 하나 씩 떙김
                        int num = Integer.parseInt(URLs.get(i).substring(3,URLs.get(i).indexOf('_')));
                        String string_url = URLs.get(i).substring(URLs.get(i).indexOf('_')+1);
                        if(num > id){
                            URLs.set(i, "URL"+(num-1)+"_"+string_url);
                        }
                    }
                    return false;
                }
            });
        }
    }

    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
            }
        }
        return bitmap;
    }

    private Set<String> restoreNote(String name){  // 노트 불러오기
        SharedPreferences pref = getActivity().getSharedPreferences(name, Activity.MODE_PRIVATE);
        Set<String> defValues = new HashSet<>();
        return pref.getStringSet(name,defValues);
    }

    int ResizeRatio(int width, int height){ // 메모리 초과 문제 해결하기 위해 이미지 압축
        final int goal_width = 500;
        final int goal_height = 500;
        int ratio = 1;

        while(width / (ratio+1) > goal_width && height / (ratio+1) > goal_height){
            ratio++;
        }

        return ratio;
    }
}
