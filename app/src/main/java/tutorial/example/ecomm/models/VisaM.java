package tutorial.example.ecomm.models;

@SuppressWarnings("unused")
public class VisaM {
    private String id, accName, bank, accNum, exDate, secCode;

    public VisaM() {
    }

    public VisaM(String id, String accName, String bank, String accNum, String exDate, String secCode) {
        this.id = id;
        this.accName = accName;
        this.bank = bank;
        this.accNum = accNum;
        this.exDate = exDate;
        this.secCode = secCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getAccNum() {
        return accNum;
    }

    public void setAccNum(String accNum) {
        this.accNum = accNum;
    }

    public String getSecCode() {
        return secCode;
    }

    public void setSecCode(String secCode) {
        this.secCode = secCode;
    }

    public String getExDate() {
        return exDate;
    }

    public void setExDate(String exDate) {
        this.exDate = exDate;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }
}
