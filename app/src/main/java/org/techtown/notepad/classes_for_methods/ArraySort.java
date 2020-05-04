package org.techtown.notepad.classes_for_methods;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

public class ArraySort {
    public static String[] arrayListToArrayForPic(ArrayList<String> arrayList) {
        // 사진의 순서를 정렬하기 위해 Set -> Array 형태로 변환
        String sorted_array[] = new String[arrayList.size()];
        int i = 0;
        for (String s : arrayList) {
            sorted_array[i++] = s;
        }
        java.util.Arrays.sort(sorted_array,new Comparator<String>() {  // 숫자 부분만 비교해야지 사진이 10개 이상일 때 정렬이 제대로 됨.
            @Override
            public int compare(String s1, String s2) {
                return Integer.parseInt(s1.substring(3,s1.indexOf('_'))) - Integer.parseInt(s2.substring(3, s2.indexOf('_')));  // 오름차순 정렬
            }
        });

        return sorted_array;
    }

    public static String[] setToArray(Set<String> set) {
        String sortedArray[] = new String[set.size()];
        int k = 0;
        for (String s : set) {
            sortedArray[k++] = s;
        }
        java.util.Arrays.sort(sortedArray);
        return sortedArray;
    }
}
