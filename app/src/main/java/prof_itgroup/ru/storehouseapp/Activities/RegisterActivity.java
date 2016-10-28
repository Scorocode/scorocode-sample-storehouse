package prof_itgroup.ru.storehouseapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackRegisterUser;
import ru.profit_group.scorocode_sdk.scorocode_objects.User;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.etUsername) EditText etUsername;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.etPasswordCheck) EditText etPasswordCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnRegister)
    public void onBtnRegisterClicked() {

        String userName = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String passwordCheck = etPasswordCheck.getText().toString();

        if(isInputValid(userName, email, password, passwordCheck)) {
            new User().register(userName, email, password, new CallbackRegisterUser() {
                @Override
                public void onRegisterSucceed() {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.register_succeed), Toast.LENGTH_SHORT).show();
                    LoginActivity.display(RegisterActivity.this);
                }

                @Override
                public void onRegisterFailed(String errorCode, String errorMessage) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.error_register), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, getResources().getString(R.string.wrong_data) , Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isInputValid(String userName, String email, String password, String passwordCheck) {
        return !userName.isEmpty() && !email.isEmpty() && !password.isEmpty() && password.equals(passwordCheck);
    }

    public static void display(Context context) {
        context.startActivity(new Intent(context, RegisterActivity.class));
    }
}
