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

El proyecto sigue la estructura estándar de una aplicación Quarkus. El código Java y los recursos REST se encuentran organizados bajo `src/main`:

```text
src/
└── main/
    ├── java/
    │   └── museo/
    │       ├── Artist.java
    │       ├── ArtistResource.java
    │       ├── Artifact.java
    │       ├── ArtifactResource.java
    │       ├── Category.java
    │       ├── CategoryResource.java
    │       ├── ExhibitionHistory.java
    │       ├── ExhibitionRoom.java
    │       ├── ExhibitionRoomResource.java
    │       ├── Museum.java
    │       ├── MuseumResource.java
    │       ├── Tour.java
    │       └── TourResource.java
    ├── docker/
    │   ├── Dockerfile.jvm
    │   ├── Dockerfile.legacy-jar
    │   ├── Dockerfile.native
    │   └── Dockerfile.native-micro
    └── resources/
        ├── application.properties
        └── META-INF/
            └── resources/
                ├── index.html
                ├── app.js
                └── styles.css
```

Pruebas del funcionamiento
*Pagina*
<img width="958" height="474" alt="{79966AB6-7950-425E-A85E-5CD971FBA57E}" src="https://github.com/user-attachments/assets/113c05e6-1eff-4811-9548-789d67d80f50" />
*Base de datos*

<img width="522" height="205" alt="{6AC77E89-727E-452E-8A2B-771928652CF0}" src="https://github.com/user-attachments/assets/8059bde7-c0f3-45e3-9a46-c9e7a46b19ec" />
