package com.example.tictactoemultiplayeredition;

import java.util.ArrayList;

public class Game {
    String username_user1;
    String username_user2;
    String game_status;
    String r1c1;
    String r1c2;
    String r1c3;
    String r2c1;
    String r2c2;
    String r2c3;
    String r3c1;
    String r3c2;
    String r3c3;
    String turn;
    String won;
    String terminated_by;

    public String getUsername_user1() {
        return username_user1;
    }

    public void setUsername_user1(String username_user1) {
        this.username_user1 = username_user1;
    }

    public String getUsername_user2() {
        return username_user2;
    }

    public void setUsername_user2(String username_user2) {
        this.username_user2 = username_user2;
    }

    public String getGame_status() {
        return game_status;
    }

    public void setGame_status(String game_status) {
        this.game_status = game_status;
    }

    public String getR1c1() {
        return r1c1;
    }

    public void setR1c1(String r1c1) {
        this.r1c1 = r1c1;
    }

    public String getR1c2() {
        return r1c2;
    }

    public void setR1c2(String r1c2) {
        this.r1c2 = r1c2;
    }

    public String getR1c3() {
        return r1c3;
    }

    public void setR1c3(String r1c3) {
        this.r1c3 = r1c3;
    }

    public String getR2c1() {
        return r2c1;
    }

    public void setR2c1(String r2c1) {
        this.r2c1 = r2c1;
    }

    public String getR2c2() {
        return r2c2;
    }

    public void setR2c2(String r2c2) {
        this.r2c2 = r2c2;
    }

    public String getR2c3() {
        return r2c3;
    }

    public void setR2c3(String r2c3) {
        this.r2c3 = r2c3;
    }

    public String getR3c1() {
        return r3c1;
    }

    public void setR3c1(String r3c1) {
        this.r3c1 = r3c1;
    }

    public String getR3c2() {
        return r3c2;
    }

    public void setR3c2(String r3c2) {
        this.r3c2 = r3c2;
    }

    public String getR3c3() {
        return r3c3;
    }

    public void setR3c3(String r3c3) {
        this.r3c3 = r3c3;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getWon() {
        return won;
    }

    public void setWon(String won) {
        this.won = won;
    }

    public String getTerminated_by() {
        return terminated_by;
    }

    public void setTerminated_by(String terminated_by) {
        this.terminated_by = terminated_by;
    }

    public Game(String username_user1, String username_user2, String game_status, String r1c1, String r1c2, String r1c3, String r2c1, String r2c2, String r2c3, String r3c1, String r3c2, String r3c3, String turn, String won, String terminated_by) {
        this.username_user1 = username_user1;
        this.username_user2 = username_user2;
        this.game_status = game_status;
        this.r1c1 = r1c1;
        this.r1c2 = r1c2;
        this.r1c3 = r1c3;
        this.r2c1 = r2c1;
        this.r2c2 = r2c2;
        this.r2c3 = r2c3;
        this.r3c1 = r3c1;
        this.r3c2 = r3c2;
        this.r3c3 = r3c3;
        this.turn = turn;
        this.won = won;
        this.terminated_by = terminated_by;
    }
}
