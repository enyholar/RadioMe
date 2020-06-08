package com.behruz.radiome.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.behruz.radiome.R;
import com.behruz.radiome.databinding.ItemRadioBinding;
import com.behruz.radiome.model.Radio;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gideon on 27/08/19.
 */

public class RadioListAdapter extends RecyclerView.Adapter<RadioListAdapter.MovieViewHolder> {

    private List<Radio> radioList;
    private Context context;
    int selected_position = -1;
    private Activity acttivitys;
    private ClickListner clickListner;

    public RadioListAdapter(Context context,  ClickListner listner) {
        this.context = context;
        radioList = new ArrayList<>();
        this.clickListner = listner;
    }

    private void add(Radio item) {
        radioList.add(item);
        notifyItemInserted(radioList.size() - 1);
    }

    public void addAll(List<Radio> movieDatas) {
        for (Radio movieData : movieDatas) {
            add(movieData);
        }
    }

    public void remove(Radio item) {
        int position = radioList.indexOf(item);
        if (position > -1) {
            radioList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public Radio getItem(int position) {
        return radioList.get(position);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemRadioBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_radio, parent, false);

        final MovieViewHolder movieViewHolder = new MovieViewHolder(binding);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        final Radio model = radioList.get(position);
        holder.bind(model,position);

    }

    @Override
    public int getItemCount() {
        return radioList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {


        private ItemRadioBinding binding;
        public MovieViewHolder( ItemRadioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Radio model, int pos) {
            binding.txtRadio.setText(model.getRadioName());
            Glide.with(context)
                    .load(model.getRadioImage())
                    .error(context.getResources().getDrawable(R.drawable.ic_launcher_background))
                    .into(binding.radioArt);

            binding.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListner.onItemClick(model,pos);
                }
            });
        }


    }

    public interface ClickListner {
        void onItemClick(Radio model, int position);
    }

}
