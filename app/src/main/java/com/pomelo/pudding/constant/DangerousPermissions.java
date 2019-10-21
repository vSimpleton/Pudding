package com.pomelo.pudding.constant;

import android.Manifest;

/**
 * Created by Sherry on 2019/10/8
 * 危险权限组常量
 */

public class DangerousPermissions {

    /**
     * permission group : CAMERA
     * CAMERA
     */
    public static final String CAMERA = Manifest.permission.CAMERA;

    /**
     * permission group : CONTACTS
     * READ_CONTACTS
     * WRITE_CONTACTS
     * GET_ACCOUNTS
     */
    public static final String CONTACTS = Manifest.permission.READ_CONTACTS;

    /**
     * permission group : LOCATION
     * ACCESS_FINE_LOCATION
     * ACCESS_COARSE_LOCATION
     */
    public static final String[] LOCATION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    /**
     * permission group : MICROPHONE
     * RECORD_AUDIO
     */
    public static final String MICROPHONE = Manifest.permission.RECORD_AUDIO;

    /**
     * permission group : SMS
     * SEND_SMS
     * RECEIVE_SMS
     * READ_SMS
     * RECEIVE_WAP_PUSH
     * RECEIVE_MMS
     */
    public static final String SMS = Manifest.permission.SEND_SMS;

    /**
     * permission group : STORAGE
     * READ_EXTERNAL_STORAGE
     * WRITE_EXTERNAL_STORAGE
     */
    public static final String STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

}
