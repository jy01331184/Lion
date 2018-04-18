# Lion
<div align=center><img width="512" height="384" src="https://github.com/jy01331184/Lion/blob/master/image/lion.jpg">
</div>
detect the info of the creation of objects.
recurse and invoke chain is supported.

for example..

```Java
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("");
        simpleDateFormat.format(date);

        new Thread("test-thread-01") {
            @Override
            public void run() {
                new Integer(1);
            }
        }.start();
        new Thread("test-thread-02") {
            @Override
            public void run() {
                new Object();
            }
        }.start();
    }
}
```

the summary
<img src="https://github.com/jy01331184/Lion/blob/master/image/s1.png">

and detail for thread-main<br>
<img width="270" height="444" src="https://github.com/jy01331184/Lion/blob/master/image/s2.png">

detail for test-thread-01<br>
<img width="270" height="444" src="https://github.com/jy01331184/Lion/blob/master/image/s3.png">
