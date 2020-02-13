package org.techtown.notepad.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;

import org.techtown.notepad.MainActivity;
import org.techtown.notepad.R;
import org.techtown.notepad.view_modify.view_modifyActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class list_item_adapter extends RecyclerView.Adapter<list_item_adapter.ViewHolder> {
    private ArrayList<list_item> items = new ArrayList<>();
    Context context;

    public list_item_adapter(Context context) { // 생성자를 만들 때 Context를 안 넘겨주면 프래그먼트 나갔다가 들어올 때 마다 리사이클러뷰에 중복으로 쌓이는 버그 생김
        this.context = context;
    }

    @NonNull
    @Override
    public list_item_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;

        itemView = inflater.inflate(R.layout.list_item,parent,false);
        return new list_item_adapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull list_item_adapter.ViewHolder holder, int position) {
        list_item item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView textView, textView2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.thumbnail);
            textView = itemView.findViewById(R.id.title);
            textView2 = itemView.findViewById(R.id.content);

            itemView.setOnClickListener(new View.OnClickListener() {  // 리스트에서 노트를 선택하면 뷰창을 엶.
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    Intent intent = new Intent(MainActivity.mContext, view_modifyActivity.class);
                    intent.putExtra("title",items.get(pos).getTitle());
                    intent.putExtra("content",items.get(pos).getContent());
                    intent.putExtra("name",items.get(pos).getTime());
                    MainActivity.mContext.startActivity(intent);
                }
            });

        }

        public void setItem(list_item item) {
            if(item.getWebImage()){ // 첫 번째 이미지가 웹이미지면
                // error()를 쓰려면 RequestOptions 클래스를 사용해야함 -> https://stackoverflow.com/questions/47910536/glide-4-3-1-how-to-use-error
                RequestOptions options = new RequestOptions().error(R.drawable.wrongurl);
                Glide.with(MainActivity.mContext).load(item.image_path).apply(options).into(imageView);
            } else if(item.image_path.equals("NO")){  // 저장된 이미지가 없다면
                Drawable no_image = MainActivity.mContext.getDrawable(R.drawable.noimage);
                imageView.setImageDrawable(no_image);
            }
            else{ // 첫 번째 이미지가 로컬 이미지이면
                byte[] bytes = Base64.decode(item.getImage_path(),Base64.DEFAULT);
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imageView.setImageBitmap(image);
            }

            textView.setText(item.getTitle());
            textView2.setText(item.getContent());
        }

    }

    public void addListItem(list_item item) {
        items.add(item);
    }
}
