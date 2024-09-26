package com.example.umtec_brian.buswaitingtime;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LifecycleObserver {

    EditText surveyorNo, location, route1,route2, route3, startTime_1, startTime_2, startTime_3,endTime_1,endTime_2,endTime_3,licensePlate_1,licensePlate_2,licensePlate_3;
    Button startButton_1,startButton_2,startButton_3, endButton_1, endButton_2, endButton_3, saveButton_1, saveButton_2, saveButton_3 , stationButton;
    RadioGroup genderGroup_1, ageGroup_1, genderGroup_2 , ageGroup_2, genderGroup_3 , ageGroup_3, surveyType_rbg;
    RadioButton genderM_1, genderF_1, under20_1, from20to45_1, above45_1,
                genderM_2, genderF_2, under20_2, from20to45_2, above45_2,
                genderM_3, genderF_3, under20_3, from20to45_3, above45_3,
                rb1,rb2,rb3,
                surveyType_1, surveyType_2, surveyType_3;
    TextView textView1 , textView2, textView15,textView16,textView17;
    String provider;
    Spinner spinner1,spinner2,spinner3;
    LocationManager locationManager ;
    double lat, lon;
    String[] busNumArray = {"請選擇","25B","25BS","50","102X","701X (往望德聖母灣)","701X (往澳大)","其它"};
    String[] typeList = {"普通","101x/102x","橫琴"};

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Toast.makeText(this, "不能返回", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        FindView();
        initialization();
        checkPermission();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, busNumArray
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new Spinner1Class());

        spinner2.setAdapter(adapter);
        spinner2.setOnItemSelectedListener(new Spinner2Class());

        spinner3.setAdapter(adapter);
        spinner3.setOnItemSelectedListener(new Spinner3Class());


        licensePlate_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }


            @Override
            public void afterTextChanged(Editable editable) {
                boolean matches = editable.toString().matches("[A-Za-z][A-Za-z][\\d]{4}");
                if (!matches){
                    textView15.setTextColor(getResources().getColor(R.color.red));
                } else {
                    textView15.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if (editable.length()==0) {
                    textView15.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        licensePlate_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }


            @Override
            public void afterTextChanged(Editable editable) {

                boolean matches = editable.toString().matches("[A-Za-z][A-Za-z][\\d]{4}");
                if (!matches){
                    textView16.setTextColor(getResources().getColor(R.color.red));

                } else {
                    textView16.setTextColor(getResources().getColor(R.color.colorPrimary));

                }
                if (editable.length()==0) {
                    textView16.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        licensePlate_3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }


            @Override
            public void afterTextChanged(Editable editable) {
                boolean matches = editable.toString().matches("[A-Za-z][A-Za-z][\\d]{4}");
                if (!matches){
                    textView17.setTextColor(getResources().getColor(R.color.red));

                } else {
                    textView17.setTextColor(getResources().getColor(R.color.colorPrimary));

                }
                if (editable.length()==0) {
                    textView17.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        startTime_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
              Boolean matchFormat = editable.toString().matches("[0-2]{2}[0-9]{2}\\/[0-1][0-9]\\/[0-2][0-9][[:blank:]][0-2][0-9]:[0-5][0-9]:[0-5][0-9]");
              if (!matchFormat){
                  startTime_1.setTextColor(getResources().getColor(R.color.red));
              }else {
                  startTime_1.setTextColor(getResources().getColor(R.color.colorPrimary));
              }

              if (editable.toString().length() == 0){
                  startTime_1.setTextColor(getResources().getColor(R.color.colorPrimary));
              }
            }
        });

        startTime_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Boolean matchFormat = editable.toString().matches("[0-2]{2}[0-9]{2}\\/[0-1][0-9]\\/[0-2][0-9][[:blank:]][0-2][0-9]:[0-5][0-9]:[0-5][0-9]");
                if (!matchFormat){
                    startTime_2.setTextColor(getResources().getColor(R.color.red));
                }else {
                    startTime_2.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if (editable.toString().length() == 0){
                    startTime_2.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        startTime_3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Boolean matchFormat = editable.toString().matches("[0-2]{2}[0-9]{2}\\/[0-1][0-9]\\/[0-2][0-9][[:blank:]][0-2][0-9]:[0-5][0-9]:[0-5][0-9]");
                if (!matchFormat){
                    startTime_3.setTextColor(getResources().getColor(R.color.red));
                }else {
                    startTime_3.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if (editable.toString().length() == 0){
                    startTime_3.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        surveyType_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                initialization();
            }
        });

        surveyType_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                initialization();
            }
        });

        stationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (surveyType_1.isChecked())
                {
                    locationList(location, "請選擇站點︰", station.location);
                }
                if (surveyType_2.isChecked()){
                    locationList(location, "請選擇站點︰", station.location_101x);
                }
                if (surveyType_3.isChecked()){
                    locationList(location, "請選擇站點︰", station.location_hengqin);

                }
            }
        });

        surveyType_rbg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.hengqin_rb){
                    spinner1.setVisibility(View.VISIBLE);
                    spinner2.setVisibility(View.VISIBLE);
                    spinner3.setVisibility(View.VISIBLE);
                    route1.setVisibility(View.GONE);
                    route2.setVisibility(View.GONE);
                    route3.setVisibility(View.GONE);
                }
                else {
                    spinner1.setVisibility(View.GONE);
                    spinner2.setVisibility(View.GONE);
                    spinner3.setVisibility(View.GONE);
                    route1.setVisibility(View.VISIBLE);
                    route2.setVisibility(View.VISIBLE);
                    route3.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void checkPermission(){
        final int PERMISSION_ALL = 1;
        final String[] PERMISSIONS = {
                WRITE_EXTERNAL_STORAGE,
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
        };
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public void locationList(final TextView textView,final String title, final String[] list){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setItems(list , new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,  int which) {

                textView.setText(list[which]);

            }
        });
        alertDialog.show();
    }

    public final <E extends View> E getView(int id) {
        return (E) findViewById(id);
    }

    public void initialization(){
        genderM_1.setChecked(true);
        genderM_2.setChecked(true);
        genderM_3.setChecked(true);
        under20_1.setChecked(true);
        under20_2.setChecked(true);
        under20_3.setChecked(true);

        startTime_1.setText("");
        startTime_2.setText("");
        startTime_3.setText("");
        endTime_1.setText("");
        endTime_2.setText("");
        endTime_3.setText("");

        startTime_1.setEnabled(true);
        startTime_2.setEnabled(true);
        startTime_3.setEnabled(true);

//        endTime_1.setEnabled(false);
//        endTime_2.setEnabled(false);
//        endTime_3.setEnabled(false);
        location.setText("");

        licensePlate_1.setText("");
        licensePlate_2.setText("");
        licensePlate_3.setText("");

        route1.setText("");
        route2.setText("");
        route3.setText("");

        spinner1.setSelection(0);
        spinner2.setSelection(0);
        spinner3.setSelection(0);


//        surveyType_1.setChecked(true);

        location.setEnabled(false);
    }

    public void markTime(View view){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formatTime = simpledateformat.format(currentTime);
        switch (view.getId()){
            case R.id.startButton_1:
                startTime_1.setText(formatTime);
                break;
            case R.id.startButton_2:
                startTime_2.setText(formatTime);
                break;
            case R.id.startButton_3:
                startTime_3.setText(formatTime);
                break;
            case R.id.endButton_1:
                endTime_1.setText(formatTime);
                break;
            case R.id.endButton_2:
                endTime_2.setText(formatTime);
                break;
            case R.id.endButton_3:
                endTime_3.setText(formatTime);
                break;
            case R.id.saveButton_1:
                int selectedID_1 = genderGroup_1.getCheckedRadioButtonId();
                rb1 = findViewById(selectedID_1);
                saveData(1, route1.getText().toString(), startTime_1.getText().toString(),endTime_1.getText().toString(), licensePlate_1.getText().toString(),rb1.getText().toString());
                break;
            case R.id.saveButton_2:
                int selectedID_2 = genderGroup_2.getCheckedRadioButtonId();
                rb2 = findViewById(selectedID_2);
                saveData(2, route2.getText().toString(), startTime_2.getText().toString(),endTime_2.getText().toString(), licensePlate_2.getText().toString(),rb2.getText().toString());
                break;
            case R.id.saveButton_3:
                int selectedID_3 = genderGroup_3.getCheckedRadioButtonId();
                rb3 = findViewById(selectedID_3);
                saveData(3, route3.getText().toString(), startTime_3.getText().toString(),endTime_3.getText().toString(), licensePlate_3.getText().toString(),rb3.getText().toString());
                break;
        }

    }

    public void saveData(final int number, final String route, final String startTime, final String endTime , final String licensePlate, final String gender){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH");
        Date currentTime = Calendar.getInstance().getTime();
        final String date = sdf.format(currentTime.getTime());
        boolean licensePlate_format = licensePlate.matches("[A-Za-z][A-Za-z][\\d]{4}");
        boolean date_Format = startTime.matches("[0-2]{2}[0-9]{2}\\/[0-1][0-9]\\/[0-3][0-9][[:blank:]][0-2][0-9]:[0-6][0-9]:[0-6][0-9]");


//        textView1.setTextColor(Color.BLACK);
//        textView2.setTextColor(Color.BLACK);
//        textView15.setTextColor(Color.BLACK);


        if (surveyorNo.getText().toString().isEmpty() || location.getText().toString().isEmpty() ){
            textView1.setTextColor(Color.RED);
            textView2.setTextColor(Color.RED);
            Toast.makeText(this,"請輸入基本資料。" , Toast.LENGTH_SHORT).show();
        }else if (!licensePlate_format){
//            textView15.setTextColor(Color.RED);
            Toast.makeText(this,"請輸入六位巴士車牌。" , Toast.LENGTH_SHORT).show();
        }else{
            if( route.isEmpty() || !date_Format || endTime.isEmpty()){
                switch(number){
                    case 1:
                        Toast.makeText(this, "乘客A未完成記錄。", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(this, "乘客B未完成記錄。", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(this, "乘客C未完成記錄。", Toast.LENGTH_SHORT).show();
                        break;
                }
            }else if (route.equals("請選擇")){
                switch (number){
                    case 1:
                        Toast.makeText(this, "乘客A請選擇線路", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(this, "乘客B請選擇線路", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(this, "乘客C請選擇線路", Toast.LENGTH_SHORT).show();
                        break;

                }

            }else{
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("確認儲存資料︰");
                dialog.setMessage("調查員編號︰"+surveyorNo.getText().toString()+"\n調查地點︰"+location.getText().toString()+
                        "\n乘客性別︰" + gender +
                        "\n巴士路線︰" + route +
                        "\n巴士車牌︰" + licensePlate +
                        "\n乘客到站時間︰" + startTime +
                        "\n乘客上車時間︰" + endTime);
                dialog.setNegativeButton("N0",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                dialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (surveyType_1.isChecked()){
                            String sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            String fileDir= sdcard0Path + "/UMTEC/";
                            boolean wasSuccessful = false;

                            Calendar calendar = Calendar.getInstance();
                            String yearStr = calendar.get(Calendar.YEAR)+"";//获取年份
                            int month = calendar.get(Calendar.MONTH) + 1;//获取月份
                            String monthStr = month < 10 ? "0" + month : month + "";
                            int day = calendar.get(Calendar.DATE);//获取日
                            String dayStr = day < 10 ? "0" + day : day + "";

                            File file=new File(fileDir);
                            if(!file.exists()){
                                wasSuccessful = file.mkdir();  //Create New folder
                            }

                            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            fileDir= sdcard0Path + "/UMTEC/BusWaitingTime/";

                            file=new File(fileDir);
                            if(!file.exists()){
                                wasSuccessful = file.mkdir();  //Create New folder
                            }

                            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            fileDir= sdcard0Path + "/UMTEC/BusWaitingTime/"+ yearStr +"/";

                            file=new File(fileDir);
                            if(!file.exists()){
                                wasSuccessful = file.mkdir();  //Create New folder
                            }

                            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            fileDir= sdcard0Path + "/UMTEC/BusWaitingTime/"+yearStr +"/" + yearStr + monthStr  +"/";

                            file=new File(fileDir);
                            if(!file.exists()){
                                wasSuccessful = file.mkdir();  //Create New folder
                            }

                            String fileName = surveyorNo.getText().toString() + "-" + yearStr + monthStr + dayStr+ ".txt";
                            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            fileDir= sdcard0Path + "/UMTEC/BusWaitingTime/"+yearStr +"/" + yearStr + monthStr  +"/";
                            String filePath = fileDir + fileName;

                            file = new File(fileDir);
                            if (!file.exists()) {
                                wasSuccessful = file.mkdir();  //Create New folder
                            }

                            if (!wasSuccessful){
                                Toast.makeText(MainActivity.this,"成功建立檔案", Toast.LENGTH_SHORT).show();
                            }
//                          <--------------------------------------------------------------
                            String checkingBusStop = String.valueOf(location.getText());
                            if (station.locationNeedCopy.contains(checkingBusStop)){
                                String fileName_copy = surveyorNo.getText().toString() + "-" + yearStr + monthStr + dayStr+ "-copyFromNormal" + ".txt";
                                String sdcard0Path_copy = Environment.getExternalStorageDirectory().getPath();
                                String fileDir_copy = sdcard0Path_copy + "/UMTEC/BusWaitingTime(specialRoute)/" + yearStr + "/" + yearStr + monthStr + "/";
                                String filePath_copy = fileDir_copy + fileName_copy;

                                File file_copy = new File(fileDir_copy);
                                if (!file_copy.exists()){
                                    wasSuccessful = file_copy.mkdir();
                                }

                            if (!wasSuccessful) {
                                Toast.makeText(MainActivity.this,"成功建立檔案", Toast.LENGTH_SHORT).show();
                            }

                                try {

                                    location();

                                    FileOutputStream fOut = new FileOutputStream(filePath_copy, true);
                                    fOut.write(surveyorNo.getText().toString().getBytes());
                                    fOut.write("-".getBytes());
                                    fOut.write(location.getText().toString().getBytes());
                                    fOut.write("-".getBytes());
                                    fOut.write(String.valueOf(lon).getBytes());
                                    fOut.write("-".getBytes());
                                    fOut.write(String.valueOf(lat).getBytes());
                                    fOut.write("-".getBytes());

                                    switch(number){
                                        case 1:
                                            switch (genderGroup_1.getCheckedRadioButtonId()){
                                                case R.id.genderM_1:
                                                    fOut.write("M-".getBytes());
                                                    break;
                                                case R.id.genderF_1:
                                                    fOut.write("F-".getBytes());
                                                    break;
                                            }

                                            switch(ageGroup_1.getCheckedRadioButtonId()){
                                                case R.id.under20_1:
                                                    fOut.write("A-".getBytes());
                                                    break;
                                                case R.id.from20to45_1:
                                                    fOut.write("B-".getBytes());
                                                    break;
                                                case R.id.above45_1:
                                                    fOut.write("C-".getBytes());
                                                    break;
                                            }

                                            fOut.write(route.getBytes());
                                            fOut.write("-".getBytes());
                                            fOut.write(licensePlate_1.getText().toString().getBytes());
                                            fOut.write("-".getBytes());
                                            fOut.write(startTime.getBytes());
                                            fOut.write("-".getBytes());
                                            fOut.write(endTime.getBytes());

                                            startTime_1.setText("");
                                            endTime_1.setText("");
                                            route1.setText("");
                                            licensePlate_1.setText("");
                                            textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                                            textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                            break;
                                        case 2:
                                            switch (genderGroup_2.getCheckedRadioButtonId()){
                                                case R.id.genderM_2:
                                                    fOut.write("M-".getBytes());
                                                    break;
                                                case R.id.genderF_2:
                                                    fOut.write("F-".getBytes());
                                                    break;
                                            }

                                            switch(ageGroup_2.getCheckedRadioButtonId()){
                                                case R.id.under20_2:
                                                    fOut.write("A-".getBytes());
                                                    break;
                                                case R.id.from20to45_2:
                                                    fOut.write("B-".getBytes());
                                                    break;
                                                case R.id.above45_2:
                                                    fOut.write("C-".getBytes());
                                                    break;
                                            }

                                            fOut.write(route.getBytes());
                                            fOut.write("-".getBytes());
                                            fOut.write(licensePlate_2.getText().toString().getBytes());
                                            fOut.write("-".getBytes());
                                            fOut.write(startTime.getBytes());
                                            fOut.write("-".getBytes());
                                            fOut.write(endTime.getBytes());

                                            startTime_2.setText("");
                                            endTime_2.setText("");
                                            route2.setText("");
                                            licensePlate_2.setText("");
                                            textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                                            textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                            break;
                                        case 3:
                                            switch (genderGroup_3.getCheckedRadioButtonId()){
                                                case R.id.genderM_3:
                                                    fOut.write("M-".getBytes());
                                                    break;
                                                case R.id.genderF_3:
                                                    fOut.write("F-".getBytes());
                                                    break;
                                            }

                                            switch(ageGroup_3.getCheckedRadioButtonId()){
                                                case R.id.under20_3:
                                                    fOut.write("A-".getBytes());
                                                    break;
                                                case R.id.from20to45_3:
                                                    fOut.write("B-".getBytes());
                                                    break;
                                                case R.id.above45_3:
                                                    fOut.write("C-".getBytes());
                                                    break;
                                            }

                                            fOut.write(route.getBytes());
                                            fOut.write("-".getBytes());
                                            fOut.write(licensePlate_3.getText().toString().getBytes());
                                            fOut.write("-".getBytes());
                                            fOut.write(startTime.getBytes());
                                            fOut.write("-".getBytes());
                                            fOut.write(endTime.getBytes());

                                            startTime_3.setText("");
                                            endTime_3.setText("");
                                            route3.setText("");
                                            licensePlate_3.setText("");
                                            textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                                            textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                            break;
                                    }
                                    fOut.write("\r\n".getBytes());
                                    Toast.makeText(MainActivity.this, "已儲存。",Toast.LENGTH_SHORT).show();


                                } catch (Exception e) {
                                    Log.d("Ch7_4_InternalStorage", "例外發生: " + e.toString());
                                    Toast.makeText(MainActivity.this, "Fail!!! Please Contact Developer", Toast.LENGTH_SHORT).show();
                                }
                            }

//                           checking bus stop that in the station.java locationNeedCopy arrylist
//                           if contains, create 普通 and 101x/102x folder and files.
//                          --------------------------------------------------------------->
                            try {

                                location();

                                FileOutputStream fOut = new FileOutputStream(filePath, true);
                                fOut.write(surveyorNo.getText().toString().getBytes());
                                fOut.write("-".getBytes());
                                fOut.write(location.getText().toString().getBytes());
                                fOut.write("-".getBytes());
                                fOut.write(String.valueOf(lon).getBytes());
                                fOut.write("-".getBytes());
                                fOut.write(String.valueOf(lat).getBytes());
                                fOut.write("-".getBytes());

                                switch(number){
                                    case 1:
                                        switch (genderGroup_1.getCheckedRadioButtonId()){
                                            case R.id.genderM_1:
                                                fOut.write("M-".getBytes());
                                                break;
                                            case R.id.genderF_1:
                                                fOut.write("F-".getBytes());
                                                break;
                                        }

                                        switch(ageGroup_1.getCheckedRadioButtonId()){
                                            case R.id.under20_1:
                                                fOut.write("A-".getBytes());
                                                break;
                                            case R.id.from20to45_1:
                                                fOut.write("B-".getBytes());
                                                break;
                                            case R.id.above45_1:
                                                fOut.write("C-".getBytes());
                                                break;
                                        }

                                        fOut.write(route.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(licensePlate_1.getText().toString().getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(startTime.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(endTime.getBytes());

                                        startTime_1.setText("");
                                        endTime_1.setText("");
                                        route1.setText("");
                                        licensePlate_1.setText("");
                                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        break;
                                    case 2:
                                        switch (genderGroup_2.getCheckedRadioButtonId()){
                                            case R.id.genderM_2:
                                                fOut.write("M-".getBytes());
                                                break;
                                            case R.id.genderF_2:
                                                fOut.write("F-".getBytes());
                                                break;
                                        }

                                        switch(ageGroup_2.getCheckedRadioButtonId()){
                                            case R.id.under20_2:
                                                fOut.write("A-".getBytes());
                                                break;
                                            case R.id.from20to45_2:
                                                fOut.write("B-".getBytes());
                                                break;
                                            case R.id.above45_2:
                                                fOut.write("C-".getBytes());
                                                break;
                                        }

                                        fOut.write(route.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(licensePlate_2.getText().toString().getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(startTime.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(endTime.getBytes());

                                        startTime_2.setText("");
                                        endTime_2.setText("");
                                        route2.setText("");
                                        licensePlate_2.setText("");
                                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        break;
                                    case 3:
                                        switch (genderGroup_3.getCheckedRadioButtonId()){
                                            case R.id.genderM_3:
                                                fOut.write("M-".getBytes());
                                                break;
                                            case R.id.genderF_3:
                                                fOut.write("F-".getBytes());
                                                break;
                                        }

                                        switch(ageGroup_3.getCheckedRadioButtonId()){
                                            case R.id.under20_3:
                                                fOut.write("A-".getBytes());
                                                break;
                                            case R.id.from20to45_3:
                                                fOut.write("B-".getBytes());
                                                break;
                                            case R.id.above45_3:
                                                fOut.write("C-".getBytes());
                                                break;
                                        }

                                        fOut.write(route.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(licensePlate_3.getText().toString().getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(startTime.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(endTime.getBytes());

                                        startTime_3.setText("");
                                        endTime_3.setText("");
                                        route3.setText("");
                                        licensePlate_3.setText("");
                                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        break;
                                }
                                fOut.write("\r\n".getBytes());
                                Toast.makeText(MainActivity.this, "已儲存。",Toast.LENGTH_SHORT).show();


                            } catch (Exception e) {
                                Log.d("Ch7_4_InternalStorage", "例外發生: " + e.toString());
                                Toast.makeText(MainActivity.this, "Fail!!! Please Contact Developer", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if(surveyType_2.isChecked()){
                            String sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            String fileDir= sdcard0Path + "/UMTEC/";
                            boolean wasSuccessful = false;

                            Calendar calendar = Calendar.getInstance();
                            String yearStr = calendar.get(Calendar.YEAR)+"";//获取年份
                            int month = calendar.get(Calendar.MONTH) + 1;//获取月份
                            String monthStr = month < 10 ? "0" + month : month + "";
                            int day = calendar.get(Calendar.DATE);//获取日
                            String dayStr = day < 10 ? "0" + day : day + "";

                            File file=new File(fileDir);
                            if(!file.exists()){
                                wasSuccessful = file.mkdir();  //Create New folder
                            }

                            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            fileDir= sdcard0Path + "/UMTEC/BusWaitingTime(specialRoute)/";

                            file=new File(fileDir);
                            if(!file.exists()){
                                wasSuccessful = file.mkdir();  //Create New folder
                            }

                            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            fileDir= sdcard0Path + "/UMTEC/BusWaitingTime(specialRoute)/"+ yearStr +"/";

                            file=new File(fileDir);
                            if(!file.exists()){
                                wasSuccessful = file.mkdir();  //Create New folder
                            }

                            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            fileDir= sdcard0Path + "/UMTEC/BusWaitingTime(specialRoute)/"+yearStr +"/" + yearStr + monthStr  +"/";

                            file=new File(fileDir);
                            if(!file.exists()){
                                wasSuccessful = file.mkdir();  //Create New folder
                            }


                            String fileName = surveyorNo.getText().toString() + "-" + yearStr + monthStr + dayStr+ ".txt";
                            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            fileDir= sdcard0Path + "/UMTEC/BusWaitingTime(specialRoute)/"+yearStr +"/" + yearStr + monthStr  +"/";
                            String filePath = fileDir + fileName;

                            file = new File(fileDir);
                            if (!file.exists()) {
                                wasSuccessful = file.mkdir();  //Create New folder
                            }

                            if (!wasSuccessful){
                                Toast.makeText(MainActivity.this,"成功建立檔案", Toast.LENGTH_SHORT).show();
                            }

                            Log.d("pathTest: ", String.valueOf(file.exists()));

                            try {

                                location();

                                FileOutputStream fOut = new FileOutputStream(filePath, true);

                                fOut.write(surveyorNo.getText().toString().getBytes());
                                fOut.write("-".getBytes());
                                fOut.write(location.getText().toString().getBytes());
                                fOut.write("-".getBytes());
                                fOut.write(String.valueOf(lon).getBytes());
                                fOut.write("-".getBytes());
                                fOut.write(String.valueOf(lat).getBytes());
                                fOut.write("-".getBytes());

                                switch(number){
                                    case 1:
                                        switch (genderGroup_1.getCheckedRadioButtonId()){
                                            case R.id.genderM_1:
                                                fOut.write("M-".getBytes());
                                                break;
                                            case R.id.genderF_1:
                                                fOut.write("F-".getBytes());
                                                break;
                                        }

                                        switch(ageGroup_1.getCheckedRadioButtonId()){
                                            case R.id.under20_1:
                                                fOut.write("A-".getBytes());
                                                break;
                                            case R.id.from20to45_1:
                                                fOut.write("B-".getBytes());
                                                break;
                                            case R.id.above45_1:
                                                fOut.write("C-".getBytes());
                                                break;
                                        }

                                        fOut.write(route.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(licensePlate_1.getText().toString().getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(startTime.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(endTime.getBytes());

                                        startTime_1.setText("");
                                        endTime_1.setText("");
                                        route1.setText("");
                                        licensePlate_1.setText("");
                                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        break;
                                    case 2:
                                        switch (genderGroup_2.getCheckedRadioButtonId()){
                                            case R.id.genderM_2:
                                                fOut.write("M-".getBytes());
                                                break;
                                            case R.id.genderF_2:
                                                fOut.write("F-".getBytes());
                                                break;
                                        }

                                        switch(ageGroup_2.getCheckedRadioButtonId()){
                                            case R.id.under20_2:
                                                fOut.write("A-".getBytes());
                                                break;
                                            case R.id.from20to45_2:
                                                fOut.write("B-".getBytes());
                                                break;
                                            case R.id.above45_2:
                                                fOut.write("C-".getBytes());
                                                break;
                                        }

                                        fOut.write(route.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(licensePlate_2.getText().toString().getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(startTime.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(endTime.getBytes());

                                        startTime_2.setText("");
                                        endTime_2.setText("");
                                        route2.setText("");
                                        licensePlate_2.setText("");
                                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        break;
                                    case 3:
                                        switch (genderGroup_3.getCheckedRadioButtonId()){
                                            case R.id.genderM_3:
                                                fOut.write("M-".getBytes());
                                                break;
                                            case R.id.genderF_3:
                                                fOut.write("F-".getBytes());
                                                break;
                                        }

                                        switch(ageGroup_3.getCheckedRadioButtonId()){
                                            case R.id.under20_3:
                                                fOut.write("A-".getBytes());
                                                break;
                                            case R.id.from20to45_3:
                                                fOut.write("B-".getBytes());
                                                break;
                                            case R.id.above45_3:
                                                fOut.write("C-".getBytes());
                                                break;
                                        }

                                        fOut.write(route.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(licensePlate_3.getText().toString().getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(startTime.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(endTime.getBytes());

                                        startTime_3.setText("");
                                        endTime_3.setText("");
                                        route3.setText("");
                                        licensePlate_3.setText("");
                                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        break;
                                }
                                fOut.write("\r\n".getBytes());
                                Toast.makeText(MainActivity.this, "已儲存。",Toast.LENGTH_SHORT).show();


                            } catch (Exception e) {
                                Log.d("Ch7_4_InternalStorage", "例外發生: " + e.toString());
                                Toast.makeText(MainActivity.this, "Fail!!! Please Contact Developer", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if(surveyType_3.isChecked()){
                            String sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            String fileDir= sdcard0Path + "/UMTEC/";
                            boolean wasSuccessful = false;

                            Calendar calendar = Calendar.getInstance();
                            String yearStr = calendar.get(Calendar.YEAR)+"";//获取年份
                            int month = calendar.get(Calendar.MONTH) + 1;//获取月份
                            String monthStr = month < 10 ? "0" + month : month + "";
                            int day = calendar.get(Calendar.DATE);//获取日
                            String dayStr = day < 10 ? "0" + day : day + "";

                            File file=new File(fileDir);
                            if(!file.exists()){
                                wasSuccessful = file.mkdir();  //Create New folder
                            }

                            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            fileDir= sdcard0Path + "/UMTEC/BusWaitingTime(hengqin)/";

                            file=new File(fileDir);
                            if(!file.exists()){
                                wasSuccessful = file.mkdir();  //Create New folder
                            }

                            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            fileDir= sdcard0Path + "/UMTEC/BusWaitingTime(hengqin)/"+ yearStr +"/";

                            file=new File(fileDir);
                            if(!file.exists()){
                                wasSuccessful = file.mkdir();  //Create New folder
                            }

                            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            fileDir= sdcard0Path + "/UMTEC/BusWaitingTime(hengqin)/"+yearStr +"/" + yearStr + monthStr  +"/";

                            file=new File(fileDir);
                            if(!file.exists()){
                                wasSuccessful = file.mkdir();  //Create New folder
                            }


                            String fileName = surveyorNo.getText().toString() + "-" + yearStr + monthStr + dayStr+ ".txt";
                            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
                            fileDir= sdcard0Path + "/UMTEC/BusWaitingTime(hengqin)/"+yearStr +"/" + yearStr + monthStr  +"/";
                            String filePath = fileDir + fileName;

                            file = new File(fileDir);
                            if (!file.exists()) {
                                wasSuccessful = file.mkdir();  //Create New folder
                            }

                            if (!wasSuccessful){
                                Toast.makeText(MainActivity.this,"成功建立檔案", Toast.LENGTH_SHORT).show();
                            }

                            try {

                                location();

                                FileOutputStream fOut = new FileOutputStream(filePath, true);

                                fOut.write(surveyorNo.getText().toString().getBytes());
                                fOut.write("-".getBytes());
                                fOut.write(location.getText().toString().getBytes());
                                fOut.write("-".getBytes());
                                fOut.write(String.valueOf(lon).getBytes());
                                fOut.write("-".getBytes());
                                fOut.write(String.valueOf(lat).getBytes());
                                fOut.write("-".getBytes());

                                switch(number){
                                    case 1:
                                        switch (genderGroup_1.getCheckedRadioButtonId()){
                                            case R.id.genderM_1:
                                                fOut.write("M-".getBytes());
                                                break;
                                            case R.id.genderF_1:
                                                fOut.write("F-".getBytes());
                                                break;
                                        }

                                        switch(ageGroup_1.getCheckedRadioButtonId()){
                                            case R.id.under20_1:
                                                fOut.write("A-".getBytes());
                                                break;
                                            case R.id.from20to45_1:
                                                fOut.write("B-".getBytes());
                                                break;
                                            case R.id.above45_1:
                                                fOut.write("C-".getBytes());
                                                break;
                                        }

                                        fOut.write(route.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(licensePlate_1.getText().toString().getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(startTime.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(endTime.getBytes());

                                        startTime_1.setText("");
                                        endTime_1.setText("");
//                                        route1.setText("");
                                        licensePlate_1.setText("");
                                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        break;
                                    case 2:
                                        switch (genderGroup_2.getCheckedRadioButtonId()){
                                            case R.id.genderM_2:
                                                fOut.write("M-".getBytes());
                                                break;
                                            case R.id.genderF_2:
                                                fOut.write("F-".getBytes());
                                                break;
                                        }

                                        switch(ageGroup_2.getCheckedRadioButtonId()){
                                            case R.id.under20_2:
                                                fOut.write("A-".getBytes());
                                                break;
                                            case R.id.from20to45_2:
                                                fOut.write("B-".getBytes());
                                                break;
                                            case R.id.above45_2:
                                                fOut.write("C-".getBytes());
                                                break;
                                        }

                                        fOut.write(route.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(licensePlate_2.getText().toString().getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(startTime.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(endTime.getBytes());

                                        startTime_2.setText("");
                                        endTime_2.setText("");
//                                        route2.setText("");
                                        licensePlate_2.setText("");
                                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        break;
                                    case 3:
                                        switch (genderGroup_3.getCheckedRadioButtonId()){
                                            case R.id.genderM_3:
                                                fOut.write("M-".getBytes());
                                                break;
                                            case R.id.genderF_3:
                                                fOut.write("F-".getBytes());
                                                break;
                                        }

                                        switch(ageGroup_3.getCheckedRadioButtonId()){
                                            case R.id.under20_3:
                                                fOut.write("A-".getBytes());
                                                break;
                                            case R.id.from20to45_3:
                                                fOut.write("B-".getBytes());
                                                break;
                                            case R.id.above45_3:
                                                fOut.write("C-".getBytes());
                                                break;
                                        }

                                        fOut.write(route.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(licensePlate_3.getText().toString().getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(startTime.getBytes());
                                        fOut.write("-".getBytes());
                                        fOut.write(endTime.getBytes());

                                        startTime_3.setText("");
                                        endTime_3.setText("");
//                                        route3.setText("");
                                        licensePlate_3.setText("");
                                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        break;
                                }
                                fOut.write("\r\n".getBytes());
                                Toast.makeText(MainActivity.this, "已儲存。",Toast.LENGTH_SHORT).show();


                            } catch (Exception e) {
                                Log.d("Ch7_4_InternalStorage", "例外發生: " + e.toString());
                                Toast.makeText(MainActivity.this, "Fail!!! Please Contact Developer", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                dialog.show();
            }
        }
    }

    public void FindView(){
        surveyorNo = getView(R.id.surveyorNo);
        location = getView(R.id.location);
        route1 = getView(R.id.route1);
        route2 = getView(R.id.route2);
        route3 = getView(R.id.route3);
        startTime_1 = getView(R.id.startTime_1);
        startTime_2 = getView(R.id.startTime_2);
        startTime_3 = getView(R.id.startTime_3);
        endTime_1 = getView(R.id.endTime_1);
        endTime_2 = getView(R.id.endTime_2);
        endTime_3 = getView(R.id.endTime_3);
        licensePlate_1=getView(R.id.licensePlate_1);
        licensePlate_2=getView(R.id.licensePlate_2);
        licensePlate_3=getView(R.id.licensePlate_3);
        startButton_1 = getView(R.id.startButton_1);
        startButton_2 = getView(R.id.startButton_2);
        startButton_3 = getView(R.id.startButton_3);
        endButton_1 = getView(R.id.endButton_1);
        endButton_2 = getView(R.id.endButton_2);
        endButton_3 = getView(R.id.endButton_3);
        saveButton_1 = getView(R.id.saveButton_1);
        saveButton_2 = getView(R.id.saveButton_2);
        saveButton_3 = getView(R.id.saveButton_3);

        genderF_1 = getView(R.id.genderF_1);
        genderF_2 = getView(R.id.genderF_2);
        genderF_3 = getView(R.id.genderF_3);
        genderM_1 = getView(R.id.genderM_1);
        genderM_2 = getView(R.id.genderM_2);
        genderM_3 = getView(R.id.genderM_3);
        under20_1 = getView(R.id.under20_1);
        under20_2 = getView(R.id.under20_2);
        under20_3 = getView(R.id.under20_3);
        from20to45_1 = getView(R.id.from20to45_1);
        from20to45_2 = getView(R.id.from20to45_2);
        from20to45_3 = getView(R.id.from20to45_3);
        above45_1 = getView(R.id.above45_1);
        above45_2 = getView(R.id.above45_2);
        above45_3 = getView(R.id.above45_3);
        surveyType_1 = getView(R.id.normal_rd);
        surveyType_2 = getView(R.id.S101x_rb);
        surveyType_3 = getView(R.id.hengqin_rb);

        surveyType_rbg = getView(R.id.surveyType_rbg);

        genderGroup_1 = getView(R.id.genderGroup_1);
        genderGroup_2 = getView(R.id.genderGroup_2);
        genderGroup_3 = getView(R.id.genderGroup_3);

        ageGroup_1 = getView(R.id.ageGroup_1);
        ageGroup_2 = getView(R.id.ageGroup_2);
        ageGroup_3 = getView(R.id.ageGroup_3);

        textView1 = getView(R.id.textView1);
        textView2 = getView(R.id.textView2);
        textView15 = getView(R.id.textView15);
        textView16 = getView(R.id.textView16);
        textView17 = getView(R.id.textView17);

        spinner1 = getView(R.id.spinner1);
        spinner2 = getView(R.id.spinner2);
        spinner3 = getView(R.id.spinner3);

        stationButton = getView(R.id.stationButton);
    }

    public void location(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getProvider(LocationManager.GPS_PROVIDER).getName();

        if (provider != null && !provider.equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                }
            }
            Location location = locationManager.getLastKnownLocation(provider);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                }
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                public void onProviderEnabled(String provider) {}
                public void onProviderDisabled(String provider) {}
            };

            locationManager.requestLocationUpdates(provider, 0, 0, locationListener );

            if (location != null)
                onLocationChanged(location);
            else{
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }
    }

    public void onLocationChanged(Location location) {
        // Setting Current Longitude
        lon = location.getLongitude();

        // Setting Current Latitude
        lat = location.getLatitude();
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    public void onMoveToForeground(){
//        Log.d("Event","app moved to foreground");
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("testing");
//        builder.show();
//    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog.Builder typeBuilder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("請注意調查地點是否正確?")
                .setPositiveButton("正確", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("更改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        typeBuilder.setTitle("請選擇調查種類");
                        typeBuilder.setItems(typeList, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedType = typeList[i];
                                String[] locationListItem = new String[0];
                                switch (selectedType){
                                    case "普通":
                                        locationListItem = station.location;
                                        surveyType_1.setChecked(true);
                                        break;
                                    case "101x/102x":
                                        locationListItem = station.location_101x;
                                        surveyType_2.setChecked(true);
                                        break;
                                    case "橫琴":
                                        locationListItem = station.location_hengqin;
                                        surveyType_3.setChecked(true);
                                        break;
                                }
                                locationList(location, "請選擇站點︰", locationListItem);
                            }
                        });
                        typeBuilder.show();

                    }
                });
        builder.show();
    }

    class Spinner1Class implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            route1.setText(busNumArray[i]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    class Spinner2Class implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            route2.setText(busNumArray[i]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    class Spinner3Class implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            route3.setText(busNumArray[i]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
