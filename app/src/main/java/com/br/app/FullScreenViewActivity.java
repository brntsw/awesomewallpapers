package com.br.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.br.awesomewallpapers.R;
import com.br.crop.CropImage;
import com.br.model.Wallpaper;
import com.br.utils.TouchImageView;
import com.br.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by techresult on 22/10/2014.
 */
public class FullScreenViewActivity extends Activity implements View.OnClickListener{
    private static final String TAG = FullScreenViewActivity.class.getSimpleName();
    public static final String TAG_SEL_IMAGE = "selectedImage";
    private Wallpaper selectedPhoto;
    private TouchImageView imageView;
    private LinearLayout setWallpaper, downloadWallpaper, cropWallpaper;
    private Utils utils;
    private ProgressBar pbLoader;
    private final int REQUEST_CODE_CROP_IMAGE = 1;
    private String imageName = "";

    // Picasa JSON response node keys
    private static final String TAG_ENTRY = "entry",
            TAG_MEDIA_GROUP = "media$group",
            TAG_MEDIA_CONTENT = "media$content", TAG_IMG_URL = "url",
            TAG_IMG_WIDTH = "width", TAG_IMG_HEIGHT = "height";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        Intent intent = getIntent();
        String crop = intent.getStringExtra("crop");
        if(crop != null){
            finish();
        }

        imageView = (TouchImageView) findViewById(R.id.imgFullScreen);
        setWallpaper = (LinearLayout) findViewById(R.id.setWallpaper);
        cropWallpaper = (LinearLayout) findViewById(R.id.crop);
        downloadWallpaper = (LinearLayout) findViewById(R.id.downloadWallpaper);
        pbLoader = (ProgressBar) findViewById(R.id.pbLoader);

        // hide the action bar in fullscreen mode
        if(getActionBar() != null){
            getActionBar().hide();
        }

        utils = new Utils(FullScreenViewActivity.this);

        // layout click listeners
        setWallpaper.setOnClickListener(this);
        cropWallpaper.setOnClickListener(this);
        downloadWallpaper.setOnClickListener(this);

        // setting layout buttons alpha/opacity
        //setWallpaper.getBackground().setAlpha(70);
        //downloadWallpaper.getBackground().setAlpha(70);

        Intent i = getIntent();
        selectedPhoto = (Wallpaper) i.getSerializableExtra(TAG_SEL_IMAGE);

        // check for selected photo null
        if (selectedPhoto != null) {

            // fetch photo full resolution image by making another json request
            fetchFullResolutionImage();

        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.msg_unknown_error), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Fetching image fullresolution json
     * */
    private void fetchFullResolutionImage() {
        String url = selectedPhoto.getPhotoJson();

        // show loader before making request
        pbLoader.setVisibility(View.VISIBLE);
        setWallpaper.setVisibility(View.GONE);
        downloadWallpaper.setVisibility(View.GONE);
        cropWallpaper.setVisibility(View.GONE);

        // volley's json obj request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Image full resolution json: " + response.toString());
                try {
                    // Parsing the json response
                    JSONObject entry = response.getJSONObject(TAG_ENTRY);

                    JSONArray mediacontentArry = entry.getJSONObject(
                            TAG_MEDIA_GROUP).getJSONArray(
                            TAG_MEDIA_CONTENT);

                    JSONObject mediaObj = (JSONObject) mediacontentArry.get(0);

                    String fullResolutionUrl = mediaObj.getString(TAG_IMG_URL);

                    // image full resolution widht and height
                    final int width = mediaObj.getInt(TAG_IMG_WIDTH);
                    final int height = mediaObj.getInt(TAG_IMG_HEIGHT);

                    Log.d(TAG, "Full resolution image. url: "
                            + fullResolutionUrl + ", w: " + width
                            + ", h: " + height);

                    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

                    // We download image into ImageView instead of
                    // NetworkImageView to have callback methods
                    // Currently NetworkImageView doesn't have callback
                    // methods

                    imageLoader.get(fullResolutionUrl,
                            new ImageLoader.ImageListener() {

                                @Override
                                public void onErrorResponse(
                                        VolleyError arg0) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            getString(R.string.msg_wall_fetch_error),
                                            Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onResponse(
                                        ImageLoader.ImageContainer response,
                                        boolean arg1) {
                                    if (response.getBitmap() != null) {
                                        // load bitmap into imageview
                                        imageView.setImageBitmap(response.getBitmap());
                                        adjustImageAspect(width, height);

                                        // hide loader and show set &
                                        // download buttons
                                        pbLoader.setVisibility(View.GONE);
                                        setWallpaper.setVisibility(View.VISIBLE);
                                        cropWallpaper.setVisibility(View.VISIBLE);
                                        downloadWallpaper.setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.msg_unknown_error),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                // unable to fetch wallpapers
                // either google username is wrong or
                // devices doesn't have internet connection
                Toast.makeText(getApplicationContext(),
                        getString(R.string.msg_wall_fetch_error),
                        Toast.LENGTH_LONG).show();

            }
        });

        // Remove the url from cache
        AppController.getInstance().getRequestQueue().getCache().remove(url);

        // Disable the cache for this url, so that it always fetches updated
        // json
        jsonObjReq.setShouldCache(false);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Adjusting the image aspect ration to scroll horizontally, Image height
     * will be screen height, width will be calculated respected to height
     * */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void adjustImageAspect(int bWidth, int bHeight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (bWidth == 0 || bHeight == 0)
            return;

        int sHeight = 0;

        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            sHeight = size.y;
        } else {
            Display display = getWindowManager().getDefaultDisplay();
            sHeight = display.getHeight();
        }

        int new_width = (int) Math.floor((double) bWidth * (double) sHeight / (double) bHeight);
        params.width = new_width;
        params.height = sHeight;

        Log.d(TAG, "Fullscreen image new dimensions: w = " + new_width + ", h = " + sHeight);

        imageView.setLayoutParams(params);
    }

    private void runCropImage(){
        //create explicit intent
        final Intent intent = new Intent(this, CropImage.class);

        //tell CropImage activity to look for image to crop
        final Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Type the name of the Wallpaper");

        //set up the input
        final EditText input = new EditText(this);
        //Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        //set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imageName = input.getText().toString();

                String filePath = utils.saveImageToSDCard(bitmap, imageName);

                intent.putExtra(CropImage.IMAGE_PATH, filePath);

                //allow CropImage to scale the image
                intent.putExtra(CropImage.SCALE, true);

                //if the aspect ratio is fixed to ratio 3/2
                intent.putExtra(CropImage.ASPECT_X, 3);
                intent.putExtra(CropImage.ASPECT_Y, 2);

                //Start activity CropImage with certain request code and listen for result
                startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQUEST_CODE_CROP_IMAGE:
                String path = null;
                if(data != null){
                    path = data.getStringExtra(CropImage.IMAGE_PATH);
                }

                //if nothing received
                if(path == null){
                    return;
                }

                Bundle extras = data.getExtras();

                //cropped bitmap
                Bitmap selectedBitmap = extras.getParcelable("data");

                imageView.setImageBitmap(selectedBitmap);
                break;
        }
    }

    /**
     * View click listener
     * */
    @Override
    public void onClick(View v) {
        final Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        switch (v.getId()) {
            // button Download Wallpaper tapped
            case R.id.downloadWallpaper:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Type the name of the Wallpaper");

                //set up the input
                final EditText input = new EditText(this);
                //Specify the type of input expected
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                //set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageName = input.getText().toString();

                        utils.saveImageToSDCard(bitmap, imageName);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;

            // button Set As Wallpaper tapped
            case R.id.setWallpaper:
                utils.setAsWallpaper(bitmap);
                break;
            case R.id.crop:
                runCropImage();

                break;
            default:
                break;
        }

    }
}
