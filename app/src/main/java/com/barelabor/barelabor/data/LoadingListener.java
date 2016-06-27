package com.barelabor.barelabor.data;

import java.util.List;

public interface LoadingListener {
    
	public void onLoadingComplete(List obj);

	public void onLoadingComplete(Object obj);
	
    public void onError(Object error);
};

