package prof_itgroup.ru.storehouseapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import prof_itgroup.ru.storehouseapp.Helpers.Helper;
import prof_itgroup.ru.storehouseapp.Objects.DocumentFields;
import prof_itgroup.ru.storehouseapp.Objects.FileHelper;
import prof_itgroup.ru.storehouseapp.Objects.ItemNotificator;
import prof_itgroup.ru.storehouseapp.Objects.ReadDocumentFileTask;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackDocumentSaved;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackGetDocumentById;
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;

import static prof_itgroup.ru.storehouseapp.Activities.ItemDetailsActivity.EXTRA_DOCUMENT_INFO;
import static prof_itgroup.ru.storehouseapp.Helpers.Helper.showToast;

public class SendItemActivity extends AppCompatActivity {
    @BindView(R.id.llWaitingUsers) LinearLayout llWaitingUsers;
    @BindView(R.id.tvLastChange) TextView tvLastChange;
    @BindView(R.id.tvLastChangeLabel) TextView tvLastChangeLabel;
    @BindView(R.id.tvWaitingUsers) TextView tvWaitingUsers;
    @BindView(R.id.etItemInfo) EditText etItemInfo;
    @BindView(R.id.btnSendToUser) Button btnSendToUser;
    @BindView(R.id.llInfoContainer) LinearLayout llInfoContainer;
    @BindView(R.id.tvEdit) TextView tvEdit;

    private Document document;
    private DocumentFields fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_item);
        ButterKnife.bind(this);

        fields = new DocumentFields(this, getDocumentInfo());
        document = new Document(MainActivity.COLLECTION_NAME);
        refreshWaitingList();

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick(R.id.btnAppUserInList)
    public void onBtnAddUserInListClicked() {
        Helper.showEditTextDialog(this, R.id.title_enter_user_login, InputType.TYPE_CLASS_TEXT, new Helper.CallbackEditTextDialog() {
            @Override
            public void onContinueClicked(final String buyerName) {
                addBuyerAndRefreshWaitingList(buyerName);
            }
        });
    }

    @OnClick(R.id.tvEdit)
    public void onTvEditClicked() {
        if(tvEdit.getText().toString().equals(getString(R.string.editInfo))) {
            etItemInfo.setEnabled(true);
            tvEdit.setText(R.string.saveInfo);
        } else {
            FileHelper.uploadFile(this, document, etItemInfo.getText().toString());
            etItemInfo.setEnabled(false);
            tvEdit.setText(R.string.editInfo);
        }
    }

    private DocumentInfo getDocumentInfo() {
        if(getIntent() != null) {
            return (DocumentInfo) getIntent().getSerializableExtra(ItemDetailsActivity.EXTRA_DOCUMENT_INFO);
        }
        return new DocumentInfo();
    }

    private void addBuyerAndRefreshWaitingList(final String buyerName) {
        if(!buyerName.trim().isEmpty()) {
            document.getDocumentById(getDocumentInfo().getId(), new CallbackGetDocumentById() {
                @Override
                public void onDocumentFound(DocumentInfo documentInfo) {
                    document.updateDocument().push(fields.getBuyersField(), buyerName);
                    document.saveDocument(new CallbackDocumentSaved() {
                        @Override
                        public void onDocumentSaved() {
                            document.updateDocument().getUpdateInfo().clear();
                            refreshWaitingList();
                            showToast(getBaseContext(), R.string.add_waiting_buyer);
                        }

                        @Override
                        public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                            showToast(getBaseContext(), R.string.error);
                        }
                    });
                }

                @Override
                public void onDocumentNotFound(String errorCode, String errorMessage) {
                    showToast(getBaseContext(), R.string.error);
                }
            });
        }
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
                btnSendToUser.setVisibility(fields.getBuyers().isEmpty() ? View.GONE : View.VISIBLE);

                new ReadDocumentFileTask(getBaseContext(), document, llInfoContainer).execute();
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                tvWaitingUsers.setVisibility(View.GONE);
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
                        new ItemNotificator(getBaseContext(), documentInfo.getId(), fields.getDeviceName()).notifyPersonalAboutItemSend();

                        etItemInfo.setVisibility(View.VISIBLE);
                        etItemInfo.append(fields.getLastSendTime());
                        refreshWaitingList();
                    }

                    @Override
                    public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                        showToast(getBaseContext(), R.string.error);
                    }
                });
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                showToast(getBaseContext(), R.string.error);
            }
        });
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

    public static void display(Context context, DocumentInfo documentInfo) {
        Intent intent = new Intent(context, SendItemActivity.class);
        intent.putExtra(EXTRA_DOCUMENT_INFO, documentInfo);
        context.startActivity(intent);
    }
}
