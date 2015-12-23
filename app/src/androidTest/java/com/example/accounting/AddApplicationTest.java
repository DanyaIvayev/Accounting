package com.example.accounting;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.RenamingDelegatingContext;
import android.test.UiThreadTest;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.accounting.Entity.Bill;
import com.example.accounting.Entity.Operation;
import com.example.accounting.MainActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Дамир on 22.12.2015.
 */
@RunWith(AndroidJUnit4.class)
public class AddApplicationTest extends ActivityInstrumentationTestCase2<AddOperationActivity> {
    AddOperationActivity mActivity;
    EditText valueTV;
    EditText operDesc;
    Button okButton;
    Bill first;
    private RenamingDelegatingContext context = null;
    public AddApplicationTest() {
        super(AddOperationActivity.class);
    }

    @Before
    public void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        Intent addEvent = new Intent();
        addEvent.setClassName("com.example.accounting", "com.example.accounting.AddOperationActivity");
        addEvent.putExtra("index", 0);
        setActivityIntent(addEvent);
        mActivity = getActivity();
        valueTV = (EditText) mActivity.findViewById(R.id.operValueET);
        operDesc = (EditText) mActivity.findViewById(R.id.operDescriptionET);
        okButton = (Button) mActivity.findViewById(R.id.okButton);
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                valueTV.setText(String.valueOf(1350.56));
                operDesc.setText("Описание");
            }
        });
        getInstrumentation().waitForIdleSync();
    }

    @Test
    @UiThreadTest
    public void testAddOperation(){
        assertNotNull(mActivity);
        first = mActivity.getFrom(); // Получаем первоначальный объект счета
        double firstbalance = first.getBalance();
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                okButton.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();
        mActivity.readJsonObject(BillFragment.BILLFILE);
        JSONArray array = mActivity.getArray();
        Bill second = mActivity.getFrom();
        assertNotNull(second);
        Operation operation = mActivity.getOperation();
        assertEquals(Math.abs(second.getBalance()-firstbalance), operation.getValue(), 0.01); //
        mActivity.readJsonObject(OperationActivity.OPERFILE);
        JSONArray operarray = mActivity.getArray();
        boolean check = false;
        try {
            for (int i = 0; i < operarray.length(); i++) {
                JSONObject item = operarray.getJSONObject(i);
                Gson gson = new Gson();
                Operation oper = (Operation) gson.fromJson(item.toString(), Operation.class);
                if(oper.equals(operation))
                    check=true;
            }

        } catch (JSONException ex){
            Log.e("AddApplicationTest", "onTestAddOperation " + ex.getMessage());
        }
        assertTrue("check is false", check);
    }

    @After
    public void tearDown() throws Exception{
        super.tearDown();
    }
}
