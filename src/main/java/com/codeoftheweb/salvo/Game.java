package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity

public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date gameCreatedAt = new Date();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    List<Score> scores;

    @Transient
    Boolean gameOver;

    public boolean isGameOver() {
        if (gameOver == null) {
            gameOver = isThisGameOver();
        }
        return gameOver;
    }


    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Game() {
    } //a no-argument constructor for JPA to create new instances

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

    public void addScore(Score score) {
        score.setGame(this);
        scores.add(score);
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
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

    public Map<String, Object> makeGameDTOGamePlayer(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.putAll(makeGameDTO());
        dto.put("ships", getGamePlayerOfPlayer(player).makeShipDtoList());
        return dto;
    }

    public GamePlayer getGamePlayerOfPlayer(Player player) {
        for (GamePlayer element : gamePlayers) {
            if (player == element.getPlayer()) {
                return element;
            }
        }
        return null;
    }

    public List<Map<String, Object>> getGamePlayerDto() {
        List<Map<String, Object>> myGamePlayerDtoList = new ArrayList<>();
        for (GamePlayer element : gamePlayers) {
            myGamePlayerDtoList.add(element.makeGamePlayerDTO());
        }
        return myGamePlayerDtoList;
    }

    public List<Player> getPlayer() {
        List<Player> players = new ArrayList<>();
        for (GamePlayer element : gamePlayers) {
            players.add(element.getPlayer());
        }
        return players;
    }


    private boolean isThisGameOver() {
        GamePlayer player1 = getGamePlayers().iterator().next();
        GamePlayer player2 = getGamePlayers().stream().filter(gp -> gp != player1).findAny().get();
        return (gamePlayerDestroyedAllShipsOfOtherGamePlayer(player1, player2) ||
                gamePlayerDestroyedAllShipsOfOtherGamePlayer(player2, player1))
                && player1.getSalvoes().size() == player2.getSalvoes().size();
    }

    private boolean gamePlayerDestroyedAllShipsOfOtherGamePlayer(GamePlayer gamePlayer, GamePlayer otherGamePlayer) {
        boolean result = false;
        if (gamePlayer != null) {
           List<Salvo> salvoes = this.getOnlySalvoesFromCompleteTurns().get(gamePlayer); //changed salvoes to salvoes from completed turns
            List<String> allGPShipLocations = otherGamePlayer.getGPShipLocations();
            for (int turnNumber = 1; turnNumber < salvoes.size() + 1; turnNumber++) {
                List<String> salvoesToEndTheGame = otherGamePlayer.getOpponentSalvoes(otherGamePlayer.getOpponentSalvoesByTurn(turnNumber));
                if (salvoesToEndTheGame.containsAll(allGPShipLocations)) {
                    result = true;
                }
            }
        }
        return result;
    }

    private Set<GamePlayer> toSet(GamePlayer... players) {
        return new HashSet<GamePlayer>(Arrays.asList(players));
    }

    Turn getLastTurn() {
        GamePlayer player1 = getGamePlayers().iterator().next();
        GamePlayer player2 = getGamePlayers().stream().filter(gp -> gp != player1).findAny().get();
        if(this.isGameOver()){
            return new Turn(toSet());
        }
        if(player1.hasGPplacedShips()&& player2.hasGPplacedShips()) {
            if (player1.getSalvoes().size() == player2.getSalvoes().size()) {
                return new Turn(toSet(player1, player2));
            } else if (player1.getSalvoes().size() < player2.getSalvoes().size()) {
                return new Turn(toSet(player1));
            } else {
                return new Turn(toSet(player2));
            }
        }else{return new Turn(toSet());}
    }

    Map<GamePlayer,List<Salvo>> getOnlySalvoesFromCompleteTurns() {
        GamePlayer player1 = getGamePlayers().iterator().next();
        GamePlayer player2 = getGamePlayers().stream().filter(gp -> gp != player1).findAny().get();
        int minSize = Math.min(player1.getSalvoes().size(), player2.getSalvoes().size());
        Map<GamePlayer,List<Salvo>> onlyCompletedSalvoes = new HashMap<>();
        onlyCompletedSalvoes.put(player1, player1.getSalvoes().subList(0, minSize));
        onlyCompletedSalvoes.put(player2, player2.getSalvoes().subList(0, minSize));
        return onlyCompletedSalvoes;
    }

}