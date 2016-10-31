package prof_itgroup.ru.storehouseapp.Objects;

import android.content.Context;

import prof_itgroup.ru.storehouseapp.Helpers.Helper;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackUploadFile;
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;

import static ru.profit_group.scorocode_sdk.scorocode_objects.Base64.encode;

/**
 * Created by Peter Staranchuk on 10/31/16
 */

public class FileHelper {

    public static final String FILE_NAME = "itemInfo.txt";

    public static void uploadFile(final Context context, Document document, String content) {
        document.uploadFile(new DocumentFields(context).getSendInfoField(), FILE_NAME, encode(content.getBytes()), new CallbackUploadFile() {
            @Override
            public void onDocumentUploaded() {

            }

            @Override
            public void onDocumentUploadFailed(String errorCode, String errorMessage) {
                Helper.showToast(context, R.string.error);
            }
        });
    }




}
