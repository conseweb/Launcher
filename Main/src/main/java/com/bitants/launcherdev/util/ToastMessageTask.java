package com.bitants.launcherdev.util;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by michael on 15/6/4.
 *
 * to use:
 *    new ToastMessageTask(ctx).execute("This is a TOAST message!");
 */
public class ToastMessageTask extends AsyncTask<String, String, String> {

    private String toastMessage;
    private Context mCtx;

    public ToastMessageTask(Context ctx) {
        mCtx = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        toastMessage = params[0];
        return toastMessage;
    }

    protected void OnProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    // This is executed in the context of the main GUI thread
    protected void onPostExecute(String result) {
        Toast toast = Toast.makeText(mCtx, result, Toast.LENGTH_SHORT);
        toast.show();
    }
}
