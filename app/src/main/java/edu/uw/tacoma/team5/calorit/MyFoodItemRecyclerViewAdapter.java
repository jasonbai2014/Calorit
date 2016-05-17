package edu.uw.tacoma.team5.calorit;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.uw.tacoma.team5.calorit.FoodItemFragment.OnFoodItemListener;
import edu.uw.tacoma.team5.calorit.model.FoodItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link edu.uw.tacoma.team5.calorit.model.FoodItem} and makes a call to the
 * specified {@link OnFoodItemListener}.
 */
public class MyFoodItemRecyclerViewAdapter extends RecyclerView.Adapter<MyFoodItemRecyclerViewAdapter.ViewHolder> {

    /**
     * List of FoodItems for the category selected.
     */
    private final List<FoodItem> mFoodItems;

    /**
     * Listener to watch for a user clicking/selecting a FoodItem.
     */
    private final OnFoodItemListener mListener;

    /**
     * Constructor for the MyFoodItemRecyclerViewAdapter class.
     * @param items FoodItems to be put into the RecyclerViewAdapter.
     * @param listener Listener to watch for user interaction with the items in the RecyclerViewAdapter.
     */
    public MyFoodItemRecyclerViewAdapter(List<FoodItem> items, FoodItemFragment.OnFoodItemListener listener) {
        mFoodItems = items;
        mListener = listener;
    }

    /**
     * Inflates the view to be displayed.
     * @param parent ?
     * @param viewType ?
     * @return returns the view to be displayed
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_fooditem, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds view the view holder to the view to display what should be on the view.
     * @param holder ?
     * @param position ?
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mFoodItems.get(position);

        if (position == 0) {
            holder.mFoodNameTextView.setText("Food Name");
            holder.mCaloriesTextView.setText("Calories (cal)");
        } else {
            holder.mFoodNameTextView.setText(mFoodItems.get(position).getmFoodName());
            holder.mCaloriesTextView.setText(String.valueOf(mFoodItems.get(position).getmCalories()));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        final EditText text = new EditText(holder.mView.getContext());
                        text.setInputType(InputType.TYPE_CLASS_NUMBER);
                        text.setMaxLines(1);
                        text.setHint("Please enter amount in grams");

                        AlertDialog.Builder dialog = new AlertDialog.Builder(holder.mView.getContext());
                        dialog.setTitle("Enter Amount");
                        dialog.setView(text, 70, 60, 70, 20);

                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Notify the active callbacks interface (the activity, if the
                                // fragment is attached to one) that an item has been selected.
                                String info = "";

                                try {
                                    int amount = Integer.valueOf(text.getText().toString());
                                    mListener.onFoodItemClick(holder.mItem, amount);
                                } catch (NumberFormatException e) {
                                    Toast.makeText(holder.mView.getContext(),
                                            "Please enter a valid number", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        dialog.show();
                    }
                }
            });
        }
    }

    /**
     * Getter for the item count.
     * @return size of the mFoodItems list.
     */
    @Override
    public int getItemCount() {
        return mFoodItems.size();
    }

    /**
     * This class is used to hold the views for the Recycler view. This essentially creates the
     * visible, clickable list of FoodItems that the user will see after selecting a category.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * This is the view that will hold a FoodItem.
         */
        public final View mView;

        /**
         * This is the TextView that will display the FoodItem's name.
         */
        public final TextView mFoodNameTextView;

        /**
         * This is the TextView that will display the FoodItem's calories (per 100g).
         */
        public final TextView mCaloriesTextView;

        /**
         * This is the FoodItem that will be displayed in this ViewHolder.
         */
        public FoodItem mItem;

        /**
         * This is the constructor for the ViewHolder class.
         *
         * @param view is the view used to hold the information about this FoodItem.
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mFoodNameTextView = (TextView) view.findViewById(R.id.food_name_textview);
            mCaloriesTextView = (TextView) view.findViewById(R.id.calories_textview);
        }

        /**
         * This is the toString() method for ViewHolder.
         *
         * @return A String representation of the number of calories for this FoodItem.
         */
        @Override
        public String toString() {
            return super.toString() + " '" + mFoodNameTextView.getText() + "' '"
                    + mCaloriesTextView.getText() + "'";
        }
    }
}
