package com.barelabor.barelabor.data;

import com.barelabor.barelabor.data.model.DataObject;

public abstract interface IDataProviderCallback
{
  public abstract void onDataProviderError(WebServiceRequestType requestType, DataError error);

  public abstract void onDataReceived(WebServiceRequestType requestType, DataObject dataObject);
}
