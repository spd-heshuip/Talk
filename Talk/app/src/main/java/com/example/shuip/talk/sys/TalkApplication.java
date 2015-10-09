package com.example.shuip.talk.sys;

import android.app.Application;

import com.example.shuip.talk.db.dao.DaoMaster;
import com.example.shuip.talk.db.dao.DaoSession;
import com.example.shuip.talk.model.Member;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by Administrator on 15-9-8.
 */
public class TalkApplication extends Application{
    private volatile static TalkApplication mInstance;
    private Member mLoginMember;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        if (mInstance == null)
            mInstance = this;
    }

    public static synchronized TalkApplication getInstance(){
        return mInstance;
    }

    public Member getLoginMember() {
        return mLoginMember;
    }

    public void setLoginMember(Member mLoginMember) {
        this.mLoginMember = mLoginMember;
    }

    public DaoMaster getmDaoMaster() {
        if (mDaoMaster == null){
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(),Constant.DB_NAME,null);
            mDaoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return mDaoMaster;
    }

    public DaoSession getmDaoSession() {
        if (mDaoSession == null){
            if (mDaoMaster == null){
                mDaoMaster = getmDaoMaster();
            }
            mDaoSession = mDaoMaster.newSession();
        }
        return mDaoSession;
    }

    public ImageLoader getImageLoader(){
        if (mImageLoader == null){
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.init(initImgloadConf());
        }
        return mImageLoader;
    }

    private ImageLoaderConfiguration initImgloadConf() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "img/cache");

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCacheExtraOptions(1024, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                        // .diskCacheExtraOptions(480, 800,null)  // Can slow ImageLoader, use it carefully (Better don't use it)/设置缓存的详细信息，最好不要设置这个
                .threadPoolSize(3)//线程池内加载的数量,最好是1-5
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()

                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(20)
//
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100) //缓存的文件数量
                .diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径

                        // 将缓存下来的文件以什么方式命名
                        // 里面可以调用的方法有
                        // 1.new Md5FileNameGenerator() //使用MD5对UIL进行加密命名
                        // 2.new HashCodeFileNameGenerator()//使用HASHCODE对UIL进行加密命名
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)

                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(this, 20 * 1000, 30 * 1000)) // connectTimeout (20 s), readTimeout (30 s)超时时间
                .imageDecoder(new BaseImageDecoder(false)) // default
                .writeDebugLogs() // Remove for release app
                .build();//开始构建

        return config;
    }

}
