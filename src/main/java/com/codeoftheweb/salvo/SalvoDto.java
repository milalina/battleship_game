package com.codeoftheweb.salvo;

import java.util.List;

public class SalvoDto {
    List<String> locations;

    public SalvoDto( List<String> locations) {

        this.locations = locations;
    }

    public SalvoDto() {
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
}
