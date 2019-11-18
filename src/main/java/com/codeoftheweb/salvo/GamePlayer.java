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
        if (playerScore != null && playerScore.getFinishDate() != null && this.getGame().isGameOver()) {
            return playerScore.getPlayerScore();
        }
        return null;
    }

    public Map<String, Object> makeGameDTOForGamePlayer() {
        Map<String, Object> gameDTOForGamePlayerMap = new LinkedHashMap<String, Object>();
        gameDTOForGamePlayerMap.putAll(this.getGame().makeGameDTO());
        gameDTOForGamePlayerMap.put("ships", this.makeShipDtoList());
        gameDTOForGamePlayerMap.put("over", this.getGame().isGameOver());
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

        public List<Salvo> getOpponentSalvoesByTurn ( int turn){
            return this.getOpponent().getSalvoes().subList(0, turn);
        }

        public List<String> getOpponentSalvoes (List < Salvo > opponentSalvoesByTurn) {
            List<String> opponentSalvoes = new ArrayList<>();
            List<Salvo> allSalvoes = opponentSalvoesByTurn;
            for (Salvo salvo : allSalvoes) {
                opponentSalvoes.addAll(salvo.getSalvoLocations());
            }
            return opponentSalvoes;
        }


        public List<String> getGPShipLocations () {
            List<String> shipLocations = new ArrayList<>();
            for (Ship ship : ships) {
                shipLocations.addAll(ship.getShipLocations());
            }
            return shipLocations;
        }

        public List<String> getHits () {
            List<String> shipLocations = this.getGPShipLocations();
            if (shipLocations.size() == 0) {
                return null;
            }
            List<Salvo> salvoes = this.getGame().getOnlySalvoesFromCompleteTurns().get(this.getOpponent());
            List<String> oponentSalvoes = this.getOpponentSalvoes(salvoes);
            oponentSalvoes.retainAll(shipLocations);
            return oponentSalvoes;
        }

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


        public Map<Integer, Object> makeShipTypeSinkDtoInAllTurns () {
            Map<Integer, Object> shipTypeSinkDtoInAllTurns = new LinkedHashMap<Integer, Object>();
            if (this.getOpponent() != null) {
               List<Salvo> salvoes = this.getGame().getOnlySalvoesFromCompleteTurns().get(this.getOpponent());
                for (int turnNumber = 1; turnNumber < salvoes.size() + 1; turnNumber++) {
                    List<String> salvoesToPassAsAParam = getOpponentSalvoes(getOpponentSalvoesByTurn(turnNumber));
                    shipTypeSinkDtoInAllTurns.put(turnNumber, this.makeShipTypeSinkDto(salvoesToPassAsAParam));
                }
            }
            return shipTypeSinkDtoInAllTurns;

        }


        public List<Map<String, Object>> makeShipDtoList () {
            List<Map<String, Object>> myGamePlayerShipDtoList = new ArrayList<>();
            Set<Ship> ships = this.getShips();
            for (Ship ship : ships) {
                myGamePlayerShipDtoList.add(ship.makeShipDTOMap());
            }
            return myGamePlayerShipDtoList;
        }

    boolean hasGPplacedShips(){
       if(ships.size()>0){
           return true;
       }else{ return false;}
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


        @JsonIgnore
        public GamePlayer getOpponent () {
            Set<GamePlayer> gamePlayers = this.getGame().getGamePlayers();
            return gamePlayers.stream().filter(gp -> gp.getId() != this.getId()).findAny().orElse(null);
        }

}




