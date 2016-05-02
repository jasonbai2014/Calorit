package edu.uw.tacoma.team5.calorit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    Button enterMealButton, checkMealLogButton, editBodyInfoButton, logOutButton;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inflate the layout for this fragment
        enterMealButton = (Button) view.findViewById(R.id.button_enter_meal);
//        enterMealButton.setOnClickListener();

        checkMealLogButton = (Button) view.findViewById(R.id.button_check_meal_log);
        checkMealLogButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFragmentManager().beginTransaction().add(R.id.fragment_home,
                                new MealLogFragment()).commit();
                    }
                }, 3000);
            }
        });

        editBodyInfoButton = (Button) view.findViewById(R.id.button_edit_body_info);
           editBodyInfoButton.setOnClickListener(new View.OnClickListener(){
               @Override
               public void onClick(View v){
                   Intent i = new Intent(getActivity(), BodyInfoActivity.class);
                   startActivity(i);
               }
           });

        logOutButton = (Button) view.findViewById(R.id.button_log_out);
//        logOutButton.setOnClickListener();

        return view;
    }

}
