package com.kspu.dimonn445.museum;

/**
 * Created by dimonn445 on 13.02.16.
 */
public class ExhibitsList {
    String exhibitname;
    String description;
    String date;
    String image;
    boolean check;
String img_fav_name;
    ExhibitsList(String _exhibitname, String _description, String _image, String _date, boolean _check, String _img_fav_name) {
        exhibitname = _exhibitname;
        description = _description;
        date = _date;
        image = _image;
        check = _check;
        img_fav_name = _img_fav_name;
    }
}
