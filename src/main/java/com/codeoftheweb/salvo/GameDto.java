package com.codeoftheweb.salvo;

import java.util.*;

public class GameDto {

    int id;
    Date created;
    List <GamePlayerDto> gamePlayer;

    public GameDto(int id, Date created, List<GamePlayerDto> gamePlayer) {
        this.id = id;
        this.created = created;
        this.gamePlayer = gamePlayer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public List<GamePlayerDto> getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(List<GamePlayerDto> gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

}
