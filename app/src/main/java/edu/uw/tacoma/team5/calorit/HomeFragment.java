package edu.uw.tacoma.team5.calorit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private TextView mCaloriesTextView;
    private Button mEnterMealBtn;
    private Button mEditBodyInfoBtn;
    private Button mMealLogBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mEnterMealBtn = (Button) view.findViewById(R.id.enter_meal_btn);
        mEditBodyInfoBtn = (Button) view.findViewById(R.id.edit_body_info_btn);
        mMealLogBtn = (Button) view.findViewById(R.id.meal_log_btn);

        mEnterMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mEditBodyInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mMealLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).viewMealLog();
            }
        });

        return view;
    }

}
