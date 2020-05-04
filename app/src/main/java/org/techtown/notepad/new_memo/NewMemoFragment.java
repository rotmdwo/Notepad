/*
* 사용한 라이브러리:
* https://github.com/hdodenhof/CircleImageView
* https://github.com/bumptech/glide
* https://github.com/hdodenhof/CircleImageView
*/
package org.techtown.notepad.new_memo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.bumptech.glide.request.target.Target;

import org.techtown.notepad.classes_for_methods.LoadPicture;
import org.techtown.notepad.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class NewMemoFragment extends Fragment {
    private EditText title, content, URL;
    private LinearLayout imagePreview;
    private File file;
    private Context newMemoContext;

    // 현재 노트에 첨부한 로컬사진의 byte to string 형식과 url 링크 저장
    private ArrayList<String> pics = new ArrayList<>();
    private ArrayList<String> urls = new ArrayList<>();

    // 현재 노트에 로컬사진과 url 사진이 몇 개 있는지 저장
    private int numOfPics = 0;
    private int numOfUrls = 0;

    public NewMemoFragment(Context newMemoContext) {
        this.newMemoContext = newMemoContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_new_memo, container, false);

        imagePreview = rootView.findViewById(R.id.image_preview);
        title = rootView.findViewById(R.id.title);
        content = rootView.findViewById(R.id.content);
        URL = rootView.findViewById(R.id.URL);

        ImageButton imageButton = rootView.findViewById(R.id.back);  // 뒤로가기 버튼
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewMemoActivity) newMemoContext)
                        .manager.beginTransaction()
                        .show(((NewMemoActivity) newMemoContext).frg_backbox)
                        .commit();
            }
        });

        ImageButton imageButton2 = rootView.findViewById(R.id.save);  // 저장 버튼
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewMemoActivity) newMemoContext).title = title.getText().toString();
                ((NewMemoActivity) newMemoContext).content = content.getText().toString();
                ((NewMemoActivity) newMemoContext).pics = pics;
                ((NewMemoActivity) newMemoContext).urls = urls;

                if(title.getText().toString().equals("") || content.getText().toString().equals("")){
                    Toast.makeText(getContext(),"제목과 내용 모두 입력하셔야 합니다.", Toast.LENGTH_SHORT)
                            .show();
                } else{
                    ((NewMemoActivity) newMemoContext)
                            .manager.beginTransaction()
                            .show(((NewMemoActivity) newMemoContext).frg_savebox)
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
                startActivityForResult(intent, LoadPicture.ATTACH_PICTURE);
            }
        });

        Button button2 = rootView.findViewById(R.id.takePhoto);  // 직접 사진 찍어 첨부하기
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file == null) {
                    String filename = "temp.jpg";
                    File storageDir = Environment.getExternalStorageDirectory();
                    file = new File(storageDir,filename);
                }

                Uri fileUri = FileProvider.getUriForFile(getContext(),
                        "org.techtown.notepad.intent.fileprovider", file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
                startActivityForResult(intent, LoadPicture.TAKE_PICTURE);
            }
        });

        Button button3 = rootView.findViewById(R.id.URL_Button);  // URL 주소로 첨부하기
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = URL.getText().toString();
                final CircleImageView imageView = LoadPicture.getCircleImageView(getContext());

                Glide.with(getContext()).load(url).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(getContext(),"잘못된 이미지 URL입니다.\n다시 한 번 확인해 주세요.",
                                Toast.LENGTH_SHORT)
                                .show();

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        imageView.setOnLongClickListener(new View.OnLongClickListener() {  // 사진을 길게 눌렀을시 첨부를 취소
                            @Override
                            public boolean onLongClick(View v) {
                                /*
                                * ArrayList들은 메소드에 주소를 넘겨주기 때문에 메소드 안에서도 값이 변하지만
                                * int는 값 자체를 넘겨주기 때문에 업데이트 된 값을 return으로 받아야 한다.
                                */
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
                Cursor cursor = getContext()
                                .getContentResolver()
                                .query(file, filePath, null, null, null);
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

        // 사진첨부와 사진찍기의 공통된 처리부분
        if ((requestCode == LoadPicture.ATTACH_PICTURE || requestCode == LoadPicture.TAKE_PICTURE) && resultCode == RESULT_OK) {
            // 사진이 돌아갔는지 확인하기 위해 사진의 정보 가져옴.
            try {
                exif = new ExifInterface(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            image = LoadPicture.compressPicture(imagePath, exif);

            // 사진을 String 형태로 전환
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] byteArray = bytes.toByteArray();
            pic = Base64.encodeToString(byteArray, Base64.DEFAULT);

            // 이미지 미리보기 띄우기
            CircleImageView imageView = LoadPicture.getCircleImageView(getContext());

            imageView.setImageBitmap(image);
            imageView.setId(numOfUrls + numOfPics + 1);
            imagePreview.addView(imageView);

            // 사진 string을 어레이 리스트에 저장
            numOfPics++;
            pics.add("pic" + (numOfPics + numOfUrls) + "_" + pic);

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
