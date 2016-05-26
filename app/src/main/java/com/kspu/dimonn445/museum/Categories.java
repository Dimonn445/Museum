package com.kspu.dimonn445.museum;

/**
 * Created by dimonn445 on 11.02.16.
 */
public class Categories {

    String name;
    String image;
    boolean checkImgButon = false;

    Categories(String _name, String _image, boolean _checkImgButon) {
        name = _name;
        image = _image;
        checkImgButon = _checkImgButon;
    }
}