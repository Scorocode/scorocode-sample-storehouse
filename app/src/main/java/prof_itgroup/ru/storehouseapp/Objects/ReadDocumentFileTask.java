package prof_itgroup.ru.storehouseapp.Objects;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import butterknife.ButterKnife;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;

/**
 * Created by Peter Staranchuk on 10/31/16
 */

public class ReadDocumentFileTask extends AsyncTask<Void, Void, String> {

    private DocumentFields fields;
    private LinearLayout llInfoContainer;
    private Document document;
    private EditText infoContainer;

    public ReadDocumentFileTask(Context context, Document document, LinearLayout llInfoContainer) {
        this.document = document;
        this.infoContainer = ButterKnife.findById(llInfoContainer, R.id.etItemInfo);
        fields = new DocumentFields(context);
        this.llInfoContainer = llInfoContainer;
    }

    @Override
    protected String doInBackground(Void... params) {
        String fileLink = document.getFileLink(fields.getSendInfoField(), FileHelper.FILE_NAME);
        String fileInfo = null;
        try {
            fileInfo = readUrl(fileLink);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileInfo;
    }

    @Override
    protected void onPostExecute(String fileData) {
        if(fileData != null && !fileData.trim().isEmpty()) {
            infoContainer.setText(fileData);
        } else {
            infoContainer.setText(R.string.no_info);
        }
        super.onPostExecute(fileData);
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }
}