package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
                         PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.shipRepository = shipRepository;
        this.salvoRepository = salvoRepository;
        this.scoreRepository = scoreRepository;
        this.playerRepository = playerRepository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/games")
    public Map<String, Object> getAllGamesAndCurrentPlayer(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("player", getCurrentPlayer(authentication));
        dto.put("games", getAllGames());
        return dto;
    }

    public List<Object> getAllGames() {
        List<Game> games = gameRepository
                .findAll();
        return games
                .stream()
                .map(element -> element.makeGameDTO())
                .collect(Collectors.toList());
    }

    public Map<String, Object> getCurrentPlayer(Authentication authentication) {
        if (authentication != null) {
            Map<String, Object> dto = new LinkedHashMap<String, Object>();
            dto.put("id", playerRepository.findByUserName(authentication.getName()).getId());
            dto.put("name", (authentication.getName()));
            return dto;
        }
        return null;
    }


    @RequestMapping("/gamePlayers")//the annotation is used to map web requests to Spring Controller methods
    public List<GamePlayer> getAll() {
        return gamePlayerRepository.findAll();
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public List<Map<String, Object>> findGame(@PathVariable("gamePlayerId") long gamePlayerId, Authentication authentication) {
        GamePlayer currentGamePlayer = gamePlayerRepository.findGamePlayerById(gamePlayerId);
        Player currentPlayer = currentGamePlayer.getPlayer();
        List<Map<String, Object>> gameView = new ArrayList<>();
        gameView.add(currentGamePlayer.makeGameDTOForGamePlayer());
        return gameView;
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findOneByUserName(email) != null) {
            return new ResponseEntity<>(makeMap("error", "Name already in use"), HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(makeMap("email", email), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/game", method = RequestMethod.POST)
    public ResponseEntity<Object> create(@RequestParam String username) {
        if (username.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.UNAUTHORIZED);
        }
        Game newGame = new Game(new Date());
        Player currentPlayer = playerRepository.findOneByUserName(username);
        gameRepository.save(newGame);
        GamePlayer gpTest = new GamePlayer(newGame, currentPlayer, new Date());
        gamePlayerRepository.save(gpTest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(
            path = "/game/{gameId}/player",
            method = RequestMethod.POST
    )
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {
        Player currentPlayer = playerRepository.findByUserName(authentication.getName());
        if (currentPlayer == null) {
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.UNAUTHORIZED);
        }
        Game gameToJoin = gameRepository.findGameById(gameId);
        if (gameToJoin == null) {
            return new ResponseEntity<>(makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }
        Set<GamePlayer> gamePlayer = gameToJoin.getGamePlayers();
        if (gamePlayer.size() > 1) {
            return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        }
        GamePlayer gpTest = new GamePlayer(gameToJoin, currentPlayer, new Date());
        gamePlayerRepository.save(gpTest);
        return new ResponseEntity<>(makeMap("gpid", gpTest.getId()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @RequestMapping(value = "/games/player/{gamePlayerId}/ships", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addShips(@PathVariable("gamePlayerId") long gamePlayerId, @RequestBody List<ShipDto> shipDtos, Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
        if (authentication.getName() == null) {
            return new ResponseEntity<>("There is no current user logged in", HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer == null) {
            return new ResponseEntity<>("There is no game player with the given ID", HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getUserName() != authentication.getName()) {
            return new ResponseEntity<>("The current user is not the game player the ID references", HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getShips() != null && gamePlayer.getShips().size() > 0) {
            return new ResponseEntity<>("The user already has ships placed", HttpStatus.FORBIDDEN);
        }
        for (ShipDto shipDto : shipDtos) {
            String shipType = shipDto.getType();
            List<String> shipLocations = shipDto.getLocations();
            Ship ship = new Ship(shipType, shipLocations);
            ship.setGamePlayer(gamePlayer);
            shipRepository.save(ship);
        }
        if (gamePlayer.getOpponent().getShips().size()==0){
            return new ResponseEntity<>("Ships added. Wait for your opponent to place ships", HttpStatus.CREATED);
        }else{return new ResponseEntity<>("Ships added. Fire salvos", HttpStatus.CREATED);}

    }

    @RequestMapping(value = "/games/player/{gamePlayerId}/salvoes", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addSalvoes(@PathVariable("gamePlayerId") long gamePlayerId, @RequestBody SalvoDto salvoDto, Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
        System.out.println(gamePlayer);
        if (authentication.getName() == null) {
            return new ResponseEntity<>("There is no current user logged in", HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer == null) {
            return new ResponseEntity<>("There is no game player with the given ID", HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getUserName() != authentication.getName()) {
            return new ResponseEntity<>("The current user is not the game player the ID references", HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer.getGame().isGameOver()) {
            return new ResponseEntity<>("Game over", HttpStatus.OK);
        }

       /*if(gamePlayer.getScore()==1){
            return new ResponseEntity<>("You won. Your score is 1", HttpStatus.OK);
        }

        if(gamePlayer.getScore()==0.5){
            return new ResponseEntity<>("You ended in a tie. Your score is 0.5", HttpStatus.OK);
        }

        if(gamePlayer.getScore()==0){
            return new ResponseEntity<>("You lost. Your score is 0", HttpStatus.OK);
        }*/

        Turn lastTurn = gamePlayer.getGame().getLastTurn();

        if (lastTurn.canTheGamePlayerFireSalvo(gamePlayer)) {
            List<String> salvoLocations = salvoDto.getLocations();
            Salvo salvo = new Salvo(salvoLocations);
            salvo.setGamePlayer(gamePlayer);
            salvoRepository.save(salvo);
            return new ResponseEntity<>("Salvoes added. Wait for your opponent to fire", HttpStatus.CREATED);
        }else{return new ResponseEntity<>("Salvoes not added. Wait for your opponent to finish turn", HttpStatus.CREATED);}
    }

    public List<Game> getGames() {
        return gameRepository.findAll();
    }


}
