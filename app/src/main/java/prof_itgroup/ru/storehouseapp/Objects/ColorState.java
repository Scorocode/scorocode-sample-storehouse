package prof_itgroup.ru.storehouseapp.Objects;

import android.content.Context;

/**
 * Created by peterstaranchuk on 10/31/16.
 */

public enum ColorState {
    NEW(android.R.color.holo_green_dark),
    TO_REMOVE(android.R.color.holo_red_dark),
    FROM_DB(android.R.color.black);

    private int color;

    ColorState(int color) {
        this.color = color;
    }

    public int getColor(Context context) {
        return context.getResources().getColor(color);
    }
}