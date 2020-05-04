package org.techtown.notepad.classes_for_methods;

/*
* 사용한 라이브러리:
* https://github.com/hdodenhof/CircleImageView
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoadPicture {
    public static final int ATTACH_PICTURE = 101;
    public static final int TAKE_PICTURE = 103;

    public static int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }

        return 0;
    }

    public static Bitmap rotate(Bitmap bitmap, int degrees) {
        if(degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {

            }
        }

        return bitmap;
    }

    // 메모리 초과 문제 해결하기 위해 이미지 압축
    public static int resizeRatio(int width, int height, int goal_width, int goal_height) {
        int ratio = 1;

        while(width / (ratio+1) > goal_width && height / (ratio+1) > goal_height) {
            ratio++;
        }

        return ratio;
    }

    public static Bitmap compressPicture(String imagePath, ExifInterface exif) {
        Bitmap image;
        String pic;

        // 메모리 초과 문제 해결하기 위해 이미지 압축
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath,options);
        options.inSampleSize = resizeRatio(options.outWidth,options.outHeight,500, 500);
        options.inJustDecodeBounds = false;

        // 사진이 회전 되어있다면 정방향으로 돌림
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);
        image = BitmapFactory.decodeFile(imagePath, options);
        image = rotate(image, LoadPicture.exifOrientationToDegrees(orientation));

        return image;
    }

    public static CircleImageView getCircleImageView(Context context) {
        CircleImageView imageView = new CircleImageView(context);

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                90, context.getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                90, context.getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                15, context.getResources().getDisplayMetrics());

        params.rightMargin = margin;
        params.gravity = Gravity.CENTER_VERTICAL;

        imageView.setLayoutParams(params);
        imageView.setBorderWidth(1);
        imageView.setBackgroundColor(0xFFFFFFFF);
        imageView.setBorderColor(0xFF000000);

        return imageView;
    }

    public static int deleteUrl(View v, int numOfUrls, ArrayList<String> urls, ArrayList<String> pics) {
        int id = v.getId();   // 해당 URL의 ID 받아오기
        v.setVisibility(View.GONE);  // 해당 URL을 미리보기에서 제거
        numOfUrls--;

        for (int i = 0 ; i < urls.size() ; i++) {  // URL들을 저장한 어레이 리스트에서 해당 URL을 삭제
            int num = Integer.parseInt(urls.get(i).substring(3, urls.get(i).indexOf('_')));
            if(num == id){
                urls.remove(i);
            }
        }

        // 로컬 사진들을 저장한 어레이 리스트에서 해당 URL보다 뒤에 있던 사진들의 네이밍 넘버를 하나 씩 떙김
        for (int i = 0 ; i < pics.size() ; i++) {
            int num = Integer.parseInt(pics.get(i).substring(3,pics.get(i).indexOf('_')));
            String string_image = pics.get(i).substring(pics.get(i).indexOf('_')+1);
            if (num > id) {
                pics.set(i, "pic"+(num-1)+"_"+string_image);
            }
        }
        // URL을 저장한 어레이 리스트에서 해당 URL보다 뒤에 있던 사진들의 네이밍 넘버를 하나 씩 떙김
        for (int i = 0 ; i < urls.size() ; i++) {
            int num = Integer.parseInt(urls.get(i).substring(3, urls.get(i).indexOf('_')));
            String string_url = urls.get(i).substring(urls.get(i).indexOf('_')+1);

            if (num > id) {
                urls.set(i, "URL"+(num-1)+"_"+string_url);
            }
        }

        return numOfUrls;
    }

    public static int deletePicture(View v, int numOfPics, ArrayList<String> urls, ArrayList<String> pics) {
        int id = v.getId();   // 해당 로컬사진의 ID 받아오기
        v.setVisibility(View.GONE);  // 해당 로컬사진을 미리보기에서 제거
        numOfPics--;

        // 로컬사진들을 저장한 어레이 리스트에서 해당 로컬사진을 삭제
        for (int i = 0; i < pics.size() ; i++) {
            int num = Integer.parseInt(pics.get(i).substring(3,pics.get(i).indexOf('_')));
            if (num == id) {
                pics.remove(i);
            }
        }

        // 로컬 사진들을 저장한 어레이 리스트에서 해당 로컬사진보다 뒤에 있던 사진들의 네이밍 넘버를 하나 씩 떙김
        for (int i = 0 ; i < pics.size() ; i++) {
            int num = Integer.parseInt(pics.get(i).substring(3,pics.get(i).indexOf('_')));
            String string_image = pics.get(i).substring(pics.get(i).indexOf('_')+1);
            if(num > id){
                pics.set(i, "pic"+(num-1)+"_"+string_image);
            }
        }

        // URL을 저장한 어레이 리스트에서 해당 로컬사진보다 뒤에 있던 사진들의 네이밍 넘버를 하나 씩 떙김
        for (int i = 0 ; i < urls.size() ; i++) {
            int num = Integer.parseInt(urls.get(i).substring(3,urls.get(i).indexOf('_')));
            String string_url = urls.get(i).substring(urls.get(i).indexOf('_')+1);
            if (num > id) {
                urls.set(i, "URL"+(num-1)+"_"+string_url);
            }
        }

        return numOfPics;
    }
}
