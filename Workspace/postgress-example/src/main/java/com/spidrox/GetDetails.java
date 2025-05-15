package com.spidrox;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/{email}")
@Produces(MediaType.APPLICATION_JSON)
public class GetDetails {

    @Inject
    PgPool client;
    
    @GET
    public Uni<Response> getUserByEmail(@PathParam("email") String email) {
        return fetchUserByEmail(email)
                .onItem().transform(user -> {
                    if (user != null) {
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

    private Uni<UserDTO> fetchUserByEmail(String email) {
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
                        return user;
                    }
                    return null;
                });
    }


    public static class UserDTO {
        public String name;
        public String email;
        public String collegename;
    }
}