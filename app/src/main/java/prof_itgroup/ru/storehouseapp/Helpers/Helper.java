package prof_itgroup.ru.storehouseapp.Helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import prof_itgroup.ru.storehouseapp.R;


/**
 * Created by Peter Staranchuk on 10/27/16
 */

public class Helper {

    @NonNull
    public static String getStringFrom(EditText editText) {
        if(editText != null) {
            return editText.getText().toString();
        } else {
            return "";
        }
    }

    @NonNull
    public static List<String> getColorsListFrom(String deviceColors) {
        List<String> colors = new ArrayList<>();

        for(String color: deviceColors.split(", ")) {
            if(!color.isEmpty()) {
                 colors.add(color.trim());
            }
        }
        return colors;
    }

    public static void showEditTextDialog(Context context, int titleId, int inputType, final CallbackEditTextDialog callbackEditTextDialog) {

        final EditText editText = new EditText(context);
        editText.setInputType(inputType);

        new AlertDialog.Builder(context)
                .setTitle(titleId)
                .setView(editText)
                .setPositiveButton(R.string.continue_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(callbackEditTextDialog != null) {
                            callbackEditTextDialog.onContinueClicked(getStringFrom(editText));
                        }
                    }
                })
                .setNegativeButton(R.string.close, null)
                .show();
    }

    public static void showToast(Context context, int textRes) {
        Toast.makeText(context, context.getString(textRes), Toast.LENGTH_SHORT).show();
    }

    public interface CallbackEditTextDialog{
        void onContinueClicked(String editTextContent);
    }
}
