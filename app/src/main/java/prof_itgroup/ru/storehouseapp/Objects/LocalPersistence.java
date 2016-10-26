package prof_itgroup.ru.storehouseapp.Objects;

/**
 * Created by Peter Staranchuk on 10/25/16
 */

import android.app.Activity;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class LocalPersistence {

    public final static String FILE_USER_INFO = "prof_itgroup.ru.storehouseapp.Objects.user_info";

    public synchronized static void writeObjectToFile(Context context, Object object, String filename) {

        ObjectOutputStream objectOut = null;
        if (object == null)
            context.deleteFile(filename);
        else {
            try {
                FileOutputStream fileOut = context.openFileOutput(filename, Activity.MODE_PRIVATE);
                objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(object);
                fileOut.getFD().sync();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (objectOut != null) {
                    try {
                        objectOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public synchronized static Object readObjectFromFile(Context context, String filename) {

        ObjectInputStream objectIn = null;
        Object object = null;
        boolean needRemove = false;
        try {

            boolean found = false;
            String[] arr = context.getApplicationContext().fileList();
            if (arr != null)
                for (String f : arr)
                    if (f != null && f.equals(filename))
                        found = true;
            if (!found) return null;

            FileInputStream fileIn = context.getApplicationContext().openFileInput(filename);
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            needRemove = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            needRemove = true;
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (needRemove) RemoveFile(context, filename);

        return object;
    }

    public static synchronized void RemoveFile(Context ctx, String filename) {
        try {
            ctx.getApplicationContext().deleteFile(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






}