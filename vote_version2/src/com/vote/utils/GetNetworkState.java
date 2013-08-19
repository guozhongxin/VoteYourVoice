package com.vote.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class GetNetworkState {
	public static final int WIFI = 1;
	public static final int CMWAP = 2;
	public static final int CMNET = 3;

	public static int getAPNType(Context context){  
        int netType = 0;   
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();  
          
        if(networkInfo == null){  
            return netType;  
        }  
        int nType = networkInfo.getType();  
        if(nType==ConnectivityManager.TYPE_MOBILE){  
            Log.e("networkInfo.getExtraInfo()", "networkInfo.getExtraInfo() is "+networkInfo.getExtraInfo());  
            if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet")){  
                netType = CMNET;  
            }  
            else{  
                netType = CMWAP;  
            }  
        }  
        else if(nType==ConnectivityManager.TYPE_WIFI){  
            netType = WIFI;  
        }  
        return netType;  
    }  
}