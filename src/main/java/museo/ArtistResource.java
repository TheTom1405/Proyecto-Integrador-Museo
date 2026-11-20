package museo;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/artists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtistResource {

    @Inject
    EntityManager em;

    @GET
    public List<Artist> listar() {
        return em.createQuery("SELECT a FROM Artist a", Artist.class).getResultList();
    }

    @GET
    @Path("/{id}")
    public Response buscar(@PathParam("id") Long id) {
        Artist artist = em.find(Artist.class, id);
        return artist == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(artist).build();
    }

    @POST
    @Transactional
    public Response crear(Artist artist) {
        if (artist == null || artist.getName() == null || artist.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El nombre del artista es obligatorio").build();
        }
        artist.setId(null);
        artist.setName(artist.getName().trim());
        em.persist(artist);
        return Response.status(Response.Status.CREATED).entity(artist).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response actualizar(@PathParam("id") Long id, Artist datos) {
        if (datos == null || datos.getName() == null || datos.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El nombre del artista es obligatorio").build();
        }
        Artist artist = em.find(Artist.class, id);
        if (artist == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        artist.setName(datos.getName().trim());
        artist.setBiography(datos.getBiography());
        artist.setNationality(datos.getNationality());
        return Response.ok(artist).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response eliminar(@PathParam("id") Long id) {
        Artist artist = em.find(Artist.class, id);
        if (artist == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Long artifacts = em.createQuery(
                "SELECT COUNT(a) FROM Artifact a WHERE a.artist.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        if (artifacts > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("No se puede eliminar un artista asociado a piezas").build();
        }
        em.remove(artist);
        return Response.noContent().build();
    }
}