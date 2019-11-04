package com.codeoftheweb.salvo;

import java.util.List;

public class ShipDto {
    String type;
    List <String> locations;

    public ShipDto(String type, List<String> locations) {
        this.type = type;
        this.locations = locations;
    }

    public ShipDto() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
}
