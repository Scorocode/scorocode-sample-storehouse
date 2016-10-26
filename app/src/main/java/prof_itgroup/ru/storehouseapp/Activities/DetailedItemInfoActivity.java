package prof_itgroup.ru.storehouseapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import prof_itgroup.ru.storehouseapp.Objects.DocumentFields;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackGetDocumentById;
import ru.profit_group.scorocode_sdk.ScorocodeSdk;
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;

public class DetailedItemInfoActivity extends AppCompatActivity {
    private static final String EXTRA_ITEM_ID = "prof_itgroup.ru.storehouseapp.extraItemInfo";
    @BindView(R.id.etDeviceName) EditText etDeviceName;
    @BindView(R.id.etDevicePlatform) EditText etDevicePlatform;
    @BindView(R.id.etDeviceCameraInfo) EditText etDeviceCameraInfo;
    @BindView(R.id.etDeviceColors) EditText etDeviceColors;
    @BindView(R.id.etDevicesAvailable) EditText etDevicesAvailable;
    @BindView(R.id.btnAddItem) Button btnChangeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_item_info);
        ButterKnife.bind(this);
        ScorocodeSdk.initWith(LoginActivity.APPLICATION_ID, LoginActivity.CLIENT_KEY);

        btnChangeItem.setVisibility(View.INVISIBLE);

        setFields();
        setChangeButton();

    }

    private void setFields() {
        DocumentInfo documentInfo = getDocumentInfo();

        etDeviceName.setText(getFieldValue(documentInfo, DocumentFields.DEVICE_NAME));
        etDevicePlatform.setText(getFieldValue(documentInfo,DocumentFields.PLATFORM));
        etDeviceCameraInfo.setText(getFieldValue(documentInfo,DocumentFields.CAMERA_INFO));
        etDeviceColors.setText(getFieldValue(documentInfo,DocumentFields.COLORS_AVAILABLE).replace("[","").replace("]",""));
        etDevicesAvailable.setText(getFieldValue(documentInfo,DocumentFields.AMOUNT_AVAILABLE));
    }

    public String getFieldValue(DocumentInfo documentInfo, DocumentFields field) {
        return String.valueOf(documentInfo.getFields().get(field.getFieldName(this)));
    }

    private void setChangeButton() {
        btnChangeItem.setText(getString(R.string.change_item_text));
        btnChangeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemDocument(new DocumentInfo());
            }
        });
        btnChangeItem.setVisibility(View.VISIBLE);
    }

    private void updateItemDocument(DocumentInfo documentInfo) {
        Document document = new Document(MainActivity.COLLECTION_NAME);
        document.getDocumentById(documentInfo.getId(), new CallbackGetDocumentById() {
            @Override
            public void onDocumentFound(DocumentInfo documentInfo) {

            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {

            }
        });
    }

    private DocumentInfo getDocumentInfo() {
        if(getIntent() != null) {
            return (DocumentInfo) getIntent().getSerializableExtra(EXTRA_ITEM_ID);
        } else {
            return new DocumentInfo();
        }
    }

    public static void display(Context context, DocumentInfo documentInfo) {
        Intent intent = new Intent(context, DetailedItemInfoActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, documentInfo);
        context.startActivity(intent);
    }
}
