package tutorial.example.ecomm.models;

@SuppressWarnings("unused")
public class ShipmentM {
    private String name, address, city, zip, country, method;

    public ShipmentM() {
    }

    public ShipmentM(String name, String address, String city, String zip, String country, String method) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.country = country;
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
