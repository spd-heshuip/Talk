package com.example.shuip.talk.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.shuip.talk.R;
import com.example.shuip.talk.sys.TalkApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by Administrator on 15-9-14.
 */
public class ImageUtil {

    public static void displayImage(String url,ImageView imageView,DisplayImageOptions options){
        TalkApplication.getInstance().getImageLoader().displayImage(url,imageView,options);
    }

    public static void displayImage(String url,ImageView imageView){
        TalkApplication.getInstance().getImageLoader().displayImage(url,imageView);
    }

    public static void displayImageUseDefOption(String url,ImageView imageView){
        TalkApplication.getInstance().getImageLoader().displayImage(url,imageView,getDisplayOptions());
    }

    public  static DisplayImageOptions getDisplayOptions(){

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_head) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.default_head)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.default_head)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.NONE)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
//                .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                .displayer(new RoundedBitmapDisplayer(5))
                .build();//构建完成

        return options;
    }
}
