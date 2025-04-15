package com.spidrox;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/hello2")
public class example {

    @GET
    public String hello() {
        return "Hello, from websocket";
    }
}