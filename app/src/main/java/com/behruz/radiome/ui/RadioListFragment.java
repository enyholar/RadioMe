package com.behruz.radiome.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.behruz.radiome.R;
import com.behruz.radiome.adapter.RadioListAdapter;
import com.behruz.radiome.databinding.HomeFragmentBinding;
import com.behruz.radiome.databinding.RadioListFragmentBinding;
import com.behruz.radiome.model.Radio;
import com.behruz.radiome.utils.PreferenUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

public class RadioListFragment extends Fragment {
    private ItemClickListenter mItemClickListener;
    private RadioListFragmentBinding binding;
    private List<Radio> radioArrayList = new ArrayList<>();
    private List<Radio> mRadioList = new ArrayList<>();
    private RadioListAdapter adapter;
    private PreferenUtil preferenUtil;
    private String category;

    public RadioListFragment(ItemClickListenter listenter) {
        // Required empty public constructor
        this.mItemClickListener = listenter;
    }

    public static RadioListFragment newInstance(String categoryName, ItemClickListenter listenter) {
        RadioListFragment fragment = new RadioListFragment(listenter);
        Bundle args = new Bundle();
        args.putString("category", categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString("category");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.radio_list_fragment, container, false);
        preferenUtil = PreferenUtil.getInstant(getContext());
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
  //      ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(category);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpAdapter();
        getRadioList(category);
    }

    private void setUpAdapter(){
        adapter = new RadioListAdapter(getContext(), new RadioListAdapter.ClickListner() {
            @Override
            public void onItemClick(Radio model, int position) {

                if (mItemClickListener != null) {
                    if (radioArrayList != null && radioArrayList.size() > 0) {
                        Gson gson = new Gson();
                        String jsonRadio = gson.toJson(radioArrayList);
                        preferenUtil.saveAllRadioList(jsonRadio);

                    }
                    mItemClickListener.onItemClick(model);
                }
            }
        });




        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.recyclerView.setLayoutManager(gridLayoutManager);
       // binding.recyclerView.addItemDecoration(new GridMarginDecoration(getContext(), 1, 1, 1, 1));

        binding.recyclerView.setAdapter(adapter);


    }

    private void getRadioList(String category){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
      //  DatabaseReference myRef = database.getReference("RadioList");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("RadioList").orderByChild("category").equalTo(category);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    if (mRadioList != null && mRadioList.size() > 0) {
                        mRadioList.clear();
                    }
                    Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                    while (items.hasNext()) {
                        DataSnapshot item = items.next();
                        Radio radio = item.getValue(Radio.class);
                        mRadioList.add(radio);
                    }

                    adapter.addAll(mRadioList);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }



    public interface ItemClickListenter {

        void onItemClick(Radio radio);
    }
}