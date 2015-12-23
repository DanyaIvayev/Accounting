package com.example.accounting;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.accounting.Entity.Bill;
import com.example.accounting.Entity.Operation;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

/**
 * Created by Дамир on 23.12.2015.
 */
@RunWith(AndroidJUnit4.class)
public class DeleteOperationTest extends ActivityInstrumentationTestCase2<OperationActivity> {
    OperationActivity mActivity;

    ArrayList<OperationActivity.OperItem> items;
    public DeleteOperationTest() {
        super(OperationActivity.class);
    }
    @Before
    public void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        Intent addEvent = new Intent();
        addEvent.setClassName("com.example.accounting", "com.example.accounting.OperationActivity");
        addEvent.putExtra("index", 0);
        setActivityIntent(addEvent);
        mActivity = getActivity();
    }

    @Test
    @UiThreadTest
    public void testDeleteOperation() {
        assertNotNull(mActivity);
        items = mActivity.getOperList();
        OperationActivity.OperItem delete = items.get(items.size() - 1);
        mActivity.deleteJSONObject(delete.getId(), OperationActivity.OPERFILE);
        Bill first = mActivity.getBillFro();
        double balance = first.getBalance();
        mActivity.restoreBalance();
        first=mActivity.getBillFro();
        double afterBalance = first.getBalance();
        assertEquals(balance-afterBalance, delete.getValue(), 0.01);
        mActivity.readJsonObject(OperationActivity.OPERFILE);
        JSONArray operarray = mActivity.getArray();
        boolean check = true;
        try {
            for (int i = 0; i < operarray.length(); i++) {
                JSONObject item = operarray.getJSONObject(i);
                Gson gson = new Gson();
                Operation oper = (Operation) gson.fromJson(item.toString(), Operation.class);
                if(oper.getId()==delete.getId())
                    check=false;
            }

        } catch (JSONException ex){
            Log.e("AddApplicationTest", "onTestAddOperation " + ex.getMessage());
        }
        assertTrue("Operation haven't been deleted", check);
    }

    @After
    public void tearDown() throws Exception{
        super.tearDown();
    }
}
