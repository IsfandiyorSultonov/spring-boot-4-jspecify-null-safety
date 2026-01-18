package org.learn.spring.boot.menu;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class MenuService {

    // we can use @Nullable for arrays that means array can contain null values
    // but in general it can store nulls?))
    public @Nullable String[] dailySpecials(){
        return new String[]{"milk", "potato", "end"};
    }

    // Object @Nullable [] array means individual elements cannot be null but the array itself can.
    public String @Nullable[] dailySpecialsElementNullable(){
        String @Nullable[] arr= new String[3];
        arr[0] = "milk";
        arr[1] = "potato";
//        arr[2] = null; // if here we will initialize null nullAway task validation won't be pass build won't be success
        return arr;
    }

    // @Nullable Object[] array means individual elements can be null but the array itself cannot
    public @Nullable String[] dailySpecialsArrayNullable(){
        return new  String[]{"milk", "potato", null, "end"};
    }

    // @Nullable Object[] array means individual elements can be null but the array itself cannot
    public @Nullable String @Nullable[] dailySpecialsElementArrayNullable(){
        @Nullable String @Nullable[] arr= new String[3];
        arr[0] = "milk";
        arr[1] = null;
        return arr;
    }

}
