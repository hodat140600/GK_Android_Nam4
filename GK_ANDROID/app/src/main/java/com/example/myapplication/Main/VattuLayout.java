package com.example.myapplication.Main;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Databases.VatTuDatabase;
import com.example.myapplication.Entities.NhanVien;
import com.example.myapplication.Entities.VatTu;
import com.example.myapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VattuLayout extends AppCompatActivity {
    // Main Layout
    TableLayout vt_table_list;

    List<VatTu> vtlist;

    Button insertBtn;
    Button editBtn;
    Button delBtn;
    Button exitBtn;
    Button previewBtn; // <- Nút này để set Preview cái hàng đó, bấm vào là hiên Pop up chỉ để coi, ko chỉnh sửa
    // Preview Image Layout
    TextView VT_IP_maVT;
    TextView VT_IP_tenVT;
    TextView VT_IP_DVT;
    TextView VT_IP_Gia;
    ImageView VT_IP_Hinh;
    // --------------

    // Navigation
    Button navPK;
    Button navNV;
    Button navVT;
    Button navCP;

    EditText search;

    // Dialog Layout
    Dialog vtdialog;

    Button backBtn;
    Button yesBtn;
    Button noBtn;

    EditText inputMaVT;
    EditText inputTenVT;
    EditText inputDVT;
    EditText inputGia;
    ImageView inputHinh;

    TextView showMVTError;
    TextView showTVTError;
    TextView showDVTError;
    TextView showGiaError;

    TextView showResult;
    TextView showConfirm;
    TextView showLabel;

    // Database Controller
    VatTuDatabase vattuDB;

    // Focus
    int indexofRow = -1;
    TableRow focusRow;
    TextView focusMaVT;
    TextView focusTenVT;
    TextView focusDVT;
    TextView focusGia;
    ArrayList<byte[]> image_list = new ArrayList<>();
    byte[] focusDataHinh = null;

    // Other
    float scale;

    // Key Code
    int IMAGE_FOLDER = 1000;
    int PERMISSION_GRANTED = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vattu_layout);
        scale = this.getResources().getDisplayMetrics().density;
        setControl();
        loadDatabase();
        setEvent();
        setNavigation();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) { filter(s.toString());
            editBtn.setVisibility(View.INVISIBLE);
            delBtn.setVisibility(View.INVISIBLE);
            previewBtn.setVisibility(View.INVISIBLE);}
        });
        hideSystemUI();
    }

    private void filter(String toString) {
        TableRow tr = (TableRow) vt_table_list.getChildAt(0);
        int dem =1;
        vt_table_list.removeAllViews();
        vt_table_list.addView(tr);
        image_list.clear();
        for (int k = 0; k < vtlist.size(); k++) {
            VatTu vatTu = vtlist.get(k);
            if (vatTu.getMaVt().toLowerCase().trim().contains(toString.trim().toLowerCase()) || vatTu.getTenVt().toLowerCase().contains(toString.toLowerCase())) {

                tr = createRow(VattuLayout.this, vatTu);

                tr.setId((int) dem++);
                vt_table_list.addView(tr);
                setEventTableRows(tr, vt_table_list);
            }

        }
    }

    public void setControl(){
        vt_table_list = findViewById(R.id.VT_table_list);
        insertBtn = findViewById(R.id.VT_insertBtn);
        editBtn = findViewById(R.id.VT_editBtn);
        delBtn = findViewById(R.id.VT_delBtn);
        exitBtn = findViewById(R.id.VT_exitBtn);
        previewBtn = findViewById(R.id.VT_previewBtn);

        navPK = findViewById(R.id.VT_navbar_phongkho);
        navNV = findViewById(R.id.VT_navbar_nhanvien);
        navVT = findViewById(R.id.VT_navbar_VT);
        navCP = findViewById(R.id.VT_navbar_capphat);

        search = findViewById(R.id.VT_searchEdit);

    }

    public void setCursorWindowImageSize( int B ){
        // Khai báo một field mới cho khả năng lưu hình độ phân giải lớn
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, B); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDatabase(){
        vattuDB = new VatTuDatabase(this);
        vtlist = new ArrayList<>();
        TableRow tr = null;
        // Nếu không có dòng này thì nó sẽ báo lỗi Row quá to không thể nhét vào Cursor trong select
        setCursorWindowImageSize(100 * 1024 * 1024); // 100 MB max
        vtlist = vattuDB.select();
        // Tag sẽ bắt đầu ở 1 vì phải cộng thêm thằng example đã có sẵn
        for (int i = 0; i < vtlist.size(); i++) {
            tr = createRow(this, vtlist.get(i));
            tr.setId((int) i + 1);
            vt_table_list.addView(tr);
        }
    }

    public void setEvent(){
        editBtn.setVisibility(View.INVISIBLE); // turn on when click items
        delBtn.setVisibility(View.INVISIBLE);  // this too
        previewBtn.setVisibility(View.INVISIBLE);
        setEventTable(vt_table_list);

    }

    public void setNavigation(){
        navPK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(VattuLayout.this, PhongkhoLayout.class);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                startActivity( intent );

            }
        });
        // navNV
        navNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(VattuLayout.this, PhieuNhapLayout.class);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                startActivity( intent );

            }
        });
        // navCP
        navCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VattuLayout.this, ChiTietPhieuNhapLayout.class);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                startActivity( intent );
            }

        });
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setEventTable(TableLayout list) {
        // Log.d("count", list.getChildCount()+""); // số table rows + 1
        // Không cần thay đổi vì đây chỉ mới set Event
        // Do có thêm 1 thằng example để làm gốc, nên số row thì luôn luôn phải + 1
        // Có example thì khi thêm row thì nó sẽ theo khuôn
        for (int i = 0; i < list.getChildCount(); i++) {
            setEventTableRows((TableRow) list.getChildAt(i), list);
        }
        // Có thêm 1 thằng Preview để xem trước thông tin của hàng đang chỉ định ( focusRow )

        previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Image from Database is handled to load here
                createDialog(R.layout.popup_vt_previewimage);
                // Custom set Control
                VT_IP_maVT = vtdialog.findViewById(R.id.VT_IP_maVT);
                VT_IP_tenVT = vtdialog.findViewById(R.id.VT_IP_tenVT);
                VT_IP_DVT = vtdialog.findViewById(R.id.VT_IP_DVT);
                VT_IP_Gia = vtdialog.findViewById(R.id.VT_IP_Gia);
                VT_IP_Hinh = vtdialog.findViewById(R.id.VT_IP_Hinh);
                // Load Data
                setDataImageView(VT_IP_Hinh, image_list.get(indexofRow -1));
                VT_IP_maVT.setText(focusMaVT.getText().toString().trim());
                VT_IP_tenVT.setText(focusTenVT.getText().toString().trim());
                VT_IP_DVT.setText(focusDVT.getText().toString().trim());
                VT_IP_Gia.setText(focusGia.getText().toString().trim());
            }
        });

        // Khi tạo, dùng n làm tag để thêm row
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // H bấm 1 cái là hiện ra cái pop up
                createDialog(R.layout.popup_vt);
                // Control
                setControlDialog();
                // Event
                setEventDialog(v);
                setEventImagePicker();
            }
        });
        // Khi edit
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indexofRow != -1) {
                    createDialog(R.layout.popup_vt);
                    // Control
                    setControlDialog();
                    showLabel.setText("Sửa vật tư");
                    showConfirm.setText("Bạn có muốn sửa hàng này không?");
                    // Event
                    setEventDialog(v);
                    setEventImagePicker();
                    setDataImageView(inputHinh, focusDataHinh);
                    inputMaVT.setText(focusMaVT.getText());
                    inputTenVT.setText(focusTenVT.getText());
                    inputDVT.setText(focusDVT.getText());
                    inputGia.setText(focusGia.getText());
                    inputMaVT.setEnabled(false);
                }
            }
        });
        // Khi delete, có 3 TH : nằm ở cuối hoặc nằm ở đầu hoặc chính giữa
        // Nằm ở cuối thì chỉ cần xóa cuối
        // Còn lại thì sau khi xóa xong thì phải cập nhật lại tag cho toàn bộ col
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indexofRow != -1) {
                    createDialog(R.layout.popup_vt);
                    // Control
                    setControlDialog();
                    showLabel.setText("Xóa vật tư");
                    showConfirm.setText("Bạn có muốn sửa hàng này không?");
                    // Event
                    setEventDialog(v);
                    setDataImageView(inputHinh,focusDataHinh);
                    inputMaVT.setText(focusMaVT.getText());
                    inputTenVT.setText(focusTenVT.getText());
                    inputDVT.setText(focusDVT.getText());
                    inputGia.setText(focusGia.getText());

                    inputMaVT.setEnabled(false);
                    inputTenVT.setEnabled(false);
                    inputDVT.setEnabled(false);
                    inputGia.setEnabled(false);
                    inputHinh.setEnabled(false);
                }
            }
        });

    }

    public void setNormalBGTableRows(TableLayout list) {
        // 0: là thằng example đã INVISIBLE
        // Nên bắt đầu từ 1 -> 9
        for (int i = 1; i < list.getChildCount(); i++) {
            TableRow row = (TableRow) list.getChildAt((int) i);
            if (indexofRow != (int) row.getId())
                row.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    public void setEventTableRows(TableRow tr, TableLayout list) {
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBtn.setVisibility(View.VISIBLE);
                delBtn.setVisibility(View.VISIBLE);
                previewBtn.setVisibility(View.VISIBLE);
                // v means TableRow
                v.setBackgroundColor(getResources().getColor(R.color.selectedColor));
                indexofRow = (int) v.getId();
                focusRow = (TableRow) list.getChildAt(indexofRow);
                focusMaVT = (TextView) focusRow.getChildAt(0);
                focusTenVT = (TextView) focusRow.getChildAt(1);
                focusDVT = (TextView) focusRow.getChildAt(2);
                focusGia = (TextView) focusRow.getChildAt(3);
                focusDataHinh = image_list.get( focusRow.getId() - Integer.parseInt("1") );
                setNormalBGTableRows(list);
            }
        });
    }

    // --------------- IMAGE PICKER/ LOADER HELPER ---------------------------------------------------
    // ! Nhớ thêm dòng <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" /> trong file Manifest.xml
    public void setDataImageView(ImageView imageView, byte[] imageBytes){
        if (imageBytes != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(bitmap);
        }
    }

    public void pickImageFromStorage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_FOLDER);
    }

    public void setEventImagePicker(){
        inputHinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_GRANTED);
                    }
                    else{
                        pickImageFromStorage();
                    }
                }
                else{
                    pickImageFromStorage();
                }
            }
        });
    }

    public byte[] getImageDataPicker(){
        byte[] imageBytes = null;
        try {
            Bitmap bitmap = ((BitmapDrawable) inputHinh.getDrawable()).getBitmap();
            if (bitmap != null){
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                imageBytes = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
            }
        }
        catch (IOException e){
            Toast.makeText(VattuLayout.this,"Failed loading image",
                    Toast.LENGTH_SHORT).show();
        }
        finally {
            return imageBytes;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_GRANTED){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                pickImageFromStorage();
            }
        }
        else{
            Toast.makeText(VattuLayout.this,"Permission's denied....", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == IMAGE_FOLDER && resultCode == RESULT_OK && data != null){
            inputHinh.setImageURI(data.getData());
            Toast.makeText(VattuLayout.this,"Thêm hình thành công", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // --------------- DIALOG HELPER -----------------------------------------------------------------
    public void createDialog(int layout) {
        vtdialog = new Dialog(VattuLayout.this);
        vtdialog.setContentView(layout);
        vtdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        vtdialog.show();
    }

    public void setControlDialog() {
        backBtn = vtdialog.findViewById(R.id.VT_backBtn);
        yesBtn = vtdialog.findViewById(R.id.VT_yesInsertBtn);
        noBtn = vtdialog.findViewById(R.id.VT_noInsertBtn);

        inputMaVT = vtdialog.findViewById(R.id.VT_inputMaVT);
        inputTenVT = vtdialog.findViewById(R.id.VT_inputTenVT);
        inputDVT = vtdialog.findViewById(R.id.VT_inputDVT);
        inputGia = vtdialog.findViewById(R.id.VT_inputGia);
        inputHinh = vtdialog.findViewById(R.id.VT_inputHinh);

        showMVTError = vtdialog.findViewById(R.id.VT_showMVTError);
        showTVTError = vtdialog.findViewById(R.id.VT_showTVTError);
        showDVTError = vtdialog.findViewById(R.id.VT_showDVTError);
        showGiaError = vtdialog.findViewById(R.id.VT_showGiaError);

        showResult = vtdialog.findViewById(R.id.VT_showResult);
        showConfirm = vtdialog.findViewById(R.id.VT_showConfirm);
        showLabel = vtdialog.findViewById(R.id.VT_showLabel);
    }

    public void setEventDialog(View view) {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vtdialog.dismiss();
            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vtdialog.dismiss();
            }
        });
        // Dựa vào các nút mà thằng yesBtn sẽ có event khác
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = false;
                switch (view.getId()) {
                    case R.id.VT_insertBtn: {
                        if (!isSafeDialog( false )) break;
//                        Log.d("process","1True");

                        VatTu vt = new VatTu(
                                inputMaVT.getText().toString().trim() + "",
                                inputTenVT.getText().toString().trim() + "",
                                inputDVT.getText().toString().trim() + "",
                                inputGia.getText().toString().trim()+"",
                                getImageDataPicker());
                        if (vattuDB.insert(vt) == -1) break;
//                        Log.d("process","2True");
                        inputHinh.setImageDrawable(null);
                        TableRow tr = createRow(VattuLayout.this, vt);
                        int n = vt_table_list.getChildCount();
                        tr.setId(n);
                        vt_table_list.addView(tr);
                        Log.d("insert",image_list.size()+"");
                        image_list.add(vt.getHinh());
                        Log.d("insert",image_list.size()+"");
                        setEventTableRows((TableRow) vt_table_list.getChildAt(n), vt_table_list);
                        editBtn.setVisibility(View.INVISIBLE);
                        delBtn.setVisibility(View.INVISIBLE);
                        previewBtn.setVisibility(View.INVISIBLE);
                        focusRow = null;
                        focusMaVT = null;
                        focusTenVT = null;
                        focusDVT = null;
                        focusGia = null;
                        focusDataHinh = null;
                        success = true;
                        vtlist = vattuDB.select();
                    }
                    break;
                    case R.id.VT_editBtn: {
                        if (!isSafeDialog( true )) break;
                        TableRow tr = (TableRow) vt_table_list.getChildAt(indexofRow);
                        TextView id = (TextView) tr.getChildAt(0);
                        VatTu vt = new VatTu(
                                id.getText().toString().trim()+"",
                                inputTenVT.getText().toString().trim() + "",
                                inputDVT.getText().toString().trim() + "",
                                inputGia.getText().toString().trim()+"",
                                getImageDataPicker() );
                        if( vattuDB.update( vt ) == -1 ) {
                            break;
                        }
                        vtlist = vattuDB.select();
                        focusTenVT.setText( inputTenVT.getText().toString().trim() + "");
                        focusDVT.setText( inputDVT.getText().toString().trim() + "");
                        focusGia.setText( inputGia.getText().toString().trim() + "");
                        image_list.set( indexofRow-1, vt.getHinh() );
                        focusDataHinh = vt.getHinh();
                        success = true;
                    }
                    break;
                    case R.id.VT_delBtn: {
                        if( vattuDB.delete(
                                new VatTu(
                                        focusMaVT.getText().toString().trim()+"",
                                        focusTenVT.getText().toString().trim()+"",
                                        focusDVT.getText().toString().trim() + "",
                                        focusGia.getText().toString().trim()+"",
                                        focusDataHinh
                                ))
                                == -1 ) break;
                        if (indexofRow == vt_table_list.getChildCount() - 1) {
                            vt_table_list.removeViewAt(indexofRow);
                        } else {
                            vt_table_list.removeViewAt(indexofRow);
                            for (int i = 0; i < vt_table_list.getChildCount(); i++) {
                                vt_table_list.getChildAt(i).setId((int) i);
                            }
                        }
                        Log.d("del",image_list.size()+"");
                        image_list.remove(indexofRow -1);
                        Log.d("del",image_list.size()+"");
                        editBtn.setVisibility(View.INVISIBLE);
                        delBtn.setVisibility(View.INVISIBLE);
                        previewBtn.setVisibility(View.INVISIBLE);
                        focusRow = null;
                        focusMaVT = null;
                        focusTenVT = null;
                        focusDVT = null;
                        focusGia = null;
                        focusDataHinh = null;
                        success = true;
                    }
                    break;
                    default:
                        break;
                }
                if (success) {
                    showResult.setText(showLabel.getText() + " thành công !");
                    showResult.setTextColor(getResources().getColor(R.color.yes_color));
                    showResult.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            inputMaVT.setText("");
                            inputTenVT.setText("");
                            inputDVT.setText("");
                            inputGia.setText("");
                            showResult.setVisibility(View.INVISIBLE);
                            vtdialog.dismiss();
                        }
                    }, 1000);
                } else {
                    showResult.setTextColor(getResources().getColor(R.color.thoatbtn_bgcolor));
                    showResult.setText(showLabel.getText() + " thất bại !");
                    showResult.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    public boolean isSafeDialog( boolean allowSameID ) {
        String id, mavt, tenvt, dvt, gia;
        mavt = inputMaVT.getText().toString().trim();
        boolean noError = true;
        if (mavt.equals("")) {
            showMVTError.setText("Mã VT không được trống ");
            showMVTError.setVisibility(View.VISIBLE);
            noError = false;
        }else{
            showMVTError.setVisibility(View.INVISIBLE);
            noError = true;
        }

        tenvt = inputTenVT.getText().toString().trim();
        if (tenvt.equals("")) {
            showTVTError.setText("Tên VT không được trống ");
            showTVTError.setVisibility(View.VISIBLE);
            noError = false;
        }else{
            showTVTError.setVisibility(View.INVISIBLE);
            if(noError)noError = true;
        }

        // dvt không được để trống và không chữ số
        boolean hasDigits = false;
        dvt = inputDVT.getText().toString().trim();
        hasDigits = dvt.matches(".*\\d.*");
        if (dvt.equals("")) {
            showDVTError.setText("DVT không được trống ");
            showDVTError.setVisibility(View.VISIBLE);
            noError = false;
        }else if( hasDigits ){
            showDVTError.setText("DVT không được chứa số ");
            showDVTError.setVisibility(View.VISIBLE);
            noError = false;
        }else{
            showDVTError.setVisibility(View.INVISIBLE);
            if(noError)noError = true;
        }

        // Gia không được để trống và không chữ cái
        gia = inputGia.getText().toString().trim();
        if (gia.length() > 1)
        if( gia.charAt(0) == '0')
        {if( gia.length() > 1)
            {gia = gia.substring(1,gia.length()-1);}}
        if (gia.equals("")) {
            showGiaError.setText("Giá không được trống ");
            showGiaError.setVisibility(View.VISIBLE);
            noError = false;
        }else{
            showGiaError.setVisibility(View.INVISIBLE);
            if(noError)noError = true;
        }

        if( noError ) {
            for (int i = 1; i < vt_table_list.getChildCount(); i++) {
                TableRow tr = (TableRow) vt_table_list.getChildAt(i);
                TextView mavt_data = (TextView) tr.getChildAt(0);
                TextView tenvt_data = (TextView) tr.getChildAt(1);

                if (!allowSameID)
                    if (mavt.equalsIgnoreCase(mavt_data.getText().toString())) {
                        showMVTError.setText("Mã VT không được trùng ");
                        showMVTError.setVisibility(View.VISIBLE);
                        return noError = false;
                    }
                // Trường hợp chỉ đổi ảnh, những thông tin khác ngoại trừ tên thì phải đổi tên luôn à
                if (tenvt.equalsIgnoreCase(tenvt_data.getText().toString())
                        && !tenvt_data.getText().toString().equalsIgnoreCase(
                        focusTenVT.getText().toString().trim()
                )
                ) {
                    showTVTError.setText("Tên VT không được trùng");
                    showTVTError.setVisibility(View.VISIBLE);
                    return noError = false;
                }
            }
            showMVTError.setVisibility(View.INVISIBLE);
            showTVTError.setVisibility(View.INVISIBLE);
            showDVTError.setVisibility(View.INVISIBLE);
            showGiaError.setVisibility(View.INVISIBLE);
        }
        return noError;
    }

    // --------------- CUSTOM HELPER --------------------------------------------------------------------
    public int DPtoPix(int dps) {
        return (int) (dps * scale + 0.5f);
    }

    // This Custom Columns' Max Width : 70 p0 / 200 / 55 p0 / <= 55 p0
    public TableRow createRow(Context context, VatTu vt) {
        TableRow tr = new TableRow(context);
        // Id
        //   MaVT
        TextView maVT = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maVT đạt tới max width thì nó sẽ tăng height cho bên tenVT luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        maVT.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        maVT.setMaxWidth(DPtoPix(70));
        maVT.setPadding(0,0,0,0);
        maVT.setText(vt.getMaVt());

        //   Ten VT
        TextView tenVT = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà tenVT đạt tới max width thì nó sẽ tăng height cho bên maVT luôn
        tenVT.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        tenVT.setText(vt.getTenVt());
        tenVT.setMaxWidth(DPtoPix(200));

        TextView dvt = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        dvt.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        dvt.setText(vt.getDvt());
        dvt.setPadding(0,0,0,0);
        dvt.setMaxWidth(DPtoPix(55));

        TextView gianhap = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        gianhap.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        gianhap.setText(vt.getGiaNhap());
        gianhap.setPadding(0,0,0,0);
        gianhap.setMaxWidth(DPtoPix(55));

        image_list.add(vt.getHinh());

        tr.setBackgroundColor(getResources().getColor(R.color.white));
        tr.addView(maVT);
        tr.addView(tenVT);
        tr.addView(dvt);
        tr.addView(gianhap);
        return tr;
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility == 0)
                    decorView.setSystemUiVisibility(hideSystemUIBars());
            }
        });
    }
    private int hideSystemUIBars(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}
