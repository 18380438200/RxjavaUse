package com.example.rxjavause;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import java.util.ArrayList;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.iv);
        //init();
        map3();
        /**
         * 例子大概过程是：代码调用了onSubscribe的call方法-->执行for循环-->通过call(Subscriber subscriber)传入的subscriber发送结果-->Subscriber的onNext等方法中订阅想要的结果。
         */
    }

    private void init() {

        Observable observable = Observable.create(new Observable.OnSubscribe<String>(){

            @Override
            public void call(Subscriber<? super String> subscriber) {
                //给观察者发送事件
                subscriber.onStart();
                subscriber.onNext("你好");
                subscriber.onNext("在吗");
                subscriber.onNext("中午吃什么？");
                subscriber.onCompleted();
            }
        });

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onNext(String s) {
                Log.i("minfo",s);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                Log.i("minfo",s);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };

        observable.subscribe(subscriber);
        observable.subscribe(observer);

        //其他创建Observable方式
        //just
//        Observable observableJust = Observable.just("How are you?","I am fine.","Thank you.","And you?");
//        observableJust.subscribe(observer);

        //from
//        String[] strs = {"How are you?","I am fine.","Thank you.","And you?"};
//        Observable observableFrom = Observable.from(strs);
//        observableFrom.subscribe(subscriber);

        showImage();

        setScheduler();

    }

    private void showImage(){
        //即使加载图片耗费了几十甚至几百毫秒的时间，也不会造成丝毫界面的卡顿。
        final int[] imgRes = {R.mipmap.filter_thumb_antique,R.mipmap.filter_thumb_beauty,R.mipmap.filter_thumb_blackcat,R.mipmap.filter_thumb_emerald};

        Observable.create(new Observable.OnSubscribe<Drawable>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                for(int i=0;i<imgRes.length;i++){
                    subscriber.onNext(getDrawable(imgRes[i]));
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())  //指定事件发生在IO线程
          .observeOn(AndroidSchedulers.mainThread())  //指定subscriber的回调发生在主线程
          .subscribe(new Observer<Drawable>() {
            @Override
            public void onNext(Drawable drawable) {
                imageView.setImageDrawable(drawable);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
//                Toast.makeText(activity, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 后天线程获取数据，主线程设置数据
     */
    private void setScheduler(){
        Observable.just(1,2,3,4)
                .subscribeOn(Schedulers.io())  //指定事件发生在IO线程
                .observeOn(AndroidSchedulers.mainThread())  //指定subscriber的回调发生在主线程
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {

                    }
                });
    }

    //map(): 事件对象的直接变换
    private void userMap(){
        Observable.just("../image/logo.png")
        .map(new Func1<String, Bitmap>() {
            @Override
            public Bitmap call(String filePath) {
                return BitmapFactory.decodeFile(filePath);
            }
        })
        .subscribe(new Action1<Bitmap>() {
            @Override
            public void call(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    //from需要传入是Iterable或者数组
    private void map2(){
        ArrayList<Tweet> tweets = new ArrayList<>();
        Subscriber subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String img) {
                Log.i("minfo",img);
            }
        };

        Observable
                .from(tweets)
                .map(new Func1<Tweet, String>() {

                    @Override
                    public String call(Tweet tweet) {
                        return tweet.getImg();
                    }
                })
                .subscribe(subscriber);
    }

    class Tweet{
        private int id;
        private String name;
        private String img;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }


    private void map3(){
        Observable.create(new Observable.OnSubscribe<Integer>(){

            @Override
            public void call(Subscriber subscriber) {
                for(int i=0;i<3;i++){
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).map(new Func1<Integer, Object>() {
            @Override
            public Object call(Integer integer) {
                return integer%2 == 0;
            }
        }).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                Log.i("minfo", String.valueOf(o));
            }
        });
    }

    private void userFrom(){
        String[] array={"How are you?","I am fine.","Thank you.","And you?"};
        Observable.from(array)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(String s) {
                        Log.i("TAG", "onNext" + s);
                    }
                });
    }

    private void userFilter(){
        Observable.just(2,5,25,6,9,14,34,51)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer%2 == 0;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(Integer integer) {
                        Log.i("TAG", "onNext" + integer);
                    }
                });
    }

    private void userMerge(){
        Observable<Integer> observable1 = Observable.just(22, 4, 23,13);
        Observable<Integer> observable2 = Observable.just(15, 24, 3,66);
        Observable.merge(observable1,observable2)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(Integer integer) {
                        Log.i("TAG", "onNext" + integer);
                    }
                });
    }

}
