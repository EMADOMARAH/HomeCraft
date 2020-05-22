package tutorial.example.ecomm.models;

import java.io.Serializable;

public class CollectionM implements Serializable {
    private String name, pic, picName, img, imgName, desc;

    public CollectionM() {
    }

    public CollectionM(String name, String pic, String picName, String img, String imgName, String desc) {
        this.name = name;
        this.pic = pic;
        this.picName = picName;
        this.img = img;
        this.imgName = imgName;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
