package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class AppController {
    @Autowired //tells to create an instance of 'Repository' and store it in the instance variable 'repository'
    //private PlayerRepository playerRepository;
    private GameRepository gameRepository;
    //private GamePlayerRepository gamePlayerRepository;

    @RequestMapping("/games")

    public List<Object> getAllGames() {
        List<Game> games = gameRepository
                .findAll();

        return games
                .stream()
                .map(element -> element.makeGameDTO())
                .collect(Collectors.toList());
    }




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
