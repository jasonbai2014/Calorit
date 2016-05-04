package edu.uw.tacoma.team5.calorit;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uw.tacoma.team5.calorit.model.MealLog;

import java.util.List;

/**
 * This is an adapter for the recycler view used in the MealLogFragment
 *
 * Qing Bai
 * Levi Bingham
 * 2016/05/04
 */
public class MyMealLogRecyclerViewAdapter extends RecyclerView.Adapter<MyMealLogRecyclerViewAdapter.ViewHolder> {

    /**
     * This is a list holding meal log
     */
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

    /**
     * This sets up content shown in each item of the recycler view
     *
     * @param holder is holder view for a meal log
     * @param position is position in the list
     */
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
