package com.barelabor.barelabor.data.model;

/**
 * Created by mac on 4/5/2016.
 */
public class EstimateModel extends DataObject{
    private String estimateId;
    private String lowPrice;
    private String highPrice;
    private String avgPrice;
    private String repairArrayString;
    private String highCostArrayString;
    private String averageCostArrayString;
    private String lowCostArrayString;

    public String getEstimateId() {
        return estimateId;
    }

    public void setEstimateId(String estimateId) {
        this.estimateId = estimateId;
    }

    public String getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(String lowPrice) {
        this.lowPrice = lowPrice;
    }

    public String getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(String highPrice) {
        this.highPrice = highPrice;
    }

    public String getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(String avgPrice) {
        this.avgPrice = avgPrice;
    }

    public String getRepairArrayString() {
        return repairArrayString;
    }

    public void setRepairArrayString(String repairArrayString) {
        this.repairArrayString = repairArrayString;
    }
    public String getHighCostArrayStringPrice() {
        return highCostArrayString;
    }

    public void setHighCostArrayStringPrice(String highCostArrayString) {
        this.highCostArrayString = highCostArrayString;
    }
    public String getAverageCostArrayStringPrice() {
        return averageCostArrayString;
    }

    public void setAverageCostArrayStringPrice(String averageCostArrayString) {
        this.averageCostArrayString = averageCostArrayString;
    }
    public String getLowCostArrayStringPrice() {
        return lowCostArrayString;
    }

    public void setLowCostArrayStringPrice(String lowCostArrayString) {
        this.lowCostArrayString = lowCostArrayString;
    }
}
