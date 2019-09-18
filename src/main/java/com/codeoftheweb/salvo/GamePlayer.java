package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date joinDate = new Date();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @OneToMany(mappedBy="gamePlayer", fetch = FetchType.EAGER)
    List<Ship> ships = new ArrayList<>();

    public GamePlayer() {
    }

    public GamePlayer(Game game, Player player, Date joinDate) {
        this.game = game;
        this.player = player;
        this.joinDate = joinDate;

    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Ship> getShips() {
        return ships;
    }

   public void addShip(Ship ship){
        ship.setGamePlayer(this);
        ships.add(ship);
   }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public String toString(){
        return "Player= " + this.player +" and game = " + this.game.getId();
    }

    public long getId() {
        return id;
    }
    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());//object
        dto.put("player", this.getPlayer().makePlayerDTO());
        return dto;
    }
}

