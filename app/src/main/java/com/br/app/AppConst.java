package com.br.app;

/**
 * Created by techresult on 21/10/2014.
 */
public class AppConst {
    //Number of columns in GridView
    //By default 2 but the user can configure this in settings activity
    public static final int NUM_OF_COLUMNS = 2;

    //GridView image padding
    public static final int GRID_PADDING = 4; //in dp

    //Gallery directory name to save wallpapers
    public static final String SDCARD_DIR_NAME = "Awesome wallpapers";

    // Picasa/Google web album username
    public static final String PICASA_USER = "bruno.pardini6";

    //Public albums list url
    public static final String URL_PICASA_ALBUMS = "https://picasaweb.google.com/data/feed/api/user/_PICASA_USER_?kind=album&alt=json";

    //Picasa album photos url
    public static final String URL_ALBUM_PHOTOS = "https://picasaweb.google.com/data/feed/api/user/_PICASA_USER_/albumid/_ALBUM_ID_?alt=json";

    //Picasa recenlty added photos url
    public static final String URL_RECENTLY_ADDED = "https://picasaweb.google.com/data/feed/api/user/_PICASA_USER_?kind=photo&alt=json";
}
