package com.br.task;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import com.br.awesomewallpapers.R;

import java.io.IOException;

/**
 * Created by techresult on 29/10/2014.
 */
public class SetWallpaperTask extends AsyncTask<Bitmap, Void, Boolean> {

    private Context ctx;
    private ProgressDialog progress;

    public SetWallpaperTask(Context ctx){
        this.ctx = ctx;
    }

    @Override
    protected Boolean doInBackground(Bitmap... params) {
        boolean success = false;
        WallpaperManager wm = WallpaperManager.getInstance(ctx);
        Bitmap bitmap = params[0];
        try {
            wm.setBitmap(bitmap);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();

        progress = new ProgressDialog(ctx);
        progress.setTitle("Wallpaper");
        progress.setMessage("Setting wallpaper...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    @Override
    protected void onPostExecute(Boolean result){
        super.onPostExecute(result);

        if(result){
            Toast.makeText(ctx, ctx.getString(R.string.toast_wallpaper_set), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(ctx, ctx.getString(R.string.toast_wallpaper_set_failed), Toast.LENGTH_SHORT).show();
        }

        if(progress != null){
            progress.dismiss();
        }
    }
}
