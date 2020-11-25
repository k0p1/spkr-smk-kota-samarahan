package com.example.spkr;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;


    public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<LaptopCheckOutInfo> laptopCheckOutInfoList;
    private List<LaptopCheckOutInfo> laptopCheckOutInfoListFiltered;
    private RecordAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView laptopID, serialNo;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            laptopID = view.findViewById(R.id.tv_laptopID);
            serialNo = view.findViewById(R.id.tv_laptopSerialNo);
            thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onRecordSelected(laptopCheckOutInfoListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public RecordAdapter(Context context, List<LaptopCheckOutInfo> laptopCheckOutInfoList, RecordAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.laptopCheckOutInfoList = laptopCheckOutInfoList;
        this.laptopCheckOutInfoListFiltered = laptopCheckOutInfoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.laptop_row_item, parent, false);

        return new MyViewHolder(itemView);
    }


    //edit from here to customize
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final LaptopCheckOutInfo laptopCheckOutInfo = laptopCheckOutInfoListFiltered.get(position);
        holder.laptopID.setText(laptopCheckOutInfo.getLaptopID());
        holder.serialNo.setText(laptopCheckOutInfo.getSerialNo());

//        Glide.with(context)
//                .load(contact.getImage())
//                .apply(RequestOptions.circleCropTransform())
//                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return laptopCheckOutInfoListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    laptopCheckOutInfoListFiltered = laptopCheckOutInfoList;
                } else {
                    List<LaptopCheckOutInfo> filteredList = new ArrayList<>();
                    for (LaptopCheckOutInfo  row : laptopCheckOutInfoList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getLaptopID().toLowerCase().contains(charString.toLowerCase()) || row.getSerialNo().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    laptopCheckOutInfoListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = laptopCheckOutInfoListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                laptopCheckOutInfoListFiltered = (ArrayList<LaptopCheckOutInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface RecordAdapterListener {
        void onRecordSelected(LaptopCheckOutInfo checkOutInfo);
    }
}