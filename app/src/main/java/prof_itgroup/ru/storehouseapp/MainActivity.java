package prof_itgroup.ru.storehouseapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginActivity.display(this);
    }

    public static void display(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }
}
