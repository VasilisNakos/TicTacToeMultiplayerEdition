package com.example.tictactoemultiplayeredition;

public class USER {
    String user_id;
    String email;
    String username;
    String points;
    String pfp_id;



    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getPfp_id() {
        return pfp_id;
    }

    public void setPfp_id(String pfp_id) {
        this.pfp_id = pfp_id;
    }

    public USER(String user_id, String email, String username, String points, String pfp_id) {
        this.user_id = user_id;
        this.email = email;
        this.username = username;
        this.points = points;
        this.pfp_id = pfp_id;
    }
}
