package museo;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class CategoryResource {

    @Inject
    EntityManager em;

    @GET
    public List<Category> listar() {
        return em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }

    @GET
    @Path("/{id}")
    public Response buscar(@PathParam("id") Long id) {
        Category category = em.find(Category.class, id);
        return category == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(category).build();
    }

    @POST
    @Transactional
    public Response crear(Category category) {
        if (category == null || category.getName() == null || category.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El nombre de la categoría es obligatorio").build();
        }
        category.setId(null);
        category.setName(category.getName().trim());
        em.persist(category);
        return Response.status(Response.Status.CREATED).entity(category).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response actualizar(@PathParam("id") Long id, Category datos) {
        if (datos == null || datos.getName() == null || datos.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El nombre de la categoría es obligatorio").build();
        }
        Category category = em.find(Category.class, id);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        category.setName(datos.getName().trim());
        category.setDescription(datos.getDescription());
        return Response.ok(category).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response eliminar(@PathParam("id") Long id) {
        Category category = em.find(Category.class, id);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Long artifacts = em.createQuery(
                "SELECT COUNT(a) FROM Artifact a WHERE a.category.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        if (artifacts > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("No se puede eliminar una categoría que tiene piezas asociadas").build();
        }
        em.remove(category);
        return Response.noContent().build();
    }
}