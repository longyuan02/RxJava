[TOC]
### RxJava -ABSubscriber
<pre><code>private Observable observable = Observable.timer(5, TimeUnit.SECONDS).create(new ObservableOnSubscribe<String>() {
                   @Override
                   public void subscribe(ObservableEmitter<String> e) throws Exception {
                       e.onNext("message1");
                       e.onNext("message2");
                       e.onNext("message3");
                       e.onNext("message4");
                       e.onNext("message5");
                   }
               });
               Observer<String> reader = new Observer<String>() {
                   @Override
                   public void onSubscribe(Disposable d) {
           //            mDisposable = d;
                       Log.e(TAG, "onSubscribe");
                   }
           
                   @Override
                   public void onNext(String value) {
                       Log.e(TAG, "onNext=" + value);
                   }
           
                   @Override
                   public void onError(Throwable e) {
                       Log.e(TAG, "onError=" + e.getMessage());
                   }
           
                   @Override
                   public void onComplete() {
                       Log.e(TAG, "onComplete()");
                   }
               };
           
               //链式调用
               private void linkedObservable() {
                   Observable.create(new ObservableOnSubscribe<String>() {
                       @Override
                       public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                           for (int i = 1; i < 4; i++) {
                               Log.d("TAG", "我是小说，我更新了第" + i + "季");
                               emitter.onNext(i + "");
                           }
                       }
                   }).observeOn(Schedulers.io())
                           .subscribeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Observer<String>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   Log.d("TAG", "我是读者，我和小说订阅了");
                               }
           
                               @Override
                               public void onNext(String value) {
                                   Log.d("TAG", "我是读者，我拿到了小说的新版本：" + value + "版本");
                               }
           
                               @Override
                               public void onError(Throwable e) {
           
                               }
           
                               @Override
                               public void onComplete() {
                                   Log.d("TAG", "我是读者，小说的新版本被我拿完了");
                               }
                           });
               }
           
               /* just队列完成后直接调用onComplete*/
               private void just() {
                   Observable.just("1", "2", "3", "4").subscribe(reader);
               }
           
               /*传入数组*/
               private void array() {
                   String[] numbers = {"arry1", "array2", "array3"};
                   Observable.fromArray(numbers).subscribe(reader);
               }
           
               /*list列表*/
               private void list() {
                   ArrayList<String> arrayList = new ArrayList<>();
                   arrayList.add("list1");
                   arrayList.add("list2");
                   arrayList.add("list3");
                   arrayList.add("list4");
                   Observable.fromIterable(arrayList).subscribe(reader);
               }
           
               /**
                * never:不发送任何事件
                * empty:只发送Complete事件，即emitter.complete()
                * error():发送一个异常，传入error（）中
                */
               /* 延时 */
               private void delay() {
                   Disposable disposable = Observable.timer(4, TimeUnit.SECONDS)
                           .subscribeOn(Schedulers.io())
                           .unsubscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Consumer<Long>() {
                               @Override
                               public void accept(Long aLong) throws Exception {
                                   Log.e("111======", "2222");
                               }
                           });
                   CompositeDisposable compositeDisposable = new CompositeDisposable();
                   compositeDisposable.add(disposable);
           
               }
           
               private void Flowable() {
                   /**
                    * 使用Subscriber 需要版本
                    * implementation 'io.reactivex.rxjava2:rxjava:2.2.1'
                    * implementation 'io.reactivex.rxjava2:rxandroid:2.1.0'
                    */
                   Flowable.create(new FlowableOnSubscribe<Integer>() {
                       @Override
                       public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                           Log.d("TAG", "发送事件 1");
                           emitter.onNext(1);
                           Log.d("TAG", "发送事件 2");
                           emitter.onNext(2);
                           Log.d("TAG", "发送事件 3");
                           emitter.onNext(3);
                           Log.d("TAG", "发送完成");
           
                           Log.d("TAG", "发送事件 4");
                           emitter.onNext(4);
                           Log.d("TAG", "发送事件 5");
                           emitter.onNext(5);
           
                           emitter.onComplete();
                       }
                   }, BackpressureStrategy.ERROR)
                           .subscribeOn(Schedulers.io()) // 设置被观察者在io线程中进行
                           .observeOn(AndroidSchedulers.mainThread()) // 设置观察者在主线程中进行
                           .subscribe(new Subscriber<Integer>() {
                               // 步骤2：创建观察者 =  Subscriber & 建立订阅关系
                               @Override
                               public void onSubscribe(Subscription s) {
                                   // 对比Observer传入的Disposable参数，Subscriber此处传入的参数 = Subscription
                                   // 相同点：Subscription参数具备Disposable参数的作用，
                                   // 即Disposable.dispose()切断连接, 同样的调用Subscription.cancel()切断连接
                                   // 不同点：Subscription增加了void request(long n)
                                   // 作用：决定观察者能够接收多少个事件
                                   // 如设置了s.request(3)，这就说明观察者能够接收3个事件（多出的事件存放在缓存区）
                                   // 官方默认推荐使用Long.MAX_VALUE，即s.request(Long.MAX_VALUE);
                                   Log.d("TAG", "onSubscribe");
                                   s.request(3);
                                   /**如果在异步的情况中request（）没有参数，则认为观察者不接受事件
                                    * 被观察者可以继续发送事件存到缓存区（缓存区大小=128）
                                    * */
                               }
           
                               @Override
                               public void onNext(Integer integer) {
                                   Log.d("TAG", "接收到了事件" + integer);
                               }
           
                               @Override
                               public void onError(Throwable t) {
                                   Log.w("TAG", "onError: ", t);
                               }
           
                               @Override
                               public void onComplete() {
                                   Log.d("TAG", "onComplete");
                               }
                           });
               }</code></pre>


### ScheduledThreadPoolExecutor 定时任务线程池 -SecondeActivity
<pre><code>/**
                * 开关线程池
                */
               private void actionScheduledTask() {
                   if (isTimer) {
                       startScheduledTask();
                   } else {
                       if (!scheduleTaskExecutor.isShutdown()) {
                           scheduleTaskExecutor.shutdown();
                       }
                   }
                   isTimer = !isTimer;
               }
           
               // 开启执行
               private void startScheduledTask() {
                   scheduleTaskExecutor = new ScheduledThreadPoolExecutor(2, new ThreadFactory() {
                       private AtomicInteger atoInteger = new AtomicInteger(0);
           
                       @Override
                       public Thread newThread(Runnable r) {
                           Thread t = new Thread(r);
           //                t.setName("App-Thread" + atoInteger.getAndIncrement());
                           return t;
                       }
                   });
                   //开启第一个线程
                   scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                       @Override
                       public void run() {
                           Log.e("startScheduled=====", "======1");
                       }
                   }, 0, 6, TimeUnit.SECONDS);
                   //开启第二个线程
                   scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                       @Override
                       public void run() {
                           Log.e("startScheduled=====", "======2");
                       }
                   }, 0, 3, TimeUnit.SECONDS);
               }
           
               @Override
               protected void onDestroy() {
                   super.onDestroy();
                   if (scheduleTaskExecutor != null && !scheduleTaskExecutor.isShutdown()) {
                       scheduleTaskExecutor.shutdown();
                   }
               }</code></pre>