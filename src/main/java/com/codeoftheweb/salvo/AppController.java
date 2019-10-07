package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class AppController {
    private GameRepository gameRepository;
    private GamePlayerRepository gamePlayerRepository;
    private SalvoRepository salvoRepository;
    private ShipRepository shipRepository;
    private ScoreRepository scoreRepository;
    @Autowired //tells to create an instance of 'Repository' and store it in the instance variable 'repository'
    //private PlayerRepository playerRepository;
    public AppController(GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, SalvoRepository
            salvoRepository, ShipRepository shipRepository, ScoreRepository scoreRepository){
        this.gameRepository = gameRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.shipRepository= shipRepository;
        this.salvoRepository= salvoRepository;
        this.scoreRepository= scoreRepository;
    }

    @RequestMapping("/games")
    public List<Object> getAllGames() {
        List<Game> games = gameRepository
                .findAll();
        return games
                .stream()
                .map(element -> element.makeGameDTO())
                .collect(Collectors.toList());
    }

    @RequestMapping("/gamePlayers")
    public List<GamePlayer> getAll() {
        return gamePlayerRepository.findAll();
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public List<Map<String, Object>> findGame (@PathVariable ("gamePlayerId") long gamePlayerId) {
        GamePlayer currentGamePlayer = gamePlayerRepository.findGamePlayerById(gamePlayerId);
        List<Map<String, Object>> gameView= new ArrayList<>();
        gameView.add(currentGamePlayer.makeGameDTOForGamePlayer());
       // gameView.add(currentGamePlayer.getGame().getPlayersSalvoesMap());
        return gameView;
    }

   /* @RequestMapping("/game_view/{gamePlayerId}")
    public List<Map<String, Object>> findGame (@PathVariable ("gamePlayerId") long gamePlayerId) {
        GamePlayer currentGamePlayer = gamePlayerRepository.findGamePlayerById(gamePlayerId);
        Game currentGame = currentGamePlayer.getGame();
        List<Map<String, Object>> gameView= new ArrayList<>();
        //gameView.add(currentGame.makeGameDTO());
        gameView.add(currentGame.makeGameDTOGamePlayer(currentGamePlayer.getPlayer()));
        return gameView;
    }*/





   /* private Map<String, Object> makeGameDTO(Game game) {
        List<Long> makeGameDTO = new ArrayList<>();
        getGames().forEach(oneGame -> {
            Map<String, Object> dto = new LinkedHashMap <String, Object>();
            dto.put("id", oneGame.getId());
            dto.put("created", oneGame.getGameCreatedAt().toInstant().toEpochMilli());
        });
        return dto;
    }*/


    /*public List<Long> makeGameDTO() {
        List<Long> makeGameDTO = new ArrayList<>();
        getGames().forEach(oneGame -> {
            Map<String, Object> dto = new LinkedHashMap <String, Object>();
            dto.put("id", oneGame.getId());
            dto.put("created", oneGame.getGameCreatedAt().toInstant().toEpochMilli());
        });
        return makeGameDTO;
    }*/

    /*public List<Long> getIds() {
        List<Long> gameIds = new ArrayList<>();
        getGames().stream().forEach(oneGame -> {
            gameIds.add(oneGame.getId());
        });
        return gameIds;
    }*/

    /*public List<Long> getIds2() {
       return getGames().stream()
               .map(Game::getId)
               .collect(toList());
    }*/

    public List<Game> getGames() {
        return gameRepository.findAll();
    }


}
