package com.example.accounting;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.accounting.Entity.Bill;
import com.example.accounting.Entity.IncomeCategory;
import com.example.accounting.Entity.Operation;
import com.example.accounting.Entity.RateCategory;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddOperationActivity extends ActionBarActivity {
    TextView editDate;
    int myYear = 2015;
    int myMonth = 10;
    int myDay = 25;
    JSONArray array;
    List<String> billFromList;
    Bill from;
    int selectedBillFromId;
    String selectedBillTo;
    int selectedBillToId;
    String selectedType;
    String selectedCategory;
    int index;
    Operation operation;
    private final String TAG = "AddOperationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_operation);
        Bundle extras = getIntent().getExtras();
        findLastId();
        if(extras!=null) {
            index = extras.getInt(getString(R.string.index));
            editDate = (TextView) findViewById(R.id.operDateEdit);
            settingEditDate();
            TextView billFromTV = (TextView) findViewById(R.id.operBillFromT);
            settingBillFrom(billFromTV, index);
            Spinner typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
            settingTypeSpinner(typeSpinner);
            Spinner billToSpinner = (Spinner) findViewById(R.id.billToSpinner);
            if (((String) typeSpinner.getSelectedItem()).equals(getString(R.string.transfer))) {
                settingBillToSpinner(billToSpinner);
            }
            final Spinner categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
            if (((String) typeSpinner.getSelectedItem()).equals(getString(R.string.transfer))) {
                settingCategorySpinner(categorySpinner, ((String) typeSpinner.getSelectedItem()));
            }
            Button ok = (Button) findViewById(R.id.okAddButton);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveOperation();
                }
            });
            Button cancel = (Button) findViewById(R.id.cancelButton);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    returnToMain();
                }
            });
        }
    }

    public void saveOperation(){
        Spinner typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        String type = selectedType;
        operation = null;
        TextView eDate = (TextView) findViewById(R.id.operDateEdit);
        TextView valueTV = (TextView) findViewById(R.id.operValueET);
        String value = valueTV.getText().toString();
        TextView descTV = (TextView) findViewById(R.id.operDescriptionET);
        switch (type) {
            case "Приход":
            case "Расход": {
                selectedBillFromId = from.getId();
                double balance = from.getBalance();
                if (type.equals(getString(R.string.income))) {
                    from.setBalance(balance + (Double.valueOf(value)).doubleValue());
                } else {
                    from.setBalance(balance - (Double.valueOf(value)).doubleValue());
                }
                createAndWriteObject(from, BillFragment.BILLFILE, index);
                if (type.equals(getString(R.string.income)))
                    operation = new Operation(eDate.getText().toString(),
                            Operation.INCOME, (Double.valueOf(value)).doubleValue(),
                            selectedBillFromId, selectedCategory, descTV.getText().toString());
                else
                    operation = new Operation(eDate.getText().toString(),
                            Operation.RATE,(Double.valueOf(value)).doubleValue(),
                            selectedBillFromId, selectedCategory, descTV.getText().toString());
            }
            break;

            case "Перевод": {
                array = null;
                readJsonObject(BillFragment.BILLFILE);
                if (array != null) {
                    try {
                        Bill to=null;
                        for (int j = 0; j < array.length(); j++) {
                            JSONObject item = array.getJSONObject(j);
                            Gson gson = new Gson();
                            Bill bill = (Bill) gson.fromJson(item.toString(), Bill.class);
                            if (bill.getBillName().equals(selectedBillTo)) {
                                selectedBillToId = bill.getId();
                                double balance = bill.getBalance();
                                bill.setBalance(balance + (Double.valueOf(value)).doubleValue());
                                to=bill;
                            }
                        }
                        selectedBillFromId = from.getId();
                        double balance = from.getBalance();
                        from.setBalance(balance - (Double.valueOf(value)).doubleValue());
                        createAndWriteObject(from, BillFragment.BILLFILE, index);
                        int billToIndex = findBillToIndex(selectedBillToId);
                        if (billToIndex != -1) {
                            createAndWriteObject(to, BillFragment.BILLFILE, billToIndex);
                        }
                    } catch (JSONException ex) {
                        Log.e(TAG, "onCreateView " + ex.getMessage());
                    }
                }
                operation = new Operation(eDate.getText().toString(),
                        Operation.TRANSFER, Double.valueOf(value.trim()),
                        selectedBillFromId, selectedBillToId, descTV.getText().toString());
            }
            break;
        }
        if(from.getBalance()>=0){
            if (operation != null) {
                array = null;
                readJsonObject(OperationActivity.OPERFILE);
                createAndWriteObject(operation, OperationActivity.OPERFILE, null);
            }
            Toast.makeText(AddOperationActivity.this, R.string.add_success_message, Toast.LENGTH_SHORT).show();
            //returnToMain();
        } else {
            double balance = from.getBalance();
            from.setBalance(balance + Double.valueOf(value.trim()));
            valueTV.setText(String.valueOf(from.getBalance()));
            Toast.makeText(AddOperationActivity.this, R.string.add_error_balance, Toast.LENGTH_SHORT).show();
        }
    }

    private void settingEditDate(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        editDate.setText(format.format(date));
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataPicker(myCallBack);
            }
        });
    }

    private void settingTypeSpinner(Spinner typeSpinner){
        ArrayAdapter<String> adapter = null;
        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.income));
        list.add(getString(R.string.rate));
        list.add(getString(R.string.transfer));
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Spinner typeSpinner = (Spinner)  findViewById(R.id.typeSpinner);
                selectedType = (String) typeSpinner.getSelectedItem();
                Spinner billToSpinner = (Spinner) findViewById(R.id.billToSpinner);
                Spinner categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
                if (((String) typeSpinner.getSelectedItem()).equals(getString(R.string.transfer))) {
                    billToSpinner.setEnabled(true);
                    settingBillToSpinner(billToSpinner);
                    categorySpinner.setEnabled(false);

                } else {
                    billToSpinner.setEnabled(false);
                    categorySpinner.setEnabled(true);
                    settingCategorySpinner(categorySpinner, (String) typeSpinner.getSelectedItem());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private int findBillToIndex(int id){
        array=null;
        readJsonObject(BillFragment.BILLFILE);
        int index=-1;
        try {
            if (array != null) {
                int i = 0;
                while (i < array.length()) {
                    JSONObject item = array.getJSONObject(i);
                    Gson gson = new Gson();
                    Bill bill = (Bill) gson.fromJson(item.toString(), Bill.class);
                    if(bill.getId()==id){
                        index=i;
                        i=array.length();
                    }
                    i++;
                }

            } else
                return index;
        } catch (JSONException e){
            Log.e(TAG, "findBillToIndex "+e.getMessage());
        } finally {
            return index;
        }
    }

    public Bill getFrom() {
        return from;
    }

    public Operation getOperation() {
        return operation;
    }

    public JSONArray getArray() {
        return array;
    }

    private void settingBillFrom(TextView billFromTV, int index){
        billFromList = new ArrayList<String>();
        array=null;
        readJsonObject(BillFragment.BILLFILE);
        if(array!=null){
            try {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.getJSONObject(i);
                    Gson gson = new Gson();
                    Bill bill = (Bill)gson.fromJson(item.toString(), Bill.class);
                    billFromList.add(bill.getBillName());
                    if(i==index){
                        from = bill;
                        billFromTV.setText(from.getBillName());
                    }
                }
            } catch (JSONException ex){
                Log.e(TAG, "onCreateView "+ex.getMessage());
            }
        }
    }

    private void settingBillToSpinner(Spinner billToSpinner){
        ArrayAdapter<String> adapter=null;
        if(!billFromList.isEmpty()) {
            if (!from.getBillName().equals("")) {
                List<String> billToList = new ArrayList<>(billFromList);
                billToList.remove(from.getBillName());
                if(billToList.isEmpty()) {
                    Toast.makeText(AddOperationActivity.this, R.string.add_error_bills, Toast.LENGTH_SHORT).show();
                    Spinner typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
                    typeSpinner.setSelection(0);
                } else {
                    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, billToList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    billToSpinner.setAdapter(adapter);
                    billToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            Spinner billToSpinner = (Spinner) findViewById(R.id.billToSpinner);
                            selectedBillTo = (String) billToSpinner.getSelectedItem();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }
        }
    }
    public void deleteJSONObject(int index, String filename){
        if(array==null){
            readJsonObject(filename);
        }
        try {
            int len = array.length();
            ArrayList<JSONObject> elements = new ArrayList<JSONObject>(len);
            for (int i = 0; i < len; i++) {
                if (i != index) {
                    JSONObject obj = array.getJSONObject(i);
                    if(obj!=null)
                        elements.add(obj);
                }
            }
            array = new JSONArray();
            for (JSONObject jObj : elements){
                array.put(jObj);
            }
            writeJsonObject(filename);
        } catch (JSONException e){
            Log.e(TAG, "deleteJsonObject "+ e.getMessage());
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
    private void settingCategorySpinner(final Spinner categorySpinner,String type){
        ArrayAdapter<String> adapter=null;
        List<String> categoryList = new ArrayList<String>();
        array=null;
        if(type.equals(getString(R.string.income)))
            readJsonObject(IncomeFragment.INCOMEFILE);
        else if(type.equals(getString(R.string.rate)))
            readJsonObject(IncomeFragment.RATEFILE);
        if(array!=null){
            try {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.getJSONObject(i);
                    Gson gson = new Gson();
                    if(type.equals(getString(R.string.income))) {
                        IncomeCategory income = (IncomeCategory) gson.fromJson(item.toString(), IncomeCategory.class);
                        categoryList.add(income.getIncomeName());
                    } else {
                        RateCategory rate = (RateCategory) gson.fromJson(item.toString(), RateCategory.class);
                        categoryList.add(rate.getIncomeName());
                    }
                }
                adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedCategory = (String) categorySpinner.getSelectedItem();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            } catch (JSONException ex){
                Log.e(TAG, "onCreateView "+ex.getMessage());
            }
        }

    };

    private void showDataPicker(DatePickerDialog.OnDateSetListener myCallBack) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(date);
        String currentYear = currentDate.substring(0, currentDate.indexOf("-"));
        currentDate = currentDate.substring(currentDate.indexOf("-") + 1);
        String currentMonth = currentDate.substring(0, currentDate.indexOf("-"));
        String currentDay = currentDate.substring(currentDate.lastIndexOf("-") + 1);
        myYear = Integer.parseInt(currentYear);
        myMonth = Integer.parseInt(currentMonth);
        myDay = Integer.parseInt(currentDay);
        DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth - 1, myDay);
        tpd.show();
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;
            if (myMonth >= 0 && myMonth < 9) {
                if (myDay >= 0 && myDay < 10)
                    editDate.setText(myYear + "-" + "0" + (myMonth + 1) + "-" + "0" + myDay);
                else
                    editDate.setText(myYear + "-" + "0" + (myMonth + 1) + "-" + myDay);
            } else {
                if (myDay >= 0 && myDay < 10)
                    editDate.setText(myYear + "-" + (myMonth + 1) + "-" + "0" + myDay);
                else
                    editDate.setText(myYear + "-" + (myMonth + 1) + "-" + myDay);
            }
        }
    };

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
    private void createAndWriteObject(Object forWrite, String filename, Integer index){
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

    private void returnToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    private void findLastId(){
        readJsonObject(OperationActivity.OPERFILE);
        if(array!=null){
            int maxID =0;
            try {
                JSONObject item = array.getJSONObject(array.length()-1);
                Gson gson = new Gson();
                Operation oper = (Operation) gson.fromJson(item.toString(), Operation.class);
                Operation.setIncID(oper.getId());
            }catch (JSONException e){
                Log.e(TAG, "findLastId "+e.getMessage());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_operation, menu);
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
}
