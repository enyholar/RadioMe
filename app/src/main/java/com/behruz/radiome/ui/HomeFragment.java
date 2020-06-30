package com.behruz.radiome.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.behruz.radiome.R;
import com.behruz.radiome.adapter.RadioListAdapter;
import com.behruz.radiome.databinding.HomeFragmentBinding;
import com.behruz.radiome.model.Radio;
import com.behruz.radiome.utils.Constants;
import com.behruz.radiome.utils.PreferenUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private ItemClickListenter mItemClickListener;
    private HomeFragmentBinding binding;
    private List<Radio> inspirationalRadioList = new ArrayList<>();
    private List<Radio> gospelRadioList = new ArrayList<>();
    private List<Radio> fmRadioList = new ArrayList<>();
    private List<Radio> entertainmentRadioList = new ArrayList<>();
    private List<Radio> mRadioList = new ArrayList<>();
    private RadioListAdapter inspAdapter;
    private RadioListAdapter entertainAdapter;
    private RadioListAdapter fmAdapter;
    private RadioListAdapter gospelAdapter;
    private LinearLayoutManager mLayoutManager;
    private PreferenUtil preferenUtil;

    public HomeFragment(ItemClickListenter listenter) {
        // Required empty public constructor
        this.mItemClickListener = listenter;
    }

    public static HomeFragment newInstance(ItemClickListenter listenter) {
        HomeFragment fragment = new HomeFragment(listenter);
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.home_fragment, container, false);
        preferenUtil = PreferenUtil.getInstant(getContext());
        binding.buttonChristain.setOnClickListener(this);
        binding.buttonEntertainment.setOnClickListener(this);
        binding.buttonFmRadio.setOnClickListener(this);
        binding.buttonInspirational.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpAdapter();
        getRadioList();
    }

    private void setUpAdapter(){
        entertainAdapter = new RadioListAdapter(getContext(), new RadioListAdapter.ClickListner() {
            @Override
            public void onItemClick(Radio model, int position) {

                if (mItemClickListener != null) {
                    if (entertainmentRadioList != null && entertainmentRadioList.size() > 0) {
                        Gson gson = new Gson();
                        String jsonRadio = gson.toJson(entertainmentRadioList);
                        preferenUtil.saveAllRadioList(jsonRadio);

                    }
                    mItemClickListener.onItemClick(model);
                }
            }
        });

        inspAdapter = new RadioListAdapter(getContext(), new RadioListAdapter.ClickListner() {
            @Override
            public void onItemClick(Radio model, int position) {
                if (mItemClickListener != null) {
                    if (inspirationalRadioList != null && inspirationalRadioList.size() > 0) {
                        Gson gson = new Gson();
                        String jsonRadio = gson.toJson(inspirationalRadioList);
                        preferenUtil.saveAllRadioList(jsonRadio);

                    }
                    mItemClickListener.onItemClick(model);
                }
            }
        });

        fmAdapter = new RadioListAdapter(getContext(), new RadioListAdapter.ClickListner() {
            @Override
            public void onItemClick(Radio model, int position) {
                if (mItemClickListener != null) {
                    if (fmRadioList != null && fmRadioList.size() > 0) {
                        Gson gson = new Gson();
                        String jsonRadio = gson.toJson(fmRadioList);
                        preferenUtil.saveAllRadioList(jsonRadio);

                    }
                    mItemClickListener.onItemClick(model);
                }
            }
        });

        gospelAdapter = new RadioListAdapter(getContext(), new RadioListAdapter.ClickListner() {
            @Override
            public void onItemClick(Radio model, int position) {
                if (mItemClickListener != null) {
                    if (gospelRadioList != null && gospelRadioList.size() > 0) {
                        Gson gson = new Gson();
                        String jsonRadio = gson.toJson(gospelRadioList);
                        preferenUtil.saveAllRadioList(jsonRadio);

                    }
                    mItemClickListener.onItemClick(model);
                }
            }
        });

        LinearLayoutManager  mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager  mLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager  mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager  mLayoutManager3 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.recyclerViewChristainFaith.setLayoutManager(mLayoutManager);
        binding.recyclerViewEntertainment.setLayoutManager(mLayoutManager1);
        binding.recyclerViewFmRadio.setLayoutManager(mLayoutManager2);
        binding.recyclerViewInspirational.setLayoutManager(mLayoutManager3);
        binding.recyclerViewInspirational.setAdapter(inspAdapter);
        binding.recyclerViewFmRadio.setAdapter(fmAdapter);
        binding.recyclerViewEntertainment.setAdapter(entertainAdapter);
        binding.recyclerViewChristainFaith.setAdapter(gospelAdapter);


    }

    private void getRadioList(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("RadioList");
        myRef.addValueEventListener(new ValueEventListener() {
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

                    sortRadioBasedOnCategory(mRadioList);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void sortRadioBasedOnCategory(List<Radio> radioList){
        for (int i = 0; i < radioList.size(); i++) {
            Radio radio = radioList.get(i);
            if (radio.getCategory().equals("Entertainment Radio")){
                entertainmentRadioList.add(radio);
            } else if (radio.getCategory().equals("Inspirational Radio")){
                inspirationalRadioList.add(radio);
            }else if (radio.getCategory().equals("Christain Faith Radio")){
                gospelRadioList.add(radio);
            }else if (radio.getCategory().equals("FM Radio")){
                fmRadioList.add(radio);
            }
        }
        inspAdapter.addAll(inspirationalRadioList);
        entertainAdapter.addAll(entertainmentRadioList);
        gospelAdapter.addAll(gospelRadioList);
        fmAdapter.addAll(fmRadioList);
    }

    public void gotoArtistAlbumList (Context context, String catgory ) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.ACTION.NAVIGATE_RADIO_LIST);
        intent.putExtra("category",catgory);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_entertainment:
                gotoArtistAlbumList(getContext(),"Entertainment Radio");
                break;

            case R.id.button_christain:
                gotoArtistAlbumList(getContext(),"Christain Faith Radio");

                break;

            case R.id.button_inspirational:
                gotoArtistAlbumList(getContext(),"Inspirational Radio");

                break;

            case R.id.button_fm_radio:
                gotoArtistAlbumList(getContext(),"FM Radio");

                break;
        }
    }


    public interface ItemClickListenter {

        void onItemClick(Radio radio);
    }
}