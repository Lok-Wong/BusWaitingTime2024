package com.example.umtec_brian.buswaitingtime;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import android.provider.Settings;
import android.content.Context;


public class MainActivity extends AppCompatActivity implements LifecycleObserver {

    String[] placeArray = {"M/永往澳","M/永往氹","澳娛","新濠天地","星際酒店","澳門銀河","威尼斯人","巴黎人","倫敦人","金沙","葡京","上葡京","回力海立方","十六浦"};
    String[] typeList = {"關閘"};
    EditText location, surveyorNo, carPlate_et, carPlate_et2, upPPl_et,upPPl_et2,downppl_et,downppl_et2,leftppl_et,leftppl_et2;
    Button saveButton_1, saveButton_2,stationButton;
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Toast.makeText(this, "不能返回", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判斷版本，應該使用哪個版面
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // Use layout specific to Android 8.0
            setContentView(R.layout.activity_main_v26);
        } else {
            // Use default layout
            setContentView(R.layout.activity_main);
        }
        findView();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                placeArray
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner Spinner = findViewById(R.id.spinner1);
        Spinner.setAdapter(adapter);

        Spinner Spinner2 = findViewById(R.id.spinner2);
        Spinner2.setAdapter(adapter);

        stationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLocationPicker();
            }
        });

        saveButton_1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                if (surveyorNo.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"調查員編號不能為空!",Toast.LENGTH_SHORT).show();
                    surveyorNo.setFocusableInTouchMode(true);
                    surveyorNo.requestFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(surveyorNo, InputMethodManager.SHOW_IMPLICIT);
                    return;
                }

                if (location.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"調查地點不能為空!",Toast.LENGTH_SHORT).show();
                    showLocationPicker();
                    return;
                }

                if (carPlate_et.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"車牌不能為空!",Toast.LENGTH_SHORT).show();
                    carPlate_et.setFocusableInTouchMode(true);
                    carPlate_et.requestFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(carPlate_et, InputMethodManager.SHOW_IMPLICIT);
                    return;
                }

                if (upPPl_et.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"上車人數不能為空!",Toast.LENGTH_SHORT).show();
                    upPPl_et.setFocusableInTouchMode(true);
                    upPPl_et.requestFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(upPPl_et, InputMethodManager.SHOW_IMPLICIT);
                    return;
                }

                if (downppl_et.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"落車人數不能為空!",Toast.LENGTH_SHORT).show();
                    downppl_et.setFocusableInTouchMode(true);
                    downppl_et.requestFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(downppl_et, InputMethodManager.SHOW_IMPLICIT);
                    return;
                }

                if (leftppl_et.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"排隊剩餘人數不能為空!",Toast.LENGTH_SHORT).show();
                    leftppl_et.setFocusableInTouchMode(true);
                    leftppl_et.requestFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(leftppl_et, InputMethodManager.SHOW_IMPLICIT);
                    return;
                }


                try {
                    saveTextExternal((String) Spinner.getSelectedItem(),
                            String.valueOf(carPlate_et.getText()),
                            String.valueOf(upPPl_et.getText()),
                            String.valueOf(downppl_et.getText()),
                            String.valueOf(leftppl_et.getText()),
                            String.valueOf(location.getText()),
                            String.valueOf(surveyorNo.getText()),
                            dateFormat.format(date)
                    );
                    carPlate_et.setText("0000");
                    downppl_et.setText("0");
                    upPPl_et.setText("0");
                    leftppl_et.setText("0");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        saveButton_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                if (surveyorNo.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"調查員編號不能為空!",Toast.LENGTH_SHORT).show();
                    surveyorNo.setFocusableInTouchMode(true);
                    surveyorNo.requestFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(surveyorNo, InputMethodManager.SHOW_IMPLICIT);
                    return;
                }

                if (location.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"調查地點不能為空!",Toast.LENGTH_SHORT).show();
                    showLocationPicker();
                    return;
                }

                if (carPlate_et2.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"車牌不能為空!",Toast.LENGTH_SHORT).show();
                    carPlate_et2.setFocusableInTouchMode(true);
                    carPlate_et2.requestFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(carPlate_et2, InputMethodManager.SHOW_IMPLICIT);
                    return;
                }

                if (upPPl_et2.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"上車人數不能為空!",Toast.LENGTH_SHORT).show();
                    upPPl_et2.setFocusableInTouchMode(true);
                    upPPl_et2.requestFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(upPPl_et2, InputMethodManager.SHOW_IMPLICIT);
                    return;
                }

                if (downppl_et2.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"落車人數不能為空!",Toast.LENGTH_SHORT).show();
                    downppl_et2.setFocusableInTouchMode(true);
                    downppl_et2.requestFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(downppl_et2, InputMethodManager.SHOW_IMPLICIT);
                    return;
                }

                if (leftppl_et2.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"排隊剩餘人數不能為空!",Toast.LENGTH_SHORT).show();
                    leftppl_et2.setFocusableInTouchMode(true);
                    leftppl_et2.requestFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(leftppl_et2, InputMethodManager.SHOW_IMPLICIT);
                    return;
                }

                try {
                    saveTextExternal((String) Spinner2.getSelectedItem(),
                            String.valueOf(carPlate_et2.getText()),
                            String.valueOf(upPPl_et2.getText()),
                            String.valueOf(downppl_et2.getText()),
                            String.valueOf(leftppl_et2.getText()),
                            String.valueOf(location.getText()),
                            String.valueOf(surveyorNo.getText()),
                            dateFormat.format(date)
                    );
                    carPlate_et2.setText("0000");
                    downppl_et2.setText("0");
                    upPPl_et2.setText("0");
                    leftppl_et2.setText("0");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void showLocationPicker() {
        new AlertDialog.Builder(this)
                .setTitle("請選擇調查地點")
                .setItems(typeList, (dialog, which) -> {
                    String choice = typeList[which];
                    location.setText(choice);                     // 顯示結果
                    Toast.makeText(this, "你選了：" + choice,       // (可省略)
                            Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    public final <E extends View> E getView (int id){
        return findViewById(id);
    }

    public void findView(){
        surveyorNo = findViewById(R.id.surveyorNo);
        saveButton_1 = findViewById(R.id.saveButton_1);
        saveButton_2 = findViewById(R.id.saveButton_2);
        carPlate_et = findViewById(R.id.carPlate_et);
        carPlate_et2 = findViewById(R.id.carPlate_et2);
        upPPl_et = findViewById(R.id.upppl_et);
        upPPl_et2 = findViewById(R.id.upppl_et2);
        downppl_et = findViewById(R.id.downppl_et);
        downppl_et2 = findViewById(R.id.downppl_et2);
        leftppl_et = findViewById(R.id.leftppl_et);
        leftppl_et2 = findViewById(R.id.leftppl_et2);
        location = findViewById(R.id.location);
        stationButton = findViewById(R.id.stationButton);
    }

    public void saveTextExternal(String parm1, String parm2, String parm3, String parm4, String parm5,String parm6,String parm7, String parm8) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 9990);   // 使用者必須手動打開開關
                return;
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        Calendar c = Calendar.getInstance();
        int years = c.get(Calendar.YEAR);
        int months = c.get(Calendar.MONTH) + 1;
        int days = c.get(Calendar.DAY_OF_MONTH);

        String surveyNum = surveyorNo.getText().toString();
        String sdcard0Path = Environment.getExternalStorageDirectory().toString();
        String fileDir = String.format(Locale.getDefault(),
                "%s/UMTEC/casino2025/%d/%02d/%02d/%s",
                Environment.getExternalStorageDirectory().getAbsolutePath(),
                years, months, days, surveyNum);
        File dir = new File(fileDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("無法建立資料夾: " + dir);
        }

        if (!dir.mkdirs() && !dir.isDirectory()) {       // 同時檢查已存在與否
            Toast.makeText(this, "Create folder failed, please contact your admin!!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        String deviceId = DeviceUtils.getAndroidId(this);

        String fileName = currentDateandTime + "-" + location.getText().toString() + "-" + deviceId + ".txt";
        String filePath = fileDir +"/" + fileName;
        File file = new File(filePath);
        boolean isFirstRecord = !file.exists() || file.length() == 0;

        try {
            FileOutputStream fos = new FileOutputStream(filePath, true);
            if (isFirstRecord) {
                fos.write(("公司"+"_"+"車牌"+"_"+"排隊人數"+"_"+"鐵欄外人數"+"_"+"停車數"+"_"+"地點"+"_"+"調查員"+"_"+"時間"+",").getBytes());   // Windows 風格；只想要 \n 也行
                fos.write("\r\n".getBytes());
            }
            fos.write((parm1 + "_" + parm2 + "_" + parm3 + "_" + parm4 +"_" + parm5 +"_" + parm6 +"_" + parm7 + "_" + parm8 +",").getBytes());
            fos.write("\r\n".getBytes());
            Toast.makeText(this, "Create success as" + parm8, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();          // 目前有焦點的 View
            if (v instanceof EditText) {
                if (!isTouchInsideView(v, ev)) { // 點擊位置不在該 EditText 內
                    hideKeyboardAndClearFocus(v);
                }
            }
        }
        return super.dispatchTouchEvent(ev);     // 繼續交給系統處理
    }

    // 判斷觸控點是否落在指定 View 內
    private boolean isTouchInsideView(View v, MotionEvent ev) {
        int[] loc = new int[2];
        v.getLocationOnScreen(loc);
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        return x >= loc[0] && x <= loc[0] + v.getWidth() &&
                y >= loc[1] && y <= loc[1] + v.getHeight();
    }

    // 隱藏鍵盤並去除焦點
    private void hideKeyboardAndClearFocus(View v) {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        v.clearFocus();
    }

    public static class DeviceUtils {
        // 獲取 Android ID（推薦）
        public static String getAndroidId(Context context) {
            String androidId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );
            return androidId;
        }
    }

}
    
