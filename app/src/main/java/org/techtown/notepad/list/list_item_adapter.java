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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.techtown.notepad.MainActivity;
import org.techtown.notepad.R;
import org.techtown.notepad.view_modify.view_modifyActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        CircleImageView imageView; // 라이브러리: https://github.com/hdodenhof/CircleImageView
        TextView textView, textView2, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.thumbnail);
            textView = itemView.findViewById(R.id.title);
            textView2 = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);

            itemView.setOnClickListener(new View.OnClickListener() {  // 리스트에서 노트를 선택하면 뷰창을 엶.
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    Intent intent = new Intent(MainActivity.mContext, view_modifyActivity.class);
                    intent.putExtra("title",items.get(pos).getTitle());
                    intent.putExtra("content",items.get(pos).getContent());
                    intent.putExtra("name",items.get(pos).getTime());
                    MainActivity.mContext.startActivity(intent);
                    ((MainActivity)MainActivity.mContext).overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_not_move);
                }
            });

        }

        public void setItem(list_item item) {
            if(item.getWebImage()){ // 첫 번째 이미지가 웹이미지면
                // error()를 쓰려면 RequestOptions 클래스를 사용해야함 -> https://stackoverflow.com/questions/47910536/glide-4-3-1-how-to-use-error
                RequestOptions options = new RequestOptions().error(R.drawable.wrongurl);
                Glide.with(MainActivity.mContext).load(item.image_path).apply(options).into(imageView);  // 라이브러리: https://github.com/bumptech/glide
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

            // 마지막 수정시간 계산
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            String current_date = format.format(date);
            String then_date = item.getTime();
            int current_year = Integer.parseInt(current_date.substring(0,4));
            int then_year = Integer.parseInt(then_date.substring(0,4));
            int current_month = Integer.parseInt(current_date.substring(4,6));
            int then_month = Integer.parseInt(then_date.substring(4,6));
            int current_day = Integer.parseInt(current_date.substring(6,8));
            int then_day = Integer.parseInt(then_date.substring(6,8));
            int current_hour = Integer.parseInt(current_date.substring(8,10));
            int then_hour = Integer.parseInt(then_date.substring(8,10));
            int current_minute = Integer.parseInt(current_date.substring(10,12));
            int then_minute = Integer.parseInt(then_date.substring(10,12));
            int current_second = Integer.parseInt(current_date.substring(12,14));
            int then_second = Integer.parseInt(then_date.substring(12,14));

            if((current_year-then_year>=2) || (current_year - then_year == 1 && current_month - then_month >= 0)){
                time.setText(current_year-then_year+"년 전");
            } else if(current_year - then_year == 1 && current_month - then_month < 0){
                time.setText(current_month + 12 - then_month+"달 전");
            } else if((current_month-then_month>=2) || (current_month - then_month == 1 && current_day - then_day >= 0)){
                time.setText(current_month-then_month+"달 전");
            } else if(current_month - then_month == 1 && current_day - then_day < 0){
                time.setText(current_day + 30 - then_day+"일 전");
            } else if((current_day-then_day>=2) || (current_day - then_day == 1 && current_hour - then_hour >= 0)){
                time.setText(current_day-then_day+"일 전");
            } else if(current_day - then_day == 1 && current_hour - then_hour < 0){
                time.setText(current_hour + 24 - then_hour+"시간 전");
            } else if((current_hour-then_hour>=2) || (current_hour - then_hour == 1 && current_minute - then_minute >= 0)){
                time.setText(current_hour-then_hour+"시간 전");
            } else if(current_hour - then_hour == 1 && current_minute - then_minute < 0){
                time.setText(current_minute + 60 - then_minute+"분 전");
            } else if((current_minute-then_minute>=2) || (current_minute - then_minute == 1 && current_second - then_second >= 0)){
                time.setText(current_minute-then_minute+"분 전");
            } else{
                time.setText("방금 전");
            }
        }

    }

    public void addListItem(list_item item) {
        items.add(item);
    }

    public void addListItem(list_item item, int index) {
        items.add(index,item);
    }

    public void removeListItem(int index) {
        items.remove(index);
    }
}
