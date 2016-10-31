package prof_itgroup.ru.storehouseapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import prof_itgroup.ru.storehouseapp.Helpers.ColorListHelper;
import prof_itgroup.ru.storehouseapp.Helpers.DocumentHelper;
import prof_itgroup.ru.storehouseapp.Helpers.Helper;
import prof_itgroup.ru.storehouseapp.Objects.ColorState;
import prof_itgroup.ru.storehouseapp.Objects.DocumentFields;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackDocumentSaved;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackGetDocumentById;
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;

import static prof_itgroup.ru.storehouseapp.Helpers.Helper.getStringFrom;

public class DetailedItemInfoActivity extends AppCompatActivity {
    private static final String EXTRA_ITEM_ID = "prof_itgroup.ru.storehouseapp.extraItemInfo";

    @BindView(R.id.etDeviceName) EditText etDeviceName;
    @BindView(R.id.etDevicePlatform) EditText etDevicePlatform;
    @BindView(R.id.etDeviceCameraInfo) EditText etDeviceCameraInfo;
    @BindView(R.id.etDeviceColors) EditText etDeviceColors;
    @BindView(R.id.etDevicePrice) EditText etDevicePrice;
    @BindView(R.id.tvWaitingUsers) TextView tvWaitingUsers;
    @BindView(R.id.tvLastChange) TextView tvLastChange;
    @BindView(R.id.tvLastChangeLabel) TextView tvLastChangeLabel;
    @BindView(R.id.btnAddItem) Button btnChangeItem;
    @BindView(R.id.btnClear) Button btnClear;
    @BindView(R.id.llChangeColorList) LinearLayout llChangeColorList;
    @BindView(R.id.llChangePrice) LinearLayout llChangePrice;
    @BindView(R.id.llWaitingUsers) LinearLayout llWaitingUsers;
    private double increaseCount;
    private Map<String, ColorState> deviceColors;

    private Document document;
    private DocumentFields fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_item_info);
        ButterKnife.bind(this);
        setEditMode(false);

        setFields();
        setChangeButton();
    }

    @OnClick(R.id.btnIncreaseCount)
    public void onIncreaseCountButtonPressed() {
        showChangeDialog(true);
    }

    @OnClick(R.id.btnDecreaseCount)
    public void onDecreaseCountButtonPressed() {
        showChangeDialog(false);
    }

    @OnClick(R.id.btnAddColor)
    public void onAddColorButtonClicked() {
        if(isExistInDeviceColors(ColorState.TO_REMOVE)) {
            showToast(R.string.error_delete_and_add);
            return;
        }

        Helper.showEditTextDialog(this, R.string.enter_color, InputType.TYPE_CLASS_TEXT, new Helper.CallbackEditTextDialog() {
            @Override
            public void onContinueClicked(String editTextContent) {
                if (!editTextContent.isEmpty()) {
                    deviceColors.put(editTextContent, fields.getColorsAsList().contains(editTextContent)? ColorState.FROM_DB : ColorState.NEW);
                    ColorListHelper.refreshColorsList(etDeviceColors, deviceColors);
                } else {
                    showToast(R.string.wrong_data);
                }
            }
        });
    }

    @OnClick(R.id.btnRemoveColor)
    public void onRemoveColorButtonClicked() {
        if(isExistInDeviceColors(ColorState.NEW)) {
            showToast(R.string.error_delete_and_add);
            return;
        }

        Helper.showEditTextDialog(this, R.string.remove_color, InputType.TYPE_CLASS_TEXT, new Helper.CallbackEditTextDialog() {
            @Override
            public void onContinueClicked(String editTextContent) {
                if (!editTextContent.isEmpty() && deviceColors.keySet().contains(editTextContent)) {
                    deviceColors.put(editTextContent, ColorState.TO_REMOVE);
                    ColorListHelper.refreshColorsList(etDeviceColors, deviceColors);
                } else {
                    showToast(R.string.wrong_data);
                }
            }
        });
    }

    private boolean isExistInDeviceColors(ColorState colorState) {
        for(String color : deviceColors.keySet()) {
            if(colorState.equals(deviceColors.get(color))) {
                return true;
            }
        }

        return false;
    }

    @OnClick(R.id.btnClear)
    public void onButtonClearClicked() {
        setFields();
    }

    private void showChangeDialog(final boolean isValueIncrease) {
            Helper.showEditTextDialog(this, isValueIncrease? R.string.title_increse_count : R.string.title_decrese_count,
                    InputType.TYPE_CLASS_NUMBER, new Helper.CallbackEditTextDialog() {
                @Override
                public void onContinueClicked(String editTextContent) {
                    try {
                        increaseCount = Double.valueOf(editTextContent);
                        if(!isValueIncrease) {
                            increaseCount *= -1; //if we decrease value we change sign.
                        }
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        increaseCount = 0;
                    }

                    etDevicePrice.setText(String.valueOf(fields.getDevicePrice() + increaseCount));
                }
            });
    }

    @OnClick(R.id.btnSendToUser)
    public void onSendToUserButtonClicked() {
        document.getDocumentById(getDocumentInfo().getId(), new CallbackGetDocumentById() {
            @Override
            public void onDocumentFound(final DocumentInfo documentInfo) {
                document.updateDocument()
                        .popFirst(fields.getBuyersField())
                        .setCurrentDate(fields.getLastSendField());

                document.saveDocument(new CallbackDocumentSaved() {
                    @Override
                    public void onDocumentSaved() {
                        document.updateDocument().getUpdateInfo().clear();
                        refreshWaitingList();
                    }

                    @Override
                    public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                        showToast(R.string.error);
                    }
                });
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                showToast(R.string.error);
            }
        });
    }

    private void setFields() {
        fields = new DocumentFields(this, getDocumentInfo());

        document = new Document(MainActivity.COLLECTION_NAME);
        deviceColors = new HashMap<>();
        llWaitingUsers.setVisibility(View.GONE);

        deviceColors.clear();
        for(String color : fields.getColorsAsList()) {
            deviceColors.put(color, ColorState.FROM_DB);
        }

        etDeviceName.setText(fields.getDeviceName());
        etDevicePlatform.setText(fields.getPlatform());
        etDeviceCameraInfo.setText(fields.getCameraInfo());
        etDevicePrice.setText(String.valueOf(fields.getDevicePrice()));
        etDeviceColors.setText(fields.getColors());
        etDeviceColors.setEnabled(false);
        etDeviceColors.setFocusable(false);

        refreshWaitingList();
    }

    private void refreshWaitingList() {
        document.getDocumentById(getDocumentInfo().getId(), new CallbackGetDocumentById() {
            @Override
            public void onDocumentFound(DocumentInfo documentInfo) {
                fields.setDocumentInfo(documentInfo);
                tvWaitingUsers.setText(fields.getBuyers());
                tvLastChange.setText(fields.getLastSendTime());

                llWaitingUsers.setVisibility(fields.getBuyers().isEmpty() ? View.GONE : View.VISIBLE);
                tvLastChangeLabel.setVisibility(fields.getLastSendTime().isEmpty() ? View.GONE : View.VISIBLE);
                tvLastChange.setVisibility(fields.getLastSendTime().isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                tvWaitingUsers.setVisibility(View.GONE);
            }
        });
    }

    private void setEditMode(boolean isEditModeEnabled) {
        List<EditText> editTextViews = Arrays.asList(etDeviceName, etDevicePlatform,
                etDeviceCameraInfo, etDevicePrice);

        for(EditText editText : editTextViews) {
            editText.setEnabled(isEditModeEnabled);
            editText.setFocusable(isEditModeEnabled);
            editText.setFocusableInTouchMode(isEditModeEnabled);
        }

        btnChangeItem.setVisibility(isEditModeEnabled? View.VISIBLE : View.GONE);
        btnClear.setVisibility(isEditModeEnabled? View.VISIBLE : View.GONE);
        llChangeColorList.setVisibility(isEditModeEnabled? View.VISIBLE : View.GONE);
        llChangePrice.setVisibility(isEditModeEnabled? View.VISIBLE : View.GONE);
    }

    private void setChangeButton() {
        btnChangeItem.setText(getString(R.string.change_item_text));
        btnChangeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemDocument();
                setEditMode(false);
            }
        });
    }

    private void updateItemDocument() {
        document.getDocumentById(getDocumentInfo().getId(), new CallbackGetDocumentById() {
            @Override
            public void onDocumentFound(DocumentInfo documentInfo) {

                document.updateDocument()
                        .set(fields.getDeviceNameField(), getStringFrom(etDeviceName))
                        .set(fields.getPlatformField(), getStringFrom(etDevicePlatform))
                        .set(fields.getCameraInfoField(), getStringFrom(etDeviceCameraInfo));

                setColorsUpdateInfo();

                document.updateDocument().inc(fields.getDevicePriceField(), increaseCount);
                document.saveDocument(new CallbackDocumentSaved() {
                    @Override
                    public void onDocumentSaved() {
                        setEditMode(false);

                        for (String color : deviceColors.keySet()) {
                            deviceColors.put(color, ColorState.FROM_DB);
                        }

                        document.updateDocument().getUpdateInfo().clear();
                    }

                    @Override
                    public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                        showToast(R.string.error_update_item);
                        setFields();
                    }
                });
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                showToast(R.string.error_update_item);
                setFields();
            }
        });
    }

    private void setColorsUpdateInfo() {
        for (String color : deviceColors.keySet()) {
            ColorState colorState = deviceColors.get(color);

            switch (colorState) {
                case TO_REMOVE:
                    document.updateDocument().pull(fields.getColorsAvailableField(), color);
                    break;

                case NEW:
                    document.updateDocument().push(fields.getColorsAvailableField(), color);
                    break;
            }
        }
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

            case R.id.action_remove_item:
                DocumentHelper.fetchAndRemoveDocument(this, document, getDocumentInfo());
                break;

            case R.id.action_prepare_item:
                Helper.showEditTextDialog(this, R.id.title_enter_user_login, InputType.TYPE_CLASS_TEXT, new Helper.CallbackEditTextDialog() {
                    @Override
                    public void onContinueClicked(final String buyerName) {
                        addBuyerAndRefreshWaitingList(buyerName);
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addBuyerAndRefreshWaitingList(final String buyerName) {
        document.getDocumentById(getDocumentInfo().getId(), new CallbackGetDocumentById() {
            @Override
            public void onDocumentFound(DocumentInfo documentInfo) {
                document.updateDocument().push(fields.getBuyersField(), buyerName);
                document.saveDocument(new CallbackDocumentSaved() {
                    @Override
                    public void onDocumentSaved() {
                        document.updateDocument().getUpdateInfo().clear();
                        refreshWaitingList();
                        showToast(R.string.item_ready_to_sell);
                    }

                    @Override
                    public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                        showToast(R.string.error);
                    }
                });
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                showToast(R.string.error);
            }
        });
    }

    private void showToast(int textRes) {
        Toast.makeText(DetailedItemInfoActivity.this, getString(textRes), Toast.LENGTH_SHORT).show();
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
