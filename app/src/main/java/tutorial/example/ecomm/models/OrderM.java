package tutorial.example.ecomm.models;

import java.util.List;

@SuppressWarnings("unused")
public class OrderM {
    private long fakeID;
    private String orderID;
    private VisaM visaM;
    private ShipmentM shipmentM;
    private List<ProductM> productMList;
    private long date;
    private double total;

    public OrderM() {
    }

    public OrderM(String orderID, VisaM visaM, ShipmentM shipmentM, List<ProductM> productMList, long date, double total) {
        this.orderID = orderID;
        this.visaM = visaM;
        this.shipmentM = shipmentM;
        this.productMList = productMList;
        this.date = date;
        this.total = total;
    }

    public long getFakeID() {
        return fakeID;
    }

    public void setFakeID(long fakeID) {
        this.fakeID = fakeID;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public VisaM getVisaM() {
        return visaM;
    }

    public void setVisaM(VisaM visaM) {
        this.visaM = visaM;
    }

    public ShipmentM getShipmentM() {
        return shipmentM;
    }

    public void setShipmentM(ShipmentM shipmentM) {
        this.shipmentM = shipmentM;
    }

    public List<ProductM> getProductMList() {
        return productMList;
    }

    public void setProductMList(List<ProductM> productMList) {
        this.productMList = productMList;
    }
}
