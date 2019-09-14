package com.codeoftheweb.salvo;

public class GamePlayerDto {
    long id;
    PlayerDto player;

    public GamePlayerDto(long id, PlayerDto player) {
        this.id = id;
        this.player = player;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PlayerDto getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDto player) {
        this.player = player;
    }
}
