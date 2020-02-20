package org.techtown.notepad.classes_for_methods;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class LoadPicture {
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

    public int ResizeRatio(int width, int height, int goal_width, int goal_height){ // 메모리 초과 문제 해결하기 위해 이미지 압축
        int ratio = 1;

        while(width / (ratio+1) > goal_width && height / (ratio+1) > goal_height){
            ratio++;
        }

        return ratio;
    }
}
