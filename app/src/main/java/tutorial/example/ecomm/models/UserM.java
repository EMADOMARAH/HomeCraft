package tutorial.example.ecomm.models;

@SuppressWarnings("unused")
public class UserM {
    private String userId, email, userName, profPic;
    private boolean admin = false;

    public UserM() {
    }

    public UserM(String userId, String email, String userName, String profPic) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.profPic = profPic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfPic() {
        return profPic;
    }

    public void setProfPic(String profPic) {
        this.profPic = profPic;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
