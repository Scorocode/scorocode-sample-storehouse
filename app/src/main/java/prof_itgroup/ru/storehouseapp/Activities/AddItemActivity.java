package prof_itgroup.ru.storehouseapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
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
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;

import static prof_itgroup.ru.storehouseapp.Helpers.Helper.getColorsListFrom;
import static prof_itgroup.ru.storehouseapp.Helpers.Helper.getStringFrom;

public class AddItemActivity extends AppCompatActivity {
    @BindView(R.id.etDeviceName) EditText etDeviceName;
    @BindView(R.id.etDevicePlatform) EditText etDevicePlatform;
    @BindView(R.id.etDeviceCameraInfo) EditText etDeviceCameraInfo;
    @BindView(R.id.etDeviceColors) EditText etDeviceColors;
    @BindView(R.id.etDevicePrice) EditText etDevicePrice;
    private List<EditText> editTextViews;
    private DocumentFields fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        ButterKnife.bind(this);
        LoginActivity.redirectIfNotLogined(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        editTextViews = new ArrayList<>();
        editTextViews.add(etDeviceName);
        editTextViews.add(etDevicePlatform);
        editTextViews.add(etDeviceCameraInfo);
        editTextViews.add(etDeviceColors);
        editTextViews.add(etDevicePrice);

        fields = new DocumentFields(this);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick(R.id.btnAddItem)
    public void onBtnAddItemClicked() {
        Document document = new Document(MainActivity.COLLECTION_NAME);

        if (isAllFieldsFilled()) {
            document.setField(fields.getDeviceNameField(), getStringFrom(etDeviceName).trim());
            document.setField(fields.getPlatformField(), getStringFrom(etDevicePlatform).trim());
            document.setField(fields.getCameraInfoField(), getStringFrom(etDeviceCameraInfo).trim());
            document.setField(fields.getColorsAvailableField(), getColorsListFrom(getStringFrom(etDeviceColors)));
            document.setField(fields.getDevicePriceField(), Double.valueOf(getStringFrom(etDevicePrice)));

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
        } else {
            Toast.makeText(this, getString(R.string.wrong_data), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isAllFieldsFilled() {
        for(EditText editText : editTextViews) {
            if(editText.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @OnClick(R.id.btnClear)
    public void onClearButtonClicked() {
        for(EditText editText : editTextViews) {
            editText.getText().clear();
        }
    }

    public static void display(Context context) {
        context.startActivity(new Intent(context, AddItemActivity.class));
    }
}
