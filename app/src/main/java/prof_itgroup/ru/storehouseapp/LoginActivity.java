package prof_itgroup.ru.storehouseapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackLoginUser;
import ru.profit_group.scorocode_sdk.Responses.user.ResponseLogin;
import ru.profit_group.scorocode_sdk.Responses.user.UserData;
import ru.profit_group.scorocode_sdk.ScorocodeSdk;
import ru.profit_group.scorocode_sdk.scorocode_objects.User;

public class LoginActivity extends AppCompatActivity {
    public static final String SHARED_PREF_IS_USER_LOGINED = "prof_itgroup.ru.storehouseapp.isuserlogined";
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ScorocodeSdk.initWith("305ffd6cc32832f6819bf4e4f4707848", "962066371eefc0d1850a76c7ab14c1dc");
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnLogin)
    public void onBtnLoginClicked() {
        User user = new User();
        user.login(etEmail.getText().toString(), etPassword.getText().toString(), new CallbackLoginUser() {
            @Override
            public void onLoginSucceed(ResponseLogin responseLogin) {
                MainActivity.display(LoginActivity.this);
                UserData userData = responseLogin.getResult().getUser();
            }

            @Override
            public void onLoginFailed(String errorCode, String errorMessage) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_login), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void display(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }
}
