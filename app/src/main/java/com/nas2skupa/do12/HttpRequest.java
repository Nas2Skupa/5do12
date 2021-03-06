package com.nas2skupa.do12;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Ranko on 22.11.2014..
 */
public class HttpRequest {
    private boolean silent;
    private Context context;
    private ProgressDialog progressDialog;

    public HttpRequest(Context context, Uri uri, boolean silent) {
        this.context = context;
        this.silent = silent;
        new HttpWorker().execute(uri);
    }

    private class HttpWorker extends AsyncTask<Uri, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(silent) return;
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.pleaseWait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Uri... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            return serviceHandler.makeServiceCall(params[0].toString(), ServiceHandler.GET);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!silent && progressDialog.isShowing())
                progressDialog.dismiss();
            if (result == null || result.contains("HttpException")) {
                Toast toast = Toast.makeText(context, R.string.serverError, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
                result = null;
            }
            if (onHttpResultListener != null)
                onHttpResultListener.onHttpResult(result);
        }
    }

    private OnHttpResultListener onHttpResultListener;

    public interface OnHttpResultListener {
        public void onHttpResult(String result);
    }

    public void setOnHttpResultListener(OnHttpResultListener listener) {
        onHttpResultListener = listener;
    }
}
