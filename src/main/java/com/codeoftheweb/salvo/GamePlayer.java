package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch = FetchType.LAZY)
    List<Salvo> salvoes = new ArrayList<>();

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

    public Set<Ship> getShips() {
        return ships;
    }

    public List<Salvo> getSalvoes() {
        return salvoes;
    }

   public void addShip(Ship ship){
        ship.setGamePlayer(this);
        ships.add(ship);
   }

    public void addSalvo(Salvo salvo){
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
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

   public Double getScore(){
       Score playerScore = this.getPlayer().getScore(game);
       if (playerScore != null && playerScore.getFinishDate()!=null){
       return playerScore.getPlayerScore();}
       return null;
   }

    public Map<String, Object> makeGameDTOForGamePlayer() {
        Map<String, Object> gameDTOForGamePlayerMap = new LinkedHashMap<String, Object>();
        gameDTOForGamePlayerMap.putAll(this.getGame().makeGameDTO());
        gameDTOForGamePlayerMap.put("ships", this.makeShipDtoList());

        return gameDTOForGamePlayerMap;
    }

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());//object
        dto.put("player", this.getPlayer().makePlayerDTO(game));
        dto.put("salvoes", this.makeGPSalvoDTO());
        dto.put("score", this.getScore());
        return dto;
    }

    public List<Map<String, Object>> makeShipDtoList() {
        List<Map<String, Object>> myGamePlayerShipDtoList= new ArrayList<>();
        Set<Ship> ships = this.getShips();
        for (Ship ship: ships){
            myGamePlayerShipDtoList.add(ship.makeShipDTOMap());
        }
        return myGamePlayerShipDtoList;
    }



    public Map<Integer, Object> makeGPSalvoDTO() {
        Map<Integer, Object> salvoDto = new LinkedHashMap<Integer, Object>();
        List<Salvo> salvos = this.getSalvoes();
        int turnNumber = 1;
        for (Salvo salvo: salvoes) {
            salvoDto.put(turnNumber, salvo.getSalvoLocations());
            turnNumber+=1;
        }
        return salvoDto;
    }

    //redundant function for making playerSalvoDto
  /*  public Map<Long, Object> makePlayerSalvoDTO() {
        Map<Long, Object> dto = new LinkedHashMap<>();
        Long playerId = this.getPlayer().getId();
        dto.put(playerId, this.makeGPSalvoDTO());
        return dto;
    }*/

    @JsonIgnore
    public GamePlayer getOpponent(){
        Set<GamePlayer> gamePlayers = this.getGame().getGamePlayers();
        final GamePlayer[] opponent = new GamePlayer[1];
        gamePlayers.forEach(e -> {
            if(e.getId() != this.getId()){
                opponent[0] = e;
            }
        });
        return  opponent[0];
    }
}




