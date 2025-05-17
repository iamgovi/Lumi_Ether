package com.spidrox;

import jakarta.persistence.*;
import java.util.List;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    @Inject EntityManager em;

    @POST
    public Response createTask(Task task) {
        em.persist(task);
        return Response.status(201).entity(task).build();
    }

    @GET
    public List<Task> getAllTasks(@QueryParam("status") String status, @QueryParam("userId") Long userId) {
        String query = "SELECT t FROM Task t WHERE 1=1";
        if (status != null) query += " AND t.status = :status";
        if (userId != null) query += " AND t.user.id = :userId";
        var q = em.createQuery(query, Task.class);
        if (status != null) q.setParameter("status", status);
        if (userId != null) q.setParameter("userId", userId);
        return q.getResultList();
    }

    @PUT
    @Path("/{id}")
    public Task updateTask(@PathParam("id") Long id, Task updated) {
        Task task = em.find(Task.class, id);
        if (task == null) throw new WebApplicationException("Task not found", 404);
        task.title = updated.title;
        task.description = updated.description;
        task.status = updated.status;
        task.priority = updated.priority;
        task.dueDate = updated.dueDate;
        return task;
    }

    @DELETE
    @Path("/{id}")
    public void deleteTask(@PathParam("id") Long id) {
        Task task = em.find(Task.class, id);
        if (task != null) em.remove(task);
    }
}