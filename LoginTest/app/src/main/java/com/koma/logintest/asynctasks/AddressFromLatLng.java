package com.koma.logintest.asynctasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;


import com.koma.logintest.interfaces.GetAddressFromLatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressFromLatLng extends AsyncTask<Void, Void, Void> {


    Context mContext;
    String lat, lng;

    String result = null;

    GetAddressFromLatLng getAddressFromLatLng;

    public AddressFromLatLng(Context mContext, String lat, String lng) {
        this.mContext = mContext;
        this.lat = lat;
        this.lng = lng;


        getAddressFromLatLng= (GetAddressFromLatLng) mContext;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    Double.parseDouble(lat), Double.parseDouble(lng),1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(",");
                }
                //sb.append(address.getLocality());
                //sb.append(address.getPostalCode());
                sb.append(address.getCountryName());
                result = sb.toString();

                String postalCode = address.getPostalCode();
                result=postalCode;
            }
        } catch (IOException e) {
            Log.e("", "doInBackground: "+e.getLocalizedMessage() );
            // Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            result="not found";
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        update data
        getAddressFromLatLng.getAddressFromLatLngCallBack(mContext,lat,lng,result);
    }
}
