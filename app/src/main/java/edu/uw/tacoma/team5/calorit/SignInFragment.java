package edu.uw.tacoma.team5.calorit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {
    private EditText mUsername;
    private EditText mPassword;
    private Button mSignInBtn;
    private Button mSignUpBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        mUsername = (EditText) view.findViewById(R.id.sign_in_username);
        mPassword = (EditText) view.findViewById(R.id.sign_in_password);
        mSignInBtn = (Button) view.findViewById(R.id.sign_in_btn);
        mSignUpBtn = (Button) view.findViewById(R.id.sign_up_btn);

        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LogInActivity) getActivity()).signIn(mUsername.getText().toString(),
                        mPassword.getText().toString());
            }
        });

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LogInActivity) getActivity()).switchToSignUpFragment();
            }
        });

        return view;
    }
}
