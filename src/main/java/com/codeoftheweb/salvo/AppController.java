package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    private PlayerRepository playerRepository;
    @Autowired //tells to create an instance of 'Repository' and store it in the instance variable 'repository'
    //private PlayerRepository playerRepository;
    public AppController(GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, SalvoRepository
            salvoRepository, ShipRepository shipRepository, ScoreRepository scoreRepository,
                         PlayerRepository playerRepository){
        this.gameRepository = gameRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.shipRepository= shipRepository;
        this.salvoRepository= salvoRepository;
        this.scoreRepository= scoreRepository;
        this.playerRepository=playerRepository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/games")
    public Map<String, Object> getAllGamesAndCurrentPlayer(Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("player", getCurrentPlayer(authentication));
        dto.put("games", getAllGames( ));
        return dto;
    }

    public List<Object> getAllGames( ) {
        List<Game> games = gameRepository
                .findAll();
        return games
                .stream()
                .map(element -> element.makeGameDTO())
                .collect(Collectors.toList());
    }

    public Map<String, Object> getCurrentPlayer(Authentication authentication){
        if (authentication != null){
            Map<String, Object> dto = new LinkedHashMap<String, Object>();
            dto.put("id", playerRepository.findByUserName(authentication.getName()).getId());
            dto.put("name", (authentication.getName()));
            return dto;
        }  return null;
    }



    @RequestMapping("/gamePlayers")//the annotation is used to map web requests to Spring Controller methods
    public List<GamePlayer> getAll() {
        return gamePlayerRepository.findAll();
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public List<Map<String, Object>> findGame (@PathVariable ("gamePlayerId") long gamePlayerId) {
        GamePlayer currentGamePlayer = gamePlayerRepository.findGamePlayerById(gamePlayerId);
        Player currentPlayer = currentGamePlayer.getPlayer();
        List<Map<String, Object>> gameView= new ArrayList<>();
        gameView.add(currentGamePlayer.makeGameDTOForGamePlayer());
       // gameView.add(currentGamePlayer.getGame().getPlayersSalvoesMap());
        return gameView;
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findOneByUserName(email) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
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

/*
@RequestMapping("/games")
    public List<Object> getAllGames( Authentication authentication) {
        if (authentication != null){
            Map<String, Object> dto = new LinkedHashMap<String, Object>();
            dto.put("name", playerRepository.findByUserName(authentication.getName()).getId());
            dto.put("id", (authentication.getName()));
        }
        List<Game> games = gameRepository
                .findAll();
        return games
                .stream()
                .map(element -> element.makeGameDTO())
                .collect(Collectors.toList());
    }*/
