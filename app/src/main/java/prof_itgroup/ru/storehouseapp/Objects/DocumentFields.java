package prof_itgroup.ru.storehouseapp.Objects;

import android.content.Context;

import prof_itgroup.ru.storehouseapp.R;

/**
 * Created by Peter Staranchuk on 10/25/16
 */

public enum DocumentFields {

    DEVICE_NAME(R.string.fieldDeviceName),
    PLATFORM(R.string.fieldPlatformName),
    CAMERA_INFO(R.string.fieldCameraInfo),
    COLORS_AVAILABLE(R.string.fieldColorsAvailable),
    DEVICE_PRICE(R.string.fieldDevicePrice),
    LAST_SEND(R.string.fieldLastSend),
    BUYERS(R.string.fieldBuyers);

    private int fieldNameId;

    DocumentFields(int fieldNameId) {
        this.fieldNameId = fieldNameId;
    }

    public String getFieldName(Context context) {
        return context.getString(fieldNameId);
    }
}
