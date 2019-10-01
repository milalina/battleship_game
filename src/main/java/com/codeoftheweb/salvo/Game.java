package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestMapping;

@Entity

public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date gameCreatedAt = new Date();

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set <GamePlayer> gamePlayers;

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Game() { } //a no-argument constructor for JPA to create new instances

    public Game(Date gameCreatedAt) {
        this.gameCreatedAt = gameCreatedAt;
    }

    public Date getGameCreatedAt() {
        return gameCreatedAt;
    }

    public void setGameCreatedAt(Date gameCreatedAt) {
        this.gameCreatedAt = gameCreatedAt;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }

    public long getId() {
        return id;
    }

    public Map<String, Object> makeGameDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());//object
        dto.put("created", this.getGameCreatedAt().toInstant().toEpochMilli());
        dto.put("gamePlayers", getGamePlayerDto());
        //dto.put("salvoes", )
        return dto;
    }

    public Map<String, Object> makeGameDTOGamePlayer(Player player) {
      Map<String, Object> dto = new LinkedHashMap<String, Object>();
      dto.putAll(makeGameDTO());
      dto.put("ships", getGamePlayerOfPlayer(player).makeShipDtoList());
      return dto;
   }

   public GamePlayer getGamePlayerOfPlayer(Player player){
        for (GamePlayer element: gamePlayers){
            if (player == element.getPlayer()){
                return element;
            }
        }
       return null;
   }

    public List<Map<String, Object>> getGamePlayerDto() {
        List<Map<String, Object>> myGamePlayerDtoList= new ArrayList<>();
        for (GamePlayer element: gamePlayers){
            myGamePlayerDtoList.add(element.makeGamePlayerDTO());
        }
        return myGamePlayerDtoList;
    }

    public List<Player> getPlayer() {
        List<Player> players= new ArrayList<>();
        for (GamePlayer element: gamePlayers) {
            players.add(element.getPlayer());
        }
        return players;
    }

    public Map<String, Object> getPlayersSalvoesMap(){
        Map<String, Object> playersSalvoesMap = new LinkedHashMap<String, Object>();
        List<Map<Long, Object>> playersSalvoesList=new ArrayList<>();
        for (GamePlayer element: gamePlayers){
            playersSalvoesList.add(element.makePlayerSalvoDTO());}
        playersSalvoesMap.put("salvoes", playersSalvoesList);
        return playersSalvoesMap;
    }

    }

       /*

        public Map<String, Object> getPlayersSalvoes(){
        Map<String, Object> playersSalvoesMap = new LinkedHashMap<Long, Object>();
        Map<Long, Object> playersSalvoes = new LinkedHashMap<Long, Object>();
        for (GamePlayer element: gamePlayers){
            playersSalvoes.put(element.makePlayerSalvoDTO());
        }
        playersSalvoesMap.put("salvoes", )
        return playersSalvoesMap;
    }

    }

   public List<Ship> makeShipDtoList() {
        List<Ship> ships1;
        GamePlayer currentGamePlayer = new GamePlayer();
        ships1 = currentGamePlayer.getShips();
        return ships1;}

   public List<Map<String, Object>> makeShipDtoList() {
        List<Map<String, Object>> myGamePlayerShipDtoList= new ArrayList<>();
        for (List<Ship> ship: ships){
            myGamePlayerShipDtoList.add(ship.makeShipDTOMap());
        }
        return myGamePlayerShipDtoList;
    }

    public List <ArrayList> makeShipDtoList() {
        List<ArrayList> myGamePlayerShipDtoList= new ArrayList<>();
        for (GamePlayer element: gamePlayers){
            myGamePlayerShipDtoList.add(element.getShips());
        }
        return myGamePlayerShipDtoList;
    }

    public List<Ship> makeShipDtoList() {
        List<Ship> ships1;
        GamePlayer currentGamePlayer = new GamePlayer();
        ships1 = currentGamePlayer.getShips();
        return ships1;
    }

     public List<ArrayList> makeShipDtoList() {
        List<ArrayList> gamePlayerShips;
        GamePlayer currentGamePlayer = new GamePlayer();
        gamePlayerShips=currentGamePlayer.getShips().stream().map(element -> element.makeShipDTOMap());
        return gamePlayerShips;
    }
    */



//Creating DTO with DTO Classes
//   public Map<String, Object> makeGameDTO() {
//        Map<String, Object> dto = new LinkedHashMap<String, Object>();
//        dto.put("id", this.getId());//object
//        dto.put("created", this.getGameCreatedAt().toInstant().toEpochMilli());
//        dto.put("gamePlayers", getGamePlayerDto());
//        return dto;
//    }
//
//    public List<GamePlayerDto> getGamePlayerDto() {
//        List<GamePlayerDto> myGamePlayerDtoList= new ArrayList<>();
//        for (GamePlayer element: gamePlayers) {
//            Player player = element.getPlayer();
//            PlayerDto myPlayerDto = new PlayerDto(player.getId(), player.getUserName());
//            GamePlayerDto myGamePlayerDto = new GamePlayerDto (element.getId(), myPlayerDto);
//            myGamePlayerDtoList.add(myGamePlayerDto);
//        }
//        return myGamePlayerDtoList;
//    }

//    public GamePlayerDto(int id, PlayerDto player) {
//        this.id = id;
//        this.player = player;
//    }


//    public GamePlayerDto(int id, PlayerDto player) {
//        this.id = id;
//        this.player = player;
//    }





   /*
   public List<Long> makeGameDTOList() {
        List<Long> makeGameDTO = new ArrayList<>();
        Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getGameCreatedAt().toInstant().toEpochMilli());
        dto.put("GamePlayers",)
        return dto;
        });
        makeGameDTOList.add(makeGameDTO());
    }

   public List<Long> getPlayersInEachGame() {
        List<Long> PlayersInEachGame = new ArrayList<>();
        Map<String, Object> makeGameDTO(Game game) {
            Map<String, Object> dto = new LinkedHashMap<String, Object>();
            dto.put("id", game.getId());
            dto.put("created", game.getGameCreatedAt().toInstant().toEpochMilli());
            return dto;
        }
        return PlayersInEachGame;
    }*/



