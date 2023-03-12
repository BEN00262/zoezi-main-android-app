package com.zoeziMitzanimedia.androidapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Environment;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class ZoeziDownloadInterface extends ContextWrapper {
    NotificationHelper notificationHelper;
    Context context;

    public ZoeziDownloadInterface(Context base) {
        super(base);
        context = base;
        notificationHelper = new NotificationHelper(base);
    }

    @JavascriptInterface
    public void shareApp(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String value = String.format("%s%s",BaseURLs.playStoreURL,context.getApplicationContext().getPackageName());
        intent.putExtra(Intent.EXTRA_TEXT,value);
        startActivity(Intent.createChooser(intent,"Share Via"));
    }

    @JavascriptInterface
    public void getBase64FromBlobData(String base64Data) throws IOException {
        convertBase64StringToPdfAndStoreIt(base64Data);
    }

    public String getBase64StringFromBlobUrl(String blobUrl) {
        if(blobUrl.startsWith("blob")){
            return "javascript: var xhr = new XMLHttpRequest();" +
                    "xhr.open('GET', '"+ blobUrl +"', true);" +
                    "xhr.setRequestHeader('Content-type','image/png');" +
                    "xhr.responseType = 'blob';" +
                    "xhr.onload = function(e) {" +
                    "    if (this.status == 200) {" +
                    "        var blobPng = this.response;" +
                    "        var reader = new FileReader();" +
                    "        reader.readAsDataURL(blobPng);" +
                    "        reader.onloadend = function() {" +
                    "            base64data = reader.result;" +
                    "            Android.getBase64FromBlobData(base64data);" +
                    "        }" +
                    "    }" +
                    "};" +
                    "xhr.send();";
        }
        return "javascript: console.log('It is not a Blob URL');";
    }

    private void convertBase64StringToPdfAndStoreIt(String base64PDf) throws IOException {
        String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
        final File dwldsPath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/zoezi_report_" + currentDateTime + "_.png");

        byte[] pngAsBytes = Base64.decode(base64PDf.replaceFirst("^data:image/png;base64,", ""), 0);
        FileOutputStream os;
        os = new FileOutputStream(dwldsPath, false);
        os.write(pngAsBytes);
        os.flush();

        if (dwldsPath.exists()) {
            notificationHelper.createNotification(dwldsPath);
        }
        Toast.makeText(this.getApplicationContext(), "File Downloaded!", Toast.LENGTH_SHORT).show();
    }
}
