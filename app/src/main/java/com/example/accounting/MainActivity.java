package com.example.accounting;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
//import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
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
import java.lang.reflect.Field;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends ActionBarActivity implements BillFragment.OnFragmentInteractionListener, IncomeFragment.OnFragmentInteractionListener{

    private FragmentTabHost tabHost;
    private final String TAG = "MainActivity";
    JSONArray array;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ActionBar ab =getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setIcon(R.drawable.ic_launcher);
        getOverflowMenu();
        tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        addTab(getString(R.string.tab_title_1), R.drawable.ic_bill, 1);
        addTab(getString(R.string.tab_title_2), R.drawable.ic_income, 2);
        addTab(getString(R.string.tab_title_3), R.drawable.ic_rate, 3);
        findLastId();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_about:{
                showAboutDialog();
            } break;
            case R.id.action_exit:{
                showAlert();
            } break;
            case R.id.action_add_bill:{
                showAddBillDialog();
            } break;
            case R.id.action_add_income:{
                showCategoryDialog(true);
            } break;
            case R.id.action_add_rate:{
                showCategoryDialog(false);
            } break;
            case R.id.search:{
                showSearchDialog();
            } break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        int i =tabHost.getCurrentTab();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        switch (i) {

            case 0: {

                Fragment billFrgment = fragmentManager.findFragmentByTag(getString(R.string.tab_title_1));
                if (billFrgment != null) {
                    android.support.v4.app.FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
                    fragTransaction.detach(billFrgment);
                    fragTransaction.attach(billFrgment);
                    fragTransaction.commit();
                }
            } break;
            case 1: {
                Fragment incomeFrgment = fragmentManager.findFragmentByTag(getString(R.string.tab_title_2));
                if (incomeFrgment != null) {
                    android.support.v4.app.FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
                    fragTransaction = getSupportFragmentManager().beginTransaction();
                    fragTransaction.detach(incomeFrgment);
                    fragTransaction.attach(incomeFrgment);
                    fragTransaction.commit();
                }
            } break;
            case 2: {
                Fragment rateFrgment = fragmentManager.findFragmentByTag(getString(R.string.tab_title_3));
                if (rateFrgment != null) {
                    android.support.v4.app.FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
                    fragTransaction = getSupportFragmentManager().beginTransaction();
                    fragTransaction.detach(rateFrgment);
                    fragTransaction.attach(rateFrgment);
                    fragTransaction.commit();
                }
            }
        }
    }
    public JSONArray getArray() {
        return array;
    }

    public void setArray(JSONArray array) {
        this.array = array;
    }

    private View addTab(String label, int drawableId, int tabNumber) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_view, null);
        FragmentTabHost.TabSpec spec = tabHost.newTabSpec(label);
        ImageView iv = (ImageView) view.findViewById(R.id.tabIcon);
        iv.setImageResource(drawableId);
        TextView tv = (TextView) view.findViewById(R.id.tabText);
        tv.setText(label);
        spec.setIndicator(view);
        if(tabNumber==1)
            tabHost.addTab(spec, BillFragment.class, null);
        else if(tabNumber==2 || tabNumber==3)
            tabHost.addTab(spec, IncomeFragment.class, null);
        return view;
    }

    private void findLastId(){
        readJsonObject(BillFragment.BILLFILE);
        if(array!=null){
            int maxID =0;
            try {
                    JSONObject item = array.getJSONObject(array.length()-1);
                    Gson gson = new Gson();
                    Bill bill = (Bill) gson.fromJson(item.toString(), Bill.class);
                    Bill.setIncID(bill.getId());
            }catch (JSONException e){
                Log.e(TAG, "findLastId "+e.getMessage());
            }
        }
        readJsonObject(IncomeFragment.INCOMEFILE);
        if(array!=null){
            int maxID =0;
            try {
                JSONObject item = array.getJSONObject(array.length()-1);
                Gson gson = new Gson();
                IncomeCategory income = (IncomeCategory) gson.fromJson(item.toString(), IncomeCategory.class);
                IncomeCategory.setIncID(income.getId());
            }catch (JSONException e){
                Log.e(TAG, "findLastId "+e.getMessage());
            }
        }
        readJsonObject(IncomeFragment.RATEFILE);
        if(array!=null){
            int maxID =0;
            try {
                JSONObject item = array.getJSONObject(array.length()-1);
                Gson gson = new Gson();
                RateCategory income = (RateCategory) gson.fromJson(item.toString(), RateCategory.class);
                RateCategory.setIncID(income.getId());
            }catch (JSONException e){
                Log.e(TAG, "findLastId "+e.getMessage());
            }
        }
        readJsonObject(OperationActivity.OPERFILE);
        if(array!=null){
            int maxID =0;
            try {
                JSONObject item = array.getJSONObject(array.length()-1);
                Gson gson = new Gson();
                Operation oper = (Operation) gson.fromJson(item.toString(), Operation.class);
                RateCategory.setIncID(oper.getId());
            }catch (JSONException e){
                Log.e(TAG, "findLastId "+e.getMessage());
            }
        }
    }
    public void showAlert() {

        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.quit)
                .setMessage(R.string.really_quit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Stop the activity
                        finish();
                    }

                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
    public void showAboutDialog(){
        Drawable icon = ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_dialog_info).mutate();
        icon.setColorFilter(new ColorMatrixColorFilter(new float[]{
                0.5f, 0, 0, 0, 0,
                0, 0.5f, 0, 0, 0,
                0, 0, 0, 0.5f, 0,
                0, 0, 0, 1, 0,
        }));
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setIcon(icon)
                .setTitle(R.string.action_about)
                .setMessage(R.string.aboutText)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    private void showSearchDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View searchDialogView = factory.inflate(
                R.layout.search_dialog, null);
        final AlertDialog.Builder searchDialog = new AlertDialog.Builder(this);
        searchDialog.setView(searchDialogView)
                .setTitle(R.string.search_title)
                .setMessage(R.string.search_message)
                .setIcon(android.R.drawable.ic_menu_search)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText description = (EditText) searchDialogView.findViewById(R.id.searchdescET);
                        String desc = description.getText().toString();
                        startOperSearcActivity(desc);
                    }
                });
        searchDialog.show();
    }

    private void startOperSearcActivity(String desc){
        Intent intent = new Intent(this, OperationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(getString(R.string.description), desc);
        startActivity(intent);
    }

    private void showAddBillDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View billDialogView = factory.inflate(
                R.layout.add_bill_dialog, null);
        final AlertDialog.Builder billDialog = new AlertDialog.Builder(this);
        billDialog.setView(billDialogView)
                .setTitle(R.string.dialog_title)
                .setIcon(R.drawable.ic_add)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText billname = (EditText) billDialogView.findViewById(R.id.billnameET);
                        EditText balance = (EditText) billDialogView.findViewById(R.id.balanceET);
                        EditText description = (EditText) billDialogView.findViewById(R.id.descriptionET);
                        String desc = description.getText().toString();
                        createAndWriteObject(new Bill(billname.getText().toString(),
                                description.getText().toString(),
                                Double.valueOf(balance.getText().toString())), BillFragment.BILLFILE, null);
                    }
                });

        billDialog.show();
    }

    private void showCategoryDialog(final boolean isIncome){
        LayoutInflater factory = LayoutInflater.from(this);
        final View catDialogView = factory.inflate(
                R.layout.add_category_dialog, null);
        final AlertDialog.Builder catDialog = new AlertDialog.Builder(this);
        if(isIncome)
            catDialog.setTitle(R.string.category_incometitle);
        else
            catDialog.setTitle(R.string.category_ratetitle);
        catDialog.setView(catDialogView)

                .setIcon(R.drawable.ic_add)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText catname = (EditText) catDialogView.findViewById(R.id.categorynameET);
                        if(isIncome)
                            createAndWriteObject(new IncomeCategory(catname.getText().toString()), IncomeFragment.INCOMEFILE, null);
                        else
                            createAndWriteObject(new RateCategory(catname.getText().toString()), IncomeFragment.RATEFILE, null);
                    }
                });

        catDialog.show();
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
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            switch (filename) {
                case BillFragment.BILLFILE: {
                    BillFragment billFrgment = (BillFragment) fragmentManager.findFragmentByTag(getString(R.string.tab_title_1));
                    if(billFrgment!=null)
                        billFrgment.fillListView();
                } break;
                case IncomeFragment.INCOMEFILE: {
                    IncomeFragment incomeFragment = (IncomeFragment) fragmentManager.findFragmentByTag(getString(R.string.tab_title_2));
                    if(incomeFragment!=null)
                        incomeFragment.fillListView();
                } break;
                case IncomeFragment.RATEFILE:{
                    IncomeFragment incomeFragment = (IncomeFragment) fragmentManager.findFragmentByTag(getString(R.string.tab_title_3));
                    if(incomeFragment!=null)
                        incomeFragment.fillListView();
                } break;
            }
        } catch (JSONException ex){
            Log.e(TAG, "onClick "+ex.getMessage());
        }

    }

    public BillFragment getBillFragment(){
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        return (BillFragment) fragmentManager.findFragmentByTag(getString(R.string.tab_title_1));
    }
    public boolean checkCategory(){
        boolean result = true;
        array=null;
        readJsonObject(IncomeFragment.INCOMEFILE);
        if(array==null){
            result=false;
        } else if(array.length()==0)
            result = false;
        array=null;
        readJsonObject(IncomeFragment.RATEFILE);
        if(array==null){
            result=false;
        } else if(array.length()==0)
            result = false;

        if(!result)
            Toast.makeText(MainActivity.this, R.string.add_oper_error_message, Toast.LENGTH_SHORT).show();
        return result;
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
    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri){}

}
