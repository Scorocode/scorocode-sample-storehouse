package prof_itgroup.ru.storehouseapp.Helpers;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackGetDocumentById;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackRemoveDocument;
import ru.profit_group.scorocode_sdk.Responses.data.ResponseRemove;
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;

/**
 * Created by peterstaranchuk on 10/31/16.
 */

public class DocumentHelper {
    public static void removeDocument(final Context context, Document document) {
        document.removeDocument(new CallbackRemoveDocument() {
            @Override
            public void onRemoveSucceed(ResponseRemove responseRemove) {
                Toast.makeText(context, context.getString(R.string.document_removed), Toast.LENGTH_SHORT).show();
                ((Activity)context).finish();
            }

            @Override
            public void onRemoveFailed(String errorCode, String errorMessage) {
                Toast.makeText(context, context.getString(R.string.error_document_not_removed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void fetchAndRemoveDocument(final Context context, final Document document, DocumentInfo documentInfo) {
        document.getDocumentById(documentInfo.getId(), new CallbackGetDocumentById() {
            @Override
            public void onDocumentFound(DocumentInfo documentInfo) {
                removeDocument(context, document);
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                Helper.showToast(context, R.string.error_document_not_removed);
            }
        });
    }
}
