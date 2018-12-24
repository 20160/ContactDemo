package com.test.contest;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.test.contest.util.ContactUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MainActivity mActivitySupport;
    private AppCompatEditText etName;
    private Map<String, List<String>> contactModels;
    private boolean hasPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivitySupport = this;
        etName = (AppCompatEditText) findViewById(R.id.et_name);
        findViewById(R.id.btn_upload).setOnClickListener(this);
        contactModels = new HashMap<>();

        PermissionListener listener = new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, List<String> grantedPermissions) {
                // Successfully.
                hasPermission = true;
            }

            @Override
            public void onFailed(int requestCode, List<String> deniedPermissions) {
                // Failure.
            }
        };
        AndPermission.with(getApplicationContext())
                .permission(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .requestCode(200)
                .callback(listener)
                .start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_upload:
                if (hasPermission) {

                    if (TextUtils.isEmpty(etName.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "请输入备份文件名", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final AlertDialog alertDialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivitySupport);
                    builder.setTitle("提示");
                    builder.setMessage("确定备份通讯录到手机？");
                    builder.setCancelable(false);
                    alertDialog = builder.create();
                    alertDialog.setButton(BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.setButton(BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ContactUtil.getLocalContactsInfos(contactModels, getApplicationContext());
                            ContactUtil.getSIMContactsInfos(contactModels, getApplicationContext());
                            StringBuilder stringBuilder = new StringBuilder();

                            Set<String> keySet = contactModels.keySet();
                            for (String key : keySet) {
                                stringBuilder.append(key);
                                stringBuilder.append("：");
                                String[] strs = new String[contactModels.get(key).size()];
                                contactModels.get(key).toArray(strs);
                                for (String string : strs) {
                                    stringBuilder.append(string);
                                    stringBuilder.append(" ");
                                }
                                stringBuilder.append("\n");
                            }

                            try {
                                saveToSD(etName.getText().toString(), stringBuilder.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    alertDialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "请允许APP相关权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }




    /**
     * 文件保存在SD卡上
     *
     * @param name    文件名称
     * @param content 文件内容
     * @throws Exception
     */

    public void saveToSD(String name, String content) throws Exception {
        //获取外部设备
        //Environment.getExternalStorageDirectory() 获取SD的路径

        File path = new File(Environment.getExternalStorageDirectory() + File.separator + "通讯录备份");
        if (!path.exists()) {
            if (!path.mkdirs()) {
                return;
            }
        }

        File file = new File(path, name);
        FileOutputStream outStream = new FileOutputStream(file);
        //写入文件
        outStream.write(content.getBytes());
        outStream.close();
        final AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivitySupport);
        builder.setTitle("提示");
        builder.setMessage("成功，备份文件：\"手机存储/通讯录备份\"");
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setButton(BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
