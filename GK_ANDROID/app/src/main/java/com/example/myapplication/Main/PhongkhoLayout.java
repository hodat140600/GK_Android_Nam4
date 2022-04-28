package com.example.myapplication.Main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.CursorWindow;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myapplication.Databases.PhongKhoDatabase;
import com.example.myapplication.Entities.PhongKho;
import com.example.myapplication.R;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class PhongkhoLayout extends AppCompatActivity {
    // Main Layout
    TableLayout phongkho_table_list;

    Button insertBtn;
    Button editBtn;
    Button delBtn;
    Button exitBtn;

    // Navigation
    Button navPK;
    Button navNV;
    Button navVT;
    Button navCP;

    List<PhongKho> phongkholist;

    // Dialog Layout
    Dialog phongkhodialog;

    Button backBtn;
    Button yesBtn;
    Button noBtn;

    EditText inputMaPK;
    EditText inputTenPK;

    EditText PK_searchView;

    TextView showMPKError;
    TextView showTPKError;
    TextView showResult;
    TextView showConfirm;
    TextView showLabel;

    // Database Controller
    PhongKhoDatabase phongkhoDB;

    // Focus
    int indexofRow = -1;
    TableRow focusRow;
    TextView focusMaPK;
    TextView focusTenPK;

    // Other
    float scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phongkho_layout);
        scale = this.getResources().getDisplayMetrics().density;
        setControl();
        loadDatabase();
        setEvent();
        setNavigation();

        PK_searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) { filter(s.toString());
                editBtn.setVisibility(View.INVISIBLE);
                delBtn.setVisibility(View.INVISIBLE);}
        });
        hideSystemUI();
    }

    private void filter(String toString) {
        TableRow tr = (TableRow) phongkho_table_list.getChildAt(0);
        int dem =1;
        phongkho_table_list.removeAllViews();
        phongkho_table_list.addView(tr);
        for (int k = 0; k < phongkholist.size(); k++) {
            PhongKho phongKho = phongkholist.get(k);
            if (phongKho.getMapk().toLowerCase().trim().contains(toString.trim().toLowerCase()) || phongKho.getTenpk().toLowerCase().contains(toString.toLowerCase())) {

                tr = createRow(PhongkhoLayout.this, phongKho);

                tr.setId((int) dem++);
                phongkho_table_list.addView(tr);
                setEventTableRows(tr, phongkho_table_list);
            }

        }
    }

    // --------------- MAIN HELPER -----------------------------------------------------------------
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

    public void setControl() {
        phongkho_table_list = findViewById(R.id.PK_table_list);
        insertBtn = findViewById(R.id.PK_insertBtn);
        editBtn = findViewById(R.id.PK_editBtn);
        delBtn = findViewById(R.id.PK_delBtn);
        exitBtn = findViewById(R.id.PK_exitBtn);

        PK_searchView = findViewById(R.id.PK_searchEdit);

        navPK = findViewById(R.id.PK_navbar_phongkho);
        navNV = findViewById(R.id.PK_navbar_nhanvien);
        navVT = findViewById(R.id.PK_navbar_VT);
        navCP= findViewById(R.id.PK_navbar_capphat);
    }

    public void setEvent() {
        editBtn.setVisibility(View.INVISIBLE); // turn on when click items
        delBtn.setVisibility(View.INVISIBLE);  // this too
        setEventTable(phongkho_table_list);
    }

    public void setNavigation(){
        navNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(PhongkhoLayout.this, PhieuNhapLayout.class);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                startActivity( intent );

            }
        });
        navVT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(PhongkhoLayout.this, VattuLayout.class);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                startActivity( intent );

            }
        });
        // navCP
        navCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhongkhoLayout.this, ChiTietPhieuNhapLayout.class);
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
        // Khi tạo, dùng n làm tag để thêm row
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // H bấm 1 cái là hiện ra cái pop up
                createDialog(R.layout.popup_phongkho);
                // Control
                setControlDialog();
                // Event
                setEventDialog(v);
            }
        });
        // Khi edit
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indexofRow != -1) {
                    createDialog(R.layout.popup_phongkho);
                    // Control
                    setControlDialog();
                    showLabel.setText("Sửa phòng kho");
                    showConfirm.setText("Bạn có muốn sửa hàng này không?");
                    // Event
                    setEventDialog(v);
                    inputMaPK.setText(focusMaPK.getText());
                    inputMaPK.setEnabled(false);
                    inputTenPK.setText(focusTenPK.getText());
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
                    createDialog(R.layout.popup_phongkho);
                    // Control
                    setControlDialog();
                    showLabel.setText("Xóa phòng kho");
                    showConfirm.setText("Bạn có muốn xóa hàng này không?");
                    // Event
                    setEventDialog(v);
                    String mapk = focusMaPK.getText().toString();
                    String tenpk = focusTenPK.getText().toString();
                    inputMaPK.setText(focusMaPK.getText());
                    inputTenPK.setText(focusTenPK.getText());
                    inputMaPK.setEnabled(false);
                    inputTenPK.setEnabled(false);

                }
            }
        });

    }

    // To set all rows to normal state, set focusRowid = -1
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
                // v means TableRow
                v.setBackgroundColor(getResources().getColor(R.color.selectedColor));
                indexofRow = (int) v.getId();
                focusRow = (TableRow) list.getChildAt(indexofRow);
                focusMaPK = (TextView) focusRow.getChildAt(0);
                focusTenPK = (TextView) focusRow.getChildAt(1);
                setNormalBGTableRows(list);
             }
        });
    }

    // Load from the Database to the Table Layout
    public void loadDatabase() {
        phongkhoDB = new PhongKhoDatabase(this);

        TableRow tr = null;
        setCursorWindowImageSize(100 * 1024* 1024);
        phongkholist = phongkhoDB.select();
        // Tag sẽ bắt đầu ở 1 vì phải cộng thêm thằng example đã có sẵn
        for (int i = 0; i < phongkholist.size(); i++) {
            tr = createRow(this, phongkholist.get(i));
            tr.setId((int) i + 1);
            phongkho_table_list.addView(tr);
        }
    }


    // --------------- DIALOG HELPER -----------------------------------------------------------------
    public void createDialog(int layout) {
        phongkhodialog = new Dialog(PhongkhoLayout.this);
        phongkhodialog.setContentView(layout);
        phongkhodialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        phongkhodialog.show();
    }

    public void setControlDialog() {
        backBtn = phongkhodialog.findViewById(R.id.PK_backBtn);
        yesBtn = phongkhodialog.findViewById(R.id.PK_yesInsertBtn);
        noBtn = phongkhodialog.findViewById(R.id.PK_noInsertBtn);

        inputMaPK = phongkhodialog.findViewById(R.id.PK_inputMaPK);
        inputTenPK = phongkhodialog.findViewById(R.id.PK_inputTenPK);

        showMPKError = phongkhodialog.findViewById(R.id.PK_showMPKError);
        showTPKError = phongkhodialog.findViewById(R.id.PK_showTPKError);
        showResult = phongkhodialog.findViewById(R.id.PK_showResult);
        showConfirm = phongkhodialog.findViewById(R.id.PK_showConfirm);
        showLabel = phongkhodialog.findViewById(R.id.PK_showLabel);
    }

    public void setEventDialog(View view) {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phongkhodialog.dismiss();
            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phongkhodialog.dismiss();
            }
        });
        // Dựa vào các nút mà thằng yesBtn sẽ có event khác
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = false;
                switch (view.getId()) {
                    case R.id.PK_insertBtn: {
                        if (!isSafeDialog( false )) break;
                        PhongKho pk = new PhongKho(inputMaPK.getText().toString().trim() + "", inputTenPK.getText().toString().trim() + "");
                        if (phongkhoDB.insert(pk) == -1) break;
                        TableRow tr = createRow(PhongkhoLayout.this, pk);
                        int n = phongkho_table_list.getChildCount();
                        tr.setId(n);
                        phongkho_table_list.addView(tr);
                        setEventTableRows((TableRow) phongkho_table_list.getChildAt(n), phongkho_table_list);
                        success = true;
                        editBtn.setVisibility(View.INVISIBLE);
                        delBtn.setVisibility(View.INVISIBLE);
                        focusRow = null;
                        focusMaPK = null;
                        focusTenPK = null;
                        phongkholist = phongkhoDB.select();
                    }
                    break;
                    case R.id.PK_editBtn: {
                        if (!isSafeDialog( true )) break;
                        TableRow tr = (TableRow) phongkho_table_list.getChildAt(indexofRow);
                        TextView id = (TextView) tr.getChildAt(0);
                        TextView name = (TextView) tr.getChildAt(1);
                        if(phongkhoDB.update(new PhongKho(id.getText().toString().trim(), inputTenPK.getText().toString().trim())) == -1)
                        {break;}
                        phongkholist = phongkhoDB.select();
                        name.setText(inputTenPK.getText() + "");
                        success = true;

                    }
                    break;
                    case R.id.PK_delBtn: {
                        if( phongkhoDB.delete( new PhongKho(focusMaPK.getText().toString().trim(), focusTenPK.getText().toString().trim()) ) == -1 ) break;
                        if (indexofRow == phongkho_table_list.getChildCount() - 1) {
                            phongkho_table_list.removeViewAt(indexofRow);
                        } else {
                            phongkho_table_list.removeViewAt(indexofRow);
                            for (int i = 0; i < phongkho_table_list.getChildCount(); i++) {
                                phongkho_table_list.getChildAt(i).setId((int) i);
                            }
                        }
                        editBtn.setVisibility(View.INVISIBLE);
                        delBtn.setVisibility(View.INVISIBLE);
                        focusRow = null;
                        focusMaPK = null;
                        focusTenPK = null;
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
                            inputMaPK.setText("");
                            inputTenPK.setText("");
                            showResult.setVisibility(View.INVISIBLE);
                            phongkhodialog.dismiss();
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
        String id, mapk, tenpk;
        // Mã PK không được trùng với Mã PK khác và ko để trống
        mapk = inputMaPK.getText().toString().trim();
        boolean noError = true;
        if (mapk.equals("")) {
            showMPKError.setText("Mã PK không được trống ");
            showMPKError.setVisibility(View.VISIBLE);
            noError = false;
        }else{
            showMPKError.setVisibility(View.INVISIBLE);
            noError = true;
        }

        // Tên PK không được để trống và không trùng
        tenpk = inputTenPK.getText().toString().trim();
        if (tenpk.equals("")) {
            showTPKError.setText("Tên PK không được trống ");
            showTPKError.setVisibility(View.VISIBLE);
            noError = false;
        }else{
            showTPKError.setVisibility(View.INVISIBLE);
            if(noError)noError = true;
        }

        if( noError ) {
            for (int i = 1; i < phongkho_table_list.getChildCount(); i++) {
                TableRow tr = (TableRow) phongkho_table_list.getChildAt(i);
                TextView mapk_data = (TextView) tr.getChildAt(0);
                TextView tenpk_data = (TextView) tr.getChildAt(1);

                if (!allowSameID)
                    if (mapk.equalsIgnoreCase(mapk_data.getText().toString())) {
                        showMPKError.setText("Mã PK không được trùng ");
                        showMPKError.setVisibility(View.VISIBLE);
                        return noError = false;
                    }
                if (tenpk.equalsIgnoreCase(tenpk_data.getText().toString())
                        && !tenpk_data.getText().toString().equalsIgnoreCase(
                        focusTenPK.getText().toString().trim() )
                ) {
                    showTPKError.setText("Tên PK không được trùng");
                    showTPKError.setVisibility(View.VISIBLE);
                    return noError = false;
                }
            }
            showMPKError.setVisibility(View.INVISIBLE);
            showTPKError.setVisibility(View.INVISIBLE);
        }
        return noError;
    }

    // --------------- CUSTOM HELPER --------------------------------------------------------------------
    public int DPtoPix(int dps) {
        return (int) (dps * scale + 0.5f);
    }

    // This Custom Columns' Max Width : 80 / 300
    public TableRow createRow(Context context, PhongKho pk) {
        TableRow tr = new TableRow(context);
        // Id


        //   Ma PK
        TextView maPK = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maPK đạt tới max width thì nó sẽ tăng height cho bên tenPK luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        maPK.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        maPK.setMaxWidth(DPtoPix(80));
        maPK.setText(pk.getMapk());

        //   Ten PK
        TextView tenPK = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà tenPK đạt tới max width thì nó sẽ tăng height cho bên maPK luôn
        tenPK.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        tenPK.setText(pk.getTenpk());
        tenPK.setMaxWidth(DPtoPix(300));

        tr.setBackgroundColor(getResources().getColor(R.color.white));
        // Add 2 thứ vào row
        tr.addView(maPK);
        tr.addView(tenPK);

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
