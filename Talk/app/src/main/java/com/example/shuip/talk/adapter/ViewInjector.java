package com.example.shuip.talk.adapter;

import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 15-9-18.
 */
public class ViewInjector {
    public static synchronized void injectView(Object clz,View view){

        Field[] fields = clz.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0){
            for (Field field : fields){
                field.setAccessible(true);
                int id = view.getResources().getIdentifier(field.getName(),"id",view.getContext().getPackageName());

                if (id > 0){
                    View fieldView = view.findViewById(id);
                    try {
                        field.set(clz,fieldView);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                field.setAccessible(false);

            }
        }
    }
}
