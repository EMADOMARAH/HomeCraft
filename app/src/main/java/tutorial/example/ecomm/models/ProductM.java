package tutorial.example.ecomm.models;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unused")
public class ProductM implements Serializable {
    private long fakeId;
    private String productID, name, design, desc, img, image, ads, category, collection;
    private int available, quantity=1, sales=0, popularity=0, views=0;
    private float rating=0;
    private double price;
    private boolean featured;
    private List<String> pics;

    public ProductM() {
    }

    public ProductM(String productID, String name, String design, String desc, String img, String image, String ads, String category, double price, int available,boolean featured) {
        this.productID = productID;
        this.name = name;
        this.design = design;
        this.desc = desc;
        this.img = img;
        this.image = image;
        this.ads = ads;
        this.category = category;
        this.price = price;
        this.available = available;
        this.featured = featured;
    }

    public String getAds() {
        return ads;
    }

    public void setAds(String ads) {
        this.ads = ads;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public long getFakeId() {
        return fakeId;
    }

    public void setFakeId(long fakeId) {
        this.fakeId = fakeId;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesign() {
        return design;
    }

    public void setDesign(String design) {
        this.design = design;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
}
