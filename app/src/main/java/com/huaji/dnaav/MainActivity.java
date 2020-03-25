package com.huaji.dnaav;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
//这里是由知乎大佬不知道用什么方法得出的转换用数字
    static int ss[] = {11, 10, 3, 8, 4, 6, 2, 9, 5, 7};
    static long xor = 177451812; //二进制时加减数1
    static long add = 8728348608l;//十进制时加减数2
    //变量初始化工作，加载哈希表
    private static String table = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF";
    private static HashMap<String, Integer> mp = new HashMap<>();
    private static HashMap<Integer, String> mp2 = new HashMap<>();
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private TextInputEditText avtobv;
    private TextInputEditText bvtoav;
    private TextInputLayout avinput;
    private TextInputLayout bvinput;
    private ClipboardManager cm;
    private ClipData mClipData;

    //现在，定义av号和bv号互转的方法
    //定义一个power乘方方法，这是转换进制必要的
    public static long power(int a, int b) {
        long power = 1;
        for (int c = 0; c < b; c++)
            power *= a;
        return power;
    }
//bv转av方法
    public static String b2v(String s) {
        long r = 0;
        //58进制转换
        for (int i = 0; i < 58; i++) {
            String s1 = table.substring(i, i + 1);
            mp.put(s1, i);
        }
        for (int i = 0; i < 6; i++) {
            r = r + mp.get(s.substring(ss[i], ss[i] + 1)) * power(58, i);
        }
        //转换完成后，需要处理，带上两个随机数
        return "av" + ((r - add) ^ xor);
    }
//av转bv方法
    public static String v2b(String st) {
        long s = Long.valueOf(st.split("av")[1]);
        StringBuffer sb = new StringBuffer("BV1  4 1 7  ");
        //逆向思路，先将随机数还原
        s = (s ^ xor) + add;
        //58进制转回
        for (int i = 0; i < 58; i++) {
            String s1 = table.substring(i, i + 1);
            mp2.put(i, s1);
        }
        for (int i = 0; i < 6; i++) {
            String r = mp2.get((int) (s / power(58, i) % 58));
            sb.replace(ss[i], ss[i] + 1, r);
        }
        return sb.toString();
    }
//活动的oncreate方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_slideshow,
                R.id.nav_tools)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
//fragment控制器
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
//初始化菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //设定点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                break;
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    //现在，指定转换按钮的点击事件
    public void atb(View view) {   //av号转换bv号
        avinput = findViewById(R.id.textInputLayoutav);
        avtobv = (TextInputEditText) findViewById(R.id.avn);
        String av = avtobv.getText().toString();
        long avn1 = Long.parseLong(av);
        //av号是绝对不可能大于2的32次方的，不然此算法也将作废
        if (avn1 >= Math.pow(2, 32)) {
            Snackbar.make(view, "你输的数也太大了点吧喂！", Snackbar.LENGTH_SHORT).show();
            avinput.setError("你输的数也太大了点吧喂！");
            avinput.setErrorEnabled(true);
        } else {
            try {
                avinput.setErrorEnabled(false);
                final String bv = v2b("av" + av);
                Snackbar.make(view, "BV号是：" + bv, Snackbar.LENGTH_SHORT)
                        .setAction("复制", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                mClipData = ClipData.newPlainText("Label", bv);
                                cm.setPrimaryClip(mClipData);
                                Toast.makeText(MainActivity.this, "已经复制到剪切板了", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
                avinput.setError("你输入的av号有误");
                avinput.setErrorEnabled(true);
                Snackbar.make(view, "你输入的av号有误", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public void bta(View view) {   //bv号转换av号
        bvinput = findViewById(R.id.textInputLayoutbv);
        bvtoav = (TextInputEditText) findViewById(R.id.bvn);
        String bv = bvtoav.getText().toString();
        if (bv.startsWith("BV")) {//先看看你有没有把bv带进来
            char bv7 = bv.charAt(9);
            //判断bv号的格式是否正确，防止你瞎输
            if ((bv.indexOf("1") == 2) && (bv7 == '7'))
                try {
                    avinput.setErrorEnabled(false);
                    final String av = b2v(bv);
                    Snackbar.make(view, "av号是：" + av, Snackbar.LENGTH_SHORT)
                            .setAction("复制", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    mClipData = ClipData.newPlainText("Label", av);
                                    cm.setPrimaryClip(mClipData);
                                    Toast.makeText(MainActivity.this, "已经复制到剪切板了", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                    bvinput.setErrorEnabled(true);
                    bvinput.setError("你输入的bv号有误");
                    Snackbar.make(view, "你输入的bv号有误", Snackbar.LENGTH_SHORT).show();
                }
            else {//如果格式不对的话就揍你一顿
                bvinput.setErrorEnabled(true);
                bvinput.setError("你输入的bv号有误");
                Snackbar.make(view, "你输入的bv号有误", Snackbar.LENGTH_SHORT).show();
            }
        } else {//如果你不是用bv开头的
            char bv7 = bv.charAt(7);
            //判断格式是否正确
            if ((bv.indexOf("1") == 0) && (bv7 == '7'))
                try {
                    avinput.setErrorEnabled(false);
                    final String av = b2v("BV" + bv);
                    Snackbar.make(view, "av号是：" + av, Snackbar.LENGTH_SHORT)
                            .setAction("复制", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    mClipData = ClipData.newPlainText("Label", av);
                                    cm.setPrimaryClip(mClipData);
                                    Toast.makeText(MainActivity.this, "已经复制到剪切板了", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                    bvinput.setErrorEnabled(true);
                    bvinput.setError("你输入的bv号有误");
                    Snackbar.make(view, "你输入的bv号有误", Snackbar.LENGTH_SHORT).show();
                }
            else {//格式不对就揍你一顿
                bvinput.setErrorEnabled(true);
                bvinput.setError("你输入的bv号有误");
                Snackbar.make(view, "你输入的bv号有误", Snackbar.LENGTH_SHORT).show();
            }

        }
    }
}
