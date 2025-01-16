package com.example.umtec_brian.buswaitingtime;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
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
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements LifecycleObserver {


    EditText surveyorNo, location,
            route1, route2, route3, route4,
            startTime_1, startTime_2, startTime_3, startTime_4,
            endTime_1, endTime_2, endTime_3, endTime_4,
            licensePlate_1, licensePlate_2, licensePlate_3, licensePlate_4;

    Button
            startButton_1, startButton_2, startButton_3, startButton_4,
            endButton_1, endButton_2, endButton_3, endButton_4,
            saveButton_1, saveButton_2, saveButton_3, saveButton_4,
            Group1recordButton_1, Group1recordButton_2, Group1recordButton_3, Group1recordButton_4,
            Group2recordButton_1, Group2recordButton_2, Group2recordButton_3, Group2recordButton_4,
            Group3recordButton_1, Group3recordButton_2, Group3recordButton_3, Group3recordButton_4,
            Group4recordButton_1, Group4recordButton_2, Group4recordButton_3, Group4recordButton_4,
            cleanRecordButton,
            stationButton;
    FrameLayout mask_layout;
    ConstraintLayout constraint, overlay;
    RadioGroup
//            ageGroup_1, ageGroup_2, ageGroup_3, ageGroup_4,
//            genderGroup_1, genderGroup_2, genderGroup_3, genderGroup_4,
            surveyType_rbg;
    RadioButton
//            genderM_1, genderF_1, under20_1, from20to45_1, above45_1,
//            genderM_2, genderF_2, under20_2, from20to45_2, above45_2,
//            genderM_3, genderF_3, under20_3, from20to45_3, above45_3,
//            genderM_4, genderF_4, under20_4, from20to45_4, above45_4,
//            rb1, rb2, rb3, rb4,
            surveyType_1, surveyType_2, surveyType_3;
    TextView textView1, textView2,
            Group1TextView3, Group2TextView3, Group3TextView3, Group4TextView3,
            textView_Record_route_1, textView_Record_route_2, textView_Record_route_3, textView_Record_route_4,
            textView_Record_licensePlate_1, textView_Record_licensePlate_2, textView_Record_licensePlate_3, textView_Record_licensePlate_4;
    String provider;
    Spinner spinner1, spinner2, spinner3, spinner4;
    LocationManager locationManager;
    double lat, lon;
    String[] busNumArray = {"請選擇", "25B", "25BS", "50", "102X", "701X (往望德聖母灣)", "701X (往澳大)", "701XS", "其它"};
    String[] typeList = {"普通", "101x/102x", "橫琴"};
    // 定義文件存儲的基本路徑作為常量
    private static final String BASE_FOLDER = "UMTEC";
    private static final String BUS_WAITING_TIME_FOLDER = "BusWaitingTime";
    private static final String SPECIAL_ROUTE_FOLDER = "BusWaitingTime(specialRoute)";
    private static final String HENGQIN_FOLDER = "BusWaitingTime(hengqin)";
    private static final String SURVEYOR_FOLDER_PREFIX = "data_";
    private ArrayAdapter<String> sharedAdapter; // 共享适配器
    private List<String> spinnerData; // 共享数据源
    // 定义请求码常量
    private static final int REQUEST_CODE_MANAGE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

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

//        setContentView(R.layout.activity_main);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        FindView();
        initialization();
        checkPermission();
//        setRecord();
        initSpinnerData();

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this, android.R.layout.simple_spinner_dropdown_item, busNumArray
//        );
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinner1.setAdapter(adapter);
//        spinner1.setOnItemSelectedListener(new Spinner1Class());
//
//        spinner2.setAdapter(adapter);
//        spinner2.setOnItemSelectedListener(new Spinner2Class());
//
//        spinner3.setAdapter(adapter);
//        spinner3.setOnItemSelectedListener(new Spinner3Class());
//
//        spinner4.setAdapter(adapter);
//        spinner4.setOnItemSelectedListener(new Spinner4Class());
        //時間選擇
        setupEditTextWatchers();

        surveyorNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    textView1.setTextColor(getResources().getColor(R.color.red));
                } else {
                    textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });
        // 设置触摸监听器

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = parent.getItemAtPosition(position).toString();
                if ("其它".equals(selectedItemText)) {
                    showCustomInputDialog(spinner1, spinner2, spinner3, spinner4);
                } else {
                    if ("請選擇".equals(selectedItemText)) {
                        route1.setText("");
                    } else {
                        route1.setText(selectedItemText);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = parent.getItemAtPosition(position).toString();
                if ("其它".equals(selectedItemText)) {
                    showCustomInputDialog(spinner2, spinner3, spinner4, spinner1);
                } else {
                    if ("請選擇".equals(selectedItemText)) {
                        route2.setText("");
                    } else {
                        route2.setText(selectedItemText);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = parent.getItemAtPosition(position).toString();
                if ("其它".equals(selectedItemText)) {
                    showCustomInputDialog(spinner3, spinner4, spinner1, spinner2);
                } else {
                    if ("請選擇".equals(selectedItemText)) {
                        route3.setText("");
                    } else {
                        route3.setText(selectedItemText);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = parent.getItemAtPosition(position).toString();
                if ("其它".equals(selectedItemText)) {
                    showCustomInputDialog(spinner4, spinner1, spinner2, spinner3);
                } else {
                    if ("請選擇".equals(selectedItemText)) {
                        route4.setText("");
                    } else {
                        route4.setText(selectedItemText);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        route1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textView_Record_route_1.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        route2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textView_Record_route_2.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        route3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textView_Record_route_3.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        route4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textView_Record_route_4.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        //                if (charSequence.length() > 6) {
//                    showToast("輸入內容已達到最大長度");
//                    // 如果输入的字符超过6个，截取前6个字符
//                    licensePlate_1.setText(charSequence.subSequence(0, 6));
//                    // 将光标移动到末尾
//                    licensePlate_1.setSelection(6);
//                    // 显示提示信息
//                }
        licensePlate_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int after) {
                if (charSequence.length() == 6) {
                    licensePlate_1.clearFocus();
                    hideKeyboard(licensePlate_1);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean matches = editable.toString().matches("[A-Za-z][A-Za-z][\\d]{4}");
                if (!matches) {
                    Group1TextView3.setTextColor(getResources().getColor(R.color.red));
                } else {
                    Group1TextView3.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if (editable.length() == 0) {
                    Group1TextView3.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                textView_Record_licensePlate_1.setText(editable);
            }
        });

        // 设置点击监听器，以便重新聚焦

        licensePlate_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                licensePlate_1.setFocusable(true);
                licensePlate_1.setFocusableInTouchMode(true);
                licensePlate_1.clearFocus();
                licensePlate_1.requestFocus();
                showKeyboard(licensePlate_1);
            }
        });
        licensePlate_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int after) {

                if (charSequence.length() == 6) {
                    // 输入完六位后失焦
                    licensePlate_2.clearFocus();
                    hideKeyboard(licensePlate_2);
                }

            }


            @Override
            public void afterTextChanged(Editable editable) {

                boolean matches = editable.toString().matches("[A-Za-z][A-Za-z][\\d]{4}");
                if (!matches) {
                    Group2TextView3.setTextColor(getResources().getColor(R.color.red));

                } else {
                    Group2TextView3.setTextColor(getResources().getColor(R.color.colorPrimary));

                }
                if (editable.length() == 0) {
                    Group2TextView3.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                textView_Record_licensePlate_2.setText(editable);
            }
        });

        licensePlate_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                licensePlate_2.setFocusable(true);
                licensePlate_2.setFocusableInTouchMode(true);
                licensePlate_2.clearFocus();
                licensePlate_2.requestFocus();
                showKeyboard(licensePlate_2);
            }
        });
        licensePlate_3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 6) {
                    // 输入完六位后失焦
                    licensePlate_3.clearFocus();
                    hideKeyboard(licensePlate_3);
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {
                boolean matches = editable.toString().matches("^[A-Za-z][A-Za-z][\\d]{4}");
                if (!matches) {
                    Group3TextView3.setTextColor(getResources().getColor(R.color.red));

                } else {
                    Group3TextView3.setTextColor(getResources().getColor(R.color.colorPrimary));

                }
                if (editable.length() == 0) {
                    Group3TextView3.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                textView_Record_licensePlate_3.setText(editable);
            }
        });
        // licensePlate_3 的点击监听器
        licensePlate_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                licensePlate_3.setFocusable(true);
                licensePlate_3.setFocusableInTouchMode(true);
                licensePlate_3.clearFocus();
                licensePlate_3.requestFocus();
                showKeyboard(licensePlate_3);
            }
        });
        licensePlate_4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 6) {
                    // 输入完六位后失焦
                    licensePlate_4.clearFocus();
                    hideKeyboard(licensePlate_4);
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {
                boolean matches = editable.toString().matches("[A-Za-z][A-Za-z][\\d]{4}");
                if (!matches) {
                    Group4TextView3.setTextColor(getResources().getColor(R.color.red));

                } else {
                    Group4TextView3.setTextColor(getResources().getColor(R.color.colorPrimary));

                }
                if (editable.length() == 0) {
                    Group4TextView3.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                textView_Record_licensePlate_4.setText(editable);
            }
        });


// licensePlate_4 的点击监听器
        licensePlate_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                licensePlate_4.setFocusable(true);
                licensePlate_4.setFocusableInTouchMode(true);
                licensePlate_4.clearFocus();
                licensePlate_4.requestFocus();
                showKeyboard(licensePlate_4);
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
                if (surveyType_1.isChecked()) {
                    locationList(location, "請選擇站點︰", station.location);

                }
                if (surveyType_2.isChecked()) {
                    locationList(location, "請選擇站點︰", station.location_101x);
                }
                if (surveyType_3.isChecked()) {
                    locationList(location, "請選擇站點︰", station.location_hengqin);
                }
            }
        });

        surveyType_rbg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.hengqin_rb) {
                    spinner1.setVisibility(View.VISIBLE);
                    spinner2.setVisibility(View.VISIBLE);
                    spinner3.setVisibility(View.VISIBLE);
                    spinner4.setVisibility(View.VISIBLE);
                    route1.setVisibility(View.GONE);
                    route2.setVisibility(View.GONE);
                    route3.setVisibility(View.GONE);
                    route4.setVisibility(View.GONE);
                } else {
                    spinner1.setVisibility(View.GONE);
                    spinner2.setVisibility(View.GONE);
                    spinner3.setVisibility(View.GONE);
                    spinner4.setVisibility(View.GONE);
                    route1.setVisibility(View.VISIBLE);
                    route2.setVisibility(View.VISIBLE);
                    route3.setVisibility(View.VISIBLE);
                    route4.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void setupEditTextWatchers() {
        addTextWatcherToEditText(startTime_1, endTime_1);
        addTextWatcherToEditText(startTime_2, endTime_2);
        addTextWatcherToEditText(startTime_3, endTime_3);
        addTextWatcherToEditText(startTime_4, endTime_4);
    }

    //    public void performActionOne() {
//        SharedPreferences preferences = getSharedPreferences("AppData", MODE_PRIVATE);
//        String route01 = preferences.getString("data1_" + "route", "");
//        String licensePlate01 = preferences.getString("data1_" + "licensePlate", "");
//        route1.setText(route01);
//        licensePlate_1.setText(licensePlate01);
//    }
//
//    public void performActionTwo() {
//        SharedPreferences preferences = getSharedPreferences("AppData", MODE_PRIVATE);
//        String route02 = preferences.getString("data2_" + "route", "");
//        String licensePlate02 = preferences.getString("data2_" + "licensePlate", "");
//        route1.setText(route02);
//        licensePlate_1.setText(licensePlate02);
//    }
//
//    public void performActionThree() {
//        SharedPreferences preferences = getSharedPreferences("AppData", MODE_PRIVATE);
//        String route03 = preferences.getString("data3_" + "route", "");
//        String licensePlate03 = preferences.getString("data3_" + "licensePlate", "");
//        route1.setText(route03);
//        licensePlate_1.setText(licensePlate03);
//    }
//
//    public void performActionFour() {
//        SharedPreferences preferences = getSharedPreferences("AppData", MODE_PRIVATE);
//        String route04 = preferences.getString("data4_" + "route", "");
//        String licensePlate04 = preferences.getString("data4_" + "licensePlate", "");
//        route1.setText(route04);
//        licensePlate_1.setText(licensePlate04);
//    }
    private void initSpinnerData() {
        // 初始化数据源和适配器
        spinnerData = new ArrayList<>(Arrays.asList(this.busNumArray));
        sharedAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerData
        );
        sharedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 为所有Spinner设置适配器
        spinner1.setAdapter(sharedAdapter);
        spinner2.setAdapter(sharedAdapter);
        spinner3.setAdapter(sharedAdapter);
        spinner4.setAdapter(sharedAdapter);
    }

    // 隐藏软键盘
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // 显示软键盘
    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    private void showCustomInputDialog(Spinner spinner, Spinner spinnerOther1, Spinner spinnerOther2, Spinner spinnerOther3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("請輸入內容");

        // 设置输入框
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);


        builder.setPositiveButton("確定", null); // 先不设置点击事件
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                spinner.setSelection(0);
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = input.getText().toString().trim(); // 去除用户输入的首尾空格
                if (!userInput.isEmpty()) {
                    // 保存当前选中项
                    String selectionText1 = spinnerOther1.getSelectedItem().toString();
                    String selectionText2 = spinnerOther2.getSelectedItem().toString();
                    String selectionText3 = spinnerOther3.getSelectedItem().toString();
                    // 更新Spinner的显示
                    addCustomText(userInput, spinner); // 调用之前定义的方法添加自定义文本
                    spinner.setAdapter(sharedAdapter);
                    // 恢复选中项文本
                    int selectionOther1 = sharedAdapter.getPosition(selectionText1);
                    int selectionOther2 = sharedAdapter.getPosition(selectionText2);
                    int selectionOther3 = sharedAdapter.getPosition(selectionText3);

                    if (spinnerData.contains("其它:" + userInput)) {
                        spinner.setSelection(spinnerData.indexOf("其它:" + userInput));
                    } else {
                        spinner.setSelection(spinnerData.size() - 1);
                    }
                    if (selectionOther1 != -1) {
                        spinnerOther1.setSelection(selectionOther1);
                    } else {
                        spinnerOther1.setSelection(0);
                    }
                    if (selectionOther2 != -1) {
                        spinnerOther2.setSelection(selectionOther2);
                    } else {
                        spinnerOther2.setSelection(0);
                    }
                    if (selectionOther3 != -1) {
                        spinnerOther3.setSelection(selectionOther3);
                    } else {
                        spinnerOther3.setSelection(0);
                    }
                    dialog.dismiss(); // 关闭对话框
                } else {
                    Toast.makeText(MainActivity.this, "輸入框不能為空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addCustomText(String customText, Spinner currentSpinner) {
        // 定义一个匹配"其它:"+任意文本的正则表达式
        String pattern = "^其它:.*";
        //定義一個正在生成的spinnerText
        String spinnerText = "其它:" + customText;
        int customItemsCount = 0;
        int unselectedCustomItemIndex = -1;

        // 遍历数据源以计算符合条件的项的数量，并找到未被选中的"其它:"+任意文本的位置
        for (int i = 0; i < spinnerData.size(); i++) {
            String item = spinnerData.get(i);
            if (item.matches(pattern)) {
                customItemsCount++;
                // 检查这个“其它”项是否未被任何Spinner选中
                boolean isSelected = false;
                for (Spinner spinner : new Spinner[]{spinner1, spinner2, spinner3, spinner4}) {
                    if (spinner.getSelectedItem().toString().equals(item)) {
                        isSelected = true;
                        break;
                    }
                }
                // 如果这个“其它”项未被选中，记录它的位置
                if (!isSelected) {
                    unselectedCustomItemIndex = i;
                    break;
                }
            }
        }
        // 如果“其它”项的数量超过四个，移除当前正在修改的Spinner的值
        if (customItemsCount > 3 && unselectedCustomItemIndex == -1) {
            String currentSelectedItem = currentSpinner.getSelectedItem().toString();
            if (currentSelectedItem.startsWith("其它:")) {
                spinnerData.remove(currentSelectedItem); // 移除当前Spinner的“其它”项
            }
        }
        // 如果存在未被选中的“其它”项，移除它
        if (unselectedCustomItemIndex != -1) {
            spinnerData.remove(unselectedCustomItemIndex);
        }
        // 检查是否已经存在相同的自定义项
        if (!spinnerData.contains(spinnerText)) {
            spinnerData.add(spinnerText);
            sharedAdapter.notifyDataSetChanged(); // 通知数据集改变，更新所有Spinner
        }
    }

    //監聽時間的方法
    private void addTextWatcherToEditText(EditText startTime, EditText endTime) {
        startTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String startTimeStr = editable.toString();
                String endTimeStr = endTime.getText().toString();
                if (isValidDateTime(editable.toString())) {
                    if (endTime.getText().toString().isEmpty()) {
                        startTime.setTextColor(getResources().getColor(R.color.colorPrimary));
                    } else if (isBeforeEndTime(startTimeStr, endTimeStr)) {
                        startTime.setTextColor(getResources().getColor(R.color.colorPrimary));
                        endTime.setTextColor(getResources().getColor(R.color.colorPrimary));
                    } else {
                        startTime.setTextColor(getResources().getColor(R.color.red));
                    }
                } else {

                    startTime.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });
        endTime.addTextChangedListener(new TextWatcher() {
                                           @Override
                                           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                           }

                                           @Override
                                           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                           }

                                           @Override
                                           public void afterTextChanged(Editable editable) {
                                               String startTimeStr = startTime.getText().toString();
                                               String endTimeStr = editable.toString();
                                               if (isValidDateTime(editable.toString())) {
                                                   if (startTime.getText().toString().isEmpty()) {
                                                       endTime.setTextColor(getResources().getColor(R.color.colorPrimary));
                                                   } else if (isBeforeEndTime(startTimeStr, endTimeStr)) {
                                                       startTime.setTextColor(getResources().getColor(R.color.colorPrimary));
                                                       endTime.setTextColor(getResources().getColor(R.color.colorPrimary));
                                                   } else {
                                                       endTime.setTextColor(getResources().getColor(R.color.red));

                                                   }
                                               } else {
                                                   endTime.setTextColor(getResources().getColor(R.color.red));

                                               }
                                           }
                                       }
        );
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidDateTime(String dateTime) {
        return dateTime.matches("^(19|20)\\d\\d[/](0[1-9]|1[0-2])[/](0[1-9]|[12][0-9]|3[01]) (2[0-3]|[01]?[0-9]):([0-5]?[0-9]):([0-5]?[0-9])$");
    }

    private boolean isBeforeEndTime(String startTime, String endTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        try {
            Date startDate = dateFormat.parse(startTime);
            Date endDate = dateFormat.parse(endTime);
            boolean result = startDate.before(endDate);
//            Log.d("TimeComparison", "isBeforeEndTime: " + result);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
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

//    public void setRecord() {
////        SharedPreferences preferences = getSharedPreferences("AppData", MODE_PRIVATE);
////        String route01 = preferences.getString("data_1" + "route", "");
////        String route02 = preferences.getString("data_2" + "route", "");
////        String route03 = preferences.getString("data_3" + "route", "");
////        String route04 = preferences.getString("data_4" + "route", "");
////        String licensePlate01 = preferences.getString("data_1" + "licensePlate", "");
////        String licensePlate02 = preferences.getString("data_2" + "licensePlate", "");
////        String licensePlate03 = preferences.getString("data_3" + "licensePlate", "");
////        String licensePlate04 = preferences.getString("data_4" + "licensePlate", "");
//        String route01 = textView_Record_route_1.getText().toString();
//        String licensePlate01 = textView_Record_licensePlate_1.getText().toString();
//        String route02 = textView_Record_route_2.getText().toString();
//        String licensePlate02 = textView_Record_licensePlate_2.getText().toString();
//        String route03 = textView_Record_route_3.getText().toString();
//        String licensePlate03 = textView_Record_licensePlate_3.getText().toString();
//        String route04 = textView_Record_route_4.getText().toString();
//        String licensePlate04 = textView_Record_licensePlate_4.getText().toString();
//        textView_Record_route_1.setText(route01);
//        textView_Record_route_2.setText(route02);
//        textView_Record_route_3.setText(route03);
//        textView_Record_route_4.setText(route04);
//        textView_Record_licensePlate_1.setText(licensePlate01);
//        textView_Record_licensePlate_2.setText(licensePlate02);
//        textView_Record_licensePlate_3.setText(licensePlate03);
//        textView_Record_licensePlate_4.setText(licensePlate04);
//    }

    public void resetData() {
        new AlertDialog.Builder(this)
                .setTitle("清空數據")
                .setMessage("確定清空所有數據嗎？")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 重置数据的代码
                        resetAllData();
                    }
                })
                .setNegativeButton("取消", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void resetAllData() {
        // 这里添加重置所有数据的代码
        SharedPreferences preferences = getSharedPreferences("AppData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        initSpinnerData();
        startTime_1.setText("");
        endTime_1.setText("");
        route1.setText("");
        licensePlate_1.setText("");
        startTime_2.setText("");
        endTime_2.setText("");
        route2.setText("");
        licensePlate_2.setText("");
        startTime_3.setText("");
        endTime_3.setText("");
        route3.setText("");
        licensePlate_3.setText("");
        startTime_4.setText("");
        endTime_4.setText("");
        route4.setText("");
        licensePlate_4.setText("");
        surveyType_1.setChecked(true);
//        genderM_1.setChecked(true);
//        under20_1.setChecked(true);
        surveyorNo.setText("");
        location.setText("");
        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
        textView_Record_route_1.setText(null);
        textView_Record_route_2.setText(null);
        textView_Record_route_3.setText(null);
        textView_Record_route_4.setText(null);
        textView_Record_licensePlate_1.setText(null);
        textView_Record_licensePlate_2.setText(null);
        textView_Record_licensePlate_3.setText(null);
        textView_Record_licensePlate_4.setText(null);
        // 显示重置成功的Toast消息
        Toast.makeText(getApplicationContext(), "所有數據已重置", Toast.LENGTH_SHORT).show();
    }

    public void cleanRecord(View view) {
        if (view.getId() == R.id.cleanRecordButton) {
            resetData();
        }

    }

    public void checkPermission() {
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

    public void locationList(final TextView textView, final String title, final String[] list) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setItems(list, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                textView.setText(list[which]);
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        alertDialog.show();
    }

    public final <E extends View> E getView(int id) {
        return (E) findViewById(id);
    }

    public void initialization() {
//        genderM_1.setChecked(true);
//        genderM_2.setChecked(true);
//        genderM_3.setChecked(true);
//        genderM_4.setChecked(true);
//
//        under20_1.setChecked(true);
//        under20_2.setChecked(true);
//        under20_3.setChecked(true);
//        under20_4.setChecked(true);

        startTime_1.setText("");
        startTime_2.setText("");
        startTime_3.setText("");
        startTime_4.setText("");

        endTime_1.setText("");
        endTime_2.setText("");
        endTime_3.setText("");
        endTime_4.setText("");

        startTime_1.setEnabled(true);
        startTime_2.setEnabled(true);
        startTime_3.setEnabled(true);
        startTime_4.setEnabled(true);
//        endTime_1.setEnabled(false);
//        endTime_2.setEnabled(false);
//        endTime_3.setEnabled(false);
        location.setText("");

        licensePlate_1.setText("");
        licensePlate_2.setText("");
        licensePlate_3.setText("");
        licensePlate_4.setText("");

        route1.setText("");
        route2.setText("");
        route3.setText("");
        route4.setText("");

        spinner1.setSelection(0);
        spinner2.setSelection(0);
        spinner3.setSelection(0);
        spinner4.setSelection(0);


//        surveyType_1.setChecked(true);

        location.setEnabled(false);
    }

    public void setSpinnerSelectionBasedOnText(Spinner spinner, String textToMatch, Spinner spinnerOther1, Spinner spinnerOther2, Spinner spinnerOther3) {
        // 获取Spinner的适配器
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        boolean isFound = false;
        // 遍历适配器中的所有项
        for (int i = 0; i < adapter.getCount(); i++) {
            // 获取当前项的文本
            String itemText = adapter.getItem(i);
            // 检查当前项的文本是否与给定的字符串匹配
            if (itemText != null && itemText.equals(textToMatch)) {
                // 如果匹配，将其设置为选中状态
                spinner.setSelection(i);
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            String stata = textToMatch; // 示例字符串
            if (stata.startsWith("其它:")) {
                stata = stata.substring("其它:".length()); // 去掉前缀
            }
            // 保存当前选中项
            String selectionText1 = spinnerOther1.getSelectedItem().toString();
            String selectionText2 = spinnerOther2.getSelectedItem().toString();
            String selectionText3 = spinnerOther3.getSelectedItem().toString();
            // 更新Spinner的显示
            addCustomText(stata, spinner); // 调用之前定义的方法添加自定义文本
            spinner.setAdapter(sharedAdapter);
            // 恢复选中项文本
            int selectionOther1 = sharedAdapter.getPosition(selectionText1);
            int selectionOther2 = sharedAdapter.getPosition(selectionText2);
            int selectionOther3 = sharedAdapter.getPosition(selectionText3);

            if (spinnerData.contains("其它:" + stata)) {
                spinner.setSelection(spinnerData.indexOf("其它:" + stata));
            } else {
                spinner.setSelection(spinnerData.size() - 1);
            }
            if (selectionOther1 != -1) {
                spinnerOther1.setSelection(selectionOther1);
            } else {
                spinnerOther1.setSelection(0);
            }
            if (selectionOther2 != -1) {
                spinnerOther2.setSelection(selectionOther2);
            } else {
                spinnerOther2.setSelection(0);
            }
            if (selectionOther3 != -1) {
                spinnerOther3.setSelection(selectionOther3);
            } else {
                spinnerOther3.setSelection(0);
            }
            // 将Spinner的选择设置为相當於新增

//            spinner.setAdapter(sharedAdapter);
//            spinner.setSelection(0);
        }
        // 如果没有找到匹配的项，可以选择将其设置为默认状态或者不进行任何操作
    }

    @SuppressLint("NonConstantResourceId")
    public void loadRecord(View view) {
//        SharedPreferences preferences = getSharedPreferences("AppData", MODE_PRIVATE);
//        String route01 = preferences.getString("data_1" + "route", "");
//        String licensePlate01 = preferences.getString("data_1" + "licensePlate", "");
//        String route02 = preferences.getString("data_2" + "route", "");
//        String licensePlate02 = preferences.getString("data_2" + "licensePlate", "");
//        String route03 = preferences.getString("data_3" + "route", "");
//        String licensePlate03 = preferences.getString("data_3" + "licensePlate", "");
//        String route04 = preferences.getString("data_4" + "route", "");
//        String licensePlate04 = preferences.getString("data_4" + "licensePlate", "");
        String route01 = textView_Record_route_1.getText().toString();
        String licensePlate01 = textView_Record_licensePlate_1.getText().toString();
        String route02 = textView_Record_route_2.getText().toString();
        String licensePlate02 = textView_Record_licensePlate_2.getText().toString();
        String route03 = textView_Record_route_3.getText().toString();
        String licensePlate03 = textView_Record_licensePlate_3.getText().toString();
        String route04 = textView_Record_route_4.getText().toString();
        String licensePlate04 = textView_Record_licensePlate_4.getText().toString();
        if (surveyType_3.isChecked()) {
            switch (view.getId()) {
                case R.id.Group1recordButton_1:
                    if (route01.isEmpty()) {
                        spinner1.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner1, route01, spinner2, spinner3, spinner4);
                    }
                    licensePlate_1.setText(licensePlate01);
                    break;
                case R.id.Group1recordButton_2:
                    if (route02.isEmpty()) {
                        spinner1.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner1, route02, spinner2, spinner3, spinner4);
                    }
                    licensePlate_1.setText(licensePlate02);
                    break;
                case R.id.Group1recordButton_3:
                    if (route03.isEmpty()) {
                        spinner1.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner1, route03, spinner2, spinner3, spinner4);
                    }
                    licensePlate_1.setText(licensePlate03);
                    break;
                case R.id.Group1recordButton_4:
                    if (route04.isEmpty()) {
                        spinner1.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner1, route04, spinner2, spinner3, spinner4);
                    }
                    licensePlate_1.setText(licensePlate04);
                    break;
                case R.id.Group2recordButton_1:
                    if (route01.isEmpty()) {
                        spinner2.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner2, route01, spinner1, spinner3, spinner4);
                    }
                    licensePlate_2.setText(licensePlate01);
                    break;
                case R.id.Group2recordButton_2:
                    if (route02.isEmpty()) {
                        spinner2.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner2, route02, spinner1, spinner3, spinner4);
                    }
                    licensePlate_2.setText(licensePlate02);
                    break;
                case R.id.Group2recordButton_3:
                    if (route03.isEmpty()) {
                        spinner2.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner2, route03, spinner1, spinner3, spinner4);
                    }
                    licensePlate_2.setText(licensePlate03);
                    break;
                case R.id.Group2recordButton_4:
                    if (route04.isEmpty()) {
                        spinner2.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner2, route04, spinner1, spinner3, spinner4);
                    }
                    licensePlate_2.setText(licensePlate04);
                    break;
                case R.id.Group3recordButton_1:
                    if (route01.isEmpty()) {
                        spinner3.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner3, route01, spinner1, spinner2, spinner4);
                    }
                    licensePlate_3.setText(licensePlate01);
                    break;
                case R.id.Group3recordButton_2:
                    if (route02.isEmpty()) {
                        spinner3.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner3, route02, spinner1, spinner2, spinner4);
                    }
                    licensePlate_3.setText(licensePlate02);
                    break;
                case R.id.Group3recordButton_3:
                    if (route03.isEmpty()) {
                        spinner3.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner3, route03, spinner1, spinner2, spinner4);
                    }
                    licensePlate_3.setText(licensePlate03);
                    break;
                case R.id.Group3recordButton_4:
                    if (route04.isEmpty()) {
                        spinner3.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner3, route04, spinner1, spinner2, spinner4);
                    }
                    licensePlate_3.setText(licensePlate04);
                    break;
                case R.id.Group4recordButton_1:
                    if (route01.isEmpty()) {
                        spinner4.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner4, route01, spinner1, spinner2, spinner3);
                    }
                    licensePlate_4.setText(licensePlate01);
                    break;
                case R.id.Group4recordButton_2:
                    if (route02.isEmpty()) {
                        spinner4.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner4, route02, spinner1, spinner2, spinner3);
                    }
                    licensePlate_4.setText(licensePlate02);
                    break;
                case R.id.Group4recordButton_3:
                    if (route03.isEmpty()) {
                        spinner4.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner4, route03, spinner1, spinner2, spinner3);
                    }
                    licensePlate_4.setText(licensePlate03);
                    break;
                case R.id.Group4recordButton_4:
                    if (route04.isEmpty()) {
                        spinner4.setSelection(0);
                    } else {
                        setSpinnerSelectionBasedOnText(spinner4, route04, spinner1, spinner2, spinner3);
                    }
                    licensePlate_4.setText(licensePlate04);
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.Group1recordButton_1:
                    route1.setText(route01);
                    licensePlate_1.setText(licensePlate01);
                    break;
                case R.id.Group1recordButton_2:
                    route1.setText(route02);
                    licensePlate_1.setText(licensePlate02);
                    break;
                case R.id.Group1recordButton_3:
                    route1.setText(route03);
                    licensePlate_1.setText(licensePlate03);
                    break;
                case R.id.Group1recordButton_4:
                    route1.setText(route04);
                    licensePlate_1.setText(licensePlate04);
                    break;
                case R.id.Group2recordButton_1:
                    route2.setText(route01);
                    licensePlate_2.setText(licensePlate01);
                    break;
                case R.id.Group2recordButton_2:
                    route2.setText(route02);
                    licensePlate_2.setText(licensePlate02);
                    break;
                case R.id.Group2recordButton_3:
                    route2.setText(route03);
                    licensePlate_2.setText(licensePlate03);
                    break;
                case R.id.Group2recordButton_4:
                    route2.setText(route04);
                    licensePlate_2.setText(licensePlate04);
                    break;
                case R.id.Group3recordButton_1:
                    route3.setText(route01);
                    licensePlate_3.setText(licensePlate01);
                    break;
                case R.id.Group3recordButton_2:
                    route3.setText(route02);
                    licensePlate_3.setText(licensePlate02);
                    break;
                case R.id.Group3recordButton_3:
                    route3.setText(route03);
                    licensePlate_3.setText(licensePlate03);
                    break;
                case R.id.Group3recordButton_4:
                    route3.setText(route04);
                    licensePlate_3.setText(licensePlate04);
                    break;
                case R.id.Group4recordButton_1:
                    route4.setText(route01);
                    licensePlate_4.setText(licensePlate01);
                    break;
                case R.id.Group4recordButton_2:
                    route4.setText(route02);
                    licensePlate_4.setText(licensePlate02);
                    break;
                case R.id.Group4recordButton_3:
                    route4.setText(route03);
                    licensePlate_4.setText(licensePlate03);
                    break;
                case R.id.Group4recordButton_4:
                    route4.setText(route04);
                    licensePlate_4.setText(licensePlate04);
                    break;
            }
        }

    }

    public void markTime(View view) {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formatTime = simpledateformat.format(currentTime);
        switch (view.getId()) {
            case R.id.startButton_1:
                startTime_1.setText(formatTime);
                break;
            case R.id.startButton_2:
                startTime_2.setText(formatTime);
                break;
            case R.id.startButton_3:
                startTime_3.setText(formatTime);
                break;
            case R.id.startButton_4:
                startTime_4.setText(formatTime);
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
            case R.id.endButton_4:
                endTime_4.setText(formatTime);
                break;
            case R.id.saveButton_1:
//                int selectedID_1 = genderGroup_1.getCheckedRadioButtonId();
//                rb1 = findViewById(selectedID_1);
                saveData(1, route1.getText().toString(), startTime_1.getText().toString(), endTime_1.getText().toString(), licensePlate_1.getText().toString(),
//                        rb1.getText().toString(),
                        "data_1");
                break;
            case R.id.saveButton_2:
//                int selectedID_2 = genderGroup_2.getCheckedRadioButtonId();
//                rb2 = findViewById(selectedID_2);
                saveData(2, route2.getText().toString(), startTime_2.getText().toString(), endTime_2.getText().toString(), licensePlate_2.getText().toString(),
//                        rb2.getText().toString(),
                        "data_2");
                break;
            case R.id.saveButton_3:
//                int selectedID_3 = genderGroup_3.getCheckedRadioButtonId();
//                rb3 = findViewById(selectedID_3);
                saveData(3, route3.getText().toString(), startTime_3.getText().toString(), endTime_3.getText().toString(), licensePlate_3.getText().toString(),
//                        rb3.getText().toString(),
                        "data_3");
                break;
            case R.id.saveButton_4:
//                int selectedID_4 = genderGroup_4.getCheckedRadioButtonId();
//                rb4 = findViewById(selectedID_4);
                saveData(4, route4.getText().toString(), startTime_4.getText().toString(), endTime_4.getText().toString(), licensePlate_4.getText().toString(),
//                        rb4.getText().toString(),
                        "data_4");
                break;
        }

    }

    //        @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // 权限被授予，可以执行存储操作
//            } else {
//                // 权限被拒绝，提示用户或关闭相关功能
//                showToast("當前沒有讀寫權限,將不能進行保存");
//            }
//        }
//    }
    public void saveData(final int number, final String route, final String startTime,
                         final String endTime, final String licensePlate,
//                         final String gender,
                         String key) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH");
        Date currentTime = Calendar.getInstance().getTime();
        final String date = sdf.format(currentTime.getTime());
        boolean licensePlate_format = licensePlate.matches("[A-Za-z][A-Za-z][\\d]{4}");
//      boolean date_Format = startTime.matches("[0-2]{2}[0-9]{2}\\/[0-1][0-9]\\/[0-3][0-9][[:blank:]][0-2][0-9]:[0-6][0-9]:[0-6][0-9]");
        boolean date_FormatStart = isValidDateTime(startTime);
        boolean date_FormatEnd = isValidDateTime(endTime);
        boolean comparisonTime = isBeforeEndTime(startTime, endTime);
//        textView1.setTextColor(Color.BLACK);
//        textView2.setTextColor(Color.BLACK);
//        Group1TextView3.setTextColor(Color.BLACK);


        if (surveyorNo.getText().toString().isEmpty()) {
            textView1.setTextColor(Color.RED);
            Toast.makeText(this, "請輸入調查員編號", Toast.LENGTH_SHORT).show();
        } else if (location.getText().toString().isEmpty()) {
            textView2.setTextColor(Color.RED);
            Toast.makeText(this, "請輸入調查地點", Toast.LENGTH_SHORT).show();
        } else if (!licensePlate_format) {
            Toast.makeText(this, "請輸入六位巴士車牌。", Toast.LENGTH_SHORT).show();
        } else if (!date_FormatStart || !date_FormatEnd) {
            Toast.makeText(this, "請輸入準確的時間", Toast.LENGTH_SHORT).show();
        } else if (!comparisonTime) {
            Toast.makeText(this, "開始時間應小於結束時間", Toast.LENGTH_SHORT).show();
        } else {

            if ((route.isEmpty() || endTime.isEmpty()) && !surveyType_3.isChecked()) {
                switch (number) {
                    case 1:
                        Toast.makeText(this, "乘客A未完成記錄。", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(this, "乘客B未完成記錄。", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(this, "乘客C未完成記錄。", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(this, "乘客D未完成記錄。", Toast.LENGTH_SHORT).show();
                        break;
                }
            } else if (route.isEmpty() || route.equals("請選擇")) {
                switch (number) {
                    case 1:
                        Toast.makeText(this, "乘客A請選擇線路", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(this, "乘客B請選擇線路", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(this, "乘客C請選擇線路", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(this, "乘客D請選擇線路", Toast.LENGTH_SHORT).show();
                        break;

                }

            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("確認儲存資料︰");
                dialog.setMessage("調查員編號︰" + surveyorNo.getText().toString() + "\n調查地點︰" + location.getText().toString() +
//                        "\n乘客性別︰" + gender +
                        "\n巴士路線︰" + route +
                        "\n巴士車牌︰" + licensePlate +
                        "\n乘客到站時間︰" + startTime +
                        "\n乘客上車時間︰" + endTime);
                dialog.setNegativeButton("N0", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
//                        checkPermission();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (!Environment.isExternalStorageManager()) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                startActivityForResult(intent, REQUEST_CODE_MANAGE_EXTERNAL_STORAGE);
                                showToast("請先開通讀取權限,否則不能進行保存");
                            } else if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // 请求精确位置权限
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                                showToast("請先開通位置權限,否則不能進行保存");
                            } else {
                                proceedWithOperations(number, route, startTime, endTime, licensePlate,
//                                    gender,
                                        key);
                            }
                        } else {
                            int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            int permissionFine = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                            int permissionCoarse = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

                            if (permission != PackageManager.PERMISSION_GRANTED) {
                                showToast("請先開通讀取權限,否則不能進行保存");
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            } else if (permissionFine != PackageManager.PERMISSION_GRANTED && permissionCoarse != PackageManager.PERMISSION_GRANTED) {
                                // 检查是否已经被授予了精确位置权限或大致位置权限
                                showToast("請先開通位置權限，否則不能進行保存");
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            } else {
                                // 权限已被授予，可以执行存储操作
                                proceedWithOperations(number, route, startTime, endTime, licensePlate,
//                                    gender,
                                        key);
                            }
                        }
                    }
                });

                dialog.show();
            }
        }
    }

//拆出來寫
//    private void proceedWithOperations(final int number, final String route, final String startTime,
//                                       final String endTime, final String licensePlate,
////                                        final String gender,
//                                       String key) {
//        // 这里放置你的其他操作代码，例如保存数据、创建文件夹等
////        SharedPreferences preferences = getSharedPreferences("AppData", MODE_PRIVATE);
////        SharedPreferences.Editor editor = preferences.edit();
////        editor.putString(key + "route", route);
////        editor.putString(key + "licensePlate", licensePlate);
////        editor.apply();
//        //不是選擇的話就直接獲取input的值
////        if (!surveyType_3.isChecked()) {
////            switch (key) {
////                case "data1_":
////                    textView_Record_route_1.setText(route1.getText().toString());
////                    textView_Record_licensePlate_1.setText(licensePlate_1.getText().toString());
////                    break;
////                case "data2_":
////                    textView_Record_route_2.setText(route2.getText().toString());
////                    textView_Record_licensePlate_2.setText(licensePlate_2.getText().toString());
////                    break;
////                case "data3_":
////                    textView_Record_route_3.setText(route3.getText().toString());
////                    textView_Record_licensePlate_3.setText(licensePlate_3.getText().toString());
////                    break;
////                case "data4_":
////                    textView_Record_route_4.setText(route4.getText().toString());
////                    textView_Record_licensePlate_4.setText(licensePlate_4.getText().toString());
////                    break;
////            }
////        }
//
//        if (surveyType_1.isChecked()) {
//            String sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            String fileDir = sdcard0Path + "/UMTEC/";
//            boolean wasSuccessful = false;
//
//            Calendar calendar = Calendar.getInstance();
//            String yearStr = calendar.get(Calendar.YEAR) + "";//获取年份
//            int month = calendar.get(Calendar.MONTH) + 1;//获取月份
//            String monthStr = month < 10 ? "0" + month : month + "";
//            int day = calendar.get(Calendar.DATE);//获取日
//            String dayStr = day < 10 ? "0" + day : day + "";
//
//            File file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
//            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            fileDir = sdcard0Path + "/UMTEC/BusWaitingTime/";
//
//            file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
//            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            fileDir = sdcard0Path + "/UMTEC/BusWaitingTime/" + yearStr + "/";
//
//            file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
//            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            fileDir = sdcard0Path + "/UMTEC/BusWaitingTime/" + yearStr + "/" + yearStr + monthStr + "/";
//
//            file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
//            String fileName = surveyorNo.getText().toString() + "-" + yearStr + monthStr + dayStr + ".txt";
//            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            fileDir = sdcard0Path + "/UMTEC/BusWaitingTime/" + yearStr + "/" + yearStr + monthStr + "/";
//            String filePath = fileDir + fileName;
//
//            file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
////            if (!wasSuccessful) {
////                Toast.makeText(MainActivity.this, "成功建立檔案", Toast.LENGTH_SHORT).show();
////            }
//
////                          <--------------------------------------------------------------
//            String checkingBusStop = String.valueOf(location.getText());
//            if (station.locationNeedCopy.contains(checkingBusStop)) {
//                String fileName_copy = surveyorNo.getText().toString() + "-" + yearStr + monthStr + dayStr + "-copyFromNormal" + ".txt";
//                String sdcard0Path_copy = Environment.getExternalStorageDirectory().getPath();
//                String fileDir_copy = sdcard0Path_copy + "/UMTEC/BusWaitingTime(specialRoute)/" + yearStr + "/" + yearStr + monthStr + "/";
//                String filePath_copy = fileDir_copy + fileName_copy;
//
//                File file_copy = new File(fileDir_copy);
//                if (!file_copy.exists()) {
//                    wasSuccessful = file_copy.mkdir();
//                }
//
////                if (!wasSuccessful) {
////                    Toast.makeText(MainActivity.this, "成功建立檔案", Toast.LENGTH_SHORT).show();
////                }
//
//                try {
//
//                    location();
//
//                    FileOutputStream fOut = new FileOutputStream(filePath_copy, true);
//                    fOut.write(surveyorNo.getText().toString().getBytes());
//                    fOut.write("-".getBytes());
//                    fOut.write(location.getText().toString().getBytes());
//                    fOut.write("-".getBytes());
//                    fOut.write(String.valueOf(lon).getBytes());
//                    fOut.write("-".getBytes());
//                    fOut.write(String.valueOf(lat).getBytes());
//                    fOut.write("-".getBytes());
//                    setRecord();
//                    switch (number) {
//                        case 1:
////                            switch (genderGroup_1.getCheckedRadioButtonId()) {
////                                case R.id.genderM_1:
////                                    fOut.write("M-".getBytes());
////                                    break;
////                                case R.id.genderF_1:
////                                    fOut.write("F-".getBytes());
////                                    break;
////                            }
////
////                            switch (ageGroup_1.getCheckedRadioButtonId()) {
////                                case R.id.under20_1:
////                                    fOut.write("A-".getBytes());
////                                    break;
////                                case R.id.from20to45_1:
////                                    fOut.write("B-".getBytes());
////                                    break;
////                                case R.id.above45_1:
////                                    fOut.write("C-".getBytes());
////                                    break;
////                            }
//
//                            fOut.write(route.getBytes());
//                            fOut.write("-".getBytes());
//                            fOut.write(licensePlate_1.getText().toString().getBytes());
//                            fOut.write("-".getBytes());
//                            fOut.write(startTime.getBytes());
//                            fOut.write("-".getBytes());
//                            fOut.write(endTime.getBytes());
//
////                            startTime_1.setText("");
////                            endTime_1.setText("");
////                            route1.setText("");
////                            licensePlate_1.setText("");
////                            textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
////                            textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 2:
////                            switch (genderGroup_2.getCheckedRadioButtonId()) {
////                                case R.id.genderM_2:
////                                    fOut.write("M-".getBytes());
////                                    break;
////                                case R.id.genderF_2:
////                                    fOut.write("F-".getBytes());
////                                    break;
////                            }
////
////                            switch (ageGroup_2.getCheckedRadioButtonId()) {
////                                case R.id.under20_2:
////                                    fOut.write("A-".getBytes());
////                                    break;
////                                case R.id.from20to45_2:
////                                    fOut.write("B-".getBytes());
////                                    break;
////                                case R.id.above45_2:
////                                    fOut.write("C-".getBytes());
////                                    break;
////                            }
//
//                            fOut.write(route.getBytes());
//                            fOut.write("-".getBytes());
//                            fOut.write(licensePlate_2.getText().toString().getBytes());
//                            fOut.write("-".getBytes());
//                            fOut.write(startTime.getBytes());
//                            fOut.write("-".getBytes());
//                            fOut.write(endTime.getBytes());
//
////                            startTime_2.setText("");
////                            endTime_2.setText("");
////                            route2.setText("");
////                            licensePlate_2.setText("");
////                            textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
////                            textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 3:
////                            switch (genderGroup_3.getCheckedRadioButtonId()) {
////                                case R.id.genderM_3:
////                                    fOut.write("M-".getBytes());
////                                    break;
////                                case R.id.genderF_3:
////                                    fOut.write("F-".getBytes());
////                                    break;
////                            }
////
////                            switch (ageGroup_3.getCheckedRadioButtonId()) {
////                                case R.id.under20_3:
////                                    fOut.write("A-".getBytes());
////                                    break;
////                                case R.id.from20to45_3:
////                                    fOut.write("B-".getBytes());
////                                    break;
////                                case R.id.above45_3:
////                                    fOut.write("C-".getBytes());
////                                    break;
////                            }
//
//                            fOut.write(route.getBytes());
//                            fOut.write("-".getBytes());
//                            fOut.write(licensePlate_3.getText().toString().getBytes());
//                            fOut.write("-".getBytes());
//                            fOut.write(startTime.getBytes());
//                            fOut.write("-".getBytes());
//                            fOut.write(endTime.getBytes());
//
////                            startTime_3.setText("");
////                            endTime_3.setText("");
////                            route3.setText("");
////                            licensePlate_3.setText("");
////                            textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
////                            textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 4:
////                            switch (genderGroup_4.getCheckedRadioButtonId()) {
////                                case R.id.genderM_4:
////                                    fOut.write("M-".getBytes());
////                                    break;
////                                case R.id.genderF_4:
////                                    fOut.write("F-".getBytes());
////                                    break;
////                            }
////
////                            switch (ageGroup_4.getCheckedRadioButtonId()) {
////                                case R.id.under20_4:
////                                    fOut.write("A-".getBytes());
////                                    break;
////                                case R.id.from20to45_4:
////                                    fOut.write("B-".getBytes());
////                                    break;
////                                case R.id.above45_4:
////                                    fOut.write("C-".getBytes());
////                                    break;
////                            }
//                            fOut.write(route.getBytes());
//                            fOut.write("-".getBytes());
//                            fOut.write(licensePlate_4.getText().toString().getBytes());
//                            fOut.write("-".getBytes());
//                            fOut.write(startTime.getBytes());
//                            fOut.write("-".getBytes());
//                            fOut.write(endTime.getBytes());
//
////                            startTime_4.setText("");
////                            endTime_4.setText("");
////                            route4.setText("");
////                            licensePlate_4.setText("");
////                            textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
////                            textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//
//                    }
//                    fOut.write("\r\n".getBytes());
//                    Toast.makeText(MainActivity.this, "已儲存複製副本", Toast.LENGTH_SHORT).show();
//
//                } catch (Exception e) {
//                    Log.d("Ch7_4_InternalStorage", "例外發生: " + e.toString());
//                    Toast.makeText(MainActivity.this, "Fail!!! Please Contact Developer", Toast.LENGTH_SHORT).show();
//                }
//            }
//
////                           checking bus stop that in the station.java locationNeedCopy arrylist
////                           if contains, create 普通 and 101x/102x folder and files.
////                          --------------------------------------------------------------->
//            try {
//
//                location();
//
//                FileOutputStream fOut = new FileOutputStream(filePath, true);
//
//                fOut.write(surveyorNo.getText().toString().getBytes());
//                fOut.write("-".getBytes());
//                fOut.write(location.getText().toString().getBytes());
//                fOut.write("-".getBytes());
//                fOut.write(String.valueOf(lon).getBytes());
//                fOut.write("-".getBytes());
//                fOut.write(String.valueOf(lat).getBytes());
//                fOut.write("-".getBytes());
//                setRecord();
//                switch (number) {
//                    case 1:
////                        switch (genderGroup_1.getCheckedRadioButtonId()) {
////                            case R.id.genderM_1:
////                                fOut.write("M-".getBytes());
////                                break;
////                            case R.id.genderF_1:
////                                fOut.write("F-".getBytes());
////                                break;
////                        }
////
////                        switch (ageGroup_1.getCheckedRadioButtonId()) {
////                            case R.id.under20_1:
////                                fOut.write("A-".getBytes());
////                                break;
////                            case R.id.from20to45_1:
////                                fOut.write("B-".getBytes());
////                                break;
////                            case R.id.above45_1:
////                                fOut.write("C-".getBytes());
////                                break;
////                        }
//
//                        fOut.write(route.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(licensePlate_1.getText().toString().getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(startTime.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(endTime.getBytes());
//
//                        startTime_1.setText("");
//                        endTime_1.setText("");
//                        route1.setText("");
//                        licensePlate_1.setText("");
//                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        break;
//                    case 2:
////                        switch (genderGroup_2.getCheckedRadioButtonId()) {
////                            case R.id.genderM_2:
////                                fOut.write("M-".getBytes());
////                                break;
////                            case R.id.genderF_2:
////                                fOut.write("F-".getBytes());
////                                break;
////                        }
////
////                        switch (ageGroup_2.getCheckedRadioButtonId()) {
////                            case R.id.under20_2:
////                                fOut.write("A-".getBytes());
////                                break;
////                            case R.id.from20to45_2:
////                                fOut.write("B-".getBytes());
////                                break;
////                            case R.id.above45_2:
////                                fOut.write("C-".getBytes());
////                                break;
////                        }
//
//                        fOut.write(route.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(licensePlate_2.getText().toString().getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(startTime.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(endTime.getBytes());
//
//                        startTime_2.setText("");
//                        endTime_2.setText("");
//                        route2.setText("");
//                        licensePlate_2.setText("");
//                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        break;
//                    case 3:
////                        switch (genderGroup_3.getCheckedRadioButtonId()) {
////                            case R.id.genderM_3:
////                                fOut.write("M-".getBytes());
////                                break;
////                            case R.id.genderF_3:
////                                fOut.write("F-".getBytes());
////                                break;
////                        }
////
////                        switch (ageGroup_3.getCheckedRadioButtonId()) {
////                            case R.id.under20_3:
////                                fOut.write("A-".getBytes());
////                                break;
////                            case R.id.from20to45_3:
////                                fOut.write("B-".getBytes());
////                                break;
////                            case R.id.above45_3:
////                                fOut.write("C-".getBytes());
////                                break;
////                        }
//
//                        fOut.write(route.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(licensePlate_3.getText().toString().getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(startTime.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(endTime.getBytes());
//
//                        startTime_3.setText("");
//                        endTime_3.setText("");
//                        route3.setText("");
//                        licensePlate_3.setText("");
//                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        break;
//                    case 4:
////                        switch (genderGroup_4.getCheckedRadioButtonId()) {
////                            case R.id.genderM_4:
////                                fOut.write("M-".getBytes());
////                                break;
////                            case R.id.genderF_4:
////                                fOut.write("F-".getBytes());
////                                break;
////                        }
////
////                        switch (ageGroup_4.getCheckedRadioButtonId()) {
////                            case R.id.under20_4:
////                                fOut.write("A-".getBytes());
////                                break;
////                            case R.id.from20to45_4:
////                                fOut.write("B-".getBytes());
////                                break;
////                            case R.id.above45_4:
////                                fOut.write("C-".getBytes());
////                                break;
////                        }
//
//                        fOut.write(route.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(licensePlate_4.getText().toString().getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(startTime.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(endTime.getBytes());
//
//                        startTime_4.setText("");
//                        endTime_4.setText("");
//                        route4.setText("");
//                        licensePlate_4.setText("");
//                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        break;
//                }
//                fOut.write("\r\n".getBytes());
//                Toast.makeText(MainActivity.this, "已儲存。", Toast.LENGTH_SHORT).show();
//
//
//            } catch (Exception e) {
//                Log.d("Ch_4_InternalStorage", "例外發生: " + e.toString());
//                Toast.makeText(MainActivity.this, "Fail!!! Please Contact Developer", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        if (surveyType_2.isChecked()) {
//            String sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            String fileDir = sdcard0Path + "/UMTEC/";
//            boolean wasSuccessful = false;
//
//            Calendar calendar = Calendar.getInstance();
//            String yearStr = calendar.get(Calendar.YEAR) + "";//获取年份
//            int month = calendar.get(Calendar.MONTH) + 1;//获取月份
//            String monthStr = month < 10 ? "0" + month : month + "";
//            int day = calendar.get(Calendar.DATE);//获取日
//            String dayStr = day < 10 ? "0" + day : day + "";
//
//            File file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
//            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            fileDir = sdcard0Path + "/UMTEC/BusWaitingTime(specialRoute)/";
//
//            file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
//            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            fileDir = sdcard0Path + "/UMTEC/BusWaitingTime(specialRoute)/" + yearStr + "/";
//
//            file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
//            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            fileDir = sdcard0Path + "/UMTEC/BusWaitingTime(specialRoute)/" + yearStr + "/" + yearStr + monthStr + "/";
//
//            file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
//
//            String fileName = surveyorNo.getText().toString() + "-" + yearStr + monthStr + dayStr + "-" + "x" + ".txt";
//            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            fileDir = sdcard0Path + "/UMTEC/BusWaitingTime(specialRoute)/" + yearStr + "/" + yearStr + monthStr + "/";
//            String filePath = fileDir + fileName;
//
//            file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
////            if (!wasSuccessful) {
////                Toast.makeText(MainActivity.this, "成功建立檔案", Toast.LENGTH_SHORT).show();
////            }
//
//            Log.d("pathTest: ", String.valueOf(file.exists()));
//
//            try {
//
//                location();
//
//                FileOutputStream fOut = new FileOutputStream(filePath, true);
//
//                fOut.write(surveyorNo.getText().toString().getBytes());
//                fOut.write("-".getBytes());
//                fOut.write(location.getText().toString().getBytes());
//                fOut.write("-".getBytes());
//                fOut.write(String.valueOf(lon).getBytes());
//                fOut.write("-".getBytes());
//                fOut.write(String.valueOf(lat).getBytes());
//                fOut.write("-".getBytes());
//                setRecord();
//                switch (number) {
//                    case 1:
////                        switch (genderGroup_1.getCheckedRadioButtonId()) {
////                            case R.id.genderM_1:
////                                fOut.write("M-".getBytes());
////                                break;
////                            case R.id.genderF_1:
////                                fOut.write("F-".getBytes());
////                                break;
////                        }
////
////                        switch (ageGroup_1.getCheckedRadioButtonId()) {
////                            case R.id.under20_1:
////                                fOut.write("A-".getBytes());
////                                break;
////                            case R.id.from20to45_1:
////                                fOut.write("B-".getBytes());
////                                break;
////                            case R.id.above45_1:
////                                fOut.write("C-".getBytes());
////                                break;
////                        }
//
//                        fOut.write(route.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(licensePlate_1.getText().toString().getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(startTime.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(endTime.getBytes());
//
//                        startTime_1.setText("");
//                        endTime_1.setText("");
//                        route1.setText("");
//                        licensePlate_1.setText("");
//                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        break;
//                    case 2:
////                        switch (genderGroup_2.getCheckedRadioButtonId()) {
////                            case R.id.genderM_2:
////                                fOut.write("M-".getBytes());
////                                break;
////                            case R.id.genderF_2:
////                                fOut.write("F-".getBytes());
////                                break;
////                        }
////
////                        switch (ageGroup_2.getCheckedRadioButtonId()) {
////                            case R.id.under20_2:
////                                fOut.write("A-".getBytes());
////                                break;
////                            case R.id.from20to45_2:
////                                fOut.write("B-".getBytes());
////                                break;
////                            case R.id.above45_2:
////                                fOut.write("C-".getBytes());
////                                break;
////                        }
//
//                        fOut.write(route.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(licensePlate_2.getText().toString().getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(startTime.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(endTime.getBytes());
//
//                        startTime_2.setText("");
//                        endTime_2.setText("");
//                        route2.setText("");
//                        licensePlate_2.setText("");
//                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        break;
//                    case 3:
////                        switch (genderGroup_3.getCheckedRadioButtonId()) {
////                            case R.id.genderM_3:
////                                fOut.write("M-".getBytes());
////                                break;
////                            case R.id.genderF_3:
////                                fOut.write("F-".getBytes());
////                                break;
////                        }
////
////                        switch (ageGroup_3.getCheckedRadioButtonId()) {
////                            case R.id.under20_3:
////                                fOut.write("A-".getBytes());
////                                break;
////                            case R.id.from20to45_3:
////                                fOut.write("B-".getBytes());
////                                break;
////                            case R.id.above45_3:
////                                fOut.write("C-".getBytes());
////                                break;
////                        }
//
//                        fOut.write(route.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(licensePlate_3.getText().toString().getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(startTime.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(endTime.getBytes());
//
//                        startTime_3.setText("");
//                        endTime_3.setText("");
//                        route3.setText("");
//                        licensePlate_3.setText("");
//                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        break;
//                    case 4:
////                        switch (genderGroup_4.getCheckedRadioButtonId()) {
////                            case R.id.genderM_4:
////                                fOut.write("M-".getBytes());
////                                break;
////                            case R.id.genderF_4:
////                                fOut.write("F-".getBytes());
////                                break;
////                        }
////
////                        switch (ageGroup_4.getCheckedRadioButtonId()) {
////                            case R.id.under20_4:
////                                fOut.write("A-".getBytes());
////                                break;
////                            case R.id.from20to45_4:
////                                fOut.write("B-".getBytes());
////                                break;
////                            case R.id.above45_4:
////                                fOut.write("C-".getBytes());
////                                break;
////                        }
//
//                        fOut.write(route.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(licensePlate_4.getText().toString().getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(startTime.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(endTime.getBytes());
//
//                        startTime_4.setText("");
//                        endTime_4.setText("");
//                        route4.setText("");
//                        licensePlate_4.setText("");
//                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        break;
//                }
//                fOut.write("\r\n".getBytes());
//                Toast.makeText(MainActivity.this, "已儲存。", Toast.LENGTH_SHORT).show();
//
//
//            } catch (Exception e) {
//                Log.d("Ch7_4_InternalStorage", "例外發生: " + e.toString());
//                Toast.makeText(MainActivity.this, "Fail!!! Please Contact Developer", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        if (surveyType_3.isChecked()) {
//            String sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            String fileDir = sdcard0Path + "/UMTEC/";
//            boolean wasSuccessful = false;
//
//            Calendar calendar = Calendar.getInstance();
//            String yearStr = calendar.get(Calendar.YEAR) + "";//获取年份
//            int month = calendar.get(Calendar.MONTH) + 1;//获取月份
//            String monthStr = month < 10 ? "0" + month : month + "";
//            int day = calendar.get(Calendar.DATE);//获取日
//            String dayStr = day < 10 ? "0" + day : day + "";
//
//            File file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
//            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            fileDir = sdcard0Path + "/UMTEC/BusWaitingTime(hengqin)/";
//
//            file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
//            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            fileDir = sdcard0Path + "/UMTEC/BusWaitingTime(hengqin)/" + yearStr + "/";
//
//            file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
//            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            fileDir = sdcard0Path + "/UMTEC/BusWaitingTime(hengqin)/" + yearStr + "/" + yearStr + monthStr + "/";
//
//            file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
//
//            String fileName = surveyorNo.getText().toString() + "-" + yearStr + monthStr + dayStr + "-" + "h" + ".txt";
//            sdcard0Path = Environment.getExternalStorageDirectory().getPath();
//            fileDir = sdcard0Path + "/UMTEC/BusWaitingTime(hengqin)/" + yearStr + "/" + yearStr + monthStr + "/";
//            String filePath = fileDir + fileName;
//
//            file = new File(fileDir);
//            if (!file.exists()) {
//                wasSuccessful = file.mkdir();  //Create New folder
//            }
//
//
//            try {
//
//                location();
//
//                FileOutputStream fOut = new FileOutputStream(filePath, true);
//
//                fOut.write(surveyorNo.getText().toString().getBytes());
//                fOut.write("-".getBytes());
//                fOut.write(location.getText().toString().getBytes());
//                fOut.write("-".getBytes());
//                fOut.write(String.valueOf(lon).getBytes());
//                fOut.write("-".getBytes());
//                fOut.write(String.valueOf(lat).getBytes());
//                fOut.write("-".getBytes());
//                setRecord();
//                switch (number) {
//                    case 1:
////                        switch (genderGroup_1.getCheckedRadioButtonId()) {
////                            case R.id.genderM_1:
////                                fOut.write("M-".getBytes());
////                                break;
////                            case R.id.genderF_1:
////                                fOut.write("F-".getBytes());
////                                break;
////                        }
////
////                        switch (ageGroup_1.getCheckedRadioButtonId()) {
////                            case R.id.under20_1:
////                                fOut.write("A-".getBytes());
////                                break;
////                            case R.id.from20to45_1:
////                                fOut.write("B-".getBytes());
////                                break;
////                            case R.id.above45_1:
////                                fOut.write("C-".getBytes());
////                                break;
////                        }
//
//                        fOut.write(route.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(licensePlate_1.getText().toString().getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(startTime.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(endTime.getBytes());
//
//                        startTime_1.setText("");
//                        endTime_1.setText("");
////                                        route1.setText("");
//                        licensePlate_1.setText("");
//                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        break;
//                    case 2:
////                        switch (genderGroup_2.getCheckedRadioButtonId()) {
////                            case R.id.genderM_2:
////                                fOut.write("M-".getBytes());
////                                break;
////                            case R.id.genderF_2:
////                                fOut.write("F-".getBytes());
////                                break;
////                        }
////
////                        switch (ageGroup_2.getCheckedRadioButtonId()) {
////                            case R.id.under20_2:
////                                fOut.write("A-".getBytes());
////                                break;
////                            case R.id.from20to45_2:
////                                fOut.write("B-".getBytes());
////                                break;
////                            case R.id.above45_2:
////                                fOut.write("C-".getBytes());
////                                break;
////                        }
//
//                        fOut.write(route.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(licensePlate_2.getText().toString().getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(startTime.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(endTime.getBytes());
//
//                        startTime_2.setText("");
//                        endTime_2.setText("");
////                                        route2.setText("");
//                        licensePlate_2.setText("");
//                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        break;
//                    case 3:
////                        switch (genderGroup_3.getCheckedRadioButtonId()) {
////                            case R.id.genderM_3:
////                                fOut.write("M-".getBytes());
////                                break;
////                            case R.id.genderF_3:
////                                fOut.write("F-".getBytes());
////                                break;
////                        }
////
////                        switch (ageGroup_3.getCheckedRadioButtonId()) {
////                            case R.id.under20_3:
////                                fOut.write("A-".getBytes());
////                                break;
////                            case R.id.from20to45_3:
////                                fOut.write("B-".getBytes());
////                                break;
////                            case R.id.above45_3:
////                                fOut.write("C-".getBytes());
////                                break;
////                        }
//
//                        fOut.write(route.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(licensePlate_3.getText().toString().getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(startTime.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(endTime.getBytes());
//
//                        startTime_3.setText("");
//                        endTime_3.setText("");
////                                        route3.setText("");
//                        licensePlate_3.setText("");
//                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        break;
//                    case 4:
////                        switch (genderGroup_4.getCheckedRadioButtonId()) {
////                            case R.id.genderM_4:
////                                fOut.write("M-".getBytes());
////                                break;
////                            case R.id.genderF_4:
////                                fOut.write("F-".getBytes());
////                                break;
////                        }
////
////                        switch (ageGroup_4.getCheckedRadioButtonId()) {
////                            case R.id.under20_4:
////                                fOut.write("A-".getBytes());
////                                break;
////                            case R.id.from20to45_4:
////                                fOut.write("B-".getBytes());
////                                break;
////                            case R.id.above45_4:
////                                fOut.write("C-".getBytes());
////                                break;
////                        }
//
//                        fOut.write(route.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(licensePlate_4.getText().toString().getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(startTime.getBytes());
//                        fOut.write("-".getBytes());
//                        fOut.write(endTime.getBytes());
//
//                        startTime_4.setText("");
//                        endTime_4.setText("");
////                                        route3.setText("");
//                        licensePlate_4.setText("");
//                        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        break;
//                }
//                fOut.write("\r\n".getBytes());
//                Toast.makeText(MainActivity.this, "已儲存。", Toast.LENGTH_SHORT).show();
//
//
//            } catch (Exception e) {
//                Log.d("Ch7_4_InternalStorage", "例外發生: " + e.toString());
//                Toast.makeText(MainActivity.this, "Fail!!! Please Contact Developer", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private void proceedWithOperations(final int number, final String route, final String startTime,
                                       final String endTime, final String licensePlate,
                                       String key) {
        //儲存記錄
        SharedPreferences preferences = getSharedPreferences("AppData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key + "route", route);
        editor.putString(key + "licensePlate", licensePlate);
        editor.apply();

        if (!surveyType_3.isChecked()) {
            updateUI(key, route, licensePlate);
        }

        if (surveyType_1.isChecked()) {
            saveDataToFile("normal", route, licensePlate, startTime, endTime, number);
        } else if (surveyType_2.isChecked()) {
            saveDataToFile("101x", route, licensePlate, startTime, endTime, number);
        } else if (surveyType_3.isChecked()) {
            saveDataToFile("hengqin", route, licensePlate, startTime, endTime, number);
        }
    }

    private void updateUI(String key, String route, String licensePlate) {
        switch (key) {
            case SURVEYOR_FOLDER_PREFIX + "1":
                textView_Record_route_1.setText(route);
                textView_Record_licensePlate_1.setText(licensePlate);
                break;
            case SURVEYOR_FOLDER_PREFIX + "2":
                textView_Record_route_2.setText(route);
                textView_Record_licensePlate_2.setText(licensePlate);
                break;
            case SURVEYOR_FOLDER_PREFIX + "3":
                textView_Record_route_3.setText(route);
                textView_Record_licensePlate_3.setText(licensePlate);
                break;
            case SURVEYOR_FOLDER_PREFIX + "4":
                textView_Record_route_4.setText(route);
                textView_Record_licensePlate_4.setText(licensePlate);
                break;
        }
    }

    private void createDirectoryIfNotExists(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private String getFileName(String surveyorNo, String currentDate, String type) {
        //獲取安卓ID
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String baseFileName = surveyorNo + "-" + currentDate;
        switch (type) {
            case "name_copy":
                return baseFileName + "-CN" + "-" + deviceId + ".txt";
            case "name_101x":
                return baseFileName + "-x" + "-" + deviceId + ".txt";
            case "name_hengqin":
                return baseFileName + "-h" + "-" + deviceId + ".txt";
            case "name_hengqin_Copy":
                return baseFileName + "-CH" + "-" + deviceId + ".txt";
            default:
                return baseFileName + "-" + deviceId + ".txt";
        }
    }

    public String updateRouteValue(String currentRoute) {
        for (String busNumber : busNumArray) {
            if (currentRoute.equals(busNumber)) {
                switch (busNumber) {
                    case "701X (往望德聖母灣)":
                    case "701X (往澳大)":
                        return "701X";
                }
                break; // 找到匹配项后退出循环
            }
        }
        return currentRoute;// 如果没有匹配，返回原值
    }

    private void writeToFileTryAndCatch(String surveyorNo, String filePath,
                                        String route, String licensePlate,
                                        String startTime, String endTime,
                                        String type, int number,
                                        String location, String toast
//                                          double lon, double lat
    ) {
        if (type.equals("hengqinRouteWithOutWay")) {
            route = updateRouteValue(route);
        }
        try {
            location();
            FileOutputStream fOut = new FileOutputStream(filePath, true);

            writeToFile(type, fOut, surveyorNo, route, licensePlate, startTime, endTime, number, location
//                    Double.parseDouble(lon.getText().toString()), Double.parseDouble(lat.getText().toString())
            );
            switch (toast) {
                case "toastNormal":
                    showToast("已儲存。");
                    break;
                case "toastCopy":
                    showToast("已存儲並複製副本。");
                    break;
                case "toastCopyHengQin":
                    showToast("已存儲並複製兩份副本。");
                    break;
                default:
                    break;
            }
//            setRecord();

        } catch (Exception e) {
            Log.d("FileWriteError", "例外發生: " + e.toString());
            Toast.makeText(MainActivity.this, "Fail!!! Please Contact Developer", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveDataToFile(String type, String route, String licensePlate, String startTime, String endTime, int number) {
        String sdcard0Path = Environment.getExternalStorageDirectory().getPath();
        String baseFolderPath = sdcard0Path + "/" + BASE_FOLDER;
        createDirectoryIfNotExists(baseFolderPath);

        Calendar calendar = Calendar.getInstance();
        String yearStr = String.valueOf(calendar.get(Calendar.YEAR));//获取年份
        @SuppressLint("DefaultLocale") String monthStr = String.format("%02d", calendar.get(Calendar.MONTH) + 1);//获取月份
        @SuppressLint("DefaultLocale") String dayStr = String.format("%02d", calendar.get(Calendar.DATE));
        String currentDate = yearStr + monthStr + dayStr;

        // 创建普通和特殊路线文件夹的路径
        String filePath = baseFolderPath + "/" + BUS_WAITING_TIME_FOLDER + "/" + yearStr + "/" + yearStr + monthStr + "/";
        String specialRoutePath = baseFolderPath + "/" + SPECIAL_ROUTE_FOLDER + "/" + yearStr + "/" + yearStr + monthStr + "/";
        String hengqinPath = baseFolderPath + "/" + HENGQIN_FOLDER + "/" + "/" + yearStr + "/" + yearStr + monthStr + "/";
//        createDirectoryIfNotExists(filePath);


        String fileName = getFileName(surveyorNo.getText().toString(), currentDate, "name_normal");
        String fileNameCopy = getFileName(surveyorNo.getText().toString(), currentDate, "name_copy");
        String fileNameX = getFileName(surveyorNo.getText().toString(), currentDate, "name_101x");
        String fileNameH = getFileName(surveyorNo.getText().toString(), currentDate, "name_hengqin");
        String fileNameHCopy = getFileName(surveyorNo.getText().toString(), currentDate, "name_hengqin_Copy");
        String checkingBusStop = String.valueOf(location.getText());
//            if (station.locationNeedCopy.contains(checkingBusStop)) {
//
//    }
//        if (Arrays.asList(station.location_hengqin).contains(checkingBusStop)) {
//            // 如果检查到的站点是横琴澳方口岸，执行相应的操作
//        }

        switch (type) {
            case "normal":
                if (station.locationNeedCopy.contains(checkingBusStop)) {
                    createDirectoryIfNotExists(filePath);
                    writeToFileTryAndCatch(
                            surveyorNo.getText().toString(), filePath + fileName,
                            route, licensePlate,
                            startTime, endTime,
                            "none", number,
                            location.getText().toString(), "toastNone"
//                Double.parseDouble(lon.getText().toString()), Double.parseDouble(lat.getText().toString())
                    );
                    createDirectoryIfNotExists(specialRoutePath);
                    writeToFileTryAndCatch(
                            surveyorNo.getText().toString(), specialRoutePath + fileNameCopy,
                            route, licensePlate,
                            startTime, endTime,
                            "normalCleanRoute", number,
                            location.getText().toString(), "toastCopy"
//                Double.parseDouble(lon.getText().toString()), Double.parseDouble(lat.getText().toString())
                    );
                } else {
                    createDirectoryIfNotExists(filePath);
                    writeToFileTryAndCatch(
                            surveyorNo.getText().toString(), filePath + fileName,
                            route, licensePlate,
                            startTime, endTime,
                            "normalCleanRoute", number,
                            location.getText().toString(), "toastNormal"
//                Double.parseDouble(lon.getText().toString()), Double.parseDouble(lat.getText().toString())
                    );
                }
                break;
            case "101x":
                createDirectoryIfNotExists(specialRoutePath);
                writeToFileTryAndCatch(
                        surveyorNo.getText().toString(), specialRoutePath + fileNameX,
                        route, licensePlate,
                        startTime, endTime,
                        "normalCleanRoute", number,
                        location.getText().toString(), "toastNormal"
//                Double.parseDouble(lon.getText().toString()), Double.parseDouble(lat.getText().toString())
                );
                break;
            case "hengqin":
                createDirectoryIfNotExists(filePath);
                createDirectoryIfNotExists(specialRoutePath);
                createDirectoryIfNotExists(hengqinPath);
                writeToFileTryAndCatch(
                        surveyorNo.getText().toString(), filePath + fileNameHCopy,
                        route, licensePlate,
                        startTime, endTime,
                        "hengqinRouteWithOutWay", number,
                        location.getText().toString(), "toastNone"
//                Double.parseDouble(lon.getText().toString()), Double.parseDouble(lat.getText().toString())
                );
                writeToFileTryAndCatch(
                        surveyorNo.getText().toString(), specialRoutePath + fileNameHCopy,
                        route, licensePlate,
                        startTime, endTime,
                        "hengqinRouteWithOutWay", number,
                        location.getText().toString(), "toastNone"
//                Double.parseDouble(lon.getText().toString()), Double.parseDouble(lat.getText().toString())
                );
                writeToFileTryAndCatch(
                        surveyorNo.getText().toString(), hengqinPath + fileNameH,
                        route, licensePlate,
                        startTime, endTime,
                        "hengqinOption", number,
                        location.getText().toString(), "toastCopyHengQin"
//                Double.parseDouble(lon.getText().toString()), Double.parseDouble(lat.getText().toString())
                );
                break;
        }
    }

    private void writeToFile(String type, FileOutputStream fOut, String surveyorNo,
                             String route, String licensePlate,
                             String startTime, String endTime, int number,
                             String location
//                             double lon, double lat
    ) throws IOException {
        fOut.write((surveyorNo + "-").getBytes());
        fOut.write((location + "-").getBytes());
        fOut.write((String.valueOf(lon) + "-").getBytes());
        fOut.write((String.valueOf(lat) + "-").getBytes());
//        setRecord();
        switch (number) {
            case 1:
                fOut.write((route + "-").getBytes());
                fOut.write((licensePlate_1.getText().toString() + "-").getBytes());
                fOut.write((startTime + "-").getBytes());
                fOut.write((endTime + "\n").getBytes());
                cleanEditText(type, number);
                break;
            case 2:
                fOut.write((route + "-").getBytes());
                fOut.write((licensePlate_2.getText().toString() + "-").getBytes());
                fOut.write((startTime + "-").getBytes());
                fOut.write((endTime + "\n").getBytes());
                cleanEditText(type, number);
                break;
            case 3:
                fOut.write((route + "-").getBytes());
                fOut.write((licensePlate_3.getText().toString() + "-").getBytes());
                fOut.write((startTime + "-").getBytes());
                fOut.write((endTime + "\n").getBytes());
                cleanEditText(type, number);
                break;
            case 4:
                fOut.write((route + "-").getBytes());
                fOut.write((licensePlate_4.getText().toString() + "-").getBytes());
                fOut.write((startTime + "-").getBytes());
                fOut.write((endTime + "\n").getBytes());
                cleanEditText(type, number);
                break;
        }
    }

    public void cleanEditText(String type, int number) {
        if (!type.equals("none") && !type.equals("hengqinRouteWithOutWay")) {
            String route01 = textView_Record_route_1.getText().toString();
            String licensePlate01 = textView_Record_licensePlate_1.getText().toString();
            String route02 = textView_Record_route_2.getText().toString();
            String licensePlate02 = textView_Record_licensePlate_2.getText().toString();
            String route03 = textView_Record_route_3.getText().toString();
            String licensePlate03 = textView_Record_licensePlate_3.getText().toString();
            String route04 = textView_Record_route_4.getText().toString();
            String licensePlate04 = textView_Record_licensePlate_4.getText().toString();
            switch (number) {
                case 1:
                    startTime_1.setText("");
                    endTime_1.setText("");
                    if (!type.equals("hengqinOption")) {
                        route1.setText("");
                    }
                    licensePlate_1.setText("");
                    textView_Record_route_1.setText(route01);
                    textView_Record_licensePlate_1.setText(licensePlate01);
                    textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                    textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                case 2:
                    startTime_2.setText("");
                    endTime_2.setText("");
                    if (!type.equals("hengqinOption")) {
                        route2.setText("");
                    }
                    licensePlate_2.setText("");
                    textView_Record_route_2.setText(route02);
                    textView_Record_licensePlate_2.setText(licensePlate02);
                    textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                    textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                case 3:
                    startTime_3.setText("");
                    endTime_3.setText("");
                    if (!type.equals("hengqinOption")) {
                        route3.setText("");
                    }
                    licensePlate_3.setText("");
                    textView_Record_route_3.setText(route03);
                    textView_Record_licensePlate_3.setText(licensePlate03);
                    textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                    textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                case 4:
                    startTime_4.setText("");
                    endTime_4.setText("");
                    if (!type.equals("hengqinOption")) {
                        route4.setText("");
                    }
                    licensePlate_4.setText("");
                    textView_Record_route_4.setText(route04);
                    textView_Record_licensePlate_4.setText(licensePlate04);
                    textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                    textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
            }
        }


    }

    public void FindView() {
//        scrollView = findViewById(R.id.scrollView);
//        circleButton = findViewById(R.id.circleButtonOne);

        surveyorNo = getView(R.id.surveyorNo);
        location = getView(R.id.location);
        route1 = getView(R.id.route1);
        route2 = getView(R.id.route2);
        route3 = getView(R.id.route3);
        route4 = getView(R.id.route4);

        startTime_1 = getView(R.id.startTime_1);
        startTime_2 = getView(R.id.startTime_2);
        startTime_3 = getView(R.id.startTime_3);
        startTime_4 = getView(R.id.startTime_4);

        endTime_1 = getView(R.id.endTime_1);
        endTime_2 = getView(R.id.endTime_2);
        endTime_3 = getView(R.id.endTime_3);
        endTime_4 = getView(R.id.endTime_4);

        licensePlate_1 = getView(R.id.licensePlate_1);
        licensePlate_2 = getView(R.id.licensePlate_2);
        licensePlate_3 = getView(R.id.licensePlate_3);
        licensePlate_4 = getView(R.id.licensePlate_4);

        startButton_1 = getView(R.id.startButton_1);
        startButton_2 = getView(R.id.startButton_2);
        startButton_3 = getView(R.id.startButton_3);
        startButton_4 = getView(R.id.startButton_4);

        endButton_1 = getView(R.id.endButton_1);
        endButton_2 = getView(R.id.endButton_2);
        endButton_3 = getView(R.id.endButton_3);
        endButton_4 = getView(R.id.endButton_4);

        saveButton_1 = getView(R.id.saveButton_1);
        saveButton_2 = getView(R.id.saveButton_2);
        saveButton_3 = getView(R.id.saveButton_3);
        saveButton_4 = getView(R.id.saveButton_4);

//        genderF_1 = getView(R.id.genderF_1);
//        genderF_2 = getView(R.id.genderF_2);
//        genderF_3 = getView(R.id.genderF_3);
//        genderF_4 = getView(R.id.genderF_4);
//
//        genderM_1 = getView(R.id.genderM_1);
//        genderM_2 = getView(R.id.genderM_2);
//        genderM_3 = getView(R.id.genderM_3);
//        genderM_4 = getView(R.id.genderM_4);
//
//        under20_1 = getView(R.id.under20_1);
//        under20_2 = getView(R.id.under20_2);
//        under20_3 = getView(R.id.under20_3);
//        under20_4 = getView(R.id.under20_4);
//
//        from20to45_1 = getView(R.id.from20to45_1);
//        from20to45_2 = getView(R.id.from20to45_2);
//        from20to45_3 = getView(R.id.from20to45_3);
//        from20to45_4 = getView(R.id.from20to45_4);
//
//        above45_1 = getView(R.id.above45_1);
//        above45_2 = getView(R.id.above45_2);
//        above45_3 = getView(R.id.above45_3);
//        above45_4 = getView(R.id.above45_4);

        surveyType_1 = getView(R.id.normal_rd);
        surveyType_2 = getView(R.id.S101x_rb);
        surveyType_3 = getView(R.id.hengqin_rb);

        surveyType_rbg = getView(R.id.surveyType_rbg);

//        genderGroup_1 = getView(R.id.genderGroup_1);
//        genderGroup_2 = getView(R.id.genderGroup_2);
//        genderGroup_3 = getView(R.id.genderGroup_3);
//        genderGroup_4 = getView(R.id.genderGroup_4);
//
//        ageGroup_1 = getView(R.id.ageGroup_1);
//        ageGroup_2 = getView(R.id.ageGroup_2);
//        ageGroup_3 = getView(R.id.ageGroup_3);
//        ageGroup_4 = getView(R.id.ageGroup_4);

        textView1 = getView(R.id.textView1);
        textView2 = getView(R.id.textView2);

        Group1TextView3 = getView(R.id.Group1TextView3);
        Group2TextView3 = getView(R.id.Group2TextView3);
        Group3TextView3 = getView(R.id.Group3TextView3);
        Group4TextView3 = getView(R.id.Group4TextView3);

        spinner1 = getView(R.id.spinner1);
        spinner2 = getView(R.id.spinner2);
        spinner3 = getView(R.id.spinner3);
        spinner4 = getView(R.id.spinner4);

        textView_Record_licensePlate_1 = getView(R.id.textView_Record_licensePlate_1);
        textView_Record_licensePlate_2 = getView(R.id.textView_Record_licensePlate_2);
        textView_Record_licensePlate_3 = getView(R.id.textView_Record_licensePlate_3);
        textView_Record_licensePlate_4 = getView(R.id.textView_Record_licensePlate_4);
        textView_Record_route_1 = getView(R.id.textView_Record_route_1);
        textView_Record_route_2 = getView(R.id.textView_Record_route_2);
        textView_Record_route_3 = getView(R.id.textView_Record_route_3);
        textView_Record_route_4 = getView(R.id.textView_Record_route_4);

        stationButton = getView(R.id.stationButton);
        constraint = getView(R.id.constraint);
//        overlay = getView(R.id.overlay);
//        centerButton_1 = getView(R.id.centerButton_1);
        Group1recordButton_1 = getView(R.id.Group1recordButton_1);
        Group1recordButton_2 = getView(R.id.Group1recordButton_2);
        Group1recordButton_3 = getView(R.id.Group1recordButton_3);
        Group1recordButton_4 = getView(R.id.Group1recordButton_4);
    }

    //    public void location() {
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        provider = locationManager.getProvider(LocationManager.GPS_PROVIDER).getName();
//        if (provider != null && !provider.equals("")) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                        || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                }
//            }
//            Location location = locationManager.getLastKnownLocation(provider);
//
//            LocationListener locationListener = new LocationListener() {
//                public void onLocationChanged(Location location) {
//                }
//
//                public void onStatusChanged(String provider, int status, Bundle extras) {
//                }
//
//                public void onProviderEnabled(String provider) {
//                }
//
//                public void onProviderDisabled(String provider) {
//                }
//            };
//
//            locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
//
//            if (location != null){
//                onLocationChanged(location);
//
//            }
//            else {
//                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
//        }
//    }
    public void location() {
        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            String providerName;
            assert locationManager != null;
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                providerName = LocationManager.GPS_PROVIDER;
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                providerName = LocationManager.NETWORK_PROVIDER;
            } else {
//                Toast.makeText(getBaseContext(), "No location provider available", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);
                    return;
                }
            }
            Location location = locationManager.getLastKnownLocation(providerName);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            locationManager.requestLocationUpdates(providerName, 0, 0, locationListener);
            if (location != null) {
                onLocationChanged(location);
            } else {
//                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Error", "Exception in location(): " + e.getMessage());
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
    public void onMoveToBackground() {
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
                                switch (selectedType) {
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

    class Spinner1Class implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            route1.setText(busNumArray[i]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    class Spinner2Class implements AdapterView.OnItemSelectedListener {
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

    class Spinner4Class implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            route4.setText(busNumArray[i]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
