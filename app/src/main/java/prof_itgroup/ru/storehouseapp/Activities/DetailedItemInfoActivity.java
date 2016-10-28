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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import prof_itgroup.ru.storehouseapp.Helpers.ColorListHelper;
import prof_itgroup.ru.storehouseapp.Helpers.Helper;
import prof_itgroup.ru.storehouseapp.Objects.DocumentFields;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackDocumentSaved;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackGetDocumentById;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackRemoveDocument;
import ru.profit_group.scorocode_sdk.Responses.data.ResponseRemove;
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;

import static prof_itgroup.ru.storehouseapp.Helpers.Helper.getArrayAsStringFrom;
import static prof_itgroup.ru.storehouseapp.Helpers.Helper.getColorsListFrom;
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
    @BindView(R.id.btnAddItem) Button btnChangeItem;
    @BindView(R.id.btnClear) Button btnClear;
    @BindView(R.id.llChangeColorList) LinearLayout llChangeColorList;
    @BindView(R.id.llChangePrice) LinearLayout llChangePrice;
    @BindView(R.id.llWaitingUsers) LinearLayout llWaitingUsers;
    private double increaseCount;
    private Map<String, ColorListHelper.ColorState> deviceColors;

    private Document document;

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

        if(isExistInDeviceColors(ColorListHelper.ColorState.TO_REMOVE)) {
            Toast.makeText(this, getString(R.string.error_delete_and_add), Toast.LENGTH_LONG).show();
            return;
        }

        Helper.showEditTextDialog(this, R.string.enter_color, InputType.TYPE_CLASS_TEXT, new Helper.CallbackEditTextDialog() {
            @Override
            public void onContinueClicked(String editTextContent) {
                if (!editTextContent.isEmpty()) {

                    if(getDBDeviceColourList().contains(editTextContent)) {
                        deviceColors.put(editTextContent, ColorListHelper.ColorState.FROM_DB);
                    } else {
                        deviceColors.put(editTextContent, ColorListHelper.ColorState.NEW);
                    }
                    ColorListHelper.refreshColorsList(etDeviceColors, deviceColors);
                } else {
                    Toast.makeText(DetailedItemInfoActivity.this, getString(R.string.wrong_data), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick(R.id.btnRemoveColor)
    public void onRemoveColorButtonClicked() {
        if(isExistInDeviceColors(ColorListHelper.ColorState.NEW)) {
            Toast.makeText(this, getString(R.string.error_delete_and_add), Toast.LENGTH_LONG).show();
            return;
        }

        Helper.showEditTextDialog(this, R.string.remove_color, InputType.TYPE_CLASS_TEXT, new Helper.CallbackEditTextDialog() {
            @Override
            public void onContinueClicked(String editTextContent) {
                if (!editTextContent.isEmpty() && deviceColors.keySet().contains(editTextContent)) {
                    deviceColors.put(editTextContent, ColorListHelper.ColorState.TO_REMOVE);
                    ColorListHelper.refreshColorsList(etDeviceColors, deviceColors);
                } else {
                    Toast.makeText(DetailedItemInfoActivity.this, getString(R.string.wrong_data), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isExistInDeviceColors(ColorListHelper.ColorState colorState) {
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
        int titleRes = isValueIncrease? R.string.title_increse_count : R.string.title_decrese_count;

            Helper.showEditTextDialog(this, titleRes, InputType.TYPE_CLASS_NUMBER, new Helper.CallbackEditTextDialog() {
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

                    setDevicePrice(getDevicePrice());
                }
            });
    }

    @OnClick(R.id.btnSendToUser)
    public void onSendToUserButtonClicked() {
        final Context context = DetailedItemInfoActivity.this;

        document.getDocumentById(getDocumentInfo().getId(), new CallbackGetDocumentById() {
            @Override
            public void onDocumentFound(DocumentInfo documentInfo) {
                document.updateDocument().popFirst(DocumentFields.BUYERS.getFieldName(context));
                document.updateDocument().setCurrentDate(DocumentFields.LAST_SEND.getFieldName(context));
                document.saveDocument(new CallbackDocumentSaved() {
                    @Override
                    public void onDocumentSaved() {
                        refreshWaitingList();
                        document.updateDocument().getUpdateInfo().clear();
                    }

                    @Override
                    public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                        Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private double getDevicePrice() {
        Double devicePrice;
        if(getStringFrom(etDevicePrice).isEmpty()) {
            devicePrice = 0d;
        } else {
            devicePrice = Double.valueOf(getStringFrom(etDevicePrice));
        }
        devicePrice += increaseCount;
        return devicePrice;
    }

    private void setDevicePrice(Double price) {
        etDevicePrice.setText(String.valueOf(price));
    }

    private void setFields() {
        document = new Document(MainActivity.COLLECTION_NAME);
        deviceColors = new HashMap<>();
        llWaitingUsers.setVisibility(View.GONE);

        deviceColors.clear();
        for(String color : getDBDeviceColourList()) {
            deviceColors.put(color, ColorListHelper.ColorState.FROM_DB);
        }

        etDeviceName.setText(Helper.getFieldValue(this, getDocumentInfo(), DocumentFields.DEVICE_NAME));
        etDevicePlatform.setText(Helper.getFieldValue(this, getDocumentInfo(),DocumentFields.PLATFORM));
        etDeviceCameraInfo.setText(Helper.getFieldValue(this, getDocumentInfo(),DocumentFields.CAMERA_INFO));
        etDevicePrice.setText(Helper.getFieldValue(this, getDocumentInfo(),DocumentFields.DEVICE_PRICE));
        etDeviceColors.setText(Helper.getArrayAsStringFrom(DetailedItemInfoActivity.this, getDocumentInfo(), DocumentFields.COLORS_AVAILABLE));
        etDeviceColors.setEnabled(false);
        etDeviceColors.setFocusable(false);

        refreshWaitingList();
    }

    private void refreshWaitingList() {
        document.getDocumentById(getDocumentInfo().getId(), new CallbackGetDocumentById() {
            @Override
            public void onDocumentFound(DocumentInfo documentInfo) {
                if(Helper.getArrayAsStringFrom(DetailedItemInfoActivity.this, documentInfo, DocumentFields.BUYERS).isEmpty()) {
                    llWaitingUsers.setVisibility(View.GONE);
                } else {
                    llWaitingUsers.setVisibility(View.VISIBLE);
                }

                tvWaitingUsers.setText(Helper.getArrayAsStringFrom(DetailedItemInfoActivity.this, documentInfo, DocumentFields.BUYERS));

                String newDate = getArrayAsStringFrom(getBaseContext(), documentInfo, DocumentFields.LAST_SEND);
                if(newDate != null && !newDate.isEmpty() && !newDate.equals("null")) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
                    String date = formatter.format(Date.parse(newDate));
                    tvLastChange.setText(date);
                }
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                tvWaitingUsers.setVisibility(View.GONE);
            }
        });
    }

    private List<String> getDBDeviceColourList() {
        return getColorsListFrom(Helper.getArrayAsStringFrom(this, getDocumentInfo(), DocumentFields.COLORS_AVAILABLE));
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
        final Context context = DetailedItemInfoActivity.this;

        if(document != null) {
            document.getDocumentById(getDocumentInfo().getId(), new CallbackGetDocumentById() {
                @Override
                public void onDocumentFound(DocumentInfo documentInfo) {

                    document.updateDocument()
                            .set(DocumentFields.DEVICE_NAME.getFieldName(context), getStringFrom(etDeviceName))
                            .set(DocumentFields.PLATFORM.getFieldName(context), getStringFrom(etDevicePlatform))
                            .set(DocumentFields.CAMERA_INFO.getFieldName(context), getStringFrom(etDeviceCameraInfo));

                    for(String color : deviceColors.keySet()) {
                        ColorListHelper.ColorState colorState = deviceColors.get(color);

                        switch (colorState) {
                            case TO_REMOVE:
                                document.updateDocument().pull(DocumentFields.COLORS_AVAILABLE.getFieldName(context), color);
                                break;

                            case NEW:
                                document.updateDocument().push(DocumentFields.COLORS_AVAILABLE.getFieldName(context), color);
                                break;
                        }
                    }

                    document.updateDocument().inc(DocumentFields.DEVICE_PRICE.getFieldName(context), increaseCount);

                    document.saveDocument(new CallbackDocumentSaved() {
                        @Override
                        public void onDocumentSaved() {
                            setEditMode(false);

                            for(String color : deviceColors.keySet()) {
                                deviceColors.put(color, ColorListHelper.ColorState.FROM_DB);
                            }

                            document.updateDocument().getUpdateInfo().clear();
                        }

                        @Override
                        public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                            Toast.makeText(context, getString(R.string.error_update_item), Toast.LENGTH_SHORT).show();
                            setFields();
                        }
                    });
                }

                @Override
                public void onDocumentNotFound(String errorCode, String errorMessage) {
                    Toast.makeText(context, getString(R.string.error_update_item), Toast.LENGTH_SHORT).show();
                    setFields();
                }
            });
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
                fetchAndRemoveDocument();
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

    private void addBuyerAndRefreshWaitingList(final String editTextContent) {
        document.getDocumentById(getDocumentInfo().getId(), new CallbackGetDocumentById() {
            @Override
            public void onDocumentFound(DocumentInfo documentInfo) {
                document.updateDocument().push(DocumentFields.BUYERS.getFieldName(getBaseContext()), editTextContent);
                document.saveDocument(new CallbackDocumentSaved() {
                    @Override
                    public void onDocumentSaved() {
                        Toast.makeText(DetailedItemInfoActivity.this, getString(R.string.item_ready_to_sell), Toast.LENGTH_SHORT).show();
                        refreshWaitingList();
                        document.updateDocument().getUpdateInfo().clear();
                    }

                    @Override
                    public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                        Toast.makeText(DetailedItemInfoActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                Toast.makeText(DetailedItemInfoActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAndRemoveDocument() {
        document.getDocumentById(getDocumentInfo().getId(), new CallbackGetDocumentById() {
            @Override
            public void onDocumentFound(DocumentInfo documentInfo) {
                removeDocument();
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                Toast.makeText(DetailedItemInfoActivity.this, getString(R.string.error_document_not_removed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeDocument() {
        document.removeDocument(new CallbackRemoveDocument() {
            @Override
            public void onRemoveSucceed(ResponseRemove responseRemove) {
                Toast.makeText(DetailedItemInfoActivity.this, getString(R.string.document_removed), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onRemoveFailed(String errorCode, String errorMessage) {
                Toast.makeText(DetailedItemInfoActivity.this, getString(R.string.error_document_not_removed), Toast.LENGTH_SHORT).show();
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
