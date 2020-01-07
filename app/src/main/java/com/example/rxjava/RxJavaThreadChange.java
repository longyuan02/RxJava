package com.example.rxjava;


import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RxJavaThreadChange {
    private String TAG = RxJavaThreadChange.class.getSimpleName();

    /* 单线程内执行 */
    public void StartTest() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.e(TAG, "===create: " + Thread.currentThread().getName());
                emitter.onNext("1");
            }
        }).map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                Log.e(TAG, "===String -> Integer: " + Thread.currentThread().getName() + "**" + s);
                return Integer.valueOf(s);
            }
        }).flatMap(new Function<Integer, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(final Integer integer) throws Exception {
                Log.e(TAG, "===Integer->Observable: " + Thread.currentThread().getName());
                return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        Log.e(TAG, "===Observable<String> call: " + Thread.currentThread().getName());
                        for (int i = 0; i < integer; i++) {
                            emitter.onNext(i + "");
                        }
                        emitter.onComplete();
                    }
                });
            }
        }).map(new Function<Object, Object>() {
            @Override
            public Object apply(Object o) throws Exception {
                Log.e(TAG, "===String->Long: " + Thread.currentThread().getName());
                return Long.parseLong(o.toString());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {


                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(TAG, "===onNext: " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*线程切换执行 通过observeOn(Schedulers.io())|observeOn(AndroidSchedulers.mainThread())切换*/
    public void ThreadTest2() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.e(TAG, "===create: " + Thread.currentThread().getName());
                emitter.onNext("1");
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) throws Exception {
                        Log.e(TAG, "===String -> Integer: " + Thread.currentThread().getName() + "**" + s);
                        return Integer.valueOf(s);
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<Integer, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final Integer integer) throws Exception {
                        Log.e(TAG, "===Integer->Observable: " + Thread.currentThread().getName());
                        return Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                Log.e(TAG, "===Observable<String> call: " + Thread.currentThread().getName());
                                for (int i = 0; i < integer; i++) {
                                    emitter.onNext(i + "");
                                }
                                emitter.onComplete();
                            }
                        });
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) throws Exception {
                        Log.e(TAG, "===String->Long: " + Thread.currentThread().getName());
                        return Long.parseLong(o.toString());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(TAG, "===onNext: " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 切换线程,线程切换后,顺位线程保持切换不变
     */
    public void setChangeThread() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.e(TAG, "===create:" + Thread.currentThread().getName());
                emitter.onNext("1");
//                emitter.onNext("2");
            }
        }).map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                Log.e(TAG, "===String -> Integer: " + Thread.currentThread().getName() + "***" + s);
                return Integer.valueOf(s);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                Log.e(TAG, "===Integer->Observable: " + Thread.currentThread().getName() + "***" + integer);
                return ForEach(integer);
            }
        }).map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                Log.e(TAG, "===String2 -> Integer: " + Thread.currentThread().getName() + "***" + s);
                return Integer.valueOf(s);
            }
        })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "===onNext: " + Thread.currentThread().getName() + "**" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<String> ForEach(final int integer) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.e(TAG, "===Observable<String> call: " + Thread.currentThread().getName() + "**size" + integer);
                for (int i = 0; i < integer; i++) {
                    emitter.onNext(i + "");
                    Log.e("emitter:", "emitter times:" + i);
                }
                emitter.onNext("1");
//                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());/*向下传递*/
    }
}
