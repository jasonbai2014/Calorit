package edu.uw.tacoma.team5.calorit;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uw.tacoma.team5.calorit.MealLogFragment.OnListFragmentInteractionListener;
import edu.uw.tacoma.team5.calorit.model.MealLog;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MealLog} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMealLogRecyclerViewAdapter extends RecyclerView.Adapter<MyMealLogRecyclerViewAdapter.ViewHolder> {

    private final List<MealLog> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyMealLogRecyclerViewAdapter(List<MealLog> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_meallog, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mLogDateView.setText(mValues.get(position).getTodaysDate());
        holder.mCaloriesConsumedView.setText(String.valueOf(mValues.get(position).getCalsConsumed()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLogDateView;
        public final TextView mCaloriesConsumedView;
        public MealLog mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLogDateView = (TextView) view.findViewById(R.id.logDate);
            mCaloriesConsumedView = (TextView) view.findViewById(R.id.caloriesConsumed);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCaloriesConsumedView.getText() + "'";
        }
    }
}
