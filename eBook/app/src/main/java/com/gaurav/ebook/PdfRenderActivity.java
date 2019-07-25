package com.gaurav.ebook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class PdfRenderActivity extends AppCompatActivity {

    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 33;
    ProgressBar progressBar;
    PDFView pdfView;
    String mURL = "https://geohex.com.au/wp-content/uploads/2017/12/sample.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_render);
        pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.pb);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            downloadFile();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                downloadFile();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    private void downloadFile() {
        progressBar.setVisibility(View.VISIBLE);
        DownloadFileTask task = new DownloadFileTask(
                this,
                mURL,
                "/grv/pdf_file.pdf");
        task.startTask();
    }


    public void onFileDownloaded(String fileName) {
        progressBar.setVisibility(View.GONE);
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + fileName);
        if (file.exists()) {
            pdfView.fromFile(file)
                    //.pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
                    .enableSwipe(true)
                    .swipeHorizontal(true)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .enableAnnotationRendering(true)
                    .password(null)
                    .scrollHandle(null)
                    .onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            pdfView.setMinZoom(1f);
                            pdfView.setMidZoom(5f);
                            pdfView.setMaxZoom(10f);
                            pdfView.zoomTo(2f);
                            pdfView.scrollTo(100, 0);
                            pdfView.moveTo(0f, 0f);
                        }
                    })
                    .load();

        } else
            Toast.makeText(this, "DOesnt exist", Toast.LENGTH_SHORT).show();
    }

    public class DownloadFileTask {
        public static final String TAG = "DownloadFileTask";

        private PdfRenderActivity context;
        private GetTask contentTask;
        private String url;
        private String fileName;

        public DownloadFileTask(PdfRenderActivity context, String url, String fileName) {
            this.context = context;
            this.url = url;
            this.fileName = fileName;
        }

        public void startTask() {
            doRequest();
        }

        private void doRequest() {
            contentTask = new GetTask();
            contentTask.execute();
        }

        private class GetTask extends AsyncTask<String, Integer, String> {

            @Override
            protected String doInBackground(String... arg0) {
                int count;
                try {
                    Log.d(TAG, "url = " + url);
                    URL _url = new URL(url);
                    URLConnection conection = _url.openConnection();
                    conection.connect();
                    InputStream input = new BufferedInputStream(_url.openStream(),
                            8192);
                    OutputStream output = new FileOutputStream(
                            Environment.getExternalStorageDirectory() + fileName);
                    byte data[] = new byte[1024];
                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }
                return null;
            }

            protected void onPostExecute(String data) {
                context.onFileDownloaded(fileName);
            }
        }
    }
}
