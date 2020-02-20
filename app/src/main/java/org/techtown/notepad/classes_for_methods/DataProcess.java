package org.techtown.notepad.classes_for_methods;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class DataProcess {
    public static Set<String> restoreNote(String name, Context context){  // 노트 불러오기
        SharedPreferences pref = context.getSharedPreferences(name, Activity.MODE_PRIVATE);
        Set<String> defValues = new HashSet<>();
        return pref.getStringSet(name,defValues);
    }

    public static Set<String> restoreNames(Context context){  // 저장되어 있는 메모명(실제 타이틀이 아닌 마지막으로 수정된 시간으로 저장됨) 가져오기
        SharedPreferences pref = context.getSharedPreferences("names", Activity.MODE_PRIVATE);
        Set<String> defValues = new HashSet<>();
        return pref.getStringSet("names",defValues);
    }

    public static void saveNames(Set<String> names,Context context){
        // Shared Prefrences 저장
        SharedPreferences pref = context.getSharedPreferences("names", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.putStringSet("names",names);
        editor.commit();
    }

    public static void deleteNote(String name,Context context) {  // 저장되어 있는 노트 삭제
        SharedPreferences pref = context.getSharedPreferences(name, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();

        // xml 파일 삭제
        File file = new File("/data/data/org.techtown.notepad/shared_prefs/" + name + ".xml");
        file.delete();
    }
}
