package prof_itgroup.ru.storehouseapp.Objects;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import prof_itgroup.ru.storehouseapp.Activities.MainActivity;
import prof_itgroup.ru.storehouseapp.Helpers.Helper;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackFindDocument;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;
import ru.profit_group.scorocode_sdk.scorocode_objects.Query;

/**
 * Created by Peter Staranchuk on 10/30/16
 */

public class FilterDialog {
    private Context context;

    public FilterDialog(Context context) {
        this.context = context;
    }

    public void showFilterDialog(final CallbackFilterDialog callbackFilterDialog) {
        final View v = LayoutInflater.from(context).inflate(R.layout.filter_layout, null);
        final CheckBox cbPriceFilter = ButterKnife.findById(v, R.id.cbPriceFilter);
        final CheckBox cbPlatformFilter = ButterKnife.findById(v, R.id.cbPlatformFilter);
        final CheckBox cbColourFilter = ButterKnife.findById(v, R.id.cbColorFilter);
        final EditText etPlatformFilter = ButterKnife.findById(v, R.id.etPlatformFilter);
        final EditText etColors = ButterKnife.findById(v, R.id.etColors);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.titleChooseFilterProperties)
                .setPositiveButton(R.string.continue_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Query query = new Query(MainActivity.COLLECTION_NAME);

                        if (cbPriceFilter.isChecked()) {
                            setPriceFilter(v, query);
                        }

                        if (cbPlatformFilter.isChecked()) {
                            query.equalTo(new DocumentFields(context).getPlatformField(), etPlatformFilter.getText().toString());
                        }

                        if(cbColourFilter.isChecked()) {
                            List<Object> colors = new ArrayList<>();

                            colors.addAll(Arrays.asList(Helper.getStringFrom(etColors).split(",")));

                            query.containedIn(new DocumentFields(context).getColorsAvailableField(), colors);
                        }

                        query.findDocuments(new CallbackFindDocument() {
                            @Override
                            public void onDocumentFound(List<DocumentInfo> documentInfos) {
                                 callbackFilterDialog.onFilterApplied(documentInfos);
                            }

                            @Override
                            public void onDocumentNotFound(String errorCode, String errorMessage) {
                                Helper.showToast(context, R.string.error);
                            }
                        });
                    }
                }).setView(v);
        builder.show();
    }

    private void setPriceFilter(View view, Query query) {
        String priceField = new DocumentFields(view.getContext()).getDevicePriceField();

        final CheckBox cbIncludeLower = ButterKnife.findById(view, R.id.cbIncludeLower);
        final CheckBox cbIncludeUpper = ButterKnife.findById(view, R.id.cbIncludeUpper);
        final EditText etLowerPrice = ButterKnife.findById(view, R.id.etPriceFrom);
        final EditText etUpperPrice = ButterKnife.findById(view, R.id.etPriceTo);

        if(cbIncludeLower.isChecked()) {
            query.greaterThenOrEqualTo(priceField, getPrice(etLowerPrice));
        } else {
            query.greaterThan(priceField, getPrice(etLowerPrice));
        }

        if(cbIncludeUpper.isChecked()) {
            query.lessThanOrEqualTo(priceField, getPrice(etUpperPrice));
        } else {
            query.lessThan(priceField, getPrice(etUpperPrice));
        }
    }

    private Double getPrice(EditText etPrice) {
        String price = etPrice.getText().toString();

        Double priceDouble = 0d;
        if(!price.isEmpty()) {
            priceDouble = Double.valueOf(price);
        }
        return priceDouble;
    }

    public interface CallbackFilterDialog {
        void onFilterApplied(List<DocumentInfo> documentInfo);
    }
}
