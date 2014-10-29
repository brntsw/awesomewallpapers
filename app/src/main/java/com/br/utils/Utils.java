package com.br.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.br.awesomewallpapers.R;
import com.br.task.DownloadWallpaperTask;
import com.br.task.SetWallpaperTask;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

/**
 * Created by techresult on 21/10/2014.
 */
public class Utils {
    private String TAG = Utils.class.getSimpleName();
    private Context _context;
    private PrefManager pref;
    private String absolutePath = "";

    //Constructor
    public Utils(Context context){
        this._context = context;
        pref = new PrefManager(_context);
    }

    //getting screen width
    public int getScreenWidth(){
        int columnWidth;
        WindowManager wm = (WindowManager)_context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try{
            display.getSize(point);
        }
        catch(NoSuchMethodError ignore){
            point.x = display.getWidth();
            point.y = display.getHeight();
        }

        columnWidth = point.x;
        return columnWidth;
    }

    public String saveImageToSDCard(final Bitmap bitmap, String fileName){
        final File myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), pref.getGalleryName());
        myDir.mkdirs();

        String fname = "Wallpaper-" + fileName + ".jpg";
        File file = new File(myDir, fname);
        if(file.exists()){
            file.delete();
        }

        try {
            absolutePath = new DownloadWallpaperTask(_context).execute(file,bitmap).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return absolutePath;
    }

    public void setAsWallpaper(Bitmap bitmap){
        new SetWallpaperTask(_context).execute(bitmap);
    }
}
