package com.spidrox;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class userResources {

    @Inject
    PgPool client;

    
    @POST
    public Uni<Response> createUser(UserDTO user) {
        String sql = "INSERT INTO users (name, email, collegename) VALUES ($1, $2, $3) RETURNING id";
        return client.preparedQuery(sql)
                .execute(Tuple.of(user.name, user.email, user.collegename))
                .onItem().transform(rows -> {
                    if (rows.iterator().hasNext()) {
                        long id = rows.iterator().next().getLong("id");
                        return Response.ok("User inserted with ID: " + id).build();
                    } else {
                        return Response.status(500).entity("Insert failed").build();
                    }
                })
                .onFailure().recoverWithItem(err -> {
                    err.printStackTrace();
                    return Response.status(500).entity("Error inserting user").build();
                });
    }

 
    @GET
    @Path("/email/{email}")
    public Uni<Response> getUserByEmail(@PathParam("email") String email) {
        String sql = "SELECT name, email, collegename FROM users WHERE email = $1";
        return client.preparedQuery(sql)
                .execute(Tuple.of(email))
                .onItem().transform(rows -> {
                    if (rows.iterator().hasNext()) {
                        var row = rows.iterator().next();
                        UserDTO user = new UserDTO();
                        user.name = row.getString("name");
                        user.email = row.getString("email");
                        user.collegename = row.getString("collegename");
                        return Response.ok(user).build();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
                    }
                })
                .onFailure().recoverWithItem(err -> {
                    err.printStackTrace();
                    return Response.status(500).entity("Error fetching user").build();
                });
    }

   
    @PUT
    @Path("/email/{email}")
    public Uni<Response> updateUser(@PathParam("email") String email, UserDTO updatedUser) {
        String sql = "UPDATE users SET name = $1, collegename = $2 WHERE email = $3";
        return client.preparedQuery(sql)
                .execute(Tuple.of(updatedUser.name, updatedUser.collegename, email))
                .onItem().transform(rows -> {
                    if (rows.rowCount() > 0) {
                        return Response.ok("User updated successfully").build();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
                    }
                })
                .onFailure().recoverWithItem(err -> {
                    err.printStackTrace();
                    return Response.status(500).entity("Error updating user").build();
                });
    }

   
    @DELETE
    @Path("/email/{email}")
    public Uni<Response> deleteUser(@PathParam("email") String email) {
        String sql = "DELETE FROM users WHERE email = $1";
        return client.preparedQuery(sql)
                .execute(Tuple.of(email))
                .onItem().transform(rows -> {
                    if (rows.rowCount() > 0) {
                        return Response.ok("User deleted successfully").build();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
                    }
                })
                .onFailure().recoverWithItem(err -> {
                    err.printStackTrace();
                    return Response.status(500).entity("Error deleting user").build();
                });
    }

   
    public static class UserDTO {
        public String name;
        public String email;
        public String collegename;
    }
}