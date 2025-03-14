package com.example.queder;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class RecommendedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    public List<ClinicDetails> clinicDetailsList;
    private OnNoteListener onNoteListener1;

    public RecommendedRecyclerAdapter(Context context, List<ClinicDetails> clinicDetailsList, OnNoteListener onNoteListener1) {
        this.context = context;
        this.clinicDetailsList = clinicDetailsList;
        this.onNoteListener1 = onNoteListener1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.all_clinic_recycler, parent, false);
        return new ItemViewHolder(itemView, onNoteListener1);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        ClinicDetails clinicDetails = clinicDetailsList.get(position);

        itemViewHolder.ACRclinicNameTV.setText(String.valueOf(clinicDetails.getClinicName()));
        itemViewHolder.ACRpriceTV.setText(clinicDetails.getPrice());
        itemViewHolder.ACRratingTV.setText(clinicDetails.getRating());
        itemViewHolder.ACRtownTV.setText(clinicDetails.getTown());


        try {
            String queueDetails = clinicDetails.getQueueDetails();
            JSONArray queueDetailsArray = new JSONArray(queueDetails);

            String queueLength = String.valueOf(queueDetailsArray.length());

            SpannableString text = new SpannableString("Current in queue: " +
                    queueLength + " pax");

            int endNumber = queueLength.length() + 22;

            if (Integer.parseInt(queueLength) <= 10) {
                text.setSpan(new ForegroundColorSpan(
                        context.getColor(R.color.DarkGreen)), 18, endNumber , 0);
            } else if (Integer.parseInt(queueLength) <= 30) {
                text.setSpan(new ForegroundColorSpan(
                        context.getColor(R.color.SecondaryYellow)), 18, endNumber , 0);
            } else {
                text.setSpan(new ForegroundColorSpan(
                        context.getColor(R.color.BurgandyRed)), 18, endNumber , 0);
            }

            itemViewHolder.ACRqueueTV.setText(text, TextView.BufferType.SPANNABLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Picasso.get().load(clinicDetails.getImageUrl()).into(itemViewHolder.ACRimageIV);

    }


    @Override
    public int getItemCount() {
        return 3;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView ACRclinicNameTV, ACRpriceTV, ACRratingTV, ACRqueueTV, ACRtownTV;
        public ImageView ACRimageIV;
        OnNoteListener onNoteListener;

        public ItemViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            ACRclinicNameTV = itemView.findViewById(R.id.ACRclinicNameTV);
            ACRpriceTV = itemView.findViewById(R.id.ACRpriceTV);
            ACRratingTV = itemView.findViewById(R.id.ACRratingTV);
            ACRqueueTV = itemView.findViewById(R.id.ACRqueueTV);
            ACRtownTV = itemView.findViewById(R.id.ACRtownTV);
            ACRimageIV = itemView.findViewById(R.id.ACRimageIV);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAbsoluteAdapterPosition());

        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
