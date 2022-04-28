package com.example.myapplication.Main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Databases.ChiTietPhieuNhapDatabase;
import com.example.myapplication.Databases.PhieuNhapDatabase;
import com.example.myapplication.Databases.PhongKhoDatabase;
import com.example.myapplication.Databases.VatTuDatabase;
import com.example.myapplication.Entities.ChiTietPhieuNhap;
import com.example.myapplication.Entities.PhieuNhap;
import com.example.myapplication.Entities.PhongKho;
import com.example.myapplication.Entities.Rows;
import com.example.myapplication.Entities.VatTu;
import com.example.myapplication.R;
import com.example.myapplication.Statistics.BaoCaoLayout;
import com.example.myapplication.Statistics.ThongKeLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class ChiTietPhieuNhapLayout extends AppCompatActivity {
    public static PhongKho selectedPK = null;
    public static int totalMoney = 0;
    // Main Layout

    Button editBtn;
    Button delBtn;
    Button exitBtn;

    Button backBtn;
    Button yesBtn;
    Button noBtn;

    Spinner PKSpinner;
    Spinner PK_spinner_mini;
    Spinner PN_spinner_mini;
    Spinner VT_spinner_mini;
    Spinner NV_spinner_mini;

    String PK_spinner_mini_maPK;
    String PN_spinner_mini_maPN;
    String NV_spinner_mini_maNV;
    String VT_spinner_mini_maVT;

    EditText CP_searchView;
    EditText inputSLVT;

    DatePicker datePickerNLP;

    LinearLayout cp_tablesall_container;

    LinearLayout cp_tablesindex_container;
    TableLayout cp_tablevt_list;
    TableLayout cp_tablectpn_list;
    TableLayout cp_tablepn_list;

    TextView cp_totalCount;
    TextView cp_totalPrice;
    TextView warningLabel;
    TextView labelVT;
    TextView noteVTLabel;
    TextView noteTotalLabel;

    TextView showPNError;
    TextView showVTError;
    TextView showSLVTError;

    TextView showResult;
    TextView showConfirm;
    TextView showLabel;


    Button previewVTBtn;
    Button cpInsertBtn;
    Button navBC;
    Button navTK;

    // Data
    PhieuNhapDatabase phieunhapDB;
    ChiTietPhieuNhapDatabase chitietpnDB;
    VatTuDatabase vattuDB;
    PhongKhoDatabase phongkhoDB;

    List<PhieuNhap> phieunhap_list;
    List<ChiTietPhieuNhap> chitietpn_list;
    List<VatTu> vattu_list;
    List<PhongKho> phongkho_list;

    int totalPrice = 0;
    int VTCount = 0;

    // Dialog
    Dialog dialog;

    // Preview Image Layout
    TextView VT_IP_maVT;
    TextView VT_IP_tenVT;
    TextView VT_IP_DVT;
    TextView VT_IP_Gia;
    ImageView VT_IP_Hinh;

    // Focus
    TableRow focusRow;
    TextView focusSP;
    TextView focusDate;
    TextView focusMaVT;
    TextView focusTenVT;
    TextView focusMaPN;
    TextView focusDVT;
    TextView focusSL;
    TextView focusMaK;
    String dataMaPKSpinner;
    String strDate;
    // Other
    float scale;
    int indexofRow;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chitietphieunhap_layout);
        scale = this.getResources().getDisplayMetrics().density;
        Rows.scale = scale;
        Rows.tvtemplate = com.example.myapplication.R.layout.tvtemplate;

        setControl();
        loadDatabase();
        setEvent();
        setNavigation();
        CP_searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(dataMaPKSpinner.equalsIgnoreCase("All"))
                    filterALL(s.toString());
                else if(!dataMaPKSpinner.equalsIgnoreCase("All"))filterIndex(s.toString(), dataMaPKSpinner);
                editBtn.setVisibility(View.INVISIBLE);
                delBtn.setVisibility(View.INVISIBLE);
                previewVTBtn.setVisibility(View.INVISIBLE);
                labelVT.setVisibility(View.INVISIBLE);
            }
        });
        hideSystemUI();
    }

    private void filterALL(String toString) {
        Rows rowGenarator = new Rows(this);
        TableLayout pn_table1 = cp_tablectpn_list;
        pn_table1.removeViews(1, pn_table1.getChildCount() - 1);
        int[] sizeOfCell = {80, 40, 100, 100, 100};
        boolean[] isPaddingZero = {true, false, true, true, true};
        // Create List<TableRow> for TableList
        // TABLE CP INDEX 01 ----------------------------------------------------------------------------------------
        rowGenarator.setData(rowGenarator.enhanceRowData(chitietpnDB.select_PN_CTPN_VT(), 5));
        rowGenarator.setSizeOfCell(sizeOfCell);
        rowGenarator.setIsCellPaddingZero(isPaddingZero);
        List<TableRow> rows = rowGenarator.generateArrayofRows();
        for (TableRow row : rows) {
            focusMaPN = (TextView) row.getChildAt(0);
            String sptxt = focusMaPN.getText().toString();
            TextView mavt = (TextView) row.getChildAt(1);
            String mavttxt = mavt.getText().toString();
            TextView tenvt = (TextView) row.getChildAt(2);
            String tenvttxt = tenvt.getText().toString();
            if (sptxt.trim().toLowerCase().contains(toString.trim().toLowerCase())
                    || mavttxt.toLowerCase().contains(toString.toLowerCase())
                    || tenvttxt.toLowerCase().contains(toString.toLowerCase())) {

                pn_table1.addView(row);
                SetEventTableRows(row, cp_tablectpn_list);
            }

        }
    }

    private void filterIndex(String toString, String maPK) {
        Rows rowGenarator = new Rows(this);
        TableLayout cp_table1 = cp_tablesindex_container.findViewById(R.id.CP_tableVT);
        cp_table1.removeViews(1, cp_table1.getChildCount() - 1);
        int[] sizeOfCell = {85, 180, 50, 80};
        boolean[] isPaddingZero = {false, true, true, true};
        TableLayout cp_table2 = cp_tablesindex_container.findViewById(R.id.CP_tableSP);
        cp_table2.removeViews(1, cp_table2.getChildCount() - 1);
        int[] sizeOfCell2 = {90, 240};
        boolean[] isPaddingZero2 = {false, false};
        // Create List<TableRow> for TableList
        // TABLE CP INDEX 01 ----------------------------------------------------------------------------------------
        rowGenarator.setData(rowGenarator.enhanceRowData(chitietpnDB.selectVT_IndexPK(maPK), 4));
        rowGenarator.setSizeOfCell(sizeOfCell);
        rowGenarator.setIsCellPaddingZero(isPaddingZero);
        List<TableRow> rows = rowGenarator.generateArrayofRows();
        for (TableRow row : rows) {

            TextView mavt = (TextView) row.getChildAt(0);
            String mavttxt = mavt.getText().toString();
            TextView tenvt = (TextView) row.getChildAt(1);
            String tenvttxt = tenvt.getText().toString();
            if (mavttxt.toLowerCase().contains(toString.toLowerCase())
                    || tenvttxt.toLowerCase().contains(toString.toLowerCase())) {

                cp_table1.addView(row);
            }
        }
        for (int i = 1; i < cp_table1.getChildCount(); i++) {
            TableRow row = (TableRow) cp_table1.getChildAt(i);

            // Từ thằng VT được bấm gen ra thằng nhân viên đã mượn nó
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int dem = v.getId();
                    // TABLE CP INDEX 02 ----------------------------------------------------------------------------------------
                    // Set text for noteVTLabel -------------------------------------------------------------------------
                    TextView tenVTView = (TextView) row.getChildAt(1);
                    noteVTLabel.setVisibility(View.VISIBLE);
                    noteVTLabel.setText(tenVTView.getText().toString().trim() + " được nhập kho bởi phiếu nhập dưới đây");
                    // ----------------------------------------------------------------------------------------------------
                    TextView maVTView = (TextView) row.getChildAt(0);
                    for (TableRow row : rows) {
                        row.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                    row.setBackgroundColor(getResources().getColor(R.color.selectedColor));
                    rowGenarator.setData(rowGenarator.enhanceRowData(
                            chitietpnDB.selectSP_IndexPK(
                                    maPK,
                                    maVTView.getText().toString().trim()
                            ), 2));
                    cp_table2.removeViews(1, cp_table2.getChildCount() - 1);
                    List<TableRow> rows2 = rowGenarator.generateArrayofRows();
                    for (TableRow row2 : rows2) {
                        cp_table2.addView(row2);
                    }
                }
            });
        }
    }
    // --------------- MAIN HELPER -----------------------------------------------------------------
    public void setControl() {
//        Log.d("process", "setControl");
        exitBtn = findViewById(R.id.CP_backBtn);
        cpInsertBtn = findViewById(R.id.CP_insertBtn);

        PKSpinner = findViewById(R.id.CP_PKSpinner);
        CP_searchView = findViewById(R.id.CP_searchEdit);

        cp_tablesall_container = findViewById(R.id.CP_tablesAll_container);
        cp_tablesindex_container = findViewById(R.id.CP_tablesIndex_container);
        cp_tablevt_list = findViewById(R.id.CP_tableVT);
        cp_tablectpn_list = findViewById(R.id.CP_tableCTPN);
        cp_tablepn_list = findViewById(R.id.CP_tablePN);

        warningLabel = findViewById(R.id.CP_warningLabel);
        labelVT = findViewById(R.id.CP_labelVT);

        previewVTBtn = findViewById(R.id.CP_previewVTBtn);
        editBtn = findViewById(R.id.CP_editBtn);
        delBtn = findViewById(R.id.CP_delBtn);

        navBC = findViewById(R.id.CP_navbar_baocao);
        navTK = findViewById(R.id.CP_navbar_thongke);

    }

    public void loadDatabase() {
        //   Log.d("process", "loadDatabase");
        // 1.  Load Spinner ra trước
        PKSpinner.setAdapter(loadPBSpinner());

        phieunhapDB = new PhieuNhapDatabase(ChiTietPhieuNhapLayout.this);
        phieunhap_list = phieunhapDB.select();
        chitietpnDB = new ChiTietPhieuNhapDatabase(ChiTietPhieuNhapLayout.this);
        chitietpn_list = chitietpnDB.select();
        for (int i = 0; i < phieunhap_list.size(); i++) {

            PhieuNhap phieunhap = phieunhap_list.get(i);
            TableRow tr = createRow(ChiTietPhieuNhapLayout.this, phieunhap);
            tr.setId(i + 1);
            cp_tablepn_list.addView(tr);
//            Log.d("data",nv.toString()+"");
        }
        vattuDB = new VatTuDatabase(ChiTietPhieuNhapLayout.this);
        vattu_list = vattuDB.select();
        table();
    }

    public void table() {
        Rows rowGenarator = new Rows(this);
        TableLayout pn_table1 = cp_tablectpn_list;
        pn_table1.removeViews(1, pn_table1.getChildCount() - 1);
        int[] sizeOfCell = {80, 40, 100, 100, 100};
        boolean[] isPaddingZero = {true, false, true, true, true};
        // Create List<TableRow> for TableList
        // TABLE CP INDEX 01 ----------------------------------------------------------------------------------------
        rowGenarator.setData(rowGenarator.enhanceRowData(chitietpnDB.select_PN_CTPN_VT(), 5));
        rowGenarator.setSizeOfCell(sizeOfCell);
        rowGenarator.setIsCellPaddingZero(isPaddingZero);
        List<TableRow> rows = rowGenarator.generateArrayofRows();

        for (TableRow row : rows) {
            pn_table1.addView(row);
            SetEventTableRows(row, pn_table1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setEvent() {
        labelVT.setVisibility(View.INVISIBLE);
        editBtn.setVisibility(View.INVISIBLE); // turn on when click items
        delBtn.setVisibility(View.INVISIBLE);  // this too
        previewVTBtn.setVisibility(View.INVISIBLE);
        // 1. Set Event cho Spinner
        setEventPBSpinner();

        setEventTable(cp_tablectpn_list);
    }

    public void SetEventTableRows(TableRow tr, TableLayout list){
        for (int i = 1; i < list.getChildCount(); i++) {

            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editBtn.setVisibility(View.VISIBLE);
                    delBtn.setVisibility(View.VISIBLE);
                    previewVTBtn.setVisibility(View.VISIBLE);
                    for(int i = 0; i < list.getChildCount(); i ++) {
                        TableRow row = (TableRow) list.getChildAt(i);
                        row.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                    v.setBackgroundColor(getResources().getColor(R.color.selectedColor));
                    focusSL = (TextView) tr.getChildAt(4);
                    focusMaPN = (TextView) tr.getChildAt(0);
                    focusTenVT = (TextView) tr.getChildAt(2);
                    focusMaVT = (TextView) tr.getChildAt(1);
                    String mavttxt = focusMaVT.getText().toString();
                    setEventTableRowsHelper(cp_tablepn_list);
                    setEventDisplayVT(mavttxt);
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setEventTable(TableLayout list) {
        // Log.d("count", list.getChildCount()+""); // số table rows + 1
        // Không cần thay đổi vì đây chỉ mới set Event
        // Do có thêm 1 thằng example để làm gốc, nên số row thì luôn luôn phải + 1
        // Có example thì khi thêm row thì nó sẽ theo khuôn
        for (int i = 0; i < list.getChildCount(); i++) {
            TableRow row = (TableRow) list.getChildAt(i);
            SetEventTableRows(row, list);
        }
        // Khi tạo, dùng n làm tag để thêm row
        cpInsertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(R.layout.popup_chitietphieunhap);
                // Control
                setControlDialog();
                // Event
                strDate = formatDate(InttoStringDate(30, 8, 1999), true);
                setEventDialog(v);
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(R.layout.popup_chitietphieunhap);
                // Control
                setControlDialog();
                setEventDialog(v);
                showLabel.setText("Sửa CTPN");
                showConfirm.setText("Bạn có muốn sửa hàng này không?");

                inputSLVT.setText(focusSL.getText());
                int pn = 0, vt = 0;
                phieunhap_list = phieunhapDB.select();
                for (int i = 0; i < phieunhap_list.size(); i++){
                    String maPNlist = phieunhap_list.get(i).getSoPhieu();
                    String maPN = focusMaPN.getText().toString().trim();
                    if (maPNlist.equalsIgnoreCase(maPN)){
                        pn = i;break;
                    }
                }
                for (int i = 0; i < vattu_list.size(); i++){
                    String mavtl = vattu_list.get(i).getMaVt().trim();
                    String mavt = focusMaVT.getText().toString().trim();
                    if (mavtl.equalsIgnoreCase(mavt)){
                        vt = i;break;
                    }
                }
                PN_spinner_mini.setSelection(pn);
                VT_spinner_mini.setSelection(vt);
                PN_spinner_mini.setEnabled(false);
                VT_spinner_mini.setEnabled(false);
            }
        });
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(R.layout.popup_chitietphieunhap);
                // Control
                setControlDialog();
                setEventDialog(v);
                showLabel.setText("Xóa CTPN");
                showConfirm.setText("Bạn có muốn xóa hàng này không?");

                inputSLVT.setText(focusSL.getText());
                int pn = 0, vt = 0;
                phieunhap_list = phieunhapDB.select();
                for (int i = 0; i < phieunhap_list.size(); i++){
                    String maPNlist = phieunhap_list.get(i).getSoPhieu();
                    String maPN = focusMaPN.getText().toString().trim();
                    if (maPNlist.equalsIgnoreCase(maPN)){
                        pn = i;break;
                    }
                }
                for (int i = 0; i < vattu_list.size(); i++){
                    String mavtl = vattu_list.get(i).getMaVt().trim();
                    String mavt = focusMaVT.getText().toString().trim();
                    if (mavtl.equalsIgnoreCase(mavt)){
                        vt = i;break;
                    }
                }
                PN_spinner_mini.setSelection(pn);
                VT_spinner_mini.setSelection(vt);
                PN_spinner_mini.setEnabled(false);
                VT_spinner_mini.setEnabled(false);
                inputSLVT.setEnabled(false);
            }
        });
    }

    public void setNavigation() {
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        navBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if( selectedPK != null )
                    intent = new Intent(ChiTietPhieuNhapLayout.this, BaoCaoLayout.class);
                else
                    intent = new Intent(ChiTietPhieuNhapLayout.this, BaoCaoLayout.class);
                if( totalPrice != 0 ) totalMoney = totalPrice;  // with selectedPB
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                startActivity( intent );
            }
        });
        navTK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChiTietPhieuNhapLayout.this, ThongKeLayout.class);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                startActivity( intent );
            }
        });
    }

    public void transferLayout(String maPK) {
        if (maPK.trim().equalsIgnoreCase("")) return;
        // 1. maPK là all thì chuyển sang layout maPK
        switch (maPK) {
            case "All": {
                warningLabel.setText("Khi chọn phòng kho cụ thể, cấu trúc bảng sẽ khác");
                // All : show
                cp_tablesall_container.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                );
                // Index : hide
                cp_tablesindex_container.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0)
                );
                selectedPK = null;
                totalMoney = 0;
            }
            ;
            break;
            default: {
                warningLabel.setText("Khi chọn tất cả phòng kho, cấu trúc bảng sẽ khác");
                // All : hide
                cp_tablesall_container.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0)
                );
                // Index : show
                cp_tablesindex_container.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                );
                noteVTLabel = cp_tablesindex_container.findViewById(R.id.CP_noteVTLabel);
                noteVTLabel.setVisibility(View.INVISIBLE);
                createCPLayout_fromPK(maPK);
            }
            ;
            break;
        }
    }


    public void createCPLayout_fromPK(String maPK) {
        if (maPK.trim().equalsIgnoreCase("All")) return;
        // Init Variables and Control
        Rows rowGenarator = new Rows(this);
        TableLayout cp_table1 = cp_tablesindex_container.findViewById(R.id.CP_tableVT);
        cp_table1.removeViews(1, cp_table1.getChildCount() - 1);
        int[] sizeOfCell = {85, 180, 50, 80};
        boolean[] isPaddingZero = {false, true, true, true};
        TableLayout cp_table2 = cp_tablesindex_container.findViewById(R.id.CP_tableSP);
        cp_table2.removeViews(1, cp_table2.getChildCount() - 1);
        int[] sizeOfCell2 = {90, 240};
        boolean[] isPaddingZero2 = {false, false};
        cp_totalCount = cp_tablesindex_container.findViewById(R.id.CP_totalCount);
        cp_totalPrice = cp_tablesindex_container.findViewById(R.id.CP_totalPrice);

        noteTotalLabel = cp_tablesindex_container.findViewById(R.id.CP_noteTotalLabel);
        for (PhongKho pk : phongkho_list) {
            if (maPK.equalsIgnoreCase(pk.getMapk().trim())) {
                selectedPK = pk;
//                noteTotalLabel.setText("Tổng Chi phí trong " + pk.getTenpk() + " đang chứa :");
                break;
            }
        }
        totalPrice = 0;
        // Create List<TableRow> for TableList
        // TABLE CP INDEX 01 ----------------------------------------------------------------------------------------
        rowGenarator.setData(rowGenarator.enhanceRowData(chitietpnDB.selectVT_IndexPK(maPK), 4));
        rowGenarator.setSizeOfCell(sizeOfCell);
        rowGenarator.setIsCellPaddingZero(isPaddingZero);
        List<TableRow> rows = rowGenarator.generateArrayofRows();
        if (rows == null) {
            cp_totalCount.setText("0");
            cp_totalPrice.setText("0");
            return;
        }
        for (TableRow row : rows) {
            cp_table1.addView(row);
            TextView totalpriceofVTView = (TextView) row.getChildAt(row.getChildCount() - 1);
            int totalpriceofVT = 0;
            for (int i = 0; i < vattu_list.size(); i++){
                TextView slVTView = (TextView) row.getChildAt(0);
                String mavt = slVTView.getText().toString().trim();
                String mavtl = vattu_list.get(i).getMaVt().trim();
                if (mavtl.equalsIgnoreCase(mavt)) {
                    int slVT = Integer.parseInt(totalpriceofVTView.getText().toString().trim());
                    totalpriceofVT = slVT * Integer.parseInt(vattu_list.get(i).getGiaNhap());
                }
            }
            totalPrice += totalpriceofVT;
        }
        rowGenarator.setSizeOfCell(sizeOfCell2);
        rowGenarator.setIsCellPaddingZero(isPaddingZero2);

        for (int i = 1; i < cp_table1.getChildCount(); i++) {
            TableRow row = (TableRow) cp_table1.getChildAt(i);

            // Từ thằng VT được bấm gen ra thằng nhân viên đã mượn nó
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int dem = v.getId();
                    // TABLE CP INDEX 02 ----------------------------------------------------------------------------------------
                    // Set text for noteVTLabel -------------------------------------------------------------------------
                    TextView tenVTView = (TextView) row.getChildAt(1);
                    noteVTLabel.setVisibility(View.VISIBLE);
                    noteVTLabel.setText(tenVTView.getText().toString().trim() + " được nhập kho bởi phiếu nhập dưới đây");
                    // ----------------------------------------------------------------------------------------------------
                    TextView maVTView = (TextView) row.getChildAt(0);
                    for (TableRow row : rows) {
                        row.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                    row.setBackgroundColor(getResources().getColor(R.color.selectedColor));
                    rowGenarator.setData(rowGenarator.enhanceRowData(
                            chitietpnDB.selectSP_IndexPK(
                                    maPK,
                                    maVTView.getText().toString().trim()
                            ), 2));
                    cp_table2.removeViews(1, cp_table2.getChildCount() - 1);
                    List<TableRow> rows2 = rowGenarator.generateArrayofRows();
                    for (TableRow row2 : rows2) {
                        cp_table2.addView(row2);
                    }
                }
            });
        }
//        // CP_totalCount : Tổng số các VT được cấp
        cp_totalCount.setText((cp_table1.getChildCount() - 1) + "");
//        // CP_totalPrice : Tổng số tiền VT  = số lượng loại VT mà PN nhập * số tiền của mỗi loại VT
        cp_totalPrice.setText(MoneyFormat(totalPrice));
    }

    public String MoneyFormat(int money) {
        if (money == 0) return "0 đ";
        int temp_money = money;
        String moneyFormat = "";
        if (money < 1000) return String.valueOf(money) + " đ";
        else {
            int count = 0;
            while (temp_money != 0) {
                moneyFormat += (temp_money % 10) + "";
                if ((count + 1) % 3 == 0 && temp_money > 10) moneyFormat += ".";
                count++;
                temp_money /= 10;
            }
        }
        return new StringBuilder(moneyFormat).reverse().toString() + " đ";
    }

    public ArrayAdapter<String> loadPBSpinner() {
        // 1. Tạo list Phong kho // 2. Đổ Phong_kho.getTenPK() ra 1 List // 3. setAdapter cho cái list getTenPK() đó
        phongkho_list = new PhongKhoDatabase(ChiTietPhieuNhapLayout.this).select();
        ArrayList<String> phongbanNames_list = new ArrayList<>();
        phongbanNames_list.add("Tất cả phòng kho");
        // Phục vụ cho việc xổ ra Option cho Spinner
        for (PhongKho pb : phongkho_list) {
            phongbanNames_list.add(pb.getTenpk());
//            Log.d("data", pb.getTenpb());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, phongbanNames_list);
        return adapter;
    }

    public void setEventPBSpinner() {
        PKSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) dataMaPKSpinner = phongkho_list.get(position - 1).getMapk();
                else {
                    // 1.
                    dataMaPKSpinner = "All";

                }
                transferLayout(dataMaPKSpinner);
//                Toast.makeText( CapphatVTLayout.this, dataMaPKSpinner+"", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dataMaPKSpinner = "All";
            }
        });
    }

    // To set all rows to normal state, set focusRowid = -1
    public void setNormalBGTableRows(TableLayout list) {
        // 0: là thằng example đã INVISIBLE
        // Nên bắt đầu từ 1 -> 9
        for (int i = 1; i < list.getChildCount(); i++) {
            TableRow row = (TableRow) list.getChildAt((int) i);
            int dem = row.getId();
            if (indexofRow != (int) row.getId())
                row.setBackgroundColor(getResources().getColor(R.color.white));
        }
//             Toast.makeText( PhongbanLayout.this, indexofRow+"", Toast.LENGTH_LONG).show();
//        Toast.makeText(CapphatVTLayout.this, indexofRow + ":" + (int) list.getChildAt(indexofRow).getId() + "", Toast.LENGTH_LONG).show();
    }

    public int findMaPNinTableCTPN(TableLayout list) {
        TableRow tr = null;
        TextView maPN = null;
        if (focusMaPN == null) return -1;
        Log.d("focus", focusMaPN.getText() + "");
        for (int i = 1; i < list.getChildCount(); i++) {
            tr = (TableRow) list.getChildAt(i);
            maPN = (TextView) tr.getChildAt(0);
            if (maPN.getText().toString().trim().equalsIgnoreCase(focusMaPN.getText().toString().trim() + ""))
                return i;
        }
        return -1;
    }

    public VatTu findVTinListVT(String maVT) {
        for (VatTu vt : vattu_list) {
            if (vt.getMaVt().trim().equalsIgnoreCase(maVT))
                return vt;
        }
        return null;
    }

    public PhieuNhap findMaPKinTablePN(String maPN){
        for (PhieuNhap pn : phieunhap_list){
            if (pn.getSoPhieu().trim().equalsIgnoreCase(maPN)){
                return pn;
            }
        }
        return null;
    }

    public ChiTietPhieuNhap findVTinListCTPN(String maPN, String maVT) {
        for (ChiTietPhieuNhap ctpn : chitietpn_list) {
            if (ctpn.getSoPhieu().trim().equalsIgnoreCase(maPN) && ctpn.getMaVT().trim().equalsIgnoreCase(maVT))
                return ctpn;
        }
        return null;
    }

    // Hàm này giúp hàm trên bằng cách dẫn tới những dữ liệu có thể cụ thể hóa dữ liệu của hàm trên
    public void setEventTableRowsHelper(TableLayout sublist) {
        // Kiểm tra focus MaNv
        if (focusMaPN == null || focusMaPN.getText().toString().trim().equalsIgnoreCase("")
                || sublist.getChildCount() == 0) {
            Toast.makeText(ChiTietPhieuNhapLayout.this, "Sorry can't help with no input data", Toast.LENGTH_LONG);
            return;
        }

        // Rect là 1 rect tàng hình
        int index = findMaPNinTableCTPN(sublist);
        TableRow tr = (TableRow) sublist.getChildAt(index);

//        Log.d("focus", index + "");
        Rect rc = new Rect(0, 0, tr.getWidth(), tr.getHeight());
        // Khi gọi tới thằng TableRow sẽ vẽ 1 Rectangle tàng hình ở thằng TableRow đang chỉ định
        tr.getDrawingRect(rc);
        tr.requestRectangleOnScreen(rc);
        tr.setBackgroundColor(getResources().getColor(R.color.selectedColor));
        // Reset background white for others
        for (int i = 1; i < cp_tablepn_list.getChildCount(); i++) {
            TableRow row = (TableRow) cp_tablepn_list.getChildAt((int) i);
            if (index != (int) row.getId())
                row.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    public void setDataImageView(ImageView imageView, byte[] imageBytes) {
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(bitmap);
        }
    }

    public String setEventDisplayLVT(String sp) {
        int dem = 0;
        for (int i = 0; i < chitietpn_list.size(); i++) {
            if (chitietpn_list.get(i).getSoPhieu().equals(sp)) {
                dem++;
            }
        }
        String label = "Loại vật tư: " + dem;
        return label;
    }

    public void setEventDisplayVT(String maVT) {
        labelVT.setVisibility(View.VISIBLE);
        VatTu vt = findVTinListVT( maVT );
        if(vt == null) return;
        String label = vt.getMaVt() + ":" + vt.getTenVt();
        labelVT.setText(label);
        // 1. Có VanPhongPham rồi thì set on click // 2. Gọi Dialog để xem
        previewVTBtn.setVisibility(View.VISIBLE);
        previewVTBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Image from Database is handled to load here
                createDialog(R.layout.popup_vt_previewimage);
                // Custom set Control
                VT_IP_maVT = dialog.findViewById(R.id.VT_IP_maVT);
                VT_IP_tenVT = dialog.findViewById(R.id.VT_IP_tenVT);
                VT_IP_DVT = dialog.findViewById(R.id.VT_IP_DVT);
                VT_IP_Gia = dialog.findViewById(R.id.VT_IP_Gia);
                VT_IP_Hinh = dialog.findViewById(R.id.VT_IP_Hinh);
                // Load Data
                setDataImageView( VT_IP_Hinh, vt.getHinh() );
                VT_IP_maVT.setText( vt.getMaVt().toString().trim());
                VT_IP_tenVT.setText( vt.getTenVt().toString().trim());
                VT_IP_DVT.setText( vt.getDvt().toString().trim());
                VT_IP_Gia.setText( vt.getGiaNhap().toString().trim());
            }
        });
    }

    public VatTu findVTinListCTPN (String maVT ){
        for( VatTu vt : vattu_list){
            if( vt.getMaVt().trim().equalsIgnoreCase( maVT ))
                return vt;
        }
        return null;
    }

    // DIALOG HELPER ----------------------------------------------------------------------------
    public void createDialog(int layout) {
        dialog = new Dialog(ChiTietPhieuNhapLayout.this);
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void setEventSpinnerMini() {
        PN_spinner_mini.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PN_spinner_mini_maPN = phieunhap_list.get(position).getSoPhieu().trim();
//                Toast.makeText( NhanvienLayout.this, PB_spinner_mini_maPB+"", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PN_spinner_mini_maPN = phieunhap_list.get(0).getSoPhieu();
            }
        });
        VT_spinner_mini.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                VT_spinner_mini_maVT = vattu_list.get(position).getMaVt().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                VT_spinner_mini_maVT = vattu_list.get(0).getMaVt();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setControlDialog() {
        // 1 Form của CP
        backBtn = dialog.findViewById(R.id.CP_backBtn);
        yesBtn = dialog.findViewById(R.id.CP_yesInsertBtn);
        noBtn = dialog.findViewById(R.id.CP_noInsertBtn);

        showPNError = dialog.findViewById(R.id.CP_showPNError);
        showVTError = dialog.findViewById(R.id.CP_showVTError);
        showSLVTError = dialog.findViewById(R.id.CP_showSLVTError);

        showResult = dialog.findViewById(R.id.CP_showResult);
        showConfirm = dialog.findViewById(R.id.CP_showConfirm);
        showLabel = dialog.findViewById(R.id.CP_showLabel);


        PN_spinner_mini = dialog.findViewById(R.id.CP_PNSpinner_mini);
        VT_spinner_mini = dialog.findViewById(R.id.CP_VTSpinner_mini);

        inputSLVT = dialog.findViewById(R.id.CP_inputSLCP);

        ArrayList<String> PN_name = new ArrayList<>();
        for (PhieuNhap pn : phieunhap_list) {
            PN_name.add(pn.getSoPhieu());
        }
        PN_spinner_mini.setAdapter(loadSpinnerAdapter(PN_name));

        ArrayList<String> VT_name = new ArrayList<>();
        for (VatTu vt : vattu_list) {
            VT_name.add(vt.getTenVt());
        }
        VT_spinner_mini.setAdapter(loadSpinnerAdapter(VT_name));

        setEventSpinnerMini();


    }

    public void setEventDialog(View view) {
        // Them/Xoa/Sua CTPN
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = false;
                switch (view.getId()){
                    case R.id.CP_insertBtn:{
                        if(!isSafeDialog(false))break;
                        ChiTietPhieuNhap ctpn = new ChiTietPhieuNhap(PN_spinner_mini_maPN.trim(), VT_spinner_mini_maVT.trim()
                                , Long.valueOf(inputSLVT.getText().toString().trim()));

                        if(chitietpnDB.insert(ctpn) == -1)break;

                        String maPK = findMaPKinTablePN(PN_spinner_mini_maPN.trim()).getMaK().trim();
                        String dataPK = dataMaPKSpinner.trim();
                        if(!maPK.equalsIgnoreCase(dataPK)){
                            if (dataPK.equalsIgnoreCase("All")){
                                table();
                            }
                        }else{
                            createCPLayout_fromPK(dataPK);
                            table();
                        }
                        editBtn.setVisibility(View.INVISIBLE);
                        delBtn.setVisibility(View.INVISIBLE);
                        previewVTBtn.setVisibility(View.INVISIBLE);
                        PN_spinner_mini_maPN = null;
                        VT_spinner_mini_maVT = null;

                        success = true;
                    }
                        break;
                    case R.id.CP_editBtn: {
                        if (!isSafeDialog(true)) break;
                        ChiTietPhieuNhap ctpn = new ChiTietPhieuNhap(PN_spinner_mini_maPN.trim(),
                                VT_spinner_mini_maVT.trim(), Long.valueOf(inputSLVT.getText().toString().trim()));
                        if (chitietpnDB.update(ctpn) == -1) break;
                        String maPK = findMaPKinTablePN(PN_spinner_mini_maPN.trim()).getMaK().trim();
                        String dataPK = dataMaPKSpinner.trim();
                        if (!maPK.equalsIgnoreCase(dataPK)) {
                            if (dataPK.equalsIgnoreCase("All")) {
                                table();
                            }
                        } else {
                            createCPLayout_fromPK(dataPK);
                        }
                        editBtn.setVisibility(View.INVISIBLE);
                        delBtn.setVisibility(View.INVISIBLE);
                        previewVTBtn.setVisibility(View.INVISIBLE);

                        PN_spinner_mini_maPN = null;
                        VT_spinner_mini_maVT = null;

                        success = true;
                    }
                        break;
                    case R.id.CP_delBtn: {

                        ChiTietPhieuNhap ctpn = new ChiTietPhieuNhap(PN_spinner_mini_maPN.trim(),
                                VT_spinner_mini_maVT.trim(), Long.valueOf(inputSLVT.getText().toString().trim()));
                        if (chitietpnDB.delete(ctpn) == -1) break;
                        String maPK = findMaPKinTablePN(PN_spinner_mini_maPN.trim()).getMaK().trim();
                        String dataPK = dataMaPKSpinner.trim();
                        if (!maPK.equalsIgnoreCase(dataPK)) {
                            if (dataPK.equalsIgnoreCase("All")) {
                                table();
                            }
                        } else {
                            createCPLayout_fromPK(dataPK);
                        }
                        editBtn.setVisibility(View.INVISIBLE);
                        delBtn.setVisibility(View.INVISIBLE);
                        previewVTBtn.setVisibility(View.INVISIBLE);

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
//                            input.setText("");
//                            inputTenVT.setText("");
//                            inputDVT.setText("");
//                            inputGia.setText("");
                            showResult.setVisibility(View.INVISIBLE);
                            dialog.dismiss();
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
        String sophieu, vattu, slVattu;
        // Số phiếu không được trùng với Số phiếu khác và ko để trống
        sophieu = "";
        boolean noError = true;
        if ( PN_spinner_mini_maPN == null) {
            showPNError.setText("Số Phiếu không được trống ");
            showPNError.setVisibility(View.VISIBLE);
            noError = false;
        } else {
            sophieu = PN_spinner_mini_maPN.trim();
            showPNError.setVisibility(View.INVISIBLE);
            noError = true;
        }

        // Tên VT không được để trống và không trùng
        vattu = "";
        if (VT_spinner_mini_maVT == null) {
            showVTError.setText(" Vật Tư không được trống ");
            showVTError.setVisibility(View.VISIBLE);
            noError = false;
        } else {
            vattu = VT_spinner_mini_maVT.trim();
            showVTError.setVisibility(View.INVISIBLE);
            if(noError)noError = true;
        }
        slVattu = inputSLVT.getText().toString().trim();
        if (slVattu.length() > 1)
            if( slVattu.charAt(0) == '0')
            {if( slVattu.length() > 1)
            {slVattu = slVattu.substring(1,slVattu.length()-1);}}
        if (slVattu.equals("")) {
            showSLVTError.setText(" Vật Tư không được trống ");
            showSLVTError.setVisibility(View.VISIBLE);
            noError = false;
        } else {
            showSLVTError.setVisibility(View.INVISIBLE);
            if(noError)noError = true;
        }

        if (noError) {
            for (int i = 1; i < cp_tablectpn_list.getChildCount(); i++) {
                TableRow tr = (TableRow) cp_tablectpn_list.getChildAt(i);
                TextView mapn_data = (TextView) tr.getChildAt(0);
                TextView amvt_data = (TextView) tr.getChildAt(1);

                if (!allowSameID)
                    if (sophieu.equalsIgnoreCase(mapn_data.getText().toString())) {
                        if(vattu.equalsIgnoreCase(amvt_data.getText().toString())) {
                            showVTError.setText("Mã VT không được trùng ");
                            showVTError.setVisibility(View.VISIBLE);
                            return noError = false;
                        }
                    }
            }
            showPNError.setVisibility(View.INVISIBLE);
            showVTError.setVisibility(View.INVISIBLE);
            showSLVTError.setVisibility(View.INVISIBLE);
        }
        return noError;
    }

    // LAYOUT 01 -----------------------------------------------
    // Văn phòng phẩm khi init thì select theo thằng CP, sau đó focus vào thằng đầu tiên của VT
    public void setEventTableVT() {

    }

    // khi 1 hàng văn phòng phẩm được focus thì mới có nhân viên
    // --------------- CUSTOM HELPER --------------------------------------------------------------------
    public int DPtoPix(int dps) {
        return (int) (dps * scale + 0.5f);
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

    // Table 3
    // <!-- 80 / 150 / 60 / 60 / 60 -->
    public TableRow createRow(Context context, VatTu vattu) {
        TableRow tr = new TableRow(context);

        //  So phieu
        TextView maVT = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maVT đạt tới max width thì nó sẽ tăng height cho bên maNV luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        maVT.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        maVT.setWidth(DPtoPix(85));
        maVT.setText(vattu.getMaVt());

        //   Ngay cap
        TextView tenVT = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maNV đạt tới max width thì nó sẽ tăng height cho bên maVT luôn
        tenVT.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        tenVT.setText(vattu.getTenVt());
        tenVT.setWidth(DPtoPix(150));

        //  VT
        TextView dvtVT = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maVt đạt tới max width thì nó sẽ tăng height cho bên maNV luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        dvtVT.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        dvtVT.setWidth(DPtoPix(80));
        dvtVT.setText(vattu.getDvt());


        //   SL
        TextView trigia = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maNV đạt tới max width thì nó sẽ tăng height cho bên maVt luôn
        trigia.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        trigia.setText(vattu.getGiaNhap() + "");
        trigia.setWidth(DPtoPix(80));

        tr.setBackgroundColor(getResources().getColor(R.color.white));
        // Add 2 thứ vào row
        tr.addView(maVT);
        tr.addView(tenVT);
        tr.addView(dvtVT);
        tr.addView(trigia);

        return tr;
    }

    public TableRow createRow(Context context, VatTu vattu, ChiTietPhieuNhap ctpn) {
        TableRow tr = new TableRow(context);
        //  So phieu
        TextView maPN = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maVT đạt tới max width thì nó sẽ tăng height cho bên maNV luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        maPN.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        maPN.setWidth(DPtoPix(80));
        maPN.setText(ctpn.getSoPhieu().trim());
        //  So phieu
        TextView maVT = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maVT đạt tới max width thì nó sẽ tăng height cho bên maNV luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        maVT.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        maVT.setWidth(DPtoPix(40));
        maVT.setText(ctpn.getMaVT().trim());
        //   Ngay cap
        TextView tenVT = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maNV đạt tới max width thì nó sẽ tăng height cho bên maVT luôn
        tenVT.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        tenVT.setText(vattu.getTenVt());
        tenVT.setWidth(DPtoPix(100));

        //  VT
        TextView dvtVT = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maVt đạt tới max width thì nó sẽ tăng height cho bên maNV luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        dvtVT.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        dvtVT.setWidth(DPtoPix(100));
        dvtVT.setText(vattu.getDvt());


        //   SL
        TextView soluong = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maNV đạt tới max width thì nó sẽ tăng height cho bên maVt luôn
        soluong.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        soluong.setText(ctpn.getSoLuong() + "");
        soluong.setMaxWidth(DPtoPix(100));

        tr.setBackgroundColor(getResources().getColor(R.color.white));
        // Add 2 thứ vào row
        tr.addView(maPN);
        tr.addView(maVT);
        tr.addView(tenVT);
        tr.addView(dvtVT);
        tr.addView(soluong);

        return tr;
    }

    public TableRow createRow(Context context, ChiTietPhieuNhap ctpn) {
        TableRow tr = new TableRow(context);

        TextView soPhieu = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maNV đạt tới max width thì nó sẽ tăng height cho bên tenNV luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        soPhieu.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        soPhieu.setWidth(DPtoPix(80));
        soPhieu.setText(ctpn.getSoPhieu());

        TextView maVT = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maVT đạt tới max width thì nó sẽ tăng height cho bên maNV luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        maVT.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        maVT.setWidth(DPtoPix(85));
        maVT.setText(ctpn.getMaVT());


        //   SL
        TextView soluong = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maNV đạt tới max width thì nó sẽ tăng height cho bên maVt luôn
        soluong.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        soluong.setText(ctpn.getSoLuong() + "");
        soluong.setWidth(DPtoPix(80));

        tr.setBackgroundColor(getResources().getColor(R.color.white));
        // Add 2 thứ vào row
        tr.addView(soPhieu);
        tr.addView(maVT);
        tr.addView(soluong);
        return tr;
    }

    // Table 4
    // <!-- 80 / 300 -->
    public TableRow createRow(Context context, PhieuNhap pn) {
        TableRow tr = new TableRow(context);
        TextView soPhieu = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maNV đạt tới max width thì nó sẽ tăng height cho bên tenNV luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        soPhieu.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        soPhieu.setWidth(DPtoPix(80));
        soPhieu.setText(pn.getSoPhieu());

        //   Ten PB
        TextView ngayLapPhieu = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà tenNV đạt tới max width thì nó sẽ tăng height cho bên maNV luôn
        ngayLapPhieu.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        ngayLapPhieu.setText(formatDate(pn.getNgayLap(), false));
        ngayLapPhieu.setWidth(DPtoPix(190));

        TextView maKho = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        maKho.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        maKho.setText(pn.getMaK());
        maKho.setWidth(DPtoPix(85));

        tr.setBackgroundColor(getResources().getColor(R.color.white));
        // Add 2 thứ vào row
        tr.addView(soPhieu);
        tr.addView(ngayLapPhieu);
        tr.addView(maKho);

        return tr;
    }

    // --------------- CUSTOM HELPER -----------------------------------------------------------------
    public ArrayAdapter<String> loadSpinnerAdapter(ArrayList<String> entity) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, entity);
        return adapter;
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
