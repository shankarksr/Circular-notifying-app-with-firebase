package com.dalda.ksrct;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Adaptor extends RecyclerView.Adapter<Adaptor.ViewHolder> implements Filterable {

    private Context applicationContext;
    private List<Circular> circulars;
    private List<Circular> circularFull;

    public Adaptor(Context applicationContext, List<Circular> circulars) {
        this.applicationContext = applicationContext;
        this.circulars = circulars;
        circularFull = new ArrayList<>(circulars);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Circular circular = circulars.get(position);

        holder.textViewName.setText(circular.getNamE());
        holder.dateView.setText(circular.getDate());

        holder.textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(circular.getUrl()),"application/pdf");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                applicationContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return circulars.size();
    }

    @Override
    public Filter getFilter() {
        return circularFilter;
    }

    private Filter circularFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Circular> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(circularFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Circular circular : circularFull){
                    if (circular.getNamE().toLowerCase().contains(filterPattern)){
                        filteredList.add(circular);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            circulars.clear();
            circulars.addAll((List)results.values);
            notifyDataSetChanged();

        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public TextView dateView;
        public CardView cardView;

        private ViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            dateView = itemView.findViewById(R.id.Date);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
    public void clear(){
        circulars.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Circular> circularFull){
        circulars.addAll(circularFull);
        notifyDataSetChanged();
    }

}
