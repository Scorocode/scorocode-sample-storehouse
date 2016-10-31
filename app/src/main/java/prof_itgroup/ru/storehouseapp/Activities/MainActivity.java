package prof_itgroup.ru.storehouseapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import prof_itgroup.ru.storehouseapp.Objects.DocumentFields;
import prof_itgroup.ru.storehouseapp.Objects.FilterDialog;
import prof_itgroup.ru.storehouseapp.Objects.StoredItemsAdapter;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackFindDocument;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;
import ru.profit_group.scorocode_sdk.scorocode_objects.Query;

public class MainActivity extends AppCompatActivity {

    public static final String COLLECTION_NAME = "storehouse";
    @BindView(R.id.lvItemsInStorehouse) ListView lvItemsInStorehouse;
    private DocumentFields fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fields = new DocumentFields(this);
        LoginActivity.redirectIfNotLogined(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Query query = new Query(COLLECTION_NAME);
        query.findDocuments(new CallbackFindDocument() {
            @Override
            public void onDocumentFound(List<DocumentInfo> documentInfos) {
                if(documentInfos != null) {
                    setAdapter(documentInfos);
                }
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.error_get_docs), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter(List<DocumentInfo> documentInfos) {
        StoredItemsAdapter adapter = new StoredItemsAdapter(MainActivity.this, documentInfos, R.layout.stored_items_item);
        lvItemsInStorehouse.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_item:
                AddItemActivity.display(this);
                break;

            case R.id.action_set_filter:
                new FilterDialog(this).showFilterDialog(new FilterDialog.CallbackFilterDialog() {
                    @Override
                    public void onFilterApplied(List<DocumentInfo> documentInfo) {
                        setAdapter(documentInfo);
                    }
                });
                break;

            case R.id.action_logout:
                LoginActivity.logout(this);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public static void display(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }
}
