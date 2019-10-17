package com.codeoftheweb.salvo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List; //import statement for using Java class 'List in this code file

//A Repository class is analogous to a table, i.e., it is a class that manages a collection of instances
//The following doesn't define any actual runnable code. It defines a Java interface, not a class.
//Spring will create an actual class with code that implements this interface.
@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long>{
    Player findByUserName(@Param("userName")String userName);
    Player findOneByUserName(String userName);
}
