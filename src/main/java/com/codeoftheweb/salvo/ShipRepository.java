package com.codeoftheweb.salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List; //import statement for using Java class 'List' in this code file

@RepositoryRestResource
public interface ShipRepository extends JpaRepository<Ship, String >{
    //List<Ship> findByShip ();
}