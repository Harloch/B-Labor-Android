package com.barelabor.barelabor.data.model;

import java.util.List;

/**
 * Created by mac on 5/5/2016.
 */
public class TireModel extends DataObject {
    private List<String> modellList;

    public List<String> getModelList() {
        return modellList;
    }

    public void setModelList(List<String> modellList) {
        this.modellList = modellList;
    }
}
