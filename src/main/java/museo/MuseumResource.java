package museo;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/museums")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class MuseumResource {

    @Inject
    EntityManager em;

    // 1. ENDPOINT PARA LISTAR TODOS LOS MUSEOS (GET)
    @GET
    public List<Museum> listar() {
        return em.createQuery("SELECT m FROM Museum m", Museum.class).getResultList();
    }

    @GET
    @Path("/{id}")
    public Response buscar(@PathParam("id") Long id) {
        Museum museum = em.find(Museum.class, id);
        return museum == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(museum).build();
    }

    // 2. ENDPOINT PARA CREAR UN NUEVO MUSEO (POST)
    @POST
    @Transactional
    public Response crear(Museum museum) {
        Response validation = validar(museum);
        if (validation != null) {
            return validation;
        }
        museum.setId(null);
        museum.setName(museum.getName().trim());
        em.persist(museum);
        return Response.status(Response.Status.CREATED).entity(museum).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response actualizar(@PathParam("id") Long id, Museum datos) {
        Response validation = validar(datos);
        if (validation != null) {
            return validation;
        }
        Museum museum = em.find(Museum.class, id);
        if (museum == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        museum.setName(datos.getName().trim());
        museum.setDescription(datos.getDescription());
        museum.setLatitude(datos.getLatitude());
        museum.setLongitude(datos.getLongitude());
        museum.setLandingPageUrl(datos.getLandingPageUrl());
        return Response.ok(museum).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response eliminar(@PathParam("id") Long id) {
        Museum museum = em.find(Museum.class, id);
        if (museum == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Long rooms = em.createQuery(
                "SELECT COUNT(r) FROM ExhibitionRoom r WHERE r.museum.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        if (rooms > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("No se puede eliminar un museo que contiene salas").build();
        }
        em.remove(museum);
        return Response.noContent().build();
    }

    private Response validar(Museum museum) {
        if (museum == null || museum.getName() == null || museum.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El nombre del museo es obligatorio").build();
        }
        Float latitude = museum.getLatitude();
        Float longitude = museum.getLongitude();
        if (latitude == null || longitude == null
                || !Float.isFinite(latitude) || !Float.isFinite(longitude)
                || latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Las coordenadas deben estar dentro de los rangos geográficos válidos").build();
        }
        return null;
    }
}