package com.example.accounting;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;

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
public class DeletePBillFragmentTest  extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity mActivity;
    BillFragment billFragment;
    String deletedBillName;
    String deletedDescription;
    double deletedBalance;
    public DeletePBillFragmentTest(){
        super(MainActivity.class);
    }
    @Before
    public void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
        billFragment = mActivity.getBillFragment();

    }
    //@Test
    //@UiThreadTest
    public void testDeleteOperation() {
        assertNotNull(mActivity);
        assertNotNull(billFragment);
        ArrayList<BillFragment.BillItem> billItems = billFragment.getBillList();
        if(billItems.size()>=2) {
            BillFragment.BillItem first = billItems.get(0);
            int idFrom = first.getId(); //удаляем с нулевой
            deletedBillName=first.getBillName();
            deletedBalance=first.getBalance();
            deletedDescription=first.getDescription();
            int idTo = billItems.get(1).getId(); // переносим в первый
            billFragment.setIdFrom(idFrom);
            billFragment.setIdTo(idTo);
            double startBalance = billItems.get(1).getBalance();
            mActivity.readJsonObject(OperationActivity.OPERFILE);
            JSONArray array = mActivity.getArray();
            double changeBalance=0.0;

            if(array!=null) {
                try{
                    ArrayList<Operation> operations = new ArrayList<Operation>();
                for (int j = 0; j < array.length(); j++) {
                    JSONObject item = array.getJSONObject(j);
                    Gson gson = new Gson();
                    Operation oper = (Operation) gson.fromJson(item.toString(), Operation.class);
                    if (oper.getBillFrom() == idFrom) {
                        operations.add(oper);
                        if (oper.getType() == Operation.INCOME) // если приход то счет на который мы переводим увеличится
                            changeBalance += oper.getValue();
                        else if (oper.getType() == Operation.RATE) // если перевод или расход то уменьшится
                            changeBalance -= oper.getValue();
                        else if (oper.getType() == Operation.TRANSFER)
                            if(oper.getBillTo()!=idTo)
                                changeBalance -= oper.getValue();
                    }
                }
                   // assertFalse("Операций нет", operations.size() == 0);

                        mActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                billFragment.deleteBill(0); // удаляем с нулевой
                            }
                        });
                        getInstrumentation().waitForIdleSync();
                        billItems = billFragment.getBillList();
                        double endBalance = billItems.get(1).getBalance();
                        assertEquals(endBalance - startBalance, changeBalance, 0.01); // изменился ли баланс?
                        mActivity.readJsonObject(BillFragment.BILLFILE);
                        array = mActivity.getArray();
                        boolean check = true;
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject item = array.getJSONObject(i);
                                Gson gson = new Gson();
                                Bill bill = (Bill) gson.fromJson(item.toString(), Bill.class);
                                if (bill.getId() == idFrom)
                                    check = false;

                            }
                        }
                        assertTrue("Check is false", check); // Удалено?

                } catch (JSONException e){
                    Log.e("DeletePBillFragmentTest", "testDeleteOperation "+e.getMessage());
                }
            }

        }

    }
    @After
    public void tearDown() throws Exception{
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mActivity.createAndWriteObject(new Bill(deletedBillName, deletedDescription, deletedBalance), BillFragment.BILLFILE, null);
            }
        });
        super.tearDown();

    }
}
