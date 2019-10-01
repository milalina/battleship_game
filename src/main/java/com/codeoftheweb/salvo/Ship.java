package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity

public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    private String shipType;

    @ElementCollection
    @Column(name="shipLocation")
    private List<String> shipLocations = new ArrayList<>();

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public Ship() {
    }

   /* public Ship(long id, GamePlayer gamePlayer, String shipType, List<String> shipLocations) {
        this.id = id;
        this.gamePlayer = gamePlayer;
        this.shipType = shipType;
        this.shipLocations = shipLocations;
    }*/

    public Ship(String shipType, List<String> shipLocations) {
        this.shipType = shipType;
        this.shipLocations = shipLocations;
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

    public List<String> getShipLocations() {
        return shipLocations;
    }

    public void setShipLocations(List<String> shipLocations) {
        this.shipLocations = shipLocations;
    }

    public List <Ship> getGamePlayerShips(){
        List <Ship> ships;
        ships = gamePlayer.getShips();
        return ships;
    }

    public Map<String, Object> makeShipDTOMap() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", this.getShipType());//object
        dto.put("locations", this.getShipLocations());
        return dto;
    }





}
