package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity

public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    private Date turn;

    @ElementCollection
    @Column(name="salvoLocation")
    private List<String> salvoLocations;

    public Salvo() {
        this.salvoLocations = new ArrayList<>();
    }

    public Salvo(/*GamePlayer gamePlayer*/ Date turn, List<String> salvoLocations) {
        //this.gamePlayer = gamePlayer;
        this.turn = turn;
        this.salvoLocations = salvoLocations;
    }

    public Salvo(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Date getTurn() {
        return turn;
    }

    public void setTurn(Date turn) {
        this.turn = turn;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        if (salvoLocations.size()>=5){
        this.salvoLocations = salvoLocations;}
    }

}

/*
public Map<String, Object> makeSalvoDTOMap() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("1", this.getSalvoLocations());
        dto.put("2", this.getSalvoLocations());
        return dto;
    }
*/