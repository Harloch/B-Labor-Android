package com.barelabor.barelabor.data.model;

import java.util.List;

/**
 * Created by mac on 4/5/2016.
 */
public class MakeModel extends DataObject{

    private List<String> makeList;

    public List<String> getMakeList() {
        return makeList;
    }

    public void setMakeList(List<String> modelList) {
        this.makeList = modelList;
    }
}
