package org.techtown.notepad.view_modify;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.techtown.notepad.classes_for_methods.ArraySort;
import org.techtown.notepad.classes_for_methods.DataProcess;
import org.techtown.notepad.classes_for_methods.LoadPicture;
import org.techtown.notepad.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ModifyFragment extends Fragment {
    EditText title, content, URL;
    private LinearLayout imagePreview;
    private File file;
    public static ModifyFragment mFragment;
    private Context viewModifyContext;

    // 현재 노트에 첨부한 로컬사진의 byte to string 형식과 url 링크 저장
    ArrayList<String> pics = new ArrayList<>();
    ArrayList<String> urls = new ArrayList<>();

    // 현재 노트에 로컬사진과 url 사진이 몇 개 있는지 저장
    int numOfPics = 0;
    int numOfUrls = 0;

    public ModifyFragment(Context viewModifyContext) {
        this.viewModifyContext = viewModifyContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_modify, container, false);

        mFragment = this;

        imagePreview = rootView.findViewById(R.id.image_preview);
        URL = rootView.findViewById(R.id.URL);

        // 리스트로부터 받은 인텐트
        Intent intent = getActivity().getIntent();
        String titleString = intent.getStringExtra("title");
        String contentString = intent.getStringExtra("content");
        String name = intent.getStringExtra("name");

        // 제목과 내용 불러와 설정
        title = rootView.findViewById(R.id.title);
        content = rootView.findViewById(R.id.content);
        title.setText(titleString);
        content.setText(contentString);

        // 노트에 저장된 사진들을 불러옴
        Set<String> note = DataProcess.restoreNote(name,getContext());
        Iterator<String> iteratorNote = note.iterator();
        while (iteratorNote.hasNext()) {
            String temp = iteratorNote.next();
            if (temp.substring(0,3).equals("pic")) {
                pics.add(temp);
            } else if (temp.substring(0,3).equals("URL")) {
                urls.add(temp);
            }
        }

        // 로컬사진과 URL 사진의 개수
        numOfPics = pics.size();
        numOfUrls = urls.size();

        String picsArray[] = ArraySort.arrayListToArrayForPic(pics);
        String urlsArray[] = ArraySort.arrayListToArrayForPic(urls);

        // 정렬된 Array를 다시 ArrayList로 변환
        for(int k = 0 ; k < numOfPics ; k++){
            pics.set(k, picsArray[k]);
        }
        for(int k = 0 ; k < numOfUrls ; k++){
            urls.set(k, urlsArray[k]);
        }

        // 순서대로 ImageView로 미리보기에 넣어줌
        int picFound = 0, urlFound = 0;
        for(int k = 1 ; k <= numOfUrls + numOfPics ; k++){
            if (picFound < picsArray.length
                    && picsArray[picFound].substring(3, picsArray[picFound].indexOf('_')).equals(Integer.toString(k))){
                // 이미지뷰 생성
                CircleImageView imageView = LoadPicture.getCircleImageView(getContext());

                // 이미지 String 형식에서 picn_ 부분을 제거
                String string_image = picsArray[picFound].substring(picsArray[picFound].indexOf('_') + 1);

                // String to Byte 이미지 변환
                byte[] bytes = Base64.decode(string_image,Base64.DEFAULT);
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                // 사진을 길게 눌렀을시 첨부를 취소
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        numOfPics = LoadPicture.deletePicture(v, numOfPics, urls, pics);

                        return false;
                    }
                });

                // 미리보기에 추가
                imageView.setImageBitmap(image);
                imageView.setId(picFound + urlFound + 1);
                imagePreview.addView(imageView);

                picFound++;

            } else if(urlFound < urlsArray.length
                    && urlsArray[urlFound].substring(3, urlsArray[urlFound].indexOf('_')).equals(Integer.toString(k))){
                // 이미지뷰 생성
                CircleImageView imageView = LoadPicture.getCircleImageView(getContext());

                // 이미지 String 형식에서 URLn_ 부분을 제거
                String stringImage = urlsArray[urlFound].substring(urlsArray[urlFound].indexOf('_') + 1);

                imageView.setOnLongClickListener(new View.OnLongClickListener() {  // 사진을 길게 눌렀을시 첨부를 취소
                    @Override
                    public boolean onLongClick(View v) {
                        numOfUrls = LoadPicture.deleteUrl(v, numOfUrls, urls, pics);

                        return false;
                    }
                });

                RequestOptions options = new RequestOptions().error(R.drawable.wrongurl);
                Glide.with(getContext()).load(stringImage).apply(options).into(imageView);  // 라이브러리: https://github.com/bumptech/glide
                imageView.setId(picFound + urlFound + 1);
                imagePreview.addView(imageView);

                urlFound++;
            }
        }

        ImageButton imageButton = rootView.findViewById(R.id.back);  // 뒤로가기 버튼
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((view_modifyActivity) viewModifyContext).manager
                        .beginTransaction().show(((view_modifyActivity) viewModifyContext).frg_backbox2)
                        .commit();
            }
        });

        ImageButton imageButton2 = rootView.findViewById(R.id.save);  // 저장 버튼
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().equals("") || content.getText().toString().equals("")) {
                    Toast.makeText(getContext(),"제목과 내용 모두 입력하셔야 합니다.",Toast.LENGTH_SHORT).show();
                } else {
                    ((view_modifyActivity) viewModifyContext).manager
                            .beginTransaction().show(((view_modifyActivity) viewModifyContext).frg_savebox2)
                            .commit();
                }
            }
        });

        Button button = rootView.findViewById(R.id.fromAlbum);  // 앨범에서 사진 불러오기
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,LoadPicture.ATTACH_PICTURE);
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

                Uri fileUri = FileProvider.getUriForFile(getContext(), "org.techtown.notepad.intent.fileprovider", file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, LoadPicture.TAKE_PICTURE);
            }
        });

        Button button3 = rootView.findViewById(R.id.URL_Button);  // URL 주소로 첨부하기
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = URL.getText().toString();
                final CircleImageView imageView = LoadPicture.getCircleImageView(getContext());

                Glide.with(getContext()).load(url).addListener(new RequestListener<Drawable>() { // 라이브러리: https://github.com/bumptech/glide
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(getContext(),"잘못된 이미지 URL입니다.\n다시 한 번 확인해 주세요.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        imageView.setOnLongClickListener(new View.OnLongClickListener() {  // 사진을 길게 눌렀을시 첨부를 취소
                            @Override
                            public boolean onLongClick(View v) {
                                numOfUrls = LoadPicture.deleteUrl(v, numOfUrls, urls, pics);

                                return false;
                            }
                        });
                        imageView.setId(numOfPics + numOfUrls + 1);
                        imagePreview.addView(imageView);

                        // URLn_https:.. 형식으로 어레이리스트에 저장
                        numOfUrls++;
                        urls.add("URL" + (numOfPics + numOfUrls) + "_" + url);

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

        String pic;
        Bitmap image;
        ExifInterface exif = null;
        String imagePath = null;

        if (requestCode == LoadPicture.ATTACH_PICTURE && resultCode == RESULT_OK) {  // 사진첨부 처리부분
            Uri file;

            try {
                file = data.getData();

                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContext().getContentResolver().query(file, filePath, null, null, null);
                cursor.moveToFirst();
                imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                cursor.close();

            } catch (NullPointerException e) {
                e.getStackTrace();
            }
        }

        if (requestCode == LoadPicture.TAKE_PICTURE && resultCode == RESULT_OK) {  // 사진찍기 처리부분
            imagePath = file.getAbsolutePath();
        }

        if ((requestCode == LoadPicture.ATTACH_PICTURE || requestCode == LoadPicture.TAKE_PICTURE) && resultCode == RESULT_OK) {  // 사진첨부와 사진찍기의 공통된 처리부분
            // 사진이 돌아갔는지 확인하기 위해 사진의 정보 가져옴.
            try{
                exif = new ExifInterface(imagePath);
            } catch(IOException e){
                e.printStackTrace();
            }

            image = LoadPicture.compressPicture(imagePath, exif);

            // 사진을 String 형태로 전환
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] byteArray = bytes.toByteArray();
            pic = Base64.encodeToString(byteArray,Base64.DEFAULT);


            // 이미지 미리보기 띄우기
            CircleImageView imageView = LoadPicture.getCircleImageView(getContext());

            imageView.setImageBitmap(image);
            imageView.setId(numOfUrls+numOfPics+1);
            imagePreview.addView(imageView);

            // 사진 string을 어레이 리스트에 저장
            numOfPics++;
            pics.add("pic"+(numOfPics + numOfUrls)+"_"+pic);

            imageView.setOnLongClickListener(new View.OnLongClickListener() {  // 사진을 길게 눌렀을시 첨부를 취소
                @Override
                public boolean onLongClick(View v) {
                    numOfPics = LoadPicture.deletePicture(v, numOfPics, urls, pics);

                    return false;
                }
            });
        }
    }
}
