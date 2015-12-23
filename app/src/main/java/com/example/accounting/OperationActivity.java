package com.example.accounting;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.accounting.Entity.Bill;
import com.example.accounting.Entity.Operation;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class OperationActivity extends ActionBarActivity {
    public static final String OPERFILE = "oper.json";
    ListView mListView;
    private final String TAG = "OperationActivity";
    ArrayList<OperItem> operList;
    JSONArray array;
    int id=-1;
    int index=-1;
    String billFrom;
    Operation deletedOper;
    Bill billFro = null;
    Bill billTo = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);
        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            id = extras.getInt(getString(R.string.id));
            billFrom = extras.getString(getString(R.string.billname));
            if(billFrom==null) {
                index = extras.getInt(getString(R.string.index));
                findBillByIndex();
                if(billFrom!=null)
                    fillListView();
            } else {

            fillListView();}
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_operation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void fillListView(){
        readJsonObject(OPERFILE);
        if (array != null) {
            operList = new ArrayList<OperItem>();
            mListView = (ListView) findViewById(R.id.operListView);
            try {
                JSONArray operArray = new JSONArray();
                for (int i = 0; i < array.length(); i++) {
                    operArray.put(array.getJSONObject(i));
                }
                for (int i = 0; i < operArray.length(); i++) {
                    JSONObject item = operArray.getJSONObject(i);
                    Gson gson = new Gson();
                    Operation oper = (Operation) gson.fromJson(item.toString(), Operation.class);
                    if(oper.getBillFrom()==id) {
                        if (oper.getType() == Operation.TRANSFER) {
                            readJsonObject(BillFragment.BILLFILE);
                            int idTO = oper.getBillTo();
                            String billTo="";
                            if(array!=null){
                                for(int j=0; j<array.length(); j++){
                                    JSONObject item2 = array.getJSONObject(j);
                                    Gson gson2 = new Gson();
                                    Bill bill = (Bill) gson2.fromJson(item2.toString(), Bill.class);
                                    if(bill.getId()==idTO){
                                        billTo = bill.getBillName();
                                    }
                                }
                            }
                            operList.add(new OperItem(oper.getId(), oper.getDate(), getString(R.string.transfer), billFrom,billTo,
                                    "", oper.getDescription(), oper.getValue()));
                        } else {
                            if(oper.getType()==Operation.INCOME)
                                operList.add(new OperItem(oper.getId(), oper.getDate(), getString(R.string.income), billFrom, "",
                                        oper.getCategory(), oper.getDescription(), oper.getValue()));
                            else
                                operList.add(new OperItem(oper.getId(), oper.getDate(), getString(R.string.rate), billFrom, "",
                                        oper.getCategory(), oper.getDescription(), oper.getValue()));
                        }
                    }
                }
                mListView.setAdapter(new OperAdapter(this, R.layout.oper_list_item, operList));
            } catch (JSONException ex){
                Log.e(TAG, "onCreateView "+ex.getMessage());
            }

        }
    }

    private void findBillByIndex(){
        readJsonObject(BillFragment.BILLFILE);
        if (array != null) {
            try {
                JSONObject item = array.getJSONObject(index);
                Gson gson = new Gson();
                Bill bill = gson.fromJson(item.toString(), Bill.class);
                billFrom=bill.getBillName();
                id = bill.getId();
                billFro=bill;
            } catch (JSONException ex){
                Log.e(TAG, "findBillByIndex "+ex.getMessage());
            }
        }
    }

    public Bill getBillFro() {
        return billFro;
    }

    public class OperAdapter extends ArrayAdapter<OperItem> {
        private ArrayList<OperItem> items;
        private OperViewHolder operHolder;

        private class OperViewHolder {
            TextView date;
            TextView balance;
            TextView type;
            LinearLayout mainView;
        }


        public OperAdapter(Context context, int tvResId, ArrayList<OperItem> items) {
            super(context, tvResId, items);
            this.items = items;
        }

        @Override
        public View getView(final int pos, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.oper_list_item, null);
                operHolder = new OperViewHolder();
                operHolder.mainView = (LinearLayout) view.findViewById(R.id.mainview);
                //billHolder.listItem = (RelativeLayout) view.findViewById(R.id.listitem);
                operHolder.date = (TextView) view.findViewById(R.id.dateTV);
                operHolder.balance = (TextView) view.findViewById(R.id.valueTV);
                operHolder.type=(TextView) view.findViewById(R.id.typeTV);
                view.setTag(operHolder);
            } else operHolder = (OperViewHolder) view.getTag();

            OperItem oper = items.get(pos);

            if (oper != null) {
                operHolder.date.setText(oper.getDate());
                operHolder.balance.setText(String.valueOf(oper.getValue()));
                operHolder.type.setText(oper.getType());
            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) operHolder.mainView.getLayoutParams();
            params.rightMargin = 0;
            params.leftMargin = 0;
            operHolder.mainView.setLayoutParams(params);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, pos);
                }
            });

            return view;
        }

        private void showPopup(View v, final int pos) {
            final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.menu_oper_popup);
            popupMenu
                    .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete: {
                                    OperItem ai = getItem(pos);
                                    deleteJSONObject(ai.getId(), OPERFILE);
                                    fillListView();
                                    restoreBalance();
                                    Log.d(TAG, "onMenuItemClick delete" + ai.getId());
                                    return true;
                                }
                                case R.id.descript: {
                                    OperItem ai = getItem(pos);
                                    Intent intent = new Intent(OperationActivity.this, OperDescriptionActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra(getString(R.string.billfrom), ai.getBillFrom());
                                    intent.putExtra(getString(R.string.billto), ai.getBillTo());
                                    intent.putExtra(getString(R.string.category), ai.getCategory());
                                    intent.putExtra(getString(R.string.description), ai.getDescription());
                                    startActivity(intent);
                                    Log.d(TAG, "onMenuItemClick descript" + ai.getId());
                                    return true;
                                }

                            }
                            return true;
                        }
                    });

            popupMenu.show();
        }

    }

    public void readJsonObject(String jsonFileName){
        String path = this.getFilesDir().getAbsolutePath()+"/"+jsonFileName;
        File file = new File(path);
        if(file.exists()){
            try {
                FileInputStream stream = new FileInputStream(file);
                String jsonStr = null;
                try{
                    FileChannel channel = stream.getChannel();
                    MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0,channel.size());
                    jsonStr= Charset.defaultCharset().decode(buffer).toString();

                } catch(IOException ioe){
                } finally{
                    try {
                        stream.close();
                    } catch(IOException ie){

                    }
                }
                if(jsonStr!=null){
                    array = new JSONArray(jsonStr);
                }
            } catch (FileNotFoundException | JSONException fnfe){
                Log.e(TAG, "readJsonObject " + fnfe.getMessage());
            }

        }else{
            File jFile = new File(getFilesDir(), jsonFileName);
        }
    }
    public void deleteJSONObject(int id, String filename){
        readJsonObject(filename);
        if(array!=null) {
            try {
                int len = array.length();
                ArrayList<JSONObject> elements = new ArrayList<JSONObject>(len);
                for (int i = 0; i < len; i++) {
                    JSONObject obj = array.getJSONObject(i);
                    int objID = obj.getInt("id");
                    if (objID != id)
                        elements.add(obj);
                    else{
                        Gson gson = new Gson();
                        deletedOper = (Operation) gson.fromJson(obj.toString(), Operation.class);
                    }
                }
                array = new JSONArray();
                for (JSONObject jObj : elements) {
                    array.put(jObj);
                }
                writeJsonObject(filename);
            } catch (JSONException e) {
                Log.e(TAG, "deleteJsonObject " + e.getMessage());
            }
        }
    }

    public Operation getDeletedOper() {
        return deletedOper;
    }

    public ArrayList<OperItem> getOperList() {
        return operList;
    }

    public JSONArray getArray() {
        return array;
    }

    public void restoreBalance(){
        if(deletedOper!=null){
            int type = deletedOper.getType();
            if(type==Operation.TRANSFER){
                readJsonObject(BillFragment.BILLFILE);
                if(array!=null) {
                    int indexFrom = -1;
                    billFro = null;
                    int indexTo = -1;
                    Bill billTo = null;
                    try {
                        for (int j = 0; j < array.length(); j++) {
                            JSONObject item = array.getJSONObject(j);
                            Gson gson = new Gson();
                            Bill bill = (Bill) gson.fromJson(item.toString(), Bill.class);
                            if (bill.getId() == id) {
                                bill.setBalance(bill.getBalance() + deletedOper.getValue());
                                billFro = bill;
                                indexFrom = j;
                            }
                            if (bill.getId() == deletedOper.getBillTo()) {
                                bill.setBalance(bill.getBalance() - deletedOper.getValue());
                                billTo = bill;
                                indexTo = j;
                            }
                        }
                        if (billFrom != null && indexFrom != -1 && billTo != null && indexTo != -1) {
                            createAndWriteObject(billFrom, BillFragment.BILLFILE, indexFrom);
                            createAndWriteObject(billTo, BillFragment.BILLFILE, indexTo);
                        }
                    } catch (JSONException ex) {
                        Log.e(TAG, "onCreateView " + ex.getMessage());
                    }
                }
            } else{
                readJsonObject(BillFragment.BILLFILE);
                if(array!=null){
                    int indexFrom=-1;
                    billFro=null;
                    try {
                        for (int j = 0; j < array.length(); j++) {
                            JSONObject item = array.getJSONObject(j);
                            Gson gson = new Gson();
                            Bill bill = (Bill) gson.fromJson(item.toString(), Bill.class);
                            if (bill.getId() == id) {
                                if(type==Operation.INCOME)
                                    bill.setBalance(bill.getBalance()-deletedOper.getValue());
                                else
                                    bill.setBalance(bill.getBalance()+deletedOper.getValue());
                                billFro=bill;
                                indexFrom=j;
                            }
                        }
                        if(billFro!=null && indexFrom!=-1)
                            createAndWriteObject(billFro, BillFragment.BILLFILE, indexFrom);
                    } catch (JSONException ex){
                        Log.e(TAG, "onCreateView "+ex.getMessage());
                    }
                }
            }
        }
    }
    public void createAndWriteObject(Object forWrite, String filename, Integer index){
        try{
            Gson gson = new Gson();
            String objectString = gson.toJson(forWrite);
            JSONObject objJSON = new JSONObject(objectString);
            array=null;
            readJsonObject(filename);
            if(array!=null){
                if(index==null)
                    array.put(objJSON);
                else
                    array.put(index, objJSON);
            } else {
                array = new JSONArray();
                if(index==null)
                    array.put(objJSON);
                else
                    array.put(index, objJSON);
            }
            writeJsonObject(filename);

        } catch (JSONException ex){
            Log.e(TAG, "onClick "+ex.getMessage());
        }

    }

    private void writeJsonObject(String jsonFileName){
        FileOutputStream outputStream = null;
        if(array!=null) {
            try {
                outputStream = openFileOutput(jsonFileName, Context.MODE_PRIVATE);
                outputStream.write(array.toString().getBytes());
                array=null;

            } catch (IOException e){
                Log.e(TAG, "writeJsonObject "+ e.getMessage());
            } finally{
                if(outputStream!=null){
                    try{
                        outputStream.close();
                    } catch(IOException e){}
                }
            }
        }

    }
    class OperItem {
        private int id; // id записи
        private String date;
        private String type;
        private String billFrom; // название счета
        private String billTo;
        private String category;
        private String description; // описание
        private double value;

        public OperItem(int id, String date, String type, String billFrom, String billTo, String category, String description, double value) {
            this.id = id;
            this.date = date;
            this.type = type;
            this.billFrom = billFrom;
            this.billTo = billTo;
            this.category = category;
            this.description = description;
            this.value = value;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBillFrom() {
            return billFrom;
        }

        public void setBillFrom(String billFrom) {
            this.billFrom = billFrom;
        }

        public String getBillTo() {
            return billTo;
        }

        public void setBillTo(String billTo) {
            this.billTo = billTo;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}
