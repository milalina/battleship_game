package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

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
        return dto;
    }

    public List<GamePlayerDto> getGamePlayerDto() {
        List<GamePlayerDto> myGamePlayerDtoList= new ArrayList<>();
        for (GamePlayer element: gamePlayers) {
            Player player = element.getPlayer();
            PlayerDto myPlayerDto = new PlayerDto(player.getId(), player.getUserName());
            GamePlayerDto myGamePlayerDto = new GamePlayerDto (element.getId(), myPlayerDto);
            myGamePlayerDtoList.add(myGamePlayerDto);
        }
        return myGamePlayerDtoList;
    }

//    public GamePlayerDto(int id, PlayerDto player) {
//        this.id = id;
//        this.player = player;
//    }


//    public GamePlayerDto(int id, PlayerDto player) {
//        this.id = id;
//        this.player = player;
//    }

    public List<Player> getPlayer() {
        List<Player> players= new ArrayList<>();
        for (GamePlayer element: gamePlayers) {
            players.add(element.getPlayer());
        }
        return players;
    }


    }






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



