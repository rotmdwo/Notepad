package org.techtown.notepad.classes_for_methods;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DataProcess {
    public static Set<String> restoreNote(String name, Context context) {  // 노트 불러오기
        SharedPreferences pref = context.getSharedPreferences(name, Activity.MODE_PRIVATE);
        Set<String> defValues = new HashSet<>();
        return pref.getStringSet(name,defValues);
    }

    public static Set<String> restoreNames(Context context) {  // 저장되어 있는 메모명(실제 타이틀이 아닌 마지막으로 수정된 시간으로 저장됨) 가져오기
        SharedPreferences pref = context.getSharedPreferences("names", Activity.MODE_PRIVATE);
        Set<String> defValues = new HashSet<>();
        return pref.getStringSet("names", defValues);
    }

    public static void saveNames(Set<String> names, Context context) {
        // Shared Prefrences 저장
        SharedPreferences pref = context.getSharedPreferences("names", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.putStringSet("names", names);
        editor.commit();
    }

    public static void deleteNote(String name, Context context) {  // 저장되어 있는 노트 삭제
        SharedPreferences pref = context.getSharedPreferences(name, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();

        // xml 파일 삭제
        File file = new File("/data/data/org.techtown.notepad/shared_prefs/" + name + ".xml");
        file.delete();
    }

    public static void saveNote(Context context, String title, String content, ArrayList<String> pics, ArrayList<String> URLs) {
        // 현재 시간을 메모를 구분하는 이름으로 추가
        Set<String> names = DataProcess.restoreNames(context);
        Date time = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = format.format(time);
        names.add(currentTime);

        // 노트 내용을 Set으로 담음
        Set<String> note = new HashSet<>();
        note.add("title_" + title);
        note.add("content_" + content);
        for (int i = 0 ; i < URLs.size() ; i++) {  // 형식에 맞춘 URL들을 첨부해준다.  --> 형식: URLn_https:// ...
            note.add(URLs.get(i));
        }
        for (int j = 0 ; j < pics.size() ; j++) {
            note.add(pics.get(j));
        }

        // Shared Prefrences 저장
        SharedPreferences pref_names = context.getSharedPreferences("names", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editorNames = pref_names.edit();
        editorNames.clear();  // 클리어를 안 해주면 수정이 안 되는 문제 생김
        editorNames.putStringSet("names",names);
        editorNames.commit();

        SharedPreferences prefNote = context.getSharedPreferences(currentTime, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editorNote = prefNote.edit();
        editorNote.putStringSet(currentTime,note);
        editorNote.commit();
    }
}
