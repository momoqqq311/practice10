package com.example.hong.practice10;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String path;
    ListView listView;
    LinearLayout L1, L2;
    DatePicker datePicker;
    EditText edt;
    ArrayList<String> file_name = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    TextView textView;
    Button b1, b2, b3;
    int modify_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1 = (Button) findViewById(R.id.btnsave);
        b2 = (Button) findViewById(R.id.btncancel);
        b3 = (Button) findViewById(R.id.btn1);
        textView = (TextView) findViewById(R.id.tvCount);
        listView = (ListView) findViewById(R.id.listview);
        L1 = (LinearLayout) findViewById(R.id.linear1);
        L2 = (LinearLayout) findViewById(R.id.linear2);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        edt = (EditText) findViewById(R.id.editText);
        path = getExternalPath();
        File file = new File(path + "diary");
        file.mkdir();
        String msg = "디렉터리 생성";
        path = path + "diary/";
        if (file.isDirectory() == false) msg = "디렉터리 생성 오류";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        File[] files = new File(path).listFiles();
        for (File f : files) {
            f.delete();
        }
        textView.setText("등록된 메모 개수: " + file_name.size());
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, file_name);
        listView.setAdapter(adapter);
        Collections.sort(file_name, dataAsc);
        adapter.notifyDataSetChanged();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(view.getContext());
                dlg.setTitle("삭제 확인 ");
                dlg.setIcon(R.mipmap.ic_launcher);
                dlg.setMessage("해당 메모를 삭제하겠습니까?");
                dlg.setNegativeButton("취소", null);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        file_name.remove(position);
                        File delete_file = new File(path + file_name.get(position));
                        delete_file.delete();
                        Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                        Collections.sort(file_name, dataAsc);
                        adapter.notifyDataSetChanged();
                    }
                });
                dlg.show();

                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                L1.setVisibility(View.INVISIBLE);
                L2.setVisibility(View.VISIBLE);
                b1.setText("수정");
                modify_position = position;
                try {
                    BufferedReader br = new BufferedReader(new
                            FileReader(path + file_name.get(position)));
                    String readStr = "";
                    String str = null;
                    while ((str = br.readLine()) != null) readStr += str + "\n";
                    br.close();
                    edt.setText(readStr);
                    return;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "File not found", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        });
    }

    public String getExternalPath() {
        String sdPath = "";
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            sdPath =
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
//sdPath = "/mnt/sdcard/";
        } else
            sdPath = getFilesDir() + "";
        return sdPath;
    }

    public void onClick(View v) throws IOException {
        switch (v.getId()) {
            case R.id.btn1:
                L1.setVisibility(View.INVISIBLE);
                L2.setVisibility(View.VISIBLE);
                break;
            case R.id.btnsave:
                if (b1.getText().toString().equals("저장")) {
                    edt.setText("");
                    try {
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth();
                        int year = datePicker.getYear();
                        String name = year + "-" + month + "-" + day + ".memo";
                        if(check(name)){
                            BufferedWriter bw = new BufferedWriter(new FileWriter(path + name, true));
                            bw.write(edt.getText().toString());
                            bw.close();
                            Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();
                            file_name.add(name);
                            adapter.notifyDataSetChanged();
                            L1.setVisibility(View.VISIBLE);
                            L2.setVisibility(View.INVISIBLE);
                            textView.setText("등록된 메모 개수: " + file_name.size());
                            Collections.sort(file_name, dataAsc);
                            adapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(this, "이미 존재하는 메모입니다.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage() + ":" + getFilesDir() + "오류!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int day = datePicker.getDayOfMonth();
                    int month = datePicker.getMonth();
                    int year = datePicker.getYear();

                    String name = year + "-" + month + "-" + day + ".memo";

                    File delete_file = new File(path + file_name.get(modify_position));
                    delete_file.delete();
                    file_name.remove(modify_position);
                    try {
                        if(check(name)){
                            BufferedWriter bw = new BufferedWriter(new FileWriter(path + name, true));
                            bw.write(edt.getText().toString());
                            bw.close();
                            Toast.makeText(this, "수정정완료", Toast.LENGTH_SHORT).show();
                            file_name.add(name);
                            adapter.notifyDataSetChanged();
                            L1.setVisibility(View.VISIBLE);
                            L2.setVisibility(View.INVISIBLE);
                            textView.setText("등록된 메모 개수: " + file_name.size());
                            Collections.sort(file_name, dataAsc);
                            adapter.notifyDataSetChanged();
                            b1.setText("저장");
                        }else{
                            Toast.makeText(this, "이미 존재하는 메모입니다.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage() + ":" + getFilesDir() + "오류!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.btncancel:
                L1.setVisibility(View.VISIBLE);
                L2.setVisibility(View.INVISIBLE);
                b1.setText("저장");
                break;
        }
    }

    Comparator<String> dataAsc = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    };

    boolean check(String name) {
        File[] files = new File(path).listFiles();
        for (File f : files) {
            if(f.getName().equals(name)){
                return false;
            }
        }
        return true;
    }

}
