package edu.uw.tacoma.team5.calorit;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uw.tacoma.team5.calorit.model.MealLog;

import java.util.List;

public class MyMealLogRecyclerViewAdapter extends RecyclerView.Adapter<MyMealLogRecyclerViewAdapter.ViewHolder> {

    private final List<MealLog> mValues;

    public MyMealLogRecyclerViewAdapter(List<MealLog> items) {
        mValues = items;
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

        if (position == 0) {
            holder.mDateView.setText("Date");
            holder.mCaloriesConsumedView.setText("Consumed");
            holder.mCaloriesBurnedView.setText("Burned");
        } else {
            holder.mDateView.setText(mValues.get(position).getmLogDate());
            holder.mCaloriesConsumedView.setText(String.valueOf(mValues.get(position).getmCaloriesConsumed()));
            holder.mCaloriesBurnedView.setText(String.valueOf(mValues.get(position).getmCaloriesBurned()));
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDateView;
        public final TextView mCaloriesConsumedView;
        public final TextView mCaloriesBurnedView;
        public MealLog mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDateView = (TextView) view.findViewById(R.id.log_date);
            mCaloriesConsumedView = (TextView) view.findViewById(R.id.calories_consumed);
            mCaloriesBurnedView = (TextView) view.findViewById(R.id.calories_burned);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDateView.getText() + "' '" +
                    mCaloriesConsumedView.getText() + "' '"
                    + mCaloriesBurnedView.getText() + "' ";
        }
    }
}
