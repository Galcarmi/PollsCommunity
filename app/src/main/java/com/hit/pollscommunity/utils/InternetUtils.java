package com.hit.pollscommunity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


public class Utils {

    public static boolean checkForInternetConnection(Activity activity ){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if( !(activeNetworkInfo != null && activeNetworkInfo.isConnected())){
            Toast.makeText(activity, activity.getResources().getString(R.string.no_internet_connection),Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }
}
