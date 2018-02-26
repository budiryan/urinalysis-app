package com.example.urinalysis.urinalysis.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.urinalysis.urinalysis.R;
import com.example.urinalysis.urinalysis.models.Substance;

import java.util.List;

/**
 * Created by budiryan on 2/26/18.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<Substance> substances;

    // Provide a suitable constructor (depends on the kind of dataset)
    public HistoryAdapter(Context context, List<Substance> substances) {
        this.substances = substances;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history_item, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // For value
        TextView readingTextView = holder.mView.findViewById(R.id.item_history_reading);


        // For date and time
        TextView datetimeTextView = holder.mView.findViewById(R.id.item_history_time);


        // For notes
        TextView notesTextView = holder.mView.findViewById(R.id.item_history_type);

        readingTextView.setText(String.format("%s %s",
                this.substances.get(position).getValue(),
                this.substances.get(position).getUnitName()));
        datetimeTextView.setText(String.format("%s %s",
                this.substances.get(position).getRecordDate(),
                this.substances.get(position).getRecordTime()));
        notesTextView.setText(this.substances.get(position).getNotes());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.substances.toArray().length;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }
}