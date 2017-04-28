package com.handongkeji.installapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PackageManager packageManager;
    private List<PackageInfo> inpg;
    private Toolbar toolbar;
    private EditText editText;
    private ListView lv;
    private AppAdapter appAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        lv = (ListView) findViewById(R.id.lv);

        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar!=null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.action_search){
                    showSearchDialog();
                }


                return true;
            }
        });

        setTitle("已安装应用");
        isInstalled();
    }


    //搜索引用
    private void showSearchDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.edit_search, null);

        editText = (EditText) view.findViewById(R.id.edt);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        search();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();


    }

    private void search() {

        ProgressDialog dialog = ProgressDialog.show(this, null, "搜索中...");

        String trim = editText.getText().toString().trim();
        if(inpg!=null){
            for (int i = 0; i < inpg.size(); i++) {

                PackageInfo packageInfo = inpg.get(i);
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();


                if(appName.equals(trim)){
                    dialog.dismiss();

                    appAdapter.select(i);
                    lv.setSelection(i);
                    return;
                }
            }
        }

        dialog.dismiss();
        Toast.makeText(this, "没找到应用...", Toast.LENGTH_SHORT).show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main,menu);

        return super.onCreateOptionsMenu(menu);
    }

    private static final String TAG = "MainActivity";

    private void isInstalled() {


        packageManager = getPackageManager();
        inpg = packageManager.getInstalledPackages(0);
        if (inpg != null) {
            toolbar.setTitle("已安装引用："+inpg.size()+"个");


            appAdapter = new AppAdapter(this, inpg);
            lv.setAdapter(appAdapter);
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PackageInfo packageInfo = inpg.get(i);
                String packageName = packageInfo.packageName;

                Intent intent = packageManager.getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    startActivity(intent);

                }else {
                    Toast.makeText(MainActivity.this, "无法打开！", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    private class AppAdapter extends QuickAdapter<PackageInfo> {


        private int poi=-1;

        public void select(int position){

            poi = position;
            notifyDataSetChanged();
        }


        AppAdapter(Context context, List<PackageInfo> data) {
            super(context, R.layout.item_app, data);
        }

        @Override
        protected void convert(BaseAdapterHelper helper, PackageInfo packageInfo) {
            String packageName = packageInfo.packageName;

            int position = helper.getPosition();



            View view = helper.getView(R.id.tv_package_name);
            View view1 = helper.getView(R.id.tv_app_name);
            if(position==poi){
                view.setSelected(true);
                view1.setSelected(true);
                helper.getView().setBackgroundColor(Color.CYAN);


            }else {
                view.setSelected(false);
                view1.setSelected(false);
                helper.getView().setBackgroundColor(Color.WHITE);
            }


            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            String appName = applicationInfo.loadLabel(packageManager).toString();
            Drawable drawable = applicationInfo.loadIcon(packageManager);
            helper.setText(R.id.tv_app_name, "应用名称：" + appName)
                    .setText(R.id.tv_package_name, "报名" + packageName)
                    .setImageDrawable(R.id.iv_icon, drawable);


        }
    }
}
