package com.example.doandidong;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //Thêm cơ sở dữ liệu SQLite
    public static String DATABASE_NAME ="dbA1.sqlite";
    public static String DB_PATH_SUFFIX ="/databases/";
    public static SQLiteDatabase database = null;

    //Khoi tao cac controls Main
    Button buttonDangNhap, buttonDangKy;
    TextView textViewQuenMatKHau;

    //Khoi tao cac controls trong dialog dang nhap
    Button buttonDangNhapDialog, buttonHuyDialog;
    EditText editTextTaiKhoanDangNhap, editTextMatKhauDangNhap;

    //Khoi TAo cac controls trong dialog danh ky
    Button buttonDangKyDialog, buttonHuyDangKyDialog;
    EditText editTextHoTen, editTextMatKhau, editTextTaiKhoan;
    TextView textViewGioiTinh, textViewNamSinh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Thêm cơ sở dữ liệu vào app
        ThemCSDL();

        //Khởi tạo database
        KhoiTaoDataBase();

        //Tham chieu toi cac controls
        AnhXa();

        //Su kien click button dang nhap tren activity main
        ClickBuuttonDangNhap();

        //su kien click button dang ky tren activity main
        ClickButtonDangKy();
    }


    //Xu ly su kien click button dang ky, show dialog dang ky
    private void ClickButtonDangKy() {
        buttonDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialogDangKy();
            }
        });

    }

    private void ShowDialogDangKy() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog_dangky);
        dialog.show();

        //Anh xa cac controls trong dialog dang ky
        buttonDangKyDialog = dialog.findViewById(R.id.buttonDangKyDialog);
        buttonHuyDangKyDialog = dialog.findViewById(R.id.buttonHuyDangKyDialog);
        editTextTaiKhoan = dialog.findViewById(R.id.editTextTaiKhoan);
        editTextHoTen = dialog.findViewById(R.id.editTextHoTen);
        editTextMatKhau = dialog.findViewById(R.id.editTextMatKhau);
        textViewNamSinh = dialog.findViewById(R.id.textViewNamSinh);
        textViewGioiTinh = dialog.findViewById(R.id.textViewGioiTinh);

        //xu ly khi nguoi dung click vao nam sinh
        textViewNamSinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChayDataTimePicker();
            }
        });

        //xu ly chon gioi tinh
        textViewGioiTinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popupMenu = new PopupMenu(MainActivity.this,view);
                popupMenu.inflate(R.menu.popup_namnu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.itemNam){
                            textViewGioiTinh.setText("Nam");
                        }
                        else if(menuItem.getItemId() == R.id.itemNu){
                                textViewGioiTinh.setText("Nữ");
                        }
                        else {
                            textViewGioiTinh.setText("Nam");
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        //Su kien click button dang ky tren dialog dang ky
        buttonDangKyDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(editTextHoTen) || isEmpty(editTextMatKhau) || isEmpty(editTextTaiKhoan) ||textViewNamSinh.getText().toString().equals("") ) {
                    Toast.makeText(MainActivity.this, "Người dùng chưa điền đầy đủ thông tin", Toast.LENGTH_LONG).show();
                } else {
                    //khai bao de add vao cdsl
                    String ten = editTextTaiKhoan.getText().toString();
                    String gioiTinh = textViewGioiTinh.getText().toString();
                    String namSinh = textViewNamSinh.getText().toString();
                    String taiKhoan = editTextTaiKhoan.getText().toString();
                    String matKhau = editTextMatKhau.getText().toString();

                    //put vao Content values
                    ContentValues values = new ContentValues();
                    values.put("Ten", ten);
                    values.put("GioiTinh", gioiTinh);
                    values.put("NamSinh", namSinh);
                    values.put("TaiKhoan", taiKhoan);
                    values.put("MatKhau", matKhau);

                    //insert database
                    long kq = MainActivity.database.insert("NguoiChoi", null, values);
                    if (kq > 0) {
                        Toast.makeText(MainActivity.this, "Đăng ký thành công!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } else {

                        Toast.makeText(MainActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //su kien click button huy dang ky tren dialog dang ky
        buttonHuyDangKyDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //tat outside off dialog
        dialog.setCanceledOnTouchOutside(false);
    }

    //Tao datatime picker
    private void ChayDataTimePicker() {

        final Calendar calendar = Calendar.getInstance();
        int nam = 1997;
        int thang = 9;
        int ngay = 29;
        DatePickerDialog datePickerDialog =new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                calendar.set(i,i1,i2);
                textViewNamSinh.setText(simpleDateFormat.format(calendar.getTime()));
            }
        },nam,thang,ngay);
        datePickerDialog.show();
    }

    //kiem tra edittext co null hay khong
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    //Su kien click button dang nhap show dialog
    private void ClickBuuttonDangNhap() {
        buttonDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialogDangNhap();
            }
        });
    }

    //Show dialog dang nhap
    private void ShowDialogDangNhap() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog_dangnhap);
        dialog.show();

        //anh xa controls trong dialog
        buttonDangNhapDialog =dialog.findViewById(R.id.buttonDangNhapDialog);
        buttonHuyDialog = dialog.findViewById(R.id.buttonHuyDialog);
        editTextTaiKhoanDangNhap = dialog.findViewById(R.id.editTextUserName);
        editTextMatKhauDangNhap = dialog.findViewById(R.id.editTextPassword);

        //xu ly su kien dang nhap, kiem tra va cho nguoi dung dang nhap
        buttonDangNhapDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Xu ly dang nhap kiem tra nguoi dung
                KiemTraTaiKhoanMatKhau();
            }

            //Kiem tra tai khoan mat khau cua nguoi dung
            private void KiemTraTaiKhoanMatKhau() {
                //Lay tai khoan nguoi dung nhap vao
                String taiKhoan = editTextTaiKhoanDangNhap.getText().toString();
                String matKhau = editTextMatKhauDangNhap.getText().toString();

                //lay database va kiem tra
                Cursor cursor = database.rawQuery("select * from NguoiChoi",null);
                while (cursor.moveToNext()){
                    String taiKhoanCSDL = cursor.getString(4);
                    if(taiKhoan.equalsIgnoreCase(taiKhoanCSDL)) {
                        String matKHauCSDL = cursor.getString(5);
                        if(matKhau.equalsIgnoreCase(matKHauCSDL)){
                            Toast.makeText(MainActivity.this, "Người dùng đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            ChuyenActivity();
                        }
                    }
                }
            }
        });

        //Xu ly su kien huy
        buttonHuyDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
    }

    //Chuyen activity
    private void ChuyenActivity() {
        Intent intent = new Intent(MainActivity.this,NavigationDrawerActivity.class);
        startActivity(intent);
    }


    //Tham chieu toi cac controls
    private void AnhXa() {
        buttonDangKy = findViewById(R.id.buttonDangKy);
        buttonDangNhap = findViewById(R.id.buttonDangNhap);
        textViewQuenMatKHau = findViewById(R.id.textViewQuenMatKhau1);
    }

    private void KhoiTaoDataBase() {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
    }

    //Thêm cơ sở dữ liệu
    private void ThemCSDL() {
        try{
            File dbFile = getDatabasePath(DATABASE_NAME);
            if(!dbFile.exists()){
                //
                CopyTuFileAssets();
                Toast.makeText(this, "Da xong", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex){
            Toast.makeText(this, ""+ex.toString(), Toast.LENGTH_LONG).show();
            Log.d("aaa",ex.toString());
        }
    }

    //Copy từ file assets
    private void CopyTuFileAssets() {
        try{
            InputStream myInput = getAssets().open(DATABASE_NAME);
            String outFileName = getDatabasePath();
            File f = new File(getApplicationInfo().dataDir+DB_PATH_SUFFIX);
            if(!f.exists()){
                f.mkdir();
            }
            OutputStream myOutPut = new FileOutputStream(outFileName);
            byte[] buff = new byte[1024];
            int length;
            while((length = myInput.read(buff)) > 0){
                myOutPut.write(buff,0,length);
            }
            myOutPut.flush();
            myOutPut.close();
            myInput.close();
        }
        catch (Exception ex){
            Log.e("loi", ex.toString());

        }
    }
    //Trả đường dẫn
    private String getDatabasePath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
    }



}
