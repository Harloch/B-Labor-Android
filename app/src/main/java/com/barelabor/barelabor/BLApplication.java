package com.barelabor.barelabor;

import android.app.Application;
import android.content.SharedPreferences;

import com.barelabor.barelabor.data.DataProvider;
import com.barelabor.barelabor.data.DataProviderCallback;
import com.barelabor.barelabor.util.MessageService;
import com.barelabor.barelabor.util.NetworkMonitor;
import com.barelabor.barelabor.util.Shared_Preferences;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

/**
 * Created by mac on 2/5/2016.
 */
public class BLApplication extends Application {

    private NetworkMonitor networkMonitor;
    private DataProviderCallback dataProviderCallback;
    private DataProvider dataProvider;
    private MessageService messageService;
    private Shared_Preferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        this.messageService = new MessageService();
        this.networkMonitor = new NetworkMonitor(this);

        this.dataProvider = new DataProvider(this, getResources().getConfiguration().locale, this.networkMonitor);
        this.networkMonitor.setNetworkAvailabilityChangedListener(this.dataProvider);
        this.dataProviderCallback = new DataProviderCallback(this.dataProvider);
        this.sharedPreferences = new Shared_Preferences(this);
    }

    public NetworkMonitor getNetworkMonitor() {
        return networkMonitor;
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public DataProviderCallback getDataProviderCallback() {
        System.out.println("dataProviderCallback = " + dataProviderCallback);
        return dataProviderCallback;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public Shared_Preferences getSharedPreferences() {
        return sharedPreferences;
    }
}
