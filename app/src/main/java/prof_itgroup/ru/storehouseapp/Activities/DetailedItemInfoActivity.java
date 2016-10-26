package prof_itgroup.ru.storehouseapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import prof_itgroup.ru.storehouseapp.Objects.DocumentFields;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackDocumentSaved;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackGetDocumentById;
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;

import static prof_itgroup.ru.storehouseapp.Activities.AddItemActivity.getStringFrom;

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

        setFields();
        setChangeButton();
        setEditMode(false);
    }

    private void setFields() {
        etDeviceName.setText(getFieldValue(getDocumentInfo(), DocumentFields.DEVICE_NAME));
        etDevicePlatform.setText(getFieldValue(getDocumentInfo(),DocumentFields.PLATFORM));
        etDeviceCameraInfo.setText(getFieldValue(getDocumentInfo(),DocumentFields.CAMERA_INFO));
        etDeviceColors.setText(getFieldValue(getDocumentInfo(),DocumentFields.COLORS_AVAILABLE).replace("[","").replace("]",""));
        etDevicesAvailable.setText(getFieldValue(getDocumentInfo(),DocumentFields.AMOUNT_AVAILABLE));
    }

    private void setEditMode(boolean isEditModeEnabled) {
        List<EditText> editTextViews = Arrays.asList(etDeviceName, etDevicePlatform,
                etDeviceCameraInfo, etDeviceColors, etDevicesAvailable);

        for(EditText editText : editTextViews) {
            editText.setEnabled(isEditModeEnabled);
        }

        btnChangeItem.setVisibility(isEditModeEnabled? View.VISIBLE : View.INVISIBLE);
    }

    public String getFieldValue(DocumentInfo documentInfo, DocumentFields field) {
        return String.valueOf(documentInfo.getFields().get(field.getFieldName(this)));
    }

    private void setChangeButton() {
        btnChangeItem.setText(getString(R.string.change_item_text));
        btnChangeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemDocument();
                setEditMode(true);
            }
        });
    }

    private void updateItemDocument() {
        final Context context = DetailedItemInfoActivity.this;

        final Document document = new Document(MainActivity.COLLECTION_NAME);
        document.getDocumentById(getDocumentInfo().getId(), new CallbackGetDocumentById() {
            @Override
            public void onDocumentFound(DocumentInfo documentInfo) {

                document.updateDocument()
                        .set(DocumentFields.DEVICE_NAME.getFieldName(context), getStringFrom(etDeviceName))
                        .set(DocumentFields.PLATFORM.getFieldName(context), getStringFrom(etDevicePlatform))
                        .set(DocumentFields.CAMERA_INFO.getFieldName(context), getStringFrom(etDeviceCameraInfo))
                        .set(DocumentFields.COLORS_AVAILABLE.getFieldName(context), AddItemActivity.getColorsList(getStringFrom(etDeviceColors)))
                        .set(DocumentFields.AMOUNT_AVAILABLE.getFieldName(context), Long.valueOf(getStringFrom(etDevicesAvailable)));

                document.saveDocument(new CallbackDocumentSaved() {
                    @Override
                    public void onDocumentSaved() {
                        setEditMode(false);
                    }

                    @Override
                    public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                        Toast.makeText(context, getString(R.string.error_update_item), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                Toast.makeText(context, getString(R.string.error_update_item), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailed_info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_item:
                setEditMode(true);
                break;
        }
        return super.onOptionsItemSelected(item);
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
