package com.spidrox;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/hello2")
public class Hello2 {

    @GET
    public String hello() {
        return "Hello, from websocket";
    }
}