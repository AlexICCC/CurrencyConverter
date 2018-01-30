package org.stalexman.currencyconvertor.converter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import org.stalexman.currencyconvertor.R;

import java.util.ArrayList;
import java.util.TreeMap;

public class DialogSearch extends DialogFragment {

    private static final String BUNDLE_CURRENCIES = "BUNDLE_CURRENCIES";
    private static final String BUNDLE_SENDER_ID = "BUNDLE_SENDER_ID";
    public static final String BROADCAST_ACTION = "org.stalexman.currencyconvertor.dialogsearch";
    public static final String BROADCAST_TAG_TEXT = "BROADCAST_TAG_TEXT";
    public static final String BROADCAST_TAG_SENDER_ID = "BROADCAST_TAG_SENDER_ID";

    private ListView listView;
    private SearchView searchView;
    private ArrayAdapter<String> adapter;

    private int mSenderId;
    private TreeMap<String, String> currencyNameToCharCode;
    private String [] nameArray;
    private String [] selectedArray;

    public static DialogFragment newInstance(int senderId, TreeMap<String, String> currencies) {
        DialogSearch f = new DialogSearch();
        Bundle b = new Bundle();
        b.putInt(BUNDLE_SENDER_ID, senderId);
        b.putSerializable(BUNDLE_CURRENCIES, currencies);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSenderId = getArguments().getInt(BUNDLE_SENDER_ID);
        currencyNameToCharCode = (TreeMap<String, String>) getArguments().getSerializable(BUNDLE_CURRENCIES);
        if (currencyNameToCharCode != null) {
            nameArray =  currencyNameToCharCode.keySet().toArray(new String [currencyNameToCharCode.size()]);
            selectedArray = nameArray;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_dialog, null);
        getDialog().setTitle(R.string.sd_title);
        searchView = (SearchView) rootView.findViewById(R.id.search_dialog_searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                executeSearch(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                executeSearch(query);
                return false;
            }
        });

        listView = (ListView) rootView.findViewById(R.id.search_dialog_listView);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, selectedArray);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                onItemSelected(i);
                dismiss();
            }
        });
        return rootView;
    }

    public void executeSearch(String query) {
        if (query == null || query.equals("")){
            selectedArray = nameArray;
            adapter.notifyDataSetChanged();
            return;
        }
        query = query.toLowerCase();
        ArrayList <String> searchList = new ArrayList<>();

        for (String name:nameArray) {
            if (name.toLowerCase().contains(query))
                searchList.add(name);
        }
        if (searchList.size() > 0){
            selectedArray = searchList.toArray(new String[searchList.size()]);
            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, selectedArray);
            listView.setAdapter(adapter);
        }
    }

    private void onItemSelected(int i){
        String name = selectedArray[i];
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(BROADCAST_TAG_TEXT, currencyNameToCharCode.get(name));
        intent.putExtra(BROADCAST_TAG_SENDER_ID, mSenderId);
        getActivity().sendBroadcast(intent);
    }


}
