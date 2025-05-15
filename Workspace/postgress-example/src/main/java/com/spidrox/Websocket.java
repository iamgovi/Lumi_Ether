package com.spidrox;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/hello")
public class Websocket {

    @GET
    public String hello() {
        return "Hello, from websocket";
    }
    
}
