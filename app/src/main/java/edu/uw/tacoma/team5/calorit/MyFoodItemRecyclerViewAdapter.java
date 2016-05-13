package edu.uw.tacoma.team5.calorit;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uw.tacoma.team5.calorit.FoodItemFragment.OnListFragmentInteractionListener;
import edu.uw.tacoma.team5.calorit.model.FoodItem;


import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link edu.uw.tacoma.team5.calorit.model.FoodItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyFoodItemRecyclerViewAdapter extends RecyclerView.Adapter<MyFoodItemRecyclerViewAdapter.ViewHolder> {

    private final List<FoodItem> mFoodItems;
    private final OnListFragmentInteractionListener mListener;

    public MyFoodItemRecyclerViewAdapter(List<FoodItem> items, OnListFragmentInteractionListener listener) {
        mFoodItems = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_fooditem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mFoodItems.get(position);
        holder.mIdView.setText(mFoodItems.get(position).getmFoodName());

        //Maybe don't need this line. Have to work on other parts first to know.
//        holder.mContentView.setText(mFoodItems.get(position).content);

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
        return mFoodItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public FoodItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
