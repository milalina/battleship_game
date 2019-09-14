package com.codeoftheweb.salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List; //import statement for using Java class 'List' in this code file
import java.util.Date;

@RepositoryRestResource
public interface GameRepository extends JpaRepository<Game, Date>{
    List<Game> findByGameCreatedAt(Date date);
}
