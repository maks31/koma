package com.koma.logintest.interfaces;

import android.content.Context;

/**
 * Created by koushal on 3/2/2017.
 */
public interface GetAddressFromLatLng {
    public void getAddressFromLatLngCallBack(Context mContext, String lat, String lng, String address);

}
