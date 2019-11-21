package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    private Date finishDate;

    private double playerScore;

    public double getPlayerScore() {
        return playerScore;
    }

    public long playerId() {
        return player.getId();
    }

    public Score(Date finishDate, double playerScore, Game game) {
        this.finishDate = finishDate;
        this.playerScore = playerScore;
        this.game = game;
    }

    public long gameId() {
        return game.getId();
    }

    public void setScore(double playerScore) {
        this.playerScore = playerScore;
    }

    Double calculatePlayerScore(GamePlayer gamePlayer) {
        if (game.isGameOver()) {
            this.finishDate = new Date();
            if (this.getAllSalvoesAsString(gamePlayer.getOpponent()).containsAll(gamePlayer.getGPShipLocations()) &&
                    this.getAllSalvoesAsString(gamePlayer).containsAll(gamePlayer.getOpponent().getGPShipLocations())) {
                playerScore = 0.5;
                return playerScore;
            } else if (this.getAllSalvoesAsString(gamePlayer.getOpponent()).containsAll(gamePlayer.getGPShipLocations()) &&
                    !this.getAllSalvoesAsString(gamePlayer).containsAll(gamePlayer.getOpponent().getGPShipLocations())) {
                playerScore = 0.0;
                return playerScore;
            } else {
                playerScore = 1.0;
                return playerScore;
            }
        } else {
            return null;
        }
    }

    private List<String> getAllSalvoesAsString(GamePlayer gamePlayer) {
        List<Salvo> salvoesAsInstances = gamePlayer.getSalvoes();
        List<String> salvoesAsAStringOfLocations = new ArrayList<>();
        for (Salvo oneSalvo : salvoesAsInstances) {
            salvoesAsAStringOfLocations.addAll(oneSalvo.getSalvoLocations());
        }
        return salvoesAsAStringOfLocations;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Score() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }


}
