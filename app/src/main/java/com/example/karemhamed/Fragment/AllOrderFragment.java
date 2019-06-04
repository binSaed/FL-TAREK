/*
 * Created by AbdOo Saed from Egypt
 *  all Copyright(c) reserved  2019.
 */

package com.example.karemhamed.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.karemhamed.Adabter.AdabterOrder;
import com.example.karemhamed.AddOrderActivity;
import com.example.karemhamed.Model.ModelOrder;
import com.example.karemhamed.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllOrderFragment extends Fragment implements SearchView.OnQueryTextListener {
    private List<ModelOrder> orderArrayListAll;

    private RecyclerView recyclerV_AllOrder;
    private String strClintId;
    private FloatingActionButton fab_add_order;
    private DatabaseReference RDatabase;
    private FirebaseDatabase fDatabase;
    private AdabterOrder viewAdapter;

    public AllOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        strClintId = getArguments().getString("strClintId");
        fDatabase = FirebaseDatabase.getInstance();
        RDatabase = fDatabase.getReference("/clint/" + strClintId).child("Order");
        RDatabase.keepSynced(true);
        View view = inflater.inflate(R.layout.fragment_all_order, container, false);
        recyclerV_AllOrder = view.findViewById(R.id.recyclerV_AllOrder);
        fab_add_order = view.findViewById(R.id.fab_add_order);
        getDataAndPutInList();
        orderArrayListAll= new ArrayList<>();
        viewAdapter = new AdabterOrder(orderArrayListAll, getActivity(), strClintId, "AllOrder");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerV_AllOrder.setLayoutManager(layoutManager);
        recyclerV_AllOrder.setAdapter(viewAdapter);
        setHasOptionsMenu(true);
        fab_add_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), strClintId, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), AddOrderActivity.class);
                intent.putExtra("strClintId", strClintId);//strClintId

                startActivity(intent);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    private void getDataAndPutInList() {
        RDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderArrayListAll.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelOrder modelOrder = snapshot.getValue(ModelOrder.class);
                    if (!modelOrder.getOrderStatus()) {
                        orderArrayListAll.add(modelOrder);
                    }


                }
                Collections.reverse(orderArrayListAll);
                viewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");

//        super.onCreateOptionsMenu(menu, inflater);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
//        filter(s.trim());
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        if (!s.isEmpty()) {
            filter(s.trim());
        } else {
            getDataAndPutInList();
        }
        return false;
    }

    private void filter(String text) {
        List<ModelOrder> filteredList = new ArrayList<>();
        for (ModelOrder item : orderArrayListAll) {
            if (item.getStrOrderName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        viewAdapter.filterList(filteredList);
    }
}
