package museo;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Path("/tours")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class TourResource {

    @Inject
    EntityManager em;

    @GET
    public List<Tour> listar() {
        return em.createQuery("SELECT DISTINCT t FROM Tour t LEFT JOIN FETCH t.rooms ORDER BY t.id", Tour.class)
                .getResultList();
    }

    @GET
    @Path("/{id}")
    public Response buscar(@PathParam("id") Long id) {
        List<Tour> tours = em.createQuery(
                "SELECT DISTINCT t FROM Tour t LEFT JOIN FETCH t.rooms WHERE t.id = :id", Tour.class)
                .setParameter("id", id)
                .getResultList();
        return tours.isEmpty()
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(tours.get(0)).build();
    }

    @POST
    @Transactional
    public Response crear(Tour tour) {
        Response validation = validar(tour);
        if (validation != null) {
            return validation;
        }
        Response roomsResponse = resolverSalas(tour);
        if (roomsResponse != null) {
            return roomsResponse;
        }
        tour.setId(null);
        tour.setName(tour.getName().trim());
        em.persist(tour);
        return Response.status(Response.Status.CREATED).entity(tour).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response actualizar(@PathParam("id") Long id, Tour datos) {
        Response validation = validar(datos);
        if (validation != null) {
            return validation;
        }
        Tour tour = em.find(Tour.class, id);
        if (tour == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Response roomsResponse = resolverSalas(datos);
        if (roomsResponse != null) {
            return roomsResponse;
        }
        tour.setName(datos.getName().trim());
        tour.setDescription(datos.getDescription());
        tour.setStartDate(datos.getStartDate());
        tour.setEndDate(datos.getEndDate());
        tour.setLandingPageUrl(datos.getLandingPageUrl());
        tour.setRooms(datos.getRooms());
        return Response.ok(tour).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response eliminar(@PathParam("id") Long id) {
        Tour tour = em.find(Tour.class, id);
        if (tour == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        em.remove(tour);
        return Response.noContent().build();
    }

    private Response validar(Tour tour) {
        if (tour == null || tour.getName() == null || tour.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El nombre de la visita guiada es obligatorio").build();
        }
        if (tour.getStartDate() == null || tour.getEndDate() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Las fechas de inicio y fin son obligatorias").build();
        }
        if (!tour.getEndDate().isAfter(tour.getStartDate())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La fecha de fin debe ser posterior a la fecha de inicio").build();
        }
        return null;
    }

    private Response resolverSalas(Tour tour) {
        if (tour.getRooms() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El recorrido debe incluir al menos una sala").build();
        }
        if (tour.getRooms().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El recorrido debe incluir al menos una sala").build();
        }
        List<ExhibitionRoom> rooms = new ArrayList<>();
        Set<Long> roomIds = new HashSet<>();
        for (ExhibitionRoom requestedRoom : tour.getRooms()) {
            if (requestedRoom == null || requestedRoom.getId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Cada sala del recorrido debe incluir un id").build();
            }
            Long roomId = requestedRoom.getId();
            if (!roomIds.add(roomId)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Una sala no puede repetirse en el recorrido").build();
            }
            ExhibitionRoom room = em.find(ExhibitionRoom.class, roomId);
            if (room == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No existe la sala con id " + roomId).build();
            }
            rooms.add(room);
        }
        tour.setRooms(rooms);
        return null;
    }
}