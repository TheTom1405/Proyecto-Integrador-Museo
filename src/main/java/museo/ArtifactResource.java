package museo;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/artifacts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtifactResource {

    @Inject
    EntityManager em;

    @GET
    public List<Artifact> listar() {
        return em.createQuery("SELECT a FROM Artifact a ORDER BY a.id", Artifact.class).getResultList();
    }

    @GET
    @Path("/{id}")
    public Response buscar(@PathParam("id") Long id) {
        Artifact artifact = em.find(Artifact.class, id);
        return artifact == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(artifact).build();
    }

    @GET
    @Path("/{id}/history")
    public Response historial(@PathParam("id") Long id) {
        if (em.find(Artifact.class, id) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<ExhibitionHistory> history = em.createQuery(
                "SELECT h FROM ExhibitionHistory h WHERE h.artifact.id = :id ORDER BY h.startDate",
                ExhibitionHistory.class)
                .setParameter("id", id)
                .getResultList();
        return Response.ok(history).build();
    }

    @POST
    @Transactional
    public Response crear(Artifact artifact) {
        Response validation = validar(artifact);
        if (validation != null) {
            return validation;
        }
        Response relations = resolverRelaciones(artifact);
        if (relations != null) {
            return relations;
        }
        Response capacity = validarCapacidad(artifact.getCurrentRoom(), null);
        if (capacity != null) {
            return capacity;
        }

        artifact.setId(null);
        em.persist(artifact);
        if (artifact.getCurrentRoom() != null) {
            abrirPeriodo(artifact, artifact.getCurrentRoom(), LocalDateTime.now());
        }
        return Response.status(Response.Status.CREATED).entity(artifact).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response actualizar(@PathParam("id") Long id, Artifact datos) {
        Response validation = validar(datos);
        if (validation != null) {
            return validation;
        }
        Artifact artifact = em.find(Artifact.class, id);
        if (artifact == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Response relations = resolverRelaciones(datos);
        if (relations != null) {
            return relations;
        }

        Long oldRoomId = artifact.getCurrentRoom() == null ? null : artifact.getCurrentRoom().getId();
        Long newRoomId = datos.getCurrentRoom() == null ? null : datos.getCurrentRoom().getId();
        boolean roomChanged = !java.util.Objects.equals(oldRoomId, newRoomId);
        if (roomChanged) {
            Response capacity = validarCapacidad(datos.getCurrentRoom(), id);
            if (capacity != null) {
                return capacity;
            }
        }

        artifact.setTitle(datos.getTitle().trim());
        artifact.setDescription(datos.getDescription());
        artifact.setImageUrl(datos.getImageUrl());
        artifact.setCreationDate(datos.getCreationDate());
        artifact.setAcquisitionDate(datos.getAcquisitionDate());
        artifact.setConservationPercentage(datos.getConservationPercentage());
        artifact.setCategory(datos.getCategory());
        artifact.setArtist(datos.getArtist());
        artifact.setCurrentRoom(datos.getCurrentRoom());

        if (roomChanged) {
            LocalDateTime now = LocalDateTime.now();
            cerrarPeriodoActual(artifact, now);
            if (artifact.getCurrentRoom() != null) {
                abrirPeriodo(artifact, artifact.getCurrentRoom(), now);
            }
        }
        return Response.ok(artifact).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response eliminar(@PathParam("id") Long id) {
        Artifact artifact = em.find(Artifact.class, id);
        if (artifact == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Long historyEntries = em.createQuery(
                "SELECT COUNT(h) FROM ExhibitionHistory h WHERE h.artifact.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        if (historyEntries > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("No se puede eliminar una pieza con historial de exposición").build();
        }
        em.remove(artifact);
        return Response.noContent().build();
    }

    private Response validar(Artifact artifact) {
        if (artifact == null || artifact.getTitle() == null || artifact.getTitle().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El título de la pieza es obligatorio").build();
        }
        Integer conservation = artifact.getConservationPercentage();
        if (conservation == null || conservation < 0 || conservation > 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El porcentaje de conservación debe estar entre 0 y 100").build();
        }
        if (artifact.getCreationDate() != null && artifact.getAcquisitionDate() != null
                && artifact.getAcquisitionDate().isBefore(artifact.getCreationDate())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La fecha de adquisición no puede ser anterior a la fecha de creación").build();
        }
        artifact.setTitle(artifact.getTitle().trim());
        return null;
    }

    private Response resolverRelaciones(Artifact artifact) {
        if (artifact.getCategory() != null) {
            Long id = artifact.getCategory().getId();
            Category category = id == null ? null : em.find(Category.class, id);
            if (category == null) {
                return Response.status(id == null ? Response.Status.BAD_REQUEST : Response.Status.NOT_FOUND)
                        .entity("La categoría indicada no existe o no tiene un id válido").build();
            }
            artifact.setCategory(category);
        }
        if (artifact.getArtist() != null) {
            Long id = artifact.getArtist().getId();
            Artist artist = id == null ? null : em.find(Artist.class, id);
            if (artist == null) {
                return Response.status(id == null ? Response.Status.BAD_REQUEST : Response.Status.NOT_FOUND)
                        .entity("El artista indicado no existe o no tiene un id válido").build();
            }
            artifact.setArtist(artist);
        }
        if (artifact.getCurrentRoom() != null) {
            Long id = artifact.getCurrentRoom().getId();
            ExhibitionRoom room = id == null ? null : em.find(ExhibitionRoom.class, id);
            if (room == null) {
                return Response.status(id == null ? Response.Status.BAD_REQUEST : Response.Status.NOT_FOUND)
                        .entity("La sala indicada no existe o no tiene un id válido").build();
            }
            artifact.setCurrentRoom(room);
        }
        return null;
    }

    private Response validarCapacidad(ExhibitionRoom room, Long artifactId) {
        if (room == null) {
            return null;
        }
        if (room.getCapacity() == null || room.getCapacity() < 1) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("La sala no tiene una capacidad válida configurada").build();
        }
        String query = "SELECT COUNT(a) FROM Artifact a WHERE a.currentRoom.id = :roomId"
                + (artifactId == null ? "" : " AND a.id <> :artifactId");
        var countQuery = em.createQuery(query, Long.class).setParameter("roomId", room.getId());
        if (artifactId != null) {
            countQuery.setParameter("artifactId", artifactId);
        }
        if (countQuery.getSingleResult() >= room.getCapacity()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("La sala alcanzó su capacidad máxima").build();
        }
        return null;
    }

    private void abrirPeriodo(Artifact artifact, ExhibitionRoom room, LocalDateTime startDate) {
        ExhibitionHistory history = new ExhibitionHistory();
        history.setArtifact(artifact);
        history.setRoom(room);
        history.setStartDate(startDate);
        em.persist(history);
    }

    private void cerrarPeriodoActual(Artifact artifact, LocalDateTime endDate) {
        List<ExhibitionHistory> activePeriods = em.createQuery(
                "SELECT h FROM ExhibitionHistory h WHERE h.artifact.id = :id AND h.endDate IS NULL",
                ExhibitionHistory.class)
                .setParameter("id", artifact.getId())
                .getResultList();
        for (ExhibitionHistory history : activePeriods) {
            history.setEndDate(endDate);
        }
    }
}
