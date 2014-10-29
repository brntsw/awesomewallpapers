package com.br.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.br.awesomewallpapers.R;
import com.br.utils.PrefManager;
import com.br.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by techresult on 29/10/2014.
 */
public class DownloadWallpaperTask extends AsyncTask<Object, Void, String> {

    private Context ctx;
    private String TAG = Utils.class.getSimpleName();
    private ProgressDialog progress;

    public DownloadWallpaperTask(Context ctx){
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(Object... params) {
        File file = (File) params[0];
        Bitmap bitmap = (Bitmap) params[1];

        String absolutePath = "";

        try{
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            absolutePath = file.getAbsolutePath();
            Log.d(TAG, "Wallpaper saved to: " + file.getAbsolutePath());
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return absolutePath;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();

        progress = new ProgressDialog(ctx);
        progress.setTitle("Download");
        progress.setMessage("Downloading wallpaper");
        progress.setProgressStyle(progress.STYLE_HORIZONTAL);
        progress.setProgress(0);
        progress.setMax(20);
        progress.show();
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);

        PrefManager pref = new PrefManager(ctx);

        if(!result.equals("")){
            Toast.makeText(ctx, ctx.getString(R.string.toast_saved).replace("#", "\"" + pref.getGalleryName() + "\""), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(ctx, ctx.getString(R.string.toast_saved_failed), Toast.LENGTH_SHORT).show();
        }

        progress.dismiss();
    }
}
