package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Stream;

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
        dto.put("hits", this.getHits());
        dto.put("status", this.makeShipTypeSinkDtoInAllTurns());
        return dto;
    }

    //where the opponent places salvoes

    public List<String> getOpponentSalvoes (){
        List<String> opponentSalvoes = new ArrayList<>();
        List <Salvo> allSalvoes = this.getOpponent().getSalvoes();
        for (Salvo salvo: allSalvoes){
            opponentSalvoes.addAll(salvo.getSalvoLocations());
        }
        return opponentSalvoes;
    }
    // where gp ship locations are
    public List<String> getGPShipLocations (){
        List<String> shipLocations = new ArrayList<>();
        for (Ship ship: ships){
            shipLocations.addAll(ship.getShipLocations());
        }
        return shipLocations;
    }

// where gp ships are hit by the opponent in this turn
public List<String> getHits(){
    List<String> shipLocations = this.getGPShipLocations ();
    if(shipLocations.size()<0 ){
    List<String> opponentSalvoes =this.getOpponentSalvoes().subList(this.getOpponentSalvoes().size()-0, this.getOpponentSalvoes().size());
    for (String opponentSalvo: opponentSalvoes){
        shipLocations.stream().filter(location->location == opponentSalvo);
    }} else{return null;}
    return shipLocations;
}

// sink in a ship type

 public Map<String, Object> makeShipTypeSinkDto(){
  Map<String, Object> shipTypeSinkDto = new LinkedHashMap<String, Object>();
  Map<String, Object> sinkStatus = new LinkedHashMap<String, Object>();
        for (Ship ship: ships){
            for(String oneSalvo: this.getOpponentSalvoes()){
                List<String> oneShipLocations=ship.getShipLocations();
                oneShipLocations.stream().filter(oneLocation -> oneLocation == oneSalvo);
                if (oneShipLocations.size()==0){
                    sinkStatus.put("sink","1");
                }else{sinkStatus.put("sink", null);}
            }
            shipTypeSinkDto.put(ship.getShipType(), sinkStatus);
    }

    return shipTypeSinkDto;
    }
// sink in a ship type in different turns

    public Map<Integer, Object>makeShipTypeSinkDtoInAllTurns(){
        Map<Integer, Object> shipTypeSinkDtoInAllTurns = new LinkedHashMap<Integer, Object>();
        int turnNumber = 1;
        for (Salvo salvo: salvoes){
            shipTypeSinkDtoInAllTurns.put(turnNumber, this.makeShipTypeSinkDto());
            turnNumber+=1;
        }
        return shipTypeSinkDtoInAllTurns;
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




