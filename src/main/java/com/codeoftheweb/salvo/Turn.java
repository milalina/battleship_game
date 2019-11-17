package com.codeoftheweb.salvo;

import javax.persistence.*;
import java.util.Set;

public class Turn {

    Set<GamePlayer> playersWhoCanFireSalvo;

    public Turn(Set<GamePlayer> playersWhoCanFireSalvo) {
        this.playersWhoCanFireSalvo=playersWhoCanFireSalvo;
    }

    public boolean canTheGamePlayerFireSalvo (GamePlayer gamePlayer) {
        if(playersWhoCanFireSalvo.contains(gamePlayer)){
            return true;
        }else{return false;}
    }
}
