package com.example.queder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    public List<ClinicDetails> searchResultList;
    private OnNoteListener onNoteListener;

    public SearchAdapter(Context context, List<ClinicDetails> searchResultList, OnNoteListener onNoteListener) {
        this.context = context;
        this.searchResultList = searchResultList;
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.search_recycler, parent, false);
        return new ItemViewHolder(itemView, onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        ClinicDetails searchResult = searchResultList.get(position);

        itemViewHolder.SRclinicNameTV.setText(searchResult.getClinicName());
        itemViewHolder.SRaddressTV.setText(searchResult.getAddress());
        itemViewHolder.SRpriceTV.setText(searchResult.getPrice());
        itemViewHolder.SRratingTV.setText(searchResult.getRating());


        itemViewHolder.SRopenClosedTV.setText("Open Now");
        itemViewHolder.SRopenClosedTV.setTextColor(context.getColor(R.color.DarkGreen));
    }


    @Override
    public int getItemCount() {
        return searchResultList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView SRclinicNameTV, SRaddressTV, SRpriceTV, SRratingTV, SRqueueTV, SRopenClosedTV;
        OnNoteListener onNoteListener;

        public ItemViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            SRclinicNameTV = itemView.findViewById(R.id.SRclinicNameTV);
            SRaddressTV = itemView.findViewById(R.id.SRaddressTV);
            SRpriceTV = itemView.findViewById(R.id.SRpriceTV);
            SRratingTV = itemView.findViewById(R.id.SRratingTV);
            SRqueueTV = itemView.findViewById(R.id.SRqueueTV);
            SRopenClosedTV = itemView.findViewById(R.id.SRopenClosedTV);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAbsoluteAdapterPosition(), searchResultList);
        }


    }

    public interface OnNoteListener{
        void onNoteClick(int position, List<ClinicDetails> list);
    }

}
