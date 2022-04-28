package com.example.myapplication.Main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.CursorWindow;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myapplication.Databases.ChiTietPhieuNhapDatabase;
import com.example.myapplication.Databases.PhieuNhapDatabase;
import com.example.myapplication.Databases.PhongKhoDatabase;
import com.example.myapplication.Entities.ChiTietPhieuNhap;
import com.example.myapplication.Entities.PhieuNhap;
import com.example.myapplication.Entities.PhongKho;
import com.example.myapplication.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class PhieuNhapLayout extends AppCompatActivity {
    // Main Layout
    TableLayout phieunhap_table_list;

    Spinner PK_spinner;
    String PK_spinner_maPK;

    Button insertBtn;
    Button editBtn;
    Button delBtn;
    Button exitBtn;

    // Navigation
    Button navPK;
    Button navPN;
    Button navVT;
    Button navCP;

    // Dialog Layout
    Dialog phieunhapdialog;

    Button backBtn;
    Button yesBtn;
    Button noBtn;

    EditText PN_searchView;

    Spinner PK_spinner_mini;

    EditText inputMaPN;


    DatePicker datepickerNLPN;

    String PK_spinner_mini_maPK;
    String strDate;

    TextView showMPNError;
    TextView showMPKError;

    TextView showResult;
    TextView showConfirm;
    TextView showLabel;

    // Database Controller
    PhieuNhapDatabase phieunhapDB;
    PhongKhoDatabase phongkhoDB;
    ChiTietPhieuNhapDatabase ctphieunhapDB;

    List<PhongKho> phongkholist;
    List<PhieuNhap> phieunhaplist;
    List<ChiTietPhieuNhap> ctphieunhaplist;

    // Focus
    int indexofRow = -1;
    TableRow focusRow;
    TextView focusMaPN;
    TextView focusNLPN;

    // Other
    float scale;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phieunhap_layout);
        setControl();
        loadDatabase();
        setEvent();
        setNavigation();
        PN_searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
                editBtn.setVisibility(View.INVISIBLE);
                delBtn.setVisibility(View.INVISIBLE);
            }
        });
        hideSystemUI();
    }

    private void filter(String toString) {
        TableRow tr = (TableRow) phieunhap_table_list.getChildAt(0);
        int dem =1;
        phieunhap_table_list.removeAllViews();
        phieunhap_table_list.addView(tr);
        for (int k = 0; k < phieunhaplist.size(); k++) {
            PhieuNhap pn = phieunhaplist.get(k);
            if (pn.getSoPhieu().toLowerCase().trim().contains(toString.trim().toLowerCase()) || pn.getMaK().toLowerCase().contains(toString.toLowerCase())) {

                tr = createRow(PhieuNhapLayout.this, pn);

                tr.setId((int) dem++);
                phieunhap_table_list.addView(tr);
                setEventTableRows(tr, phieunhap_table_list);
            }

        }
    }

    // --------------- MAIN HELPER -----------------------------------------------------------------
    public void setCursorWindowImageSize(int B) {
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
        phieunhap_table_list = findViewById(R.id.PN_table_list);
        PN_searchView = findViewById(R.id.PN_searchEdit);
        PK_spinner = findViewById(R.id.PN_PKSpinner);
        insertBtn = findViewById(R.id.PN_insertBtn);
        editBtn = findViewById(R.id.PN_editBtn);
        delBtn = findViewById(R.id.PN_delBtn);
        exitBtn = findViewById(R.id.PN_exitBtn);

        navPK = findViewById(R.id.PN_navbar_phongkho);
        navPN = findViewById(R.id.PN_navbar_phieunhap);
        navVT = findViewById(R.id.PN_navbar_VT);
        navCP = findViewById(R.id.PN_navbar_chitiet);
    }

    public void loadDatabase() {
        Log.d("data", "Load Database --------");
        phieunhapDB = new PhieuNhapDatabase(PhieuNhapLayout.this);
        phongkhoDB = new PhongKhoDatabase(PhieuNhapLayout.this);
        ctphieunhapDB = new ChiTietPhieuNhapDatabase(PhieuNhapLayout.this);

        phieunhaplist = new ArrayList<>();
        setCursorWindowImageSize(100 * 1024 * 1024);
        TableRow tr = null;
        phieunhaplist = phieunhapDB.select();
        // Tag sẽ bắt đầu ở 1 vì phải cộng thêm thằng example đã có sẵn
        for (int i = 0; i < phieunhaplist.size(); i++) {
            tr = createRow(PhieuNhapLayout.this, phieunhaplist.get(i));
            tr.setId((int) i + 1);
            phieunhap_table_list.addView(tr);
        }

        phongkholist = phongkhoDB.select();
        ArrayList<String> PK_name = new ArrayList<>();
        PK_name.add("Tất cả phòng kho");
        for (PhongKho pk : phongkholist) {
            PK_name.add(pk.getTenpk());
        }
        PK_spinner.setAdapter(loadSpinnerAdapter(PK_name));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setEvent() {
        editBtn.setVisibility(View.INVISIBLE); // turn on when click items
        delBtn.setVisibility(View.INVISIBLE);  // this too
        setEventTable(phieunhap_table_list);
        setEventSpinner();
    }

    public void setNavigation() {
        // navNV onclick none
        // navPB
        navPK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(PhieuNhapLayout.this, PhongkhoLayout.class);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                startActivity(intent);

            }
        });
        // navVPP
        navVT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(PhieuNhapLayout.this, VattuLayout.class);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                startActivity(intent);

            }

        });
        // navCP
        navCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhieuNhapLayout.this, ChiTietPhieuNhapLayout.class);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                startActivity(intent);
            }

        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setEventSpinner() {
        PK_spinner_maPK = "All";
        PK_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (phieunhap_table_list.getChildCount() < phieunhaplist.size() + 1) {
                        PK_spinner_maPK = "All";
                        // Nếu có sort trước đó làm cho số Phiếu nhập nhỏ hơn số Phiếu nhập tổng thì mới sort lại theo all
                        TableRow tr = (TableRow) phieunhap_table_list.getChildAt(0);
                        phieunhap_table_list.removeAllViews();
                        phieunhap_table_list.addView(tr);
                        phieunhaplist = phieunhapDB.select();
                        // Tag sẽ bắt đầu ở 1 vì phải cộng thêm thằng example đã có sẵn
                        for (int i = 0; i < phieunhaplist.size(); i++) {
                            PhieuNhap pn = phieunhaplist.get(i);
                            tr = createRow(PhieuNhapLayout.this, pn);
                            tr.setId((int) i + 1);
                            phieunhap_table_list.addView(tr);
                            setEventTableRows(tr, phieunhap_table_list);
                        }
                    }
                } else {
                    int dem = 1;
                    String mapk = phongkholist.get(position - 1).getMapk();
                    PK_spinner_maPK = mapk;
                    // Select lại toàn bộ table
                    TableRow tr = (TableRow) phieunhap_table_list.getChildAt(0);
                    phieunhap_table_list.removeAllViews();
                    phieunhap_table_list.addView(tr);
                    phieunhaplist = phieunhapDB.select();
                    for (int i = 0; i < phieunhaplist.size(); i++) {
                        PhieuNhap pn = phieunhaplist.get(i);
                        if (pn.getMaK().trim().equals(mapk.trim())) {
                            tr = createRow(PhieuNhapLayout.this, pn);
                            tr.setId((int) dem++);
                            phieunhap_table_list.addView(tr);
                            setEventTableRows(tr, phieunhap_table_list);
                        }
                    }
                }
                editBtn.setVisibility(View.INVISIBLE); // turn on when click items
                delBtn.setVisibility(View.INVISIBLE);  // this too
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PK_spinner_maPK = "All";
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                createDialog(R.layout.popup_phieunhap);
                // Control
                setControlDialog();
                // Event
                strDate = formatDate(InttoStringDate(17, 4, 2022), true);
                setEventDialog(v);

            }
        });
        // Khi edit
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indexofRow != -1) {
                    createDialog(R.layout.popup_phieunhap);
                    // Control
                    setControlDialog();
                    showLabel.setText("Sửa thông tin phiếu nhập");
                    showConfirm.setText("Bạn có muốn sửa hàng này không?");
                    // Event
                    setEventDialog(v);

                    int[] date = StringtoIntDate(focusNLPN.getText().toString().trim());

                    int index = 0;
                    for (int i = 0; i < phieunhaplist.size(); i++) {
                        // Nếu thằng được focus có mã PK trùng với PB trong list thì break
                        String maPN = phieunhaplist.get(i).getSoPhieu().trim();
                        String maPNF = focusMaPN.getText().toString().trim();
                        if (maPN.equals(maPNF)) {
                            for (int k = 0; k < phongkholist.size(); k++) {
                                String makPn = phieunhaplist.get(i).getMaK().trim();
                                String makS = phongkholist.get(k).getMapk().trim();
                                if (makPn.equals(makS)) {
                                    index = k;
                                    break;
                                }
                            }
                        }
                    }
                    inputMaPN.setText(focusMaPN.getText());
                    PK_spinner_mini.setSelection(index);
                    datepickerNLPN.updateDate(date[2], date[1] - 1, date[0]);
                    inputMaPN.setEnabled(false);

                }
            }
        });
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indexofRow != -1) {
                    createDialog(R.layout.popup_phieunhap);
                    // Control
                    setControlDialog();
                    showLabel.setText("Xóa thông tin Phiếu nhập");
                    showConfirm.setText("Bạn có muốn xóa hàng này không?");
                    // Event
                    setEventDialog(v);
                    int index = 0;
                    for (int i = 0; i < phieunhaplist.size(); i++) {
                        // Nếu thằng được focus có mã PK trùng với PB trong list thì break
                        String maPN = phieunhaplist.get(i).getSoPhieu().trim();
                        String maPNF = focusMaPN.getText().toString().trim();
                        if (maPN.equals(maPNF)) {
                            for (int k = 0; k < phongkholist.size(); k++) {
                                String makPn = phieunhaplist.get(i).getMaK().trim();
                                String makS = phongkholist.get(k).getMapk().trim();
                                if (makPn.equals(makS)) {
                                    index = k;
                                    break;
                                }
                            }
                        }
                    }
                    int[] date = StringtoIntDate(focusNLPN.getText().toString().trim());

                    PK_spinner_mini.setSelection(index);
                    inputMaPN.setText(focusMaPN.getText());
                    datepickerNLPN.updateDate(date[2], date[1] - 1, date[0]);
                    PK_spinner_mini.setEnabled(false);
                    inputMaPN.setEnabled(false);
                    datepickerNLPN.setEnabled(false);
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
//        Toast.makeText(NhanvienLayout.this, indexofRow + ":" + (int) list.getChildAt(indexofRow).getId() + "", Toast.LENGTH_LONG).show();
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
                focusMaPN = (TextView) focusRow.getChildAt(0);
                focusNLPN = (TextView) focusRow.getChildAt(1);
                setNormalBGTableRows(list);
            }
        });
    }

    // --------------- DIALOG HELPER -----------------------------------------------------------------
    public void createDialog(int layout) {
        phieunhapdialog = new Dialog(PhieuNhapLayout.this);
        phieunhapdialog.setContentView(layout);
        phieunhapdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        phieunhapdialog.show();
    }

    public void setEventSpinnerMini() {
        PK_spinner_mini.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PK_spinner_mini_maPK = phongkholist.get(position).getMapk();
//                Toast.makeText( NhanvienLayout.this, PB_spinner_mini_maPB+"", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PK_spinner_mini_maPK = phongkholist.get(0).getMapk();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setEventDatePicker() {
        strDate = formatDate(InttoStringDate(30, 8, 1999), true);
        datepickerNLPN.init(1999, 07, 30, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                strDate = formatDate(InttoStringDate(dayOfMonth, monthOfYear + 1, year), true);
//                 Toast.makeText( NhanvienLayout.this, strDate+"", Toast.LENGTH_LONG).show();
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setControlDialog() {
        backBtn = phieunhapdialog.findViewById(R.id.PN_backBtn);
        yesBtn = phieunhapdialog.findViewById(R.id.PN_yesInsertBtn);
        noBtn = phieunhapdialog.findViewById(R.id.PN_noInsertBtn);

        PK_spinner_mini = phieunhapdialog.findViewById(R.id.PN_PKSpinner_mini);
        ArrayList<String> PK_name = new ArrayList<>();
        for (PhongKho pk : phongkholist) {
            PK_name.add(pk.getTenpk());
        }
        PK_spinner_mini.setAdapter(loadSpinnerAdapter(PK_name));
        setEventSpinnerMini();

        inputMaPN = phieunhapdialog.findViewById(R.id.PN_inputSP);


        datepickerNLPN = phieunhapdialog.findViewById(R.id.PN_inputNLP);

        setEventDatePicker();

        showMPNError = phieunhapdialog.findViewById(R.id.PN_showMPNError);
        showMPKError = phieunhapdialog.findViewById(R.id.PN_showMPKError);

        showResult = phieunhapdialog.findViewById(R.id.PN_showResult);
        showConfirm = phieunhapdialog.findViewById(R.id.PN_showConfirm);
        showLabel = phieunhapdialog.findViewById(R.id.PN_showLabel);
    }

    public void setEventDialog(View view) {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phieunhapdialog.dismiss();
            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phieunhapdialog.dismiss();
            }
        });
        // Dựa vào các nút mà thằng yesBtn sẽ có event khác
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = false;
                switch (view.getId()) {
                    case R.id.PN_insertBtn: {
                        if (!isSafeDialog(false)) break;
                        PhieuNhap pn = new PhieuNhap(inputMaPN.getText().toString().trim(), strDate, PK_spinner_mini_maPK.trim());
                        if (phieunhapDB.insert(pn) == -1) break;
                        TableRow tr = createRow(PhieuNhapLayout.this, pn);
                        int n = phieunhap_table_list.getChildCount();
                        tr.setId(n);
                        if (!PK_spinner_mini_maPK.trim().equals(PK_spinner_maPK.trim())) {
                            // Nếu thằng bên trong là phòng kho nhưng bên ngoài là tất cả phòng kho thì
                            if (PK_spinner_maPK.trim().equals("All")) {
                                // cứ insert như bth
                                phieunhap_table_list.addView(tr);
                                setEventTableRows((TableRow) phieunhap_table_list.getChildAt(n), phieunhap_table_list);
                            }
                            // Nếu thằng bên trong là phòng kho nhưng bên ngoài là phòng kho khác thì khỏi thêm table
                            // Nếu trùng
                        } else {
                            phieunhap_table_list.addView(tr);
                            setEventTableRows((TableRow) phieunhap_table_list.getChildAt(n), phieunhap_table_list);
                        }
                        phieunhaplist.add(pn);
                        editBtn.setVisibility(View.INVISIBLE);
                        delBtn.setVisibility(View.INVISIBLE);
                        focusRow = null;
                        focusMaPN = null;
                        focusNLPN = null;
                        PK_spinner_mini_maPK = null;
                        success = true;
                    }
                    break;
                    case R.id.PN_editBtn: {
                        if (!isSafeDialog(true)) break;
                        TableRow tr = (TableRow) phieunhap_table_list.getChildAt(indexofRow);
                        TextView id = (TextView) tr.getChildAt(0);
                        TextView date = (TextView) tr.getChildAt(1);
                        PhieuNhap pn = new PhieuNhap(
                                id.getText().toString().trim()
                                , strDate
                                , PK_spinner_mini_maPK);
                        if (phieunhapDB.update(pn) == -1) break;
                        //   Cập nhật phiếu nhập list bằng cách lấy cái index ra và add vào cái index đó
                        int index = 0;
                        phieunhaplist = phieunhapDB.select();
                        for (int i = 0; i < phieunhaplist.size(); i++) {
                            if (phieunhaplist.get(i).getSoPhieu().equals(id.getText().toString().trim())) {
                                index = i;
                                break;
                            }
                        }
                        Log.d("process", index + "");
                        phieunhaplist.set(index, pn);
                        boolean edit = false, changePN = false;
                        if (!PK_spinner_mini_maPK.trim().equals(PK_spinner_maPK.trim())) {
                            // Khi không cần biết thay đổi PK như thế nào nhưng bên ngoài là All thì cứ edit thôi
                            if (PK_spinner_maPK.trim().equals("All"))
                                edit = true;
                                // Vậy trường hợp đang là Phòng kho Thủ Đức muốn thay Phòng kho Bình Chánh thì
                            else {
                                changePN = true;
                            }
                        } else {
                            // Khi giữ nguyên phòng kho
                            edit = true;
                        }

                        if (edit) {
                            date.setText(formatDate(strDate, false));
                            tr.setTag(pn.getMaK());
                        }
                        if (changePN) {
                            if (indexofRow == phieunhap_table_list.getChildCount() - 1) {
                                phieunhap_table_list.removeViewAt(indexofRow);
                            } else {
                                phieunhap_table_list.removeViewAt(indexofRow);
                                for (int i = 0; i < phieunhap_table_list.getChildCount(); i++) {
                                    phieunhap_table_list.getChildAt(i).setId((int) i);
                                }
                            }
                        }
                        success = true;
                    }
                    break;
                    case R.id.PN_delBtn: {
                        PhieuNhap pn = new PhieuNhap(
                                focusMaPN.getText().toString().trim(),
                                formatDate(focusNLPN.getText().toString().trim(), true),
                                PK_spinner_mini_maPK.trim());
                        boolean del = false;
                        if (phieunhapDB.delete(pn) == -1) break;
                        if (indexofRow == phieunhap_table_list.getChildCount() - 1) {
                            phieunhap_table_list.removeViewAt(indexofRow);
                        } else {
                            phieunhap_table_list.removeViewAt(indexofRow);
                            for (int i = 0; i < phieunhap_table_list.getChildCount(); i++) {
                                phieunhap_table_list.getChildAt(i).setId((int) i);
                            }
                        }
//                        int index = 0;
//                        for (int i = 0; i < phieunhaplist.size(); i++) {
//                            if (phieunhaplist.get(i).getSoPhieu().equals(focusMaPN.getText().toString().trim())) {
//                                index = i;
//                                break;
//                            }
//                        }
//                        phieunhaplist.remove(index);

                        editBtn.setVisibility(View.INVISIBLE);
                        delBtn.setVisibility(View.INVISIBLE);
                        focusRow = null;
                        focusMaPN = null;
                        focusNLPN = null;
                        PK_spinner_mini_maPK = null;
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
                            inputMaPN.setText("");
                            showResult.setVisibility(View.INVISIBLE);
                            phieunhapdialog.dismiss();
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

    public boolean isSafeDialog(boolean allowSameID) {
        String mapn, tenpk;
        // Mã PN không được trùng với Mã PN khác và ko để trống
        mapn = inputMaPN.getText().toString().trim();
        boolean noError = true;
        if (mapn.equals("")) {
            showMPNError.setText("Mã PN không được trống ");
            showMPNError.setVisibility(View.VISIBLE);
            noError = false;
        } else {
            showMPNError.setVisibility(View.INVISIBLE);
            noError = true;
        }


        if (PK_spinner_mini_maPK == null) {
            showMPKError.setText("Mã PK không được trống ");
            showMPKError.setVisibility(View.VISIBLE);
            noError = false;
        } else {
            showMPKError.setVisibility(View.INVISIBLE);
            if(noError)noError = true;
        }

        if (noError) {
            for (int i = 1; i < phieunhap_table_list.getChildCount(); i++) {
                TableRow tr = (TableRow) phieunhap_table_list.getChildAt(i);
                TextView mapn_data = (TextView) tr.getChildAt(0);
                if (!allowSameID)
                    if (mapn.equalsIgnoreCase(mapn_data.getText().toString())) {
                        showMPNError.setText("Mã PN không được trùng ");
                        showMPNError.setVisibility(View.VISIBLE);
                        return noError = false;
                    }
            }
            showMPNError.setVisibility(View.INVISIBLE);
        }
        return noError;
    }

    // --------------- CUSTOM HELPER -----------------------------------------------------------------
    public ArrayAdapter<String> loadSpinnerAdapter(ArrayList<String> phongkho) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, phongkho);
        return adapter;
    }

    public int DPtoPix(int dps) {
        return (int) (dps * scale + 0.5f);
    }

    public int[] StringtoIntDate(String str) {
        int[] date = new int[3];
        String[] arr = str.split("/");
        date[0] = Integer.parseInt(arr[0]);
        date[1] = Integer.parseInt(arr[1]);
        date[2] = Integer.parseInt(arr[2]);
        return date; // 30/08/1999 -> [30,08,1999]
    }

    public String InttoStringDate(int[] date) {
        String day = (date[0] < 10) ? '0' + date[0] + "" : date[0] + "";
        String month = (date[1] < 10) ? '0' + date[1] + "" : date[1] + "";
        String year = date[2] + "";
        return day + "/" + month + "/" + year; // [30,08,1999] -> 30/08/1999
    }

    public String InttoStringDate(int date_day, int date_month, int date_year) {
        Log.d("day", date_day + "");
        String day = (date_day < 10) ? "0" + date_day + "" : date_day + "";
        String month = (date_month < 10) ? "0" + date_month + "" : date_month + "";
        String year = date_year + "";
        return day + "/" + month + "/" + year; // [30,08,1999] -> 30/08/1999
    }

    public String formatDate(String str, boolean toSQL) {
        String[] date;
        String result = "";
        if (toSQL) {
            date = str.split("/");
            result = date[2] + "-" + date[1] + "-" + date[0];
        } else {
            date = str.split("-");
            result = date[2] + "/" + date[1] + "/" + date[0];
        }

        return result;
    }

    // This Custom Columns' Max Width : 65 p0 / 220 / <= 100 p0
    public TableRow createRow(Context context, PhieuNhap pn) {
        TableRow tr = new TableRow(context);

        //   Mã NV
        TextView maPN = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);

        maPN.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        maPN.setMaxWidth(DPtoPix(100));
        maPN.setPadding(20, 20, 20, 20);
        maPN.setText(pn.getSoPhieu());


        //  Ngày lập phiếu
        TextView ngayLapPN = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);

        ngayLapPN.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        ngayLapPN.setPadding(0, 0, 0, 0);
        ngayLapPN.setMaxWidth(DPtoPix(200));
        ngayLapPN.setText(formatDate(pn.getNgayLap(), false));

        tr.setBackgroundColor(getResources().getColor(R.color.white));
        tr.addView(maPN);
        tr.addView(ngayLapPN);

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
