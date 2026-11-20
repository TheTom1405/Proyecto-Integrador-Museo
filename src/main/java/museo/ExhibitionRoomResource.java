package museo;

import java.util.List;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExhibitionRoomResource {

    @Inject
    EntityManager em;

    @GET
    public List<ExhibitionRoom> listar() {
        return em.createQuery("SELECT r FROM ExhibitionRoom r ORDER BY r.id", ExhibitionRoom.class)
                .getResultList();
    }

    @GET
    @Path("/{id}")
    public Response buscar(@PathParam("id") Long id) {
        ExhibitionRoom room = em.find(ExhibitionRoom.class, id);
        return room == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(room).build();
    }

    @POST
    @Transactional
    public Response crear(ExhibitionRoom room) {
        Response validation = validar(room);
        if (validation != null) {
            return validation;
        }
        Response museumResponse = resolverMuseo(room);
        if (museumResponse != null) {
            return museumResponse;
        }
        room.setId(null);
        room.setName(room.getName().trim());
        em.persist(room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response actualizar(@PathParam("id") Long id, ExhibitionRoom datos) {
        Response validation = validar(datos);
        if (validation != null) {
            return validation;
        }
        ExhibitionRoom room = em.find(ExhibitionRoom.class, id);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Response museumResponse = resolverMuseo(datos);
        if (museumResponse != null) {
            return museumResponse;
        }
        if (datos.getCapacity() != null) {
            Long occupied = em.createQuery(
                    "SELECT COUNT(a) FROM Artifact a WHERE a.currentRoom.id = :id", Long.class)
                    .setParameter("id", id)
                    .getSingleResult();
            if (datos.getCapacity() < occupied) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("La capacidad no puede ser menor que las piezas actualmente asignadas").build();
            }
        }
        room.setName(datos.getName().trim());
        room.setDescription(datos.getDescription());
        room.setCapacity(datos.getCapacity());
        room.setMuseum(datos.getMuseum());
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response eliminar(@PathParam("id") Long id) {
        ExhibitionRoom room = em.find(ExhibitionRoom.class, id);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Long artifacts = em.createQuery(
                "SELECT COUNT(a) FROM Artifact a WHERE a.currentRoom.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        Long tours = em.createQuery(
                "SELECT COUNT(t) FROM Tour t JOIN t.rooms r WHERE r.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        Long history = em.createQuery(
                "SELECT COUNT(h) FROM ExhibitionHistory h WHERE h.room.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        if (artifacts > 0 || tours > 0 || history > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("No se puede eliminar una sala usada por piezas, recorridos o el historial").build();
        }
        em.remove(room);
        return Response.noContent().build();
    }

    private Response validar(ExhibitionRoom room) {
        if (room == null || room.getName() == null || room.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El nombre de la sala es obligatorio").build();
        }
        if (room.getCapacity() == null || room.getCapacity() < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La capacidad es obligatoria y debe ser mayor que cero").build();
        }
        return null;
    }

    private Response resolverMuseo(ExhibitionRoom room) {
        if (room.getMuseum() == null || room.getMuseum().getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La sala debe estar asociada a un museo existente").build();
        }
        Museum museum = em.find(Museum.class, room.getMuseum().getId());
        if (museum == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El museo indicado no existe").build();
        }
        room.setMuseum(museum);
        return null;
    }
}
