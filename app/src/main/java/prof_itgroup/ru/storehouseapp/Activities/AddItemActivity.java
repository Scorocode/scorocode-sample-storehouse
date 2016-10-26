package prof_itgroup.ru.storehouseapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import prof_itgroup.ru.storehouseapp.Objects.DocumentFields;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackDocumentSaved;
import ru.profit_group.scorocode_sdk.ScorocodeSdk;
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;

public class AddItemActivity extends AppCompatActivity {
    @BindView(R.id.etDeviceName) EditText etDeviceName;
    @BindView(R.id.etDevicePlatform) EditText etDevicePlatform;
    @BindView(R.id.etDeviceCameraInfo) EditText etDeviceCameraInfo;
    @BindView(R.id.etDeviceColors) EditText etDeviceColors;
    @BindView(R.id.etDevicesAvailable) EditText etDevicesAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        ButterKnife.bind(this);

        ScorocodeSdk.initWith(LoginActivity.APPLICATION_ID, LoginActivity.CLIENT_KEY);
    }

    @OnClick(R.id.btnAddItem)
    public void onBtnAddItemClicked() {
        Document document = new Document(MainActivity.COLLECTION_NAME);

        document.setField(DocumentFields.DEVICE_NAME.getFieldName(this), getStringFrom(etDeviceName));
        document.setField(DocumentFields.PLATFORM.getFieldName(this), getStringFrom(etDevicePlatform));
        document.setField(DocumentFields.CAMERA_INFO.getFieldName(this), getStringFrom(etDeviceCameraInfo));
        document.setField(DocumentFields.COLORS_AVAILABLE.getFieldName(this), getColorsList(getStringFrom(etDeviceColors)));
        document.setField(DocumentFields.AMOUNT_AVAILABLE.getFieldName(this), Long.valueOf(getStringFrom(etDevicesAvailable)));

        document.saveDocument(new CallbackDocumentSaved() {
            @Override
            public void onDocumentSaved() {
                Toast.makeText(AddItemActivity.this, getString(R.string.succed_add_item), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                Toast.makeText(AddItemActivity.this, getString(R.string.error_add_item), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    public static List<String> getColorsList(String deviceColors) {
        List<String> colors = new ArrayList<>();

        for(String color: deviceColors.split(",")) {
            if(!color.isEmpty()) {
                 colors.add(color);
            }
        }
        return colors;
    }

    @NonNull
    public static String getStringFrom(EditText editText) {
        if(editText != null) {
            return editText.getText().toString();
        } else {
            return "";
        }
    }

    public static void display(Context context) {
        context.startActivity(new Intent(context, AddItemActivity.class));
    }
}
