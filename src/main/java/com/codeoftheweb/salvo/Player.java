package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

//connecting the class to a db table
@Entity //create a player table for this class. An Entity class is equivalent to a row of a database
public class Player {

    @Id//the id instance variable holds the database key for this class
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")//tell JPA to use whatever ID generator is provided by the database system
    @GenericGenerator(name = "native", strategy = "native")//tell JPA to use whatever ID generator is provided by the database system
    private long id; //a unique number assigned to each player
    private String userName;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)//fetch=FetchType.EAGER=if you load a player, load gamePlayer as well
    Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)//fetch=FetchType.EAGER=if you load a player, load gamePlayer as well
    List<Score> scores = new ArrayList<>();

    public Player() { } //a no-argument constructor for JPA to create new instances

    public Player(String emailAddress) {
        this.userName = emailAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String toString() {
        return userName;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    public void addScore(Score score) {
        score.setPlayer(this);
        scores.add(score);
    }

    public long getId() {
        return id;
    }


    Set<Game> games(){
        Set<Game> gameResult = new HashSet<>();
        for (GamePlayer element : gamePlayers){
            gameResult.add(element.getGame());
        }
        return gameResult;
    }

    public Score getScore(Game game){
        for(Score element: scores){
            if(element.getGame() == game){
                return element;
            }
        }
        return null;
    }


    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());//object
        dto.put("email", this.getUserName());
        return dto;
    }


}
