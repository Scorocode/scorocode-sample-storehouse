package prof_itgroup.ru.storehouseapp.Objects;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import prof_itgroup.ru.storehouseapp.Activities.ItemDetailsActivity;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;

/**
 * Created by Peter Staranchuk on 10/25/16
 */

public class StoredItemsAdapter extends BaseAdapter {
    private Context context;
    private List<DocumentInfo> storedItems;
    private int layoutId;
    private LayoutInflater inflater;
    private DocumentFields fields;

    public StoredItemsAdapter(Context context, @NonNull List<DocumentInfo> storedItems, int layoutId) {
        this.context = context;
        this.storedItems = storedItems;
        this.layoutId = layoutId;
        inflater = LayoutInflater.from(context);
        fields = new DocumentFields(context, null);
    }

    @Override
    public int getCount() {
        return storedItems.size();
    }

    @Override
    public Object getItem(int position) {
        return storedItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        customizeView(view, holder, storedItems.get(position));

        return view;
    }

    private void customizeView(View view, ViewHolder holder, final DocumentInfo documentInfo) {
        String deviceName = (String) documentInfo.getFields().get(fields.getDeviceNameField());
        String devicePlatform = (String) documentInfo.getFields().get(fields.getPlatformField());
        Double devicePrice = (Double) documentInfo.getFields().get(fields.getDevicePriceField());

        holder.tvStoredItemName.setText(deviceName);
        holder.tvStoredItemStatus.setText(devicePlatform);
        holder.tvStoredItemPrice.setText(String.valueOf(devicePrice));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemDetailsActivity.display(context, documentInfo);
            }
        });
    }

    static class ViewHolder {
        @BindView(R.id.tvStoredItemName) TextView tvStoredItemName;
        @BindView(R.id.tvStoredItemPlatform) TextView tvStoredItemStatus;
        @BindView(R.id.tvStoredItemPrice) TextView tvStoredItemPrice;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
