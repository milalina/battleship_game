package com.codeoftheweb.salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List; //import statement for using Java class 'List' in this code file
import java.util.Date;
import java.util.Set;

@RepositoryRestResource
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long>{
    //Set<GamePlayer> findByGamePlayer (String gamePlayer);
}

