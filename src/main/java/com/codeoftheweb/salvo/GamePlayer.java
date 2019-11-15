package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date joinDate = new Date();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.LAZY)
    List<Salvo> salvoes = new ArrayList<>();

    boolean gameOver;

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public GamePlayer() {
    }

    public GamePlayer(Game game, Player player, Date joinDate) {
        this.game = game;
        this.player = player;
        this.joinDate = joinDate;
    }

    public void setShips(Set<Ship> ships) {
            this.ships = ships;
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

    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        if(ships.size()>=5)
        {ships.add(ship);}
    }

    public void addSalvo(Salvo salvo, Integer gameOverTurnNumber) {
        salvo.setGamePlayer(this);
        if(salvoes.size()<= gameOverTurnNumber){
        salvoes.add(salvo);}
    }

    public void addSalvo(Salvo salvo) {
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }

    public void setSalvoes(List<Salvo> salvoes){
            this.salvoes = salvoes;
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

    public String toString() {
        return "Player= " + this.player + " and game = " + this.game.getId();
    }

    public long getId() {
        return id;
    }

    public Double getScore() {
        Score playerScore = this.getPlayer().getScore(game);
        if (playerScore != null && playerScore.getFinishDate() != null) {
            return playerScore.getPlayerScore();
        }
        return null;
    }

    public Map<String, Object> makeGameDTOForGamePlayer() {
        Map<String, Object> gameDTOForGamePlayerMap = new LinkedHashMap<String, Object>();
        gameDTOForGamePlayerMap.putAll(this.getGame().makeGameDTO());
        gameDTOForGamePlayerMap.put("ships", this.makeShipDtoList());
        gameDTOForGamePlayerMap.put("over", this.isGameOver());
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

    Integer gettingLastSalvo() {
        Integer indexOfLastSalvo;
            if (salvoes.size() > 1) {
                indexOfLastSalvo = salvoes.size()-1;
            } else {
                indexOfLastSalvo = null;
            }
        findGameOverTurnNumber(indexOfLastSalvo);
        return indexOfLastSalvo;

    }
        //where the opponent places salvoes chronologically

        public List<Salvo> getOpponentSalvoesByTurn ( int turn){
            return this.getOpponent().getSalvoes().subList(0, turn);
        }
        //where the opponent places salvoes

        public List<String> getOpponentSalvoes (List < Salvo > opponentSalvoesByTurn) {
            List<String> opponentSalvoes = new ArrayList<>();
            List<Salvo> allSalvoes = opponentSalvoesByTurn;
            for (Salvo salvo : allSalvoes) {
                opponentSalvoes.addAll(salvo.getSalvoLocations());
            }
            return opponentSalvoes;
        }


        // where gp ship locations are
        public List<String> getGPShipLocations () {
            List<String> shipLocations = new ArrayList<>();
            for (Ship ship : ships) {
                shipLocations.addAll(ship.getShipLocations());
            }
            return shipLocations;
        }

        // where gp ships are hit by the opponent in this turn
        public List<String> getHits () {
            List<String> shipLocations = this.getGPShipLocations();
            if (shipLocations.size() == 0) {
                return null;
            }
            List<String> oponentSalvoes = this.getOpponentSalvoes(this.getOpponent().salvoes);
            oponentSalvoes.retainAll(shipLocations);
            return oponentSalvoes;
        }

// sink in a ship type

        public Map<String, Object> makeShipTypeSinkDto (List < String > salvoesToPassAsAParam) {
            Map<String, Object> shipTypeSinkDto = new LinkedHashMap<String, Object>();
            for (Ship ship : ships) {
                Map<String, Object> sinkStatus = new LinkedHashMap<String, Object>();
                List<String> oneShipLocations = ship.getShipLocations();
                if (salvoesToPassAsAParam.containsAll(oneShipLocations)) {
                    sinkStatus.put("sink", "1");
                } else {
                    sinkStatus.put("sink", null);
                }
                shipTypeSinkDto.put(ship.getShipType(), sinkStatus);
            }
            return shipTypeSinkDto;
        }
// sink in a ship type in different turns

        public Map<Integer, Object> makeShipTypeSinkDtoInAllTurns () {
            Map<Integer, Object> shipTypeSinkDtoInAllTurns = new LinkedHashMap<Integer, Object>();
            if (this.getOpponent() != null) {
                List<Salvo> salvoes = this.getOpponent().getSalvoes();
                for (int turnNumber = 1; turnNumber < salvoes.size() + 1; turnNumber++) {
                    List<String> salvoesToPassAsAParam = getOpponentSalvoes(getOpponentSalvoesByTurn(turnNumber));
                    shipTypeSinkDtoInAllTurns.put(turnNumber, this.makeShipTypeSinkDto(salvoesToPassAsAParam));
                }
            }
            gettingLastSalvo();
            return shipTypeSinkDtoInAllTurns;
        }

        //getting the next salvo, to check later if it is worth adding compared to the current length of the salvo set?


//getting the turn number at which all GP ships are sunk - game over

        public boolean findGameOverTurnNumber (Integer indexOfTheLastSalvo) {
            Integer gameOverTurnNumber = 0;
            if (this.getOpponent() != null) {
                List<Salvo> salvoes = this.getOpponent().getSalvoes();
                List<String> allGPShipLocations = getGPShipLocations();
                for (int turnNumber = 1; turnNumber < salvoes.size() + 1; turnNumber++) {
                    List<String> salvoesToEndTheGame = getOpponentSalvoes(getOpponentSalvoesByTurn(turnNumber));
                    if (salvoesToEndTheGame.containsAll(allGPShipLocations)) {
                        List<Integer> turnsAfterGameIsOver = new ArrayList<>();
                        turnsAfterGameIsOver.add(turnNumber);
                        gameOverTurnNumber = turnsAfterGameIsOver.get(0);
                       if (indexOfTheLastSalvo+1==gameOverTurnNumber){
                        // setSalvoes(salvoes, gameOverTurnNumber);
                            Salvo salvo= new Salvo();
                        addSalvo(salvo, gameOverTurnNumber);}
                        gameOver = true;
                    } else {
                        gameOver = false;
                    }
                }
            }
            return gameOver;
        }

        public List<Map<String, Object>> makeShipDtoList () {
            List<Map<String, Object>> myGamePlayerShipDtoList = new ArrayList<>();
            Set<Ship> ships = this.getShips();
            for (Ship ship : ships) {
                myGamePlayerShipDtoList.add(ship.makeShipDTOMap());
            }
            return myGamePlayerShipDtoList;
        }


        public Map<Integer, Object> makeGPSalvoDTO () {
            Map<Integer, Object> salvoDto = new LinkedHashMap<Integer, Object>();
            List<Salvo> salvos = this.getSalvoes();
            int turnNumber = 1;
            for (Salvo salvo : salvoes) {
                salvoDto.put(turnNumber, salvo.getSalvoLocations());
                turnNumber += 1;
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
        public GamePlayer getOpponent () {
            Set<GamePlayer> gamePlayers = this.getGame().getGamePlayers();
            final GamePlayer[] opponent = new GamePlayer[1];
            gamePlayers.forEach(e -> {
                if (e.getId() != this.getId()) {
                    opponent[0] = e;
                }
            });
            return opponent[0];
        }

}




