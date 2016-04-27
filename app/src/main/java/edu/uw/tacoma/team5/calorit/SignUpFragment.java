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
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private EditText mUsername;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private Button mCreateAccountBtn;

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
