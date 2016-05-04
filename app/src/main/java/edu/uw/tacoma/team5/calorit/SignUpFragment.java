package edu.uw.tacoma.team5.calorit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * This is a fragment for sign up UI
 *
 * Qing Bai
 * Levi Bingham
 * 2016/05/04
 */
public class SignUpFragment extends Fragment {
    /**
     * This is an edit text field for user name
     */
    private EditText mUsername;

    /**
     * This is an edit text field for password
     */
    private EditText mPassword;

    /**
     * This is an edit text field for confirm password
     */
    private EditText mConfirmPassword;

    /**
     * This is a button for "create account"
     */
    private Button mCreateAccountBtn;

    /**
     * This gets views in this UI and set up a listener for the button.
     *
     * @param inflater is inflater
     * @param container is container
     * @param savedInstanceState is saved state
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mUsername = (EditText) view.findViewById(R.id.sign_up_username);
        mPassword = (EditText) view.findViewById(R.id.sign_up_password);
        mConfirmPassword = (EditText) view.findViewById(R.id.sign_up_confirm_password);
        mCreateAccountBtn = (Button) view.findViewById(R.id.create_account_btn);

        mCreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = mPassword.getText().toString();
                String confirmPassword = mConfirmPassword.getText().toString();

                if (password.equals(confirmPassword)) {
                    ((LogInActivity) getActivity()).signUp(mUsername.getText().toString(), password);
                    ;
                } else {
                    Toast.makeText(v.getContext(), "Please make sure you enter two same passwords",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

}
