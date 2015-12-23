package com.example.accounting;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.accounting.Entity.IncomeCategory;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Дамир on 18.12.2015.
 */
public class IncomeFragment extends Fragment {
    ListView mListView;
    private OnFragmentInteractionListener mListener;
    public static final String INCOMEFILE = "income.json";
    public static final String RATEFILE = "rate.json";
    private final String TAG = "IncomeFragment";
    ArrayList<IncomeItem> incomeList;
    public IncomeFragment() {
        // TODO Auto-generated constructor stub
    }
    public class IncomeAdapter extends ArrayAdapter<IncomeItem> {
        private ArrayList<IncomeItem> items;
        private IncomeViewHolder incomeHolder;

        private class IncomeViewHolder {
            TextView incomeName;
            LinearLayout mainView;
        }

        public IncomeAdapter(Context context, int tvResId, ArrayList<IncomeItem> items) {
            super(context, tvResId, items);
            this.items = items;
        }

        @Override
        public View getView(final int pos, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.income_list_item, null);
                incomeHolder = new IncomeViewHolder();
                incomeHolder.mainView = (LinearLayout) view.findViewById(R.id.mainincomeview);
                incomeHolder.incomeName = (TextView) view.findViewById(R.id.incomeNameTV);
                view.setTag(incomeHolder);
            } else incomeHolder = (IncomeViewHolder) view.getTag();

            IncomeItem bill = items.get(pos);

            if (bill != null) {
                incomeHolder.incomeName.setText(bill.getIncomeName());
            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) incomeHolder.mainView.getLayoutParams();
            params.rightMargin = 0;
            params.leftMargin = 0;
            incomeHolder.mainView.setLayoutParams(params);

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
            popupMenu.inflate(R.menu.menu_income_popup);
            popupMenu
                    .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_income_category: {
                                    IncomeItem ai = getItem(pos);
                                    deleteItem(pos);
                                    fillListView();
                                    Log.d(TAG, "onMenuItemClick delete"+ ai.getId());
                                    return true;
                                }
                            }
                            return true;
                        }
                    });

            popupMenu.show();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_income, null);

        mListView = (ListView) v.findViewById(android.R.id.list);
        mListView.setAdapter(null);

        ((MainActivity)getActivity()).setArray(null);
        fillListView();
//        for (int i=0; i<10; i++) {
//            incomeList.add(new IncomeItem(i, "Категория" + i));
//        }
//        mListView.setAdapter(new IncomeAdapter(getContext(), R.layout.income_list_item, incomeList));

        return v;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public void fillListView(){
        MainActivity ma = (MainActivity) getActivity();
        incomeList = new ArrayList<IncomeItem>();
        JSONArray array = ma.getArray();
        if(array==null) {
            if(this.getTag().equals(getString(R.string.tab_title_2)))
                ma.readJsonObject(INCOMEFILE);
            else
                ma.readJsonObject(RATEFILE);
        }
        array = ma.getArray();
        if (array != null) {
            try {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.getJSONObject(i);
                    Gson gson = new Gson();
                    IncomeCategory ic = (IncomeCategory)gson.fromJson(item.toString(), IncomeCategory.class);
                    incomeList.add(new IncomeItem(ic.getId(), ic.getIncomeName()));
                }
                mListView.setAdapter(new IncomeAdapter(getContext(), R.layout.list_item, incomeList));
            } catch (JSONException ex){
                Log.e(TAG, "onCreateView "+ex.getMessage());
            }

        }
    }
    public void deleteItem(int pos){
        MainActivity ma = (MainActivity) getActivity();
        if(this.getTag().equals(getString(R.string.tab_title_2)))
            ma.deleteJSONObject(pos, INCOMEFILE);
        else
            ma.deleteJSONObject(pos, RATEFILE);
    }
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

    class IncomeItem {
        private int id; // id записи

        private String incomeName; // название счета


        public IncomeItem(int id, String incomeName) {
            this.id = id;
            this.incomeName = incomeName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIncomeName() {
            return incomeName;
        }

        public void setIncomeName(String incomeName) {
            this.incomeName = incomeName;
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
