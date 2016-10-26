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
import prof_itgroup.ru.storehouseapp.Objects.LocalPersistence;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackLoginUser;
import ru.profit_group.scorocode_sdk.Responses.user.ResponseLogin;
import ru.profit_group.scorocode_sdk.ScorocodeSdk;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;
import ru.profit_group.scorocode_sdk.scorocode_objects.User;

public class LoginActivity extends AppCompatActivity {
    public static final String APPLICATION_ID = "305ffd6cc32832f6819bf4e4f4707848";
    public static final String CLIENT_KEY = "962066371eefc0d1850a76c7ab14c1dc";

    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isUserLogined(this)) {
            MainActivity.display(this);
        }

        ScorocodeSdk.initWith(APPLICATION_ID, CLIENT_KEY);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnLogin)
    public void onBtnLoginClicked() {
        User user = new User();
        user.login(etEmail.getText().toString(), etPassword.getText().toString(), new CallbackLoginUser() {
            @Override
            public void onLoginSucceed(ResponseLogin responseLogin) {
                MainActivity.display(LoginActivity.this);
                DocumentInfo userData = responseLogin.getResult().getUserInfo();
                LocalPersistence.writeObjectToFile(LoginActivity.this, userData, LocalPersistence.FILE_USER_INFO);
            }

            @Override
            public void onLoginFailed(String errorCode, String errorMessage) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_login), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btnRegister)
    public void onBtnRegisterClicked() {
        RegisterActivity.display(this);
    }

    public static boolean isUserLogined(Context context) {
        Object isUserLogined = LocalPersistence.readObjectFromFile(context, LocalPersistence.FILE_USER_INFO);

        if(isUserLogined != null && ScorocodeSdk.getSessionId() != null) {
            return true;
        } else {
            return false;
        }
    }

    public static void display(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }
}
