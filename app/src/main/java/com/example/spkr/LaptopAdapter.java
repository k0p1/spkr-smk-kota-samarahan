package com.example.spkr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class LaptopAdapter extends RecyclerView.Adapter<LaptopAdapter.MyViewHolder> implements Filterable {
private Context context;
private List<LaptopInfo> laptopList;
private List<LaptopInfo> laptopListFiltered;
private LaptopAdapterListener listener;

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
                listener.onRecordSelected(laptopListFiltered.get(getAdapterPosition()));
            }
        });
    }
}


    public LaptopAdapter(Context context, List<LaptopInfo> laptopList, LaptopAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.laptopList = laptopList;
        this.laptopListFiltered = laptopList;
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
        final LaptopInfo laptopInfo = laptopListFiltered.get(position);
        holder.laptopID.setText(laptopInfo.getLaptopID());
        holder.serialNo.setText(laptopInfo.getSerialNo());
    }

    @Override
    public int getItemCount() {
        return laptopListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    laptopListFiltered = laptopList;
                } else {
                    List<LaptopInfo> filteredList = new ArrayList<>();
                    for (LaptopInfo  row : laptopList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for laptop id or serial number match
                        if (row.getLaptopID().toLowerCase().contains(charString.toLowerCase()) || row.getSerialNo().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    laptopListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = laptopListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                laptopListFiltered = (ArrayList<LaptopInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface LaptopAdapterListener {
        void onRecordSelected(LaptopInfo laptopInfo);
    }
}