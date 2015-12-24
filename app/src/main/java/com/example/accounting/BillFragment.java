package com.example.accounting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.accounting.Entity.Bill;
import com.example.accounting.Entity.Operation;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дамир on 17.12.2015.
 */
public class BillFragment extends Fragment {
    ListView mListView;
    private OnFragmentInteractionListener mListener;
    private final String TAG = "BillFragment";
    public static final String BILLFILE = "bill.json";
    ArrayList<BillItem> billList;
    List<String> list;
    String selectedBillTo;
    int idFrom;
    int idTo;
    public BillFragment() {
        // TODO Auto-generated constructor stub
    }

    public class BillAdapter extends ArrayAdapter<BillItem> {
        private ArrayList<BillItem> items;
        private BillViewHolder billHolder;

        private class BillViewHolder {
            TextView billName;
            TextView balance;
            //RelativeLayout listItem;
            LinearLayout mainView;
        }

        public BillAdapter(Context context, int tvResId, ArrayList<BillItem> items) {
            super(context, tvResId, items);
            this.items = items;
        }

        @Override
        public View getView(final int pos, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.list_item, null);
                billHolder = new BillViewHolder();
                billHolder.mainView = (LinearLayout) view.findViewById(R.id.mainview);
                //billHolder.listItem = (RelativeLayout) view.findViewById(R.id.listitem);
                billHolder.billName = (TextView) view.findViewById(R.id.billNameTV);
                billHolder.balance = (TextView) view.findViewById(R.id.value);
                view.setTag(billHolder);
            } else billHolder = (BillViewHolder) view.getTag();

            BillItem bill = items.get(pos);

            if (bill != null) {
                billHolder.billName.setText(bill.getBillName());
                billHolder.balance.setText(String.valueOf(bill.getBalance()));
            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) billHolder.mainView.getLayoutParams();
            params.rightMargin = 0;
            params.leftMargin = 0;
            billHolder.mainView.setLayoutParams(params);

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
            popupMenu.inflate(R.menu.menu_context);
            popupMenu
                    .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete: {
                                    BillItem ai = getItem(pos);
                                    idFrom=ai.getId();
                                    showDeleteBillDialog(idFrom, pos);
                                    Log.d(TAG, "onMenuItemClick delete" + ai.getId());
                                    return true;
                                }
                                case R.id.descript: {
                                    BillItem ai = getItem(pos);
                                    startDescriptionActivity(ai.getDescription());
                                    Log.d(TAG, "onMenuItemClick descript" + ai.getId());
                                    return true;
                                }
                                case R.id.add_operat: {
                                    BillItem ai = getItem(pos);
                                    MainActivity ma = (MainActivity) getActivity();
                                    boolean check = ma.checkCategory();
                                    if(check) {
                                        Intent intent = new Intent(getActivity(), AddOperationActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra(getString(R.string.index), pos);
                                        getActivity().startActivity(intent);
                                        Log.d(TAG, "onMenuItemClick add_operat" + ai.getId());
                                    }
                                    return true;
                                }
                                case R.id.look_operat: {
                                    BillItem ai = getItem(pos);
                                    Intent intent = new Intent(getActivity(), OperationActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra(getString(R.string.id), ai.getId());
                                    intent.putExtra(getString(R.string.billname), ai.getBillName());
                                    getActivity().startActivity(intent);
                                    Log.d(TAG, "onMenuItemClick look_operat" + ai.getId());
                                }
                            }
                            return true;
                        }
                    });

            popupMenu.show();
        }

    }

    private void startDescriptionActivity(String description) {
        Intent intent;
        Activity currentActivity = getActivity();
        intent = new Intent(currentActivity, DescriptionActivity.class);
        intent.putExtra(getString(R.string.description), description);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        currentActivity.startActivity(intent);
    }

    private void showDeleteBillDialog(int selectedID, final int pos){
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View billDialogView = factory.inflate(
                R.layout.delete_bill_dialog, null);
        Spinner billToSpinner = (Spinner) billDialogView.findViewById(R.id.spinner);
        final int code = settingSpinner(billToSpinner, selectedID);
        if(code==1){
            final AlertDialog.Builder billDialog = new AlertDialog.Builder(getContext());
            billDialog.setTitle(R.string.bill_delete_title)
                    .setMessage(R.string.bill_delete_message)
                    .setView(billDialogView)
                    .setIcon(R.drawable.ic_add)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                deleteBill(pos);
                                fillListView();
                        }
                    });

            billDialog.show();
        } else if(code==-2){
            MainActivity ma = (MainActivity) getActivity();
            ma.deleteJSONObject(pos, BILLFILE);
        }
    }

    public void deleteBill(int pos){
        transferAllOperations(idFrom, idTo);
        MainActivity ma = (MainActivity) getActivity();
        ma.deleteJSONObject(pos, BILLFILE);

    }



    private int settingSpinner(final Spinner billToSpinner, int id){
        list = new ArrayList<String>();
        ArrayAdapter<String> adapter=null;
        if(billList!=null){
            fillBillList(id);
            if(list.isEmpty()) {
                BillItem bill = billList.get(0);
                int count = checkOper(bill.getId());
                if (count==0){
                    return -2;
                }
                else if(count>0){
                    Toast.makeText(getContext(), R.string.bill_delete_req, Toast.LENGTH_SHORT).show();
                    return -1;}
                else if(count<0)
                    return -1;

            } else {
                adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                billToSpinner.setAdapter(adapter);
                billToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedBillTo = (String) billToSpinner.getSelectedItem();
                        for(BillItem bi : billList){
                            if(bi.getBillName().equals(selectedBillTo))
                                idTo = bi.getId();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                return 1;
            }
        }
        return -1;
    }

    private int checkOper(int idFrom){
        MainActivity ma = (MainActivity) getActivity();
        ma.readJsonObject(OperationActivity.OPERFILE);
        JSONArray array = ma.getArray();
        int count=0;
        try{
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                Gson gson = new Gson();
                Operation oper = (Operation) gson.fromJson(item.toString(), Operation.class);
                if (oper.getBillFrom() == idFrom) {
                    count++;
                }
            }
        } catch (JSONException e){
            Log.e(TAG, "checkOper "+e.getMessage());
        } finally {
            return count;
        }
    }

    public void fillBillList(int id){
        if(billList!=null) {
            for (BillItem bi : billList) {
                if (bi.getId() != id)
                    list.add(bi.getBillName());
            }
        }
    }

    private void transferAllOperations(int idFrom, int idTo){
        MainActivity ma = (MainActivity) getActivity();
        ma.setArray(null);
        ma.readJsonObject(BillFragment.BILLFILE);
        JSONArray billarray = ma.getArray();
        Bill to=null;
        try {
            for (int j = 0; j < billarray.length(); j++) {
                JSONObject item = billarray.getJSONObject(j);
                Gson gson = new Gson();
                Bill bill = (Bill) gson.fromJson(item.toString(), Bill.class);
                if (bill.getId()==idTo) {
                    to = bill;
                }
            }
            int index = findIndex(idTo);
            ma.setArray(null);
            ma.readJsonObject(OperationActivity.OPERFILE);
            JSONArray array = ma.getArray();
            for (int j = 0; j < array.length(); j++) {
                JSONObject item = array.getJSONObject(j);
                Gson gson = new Gson();
                Operation oper = (Operation) gson.fromJson(item.toString(), Operation.class);
                if(oper.getBillFrom()==idFrom){
                    int type = oper.getType();
                    switch (type){
                        case Operation.INCOME:{
                            to.setBalance(to.getBalance()+oper.getValue());
                        } break;
                        case Operation.RATE:{
                            to.setBalance(to.getBalance()-oper.getValue());
                        } break;
                        case Operation.TRANSFER:{
                            int id = oper.getBillTo();
                            if(idTo!=id){
                                to.setBalance(to.getBalance()-oper.getValue());
                            }
                        }
                    }
                    oper.setBillFrom(idTo);
                    ma.createAndWriteObject(oper, OperationActivity.OPERFILE, j);
                }
            }
            ma.createAndWriteObject(to, BILLFILE, index);
        } catch (JSONException ex) {
            Log.e(TAG, "onCreateView " + ex.getMessage());
        }
    }

    private int findIndex(int idTo){
        int index=-1;
        if(billList!=null){
            int i=0;
            while(i<billList.size()){
                if(((BillItem)billList.get(i)).getId()==idTo){
                    index=i;
                    i=billList.size();
                }
                i++;
            }
        }
        return index;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bill, null);
        mListView = (ListView) v.findViewById(R.id.billListView);
        mListView.setAdapter(null);

        ((MainActivity)getActivity()).setArray(null);
        fillListView();
        return v;
    }
    @Override
    public void onResume(){
        super.onResume();

    }

    public ArrayList<BillItem> getBillList() {
        return billList;
    }

    public List<String> getList() {
        return list;
    }

    public void setIdFrom(int idFrom) {
        this.idFrom = idFrom;
    }

    public void setIdTo(int idTo) {
        this.idTo = idTo;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void fillListView(){
        MainActivity ma = (MainActivity) getActivity();
        billList = new ArrayList<BillItem>();
        JSONArray array = ma.getArray();
        if(array==null)
            ma.readJsonObject(BILLFILE);
        array = ma.getArray();
        if (array != null) {
            try {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.getJSONObject(i);
                    Gson gson = new Gson();
                    Bill bill = (Bill)gson.fromJson(item.toString(), Bill.class);
                    billList.add(new BillItem(bill.getId(), bill.getBillName(), bill.getDescription(), bill.getBalance()));
                }
                mListView.setAdapter(new BillAdapter(getContext(), R.layout.list_item, billList));
            } catch (JSONException ex){
                Log.e(TAG, "onCreateView "+ex.getMessage());
            }

        }
    }

//    public int findBillToIndex(int id){
//        if(billList!=null){
//            int index= -1;
//            int i=0;
//            while(i<billList.size()){
//                if(((BillItem)billList.get(i)).getId()==id){
//                    index=i;
//                    i=billList.size();
//                }
//                i++;
//            }
//            return index;
//
//        } else
//            return -1;
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    class BillItem {
        private int id; // id записи

        private String billName; // название счета
        private String description; // описание
        private double balance;

        public BillItem(int id, String billName, String description, double balance) {
            this.id = id;
            this.billName = billName;
            this.description = description;
            this.balance = balance;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getBillName() {
            return billName;
        }

        public void setBillName(String billName) {
            this.billName = billName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
