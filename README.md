Museo Backend - Quarkus API

API REST desarrollada con Quarkus, Hibernate ORM (JPA) y MySQL para la gestión integral de un museo, abarcando exposiciones, salas, artefactos y artistas.

Objetivo del Proyecto
​Centralizar y estructurar la administración operativa y cultural de museos mediante una arquitectura backend moderna, ligera y escalable. El proyecto busca facilitar el control de inventarios de obras, la asignación de artistas, la organización por categorías y salas, y la gestión de tours institucionales bajo estándares profesionales de desarrollo de software.

Características del Proyecto

* **Framework de Alto Rendimiento:** Construido con Quarkus para tiempos de inicio ultrarrápidos y bajo consumo de memoria.
* **Persistencia de Datos:** Integración con MySQL mediante Hibernate ORM y anotaciones JPA.
* **Serialización JSON Optimizada:** Configurado con Jackson para evitar bucles infinitos en relaciones bidireccionales mediante `@JsonIgnore`.
* **Endpoints REST:** Arquitectura orientada a recursos para la administración de las entidades del museo.


Tecnologías y Dependencias Utilizadas

* **Java 25** (Entorno de desarrollo)
* **Quarkus 3.40.1.1**
    * `quarkus-rest` (Soporte para servicios REST modernos)
    * `quarkus-rest-jackson` (Mapeo y serialización JSON con Jackson)
    * `quarkus-hibernate-orm` (Mapeo objeto-relacional JPA)
    * `quarkus-jdbc-mysql` (Conector JDBC para bases de datos MySQL)
* **Maven** (Gestor de dependencias y empaquetado)

Estructura del Proyecto

El código fuente se organiza bajo el paquete `museo`:

```text
src/
└── main/
    ├── java/
    │   └── museo/
    │       ├── Artist.java           # Entidad JPA para los artistas
    │       ├── Artifact.java         # Entidad JPA para las piezas y obras
    │       ├── ExhibitionRoom.java   # Entidad JPA para las salas de exposición
    │       └── Category.java         # Entidad JPA para las categorías
    └── resources/
        └── application.properties    # Configuración de conexión a la base de datos
