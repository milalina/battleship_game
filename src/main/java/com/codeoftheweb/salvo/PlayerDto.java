package com.codeoftheweb.salvo;

public class PlayerDto {

    long id;
    String email;

    public PlayerDto(long id, String email) {
        this.id = id;
        this.email=email;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
