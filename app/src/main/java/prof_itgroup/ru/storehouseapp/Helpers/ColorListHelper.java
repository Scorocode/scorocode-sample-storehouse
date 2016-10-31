package prof_itgroup.ru.storehouseapp.Helpers;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;

import java.util.Map;

import prof_itgroup.ru.storehouseapp.Objects.ColorState;

/**
 * Created by Peter Staranchuk on 10/27/16
 */

public class ColorListHelper {

    public static void refreshColorsList(EditText etDeviceColors, Map<String, ColorState> deviceColorsStates) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        for(String color : deviceColorsStates.keySet()) {
            if(!spannableStringBuilder.toString().isEmpty()) {
                spannableStringBuilder.append(", ");
            }

            ColorState colorState = deviceColorsStates.get(color);

            Spannable colorText = new SpannableString(color);
            colorText.setSpan(new ForegroundColorSpan(colorState.getColor(etDeviceColors.getContext())), 0, colorText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannableStringBuilder.append(colorText);
        }

        etDeviceColors.setText(spannableStringBuilder);
    }


}
