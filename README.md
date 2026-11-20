## Museo Backend - Quarkus API

API REST desarrollada con Quarkus, Hibernate ORM (JPA) y MySQL para la gestión integral de un museo, abarcando exposiciones, salas, artefactos y artistas.

Objetivo del Proyecto
​Centralizar y estructurar la administración operativa y cultural de museos mediante una arquitectura backend moderna, ligera y escalable. El proyecto busca facilitar el control de inventarios de obras, la asignación de artistas, la organización por categorías y salas, y la gestión de tours institucionales bajo estándares profesionales de desarrollo de software.

### Características del Proyecto

* **Framework de Alto Rendimiento:** Construido con Quarkus para tiempos de inicio ultrarrápidos y bajo consumo de memoria.
* **Persistencia de Datos:** Integración con MySQL mediante Hibernate ORM y anotaciones JPA.
* **Serialización JSON Optimizada:** Configurado con Jackson para evitar bucles infinitos en relaciones bidireccionales mediante `@JsonIgnore`.
* **Endpoints REST:** Arquitectura orientada a recursos para la administración de las entidades del museo.


### Tecnologías y Dependencias Utilizadas

* **Java 25** (Entorno de desarrollo)
* **Quarkus 3.40.1.1**
    * `quarkus-rest` (Soporte para servicios REST modernos)
    * `quarkus-rest-jackson` (Mapeo y serialización JSON con Jackson)
    * `quarkus-hibernate-orm` (Mapeo objeto-relacional JPA)
    * `quarkus-jdbc-mysql` (Conector JDBC para bases de datos MySQL)
* **Maven** (Gestor de dependencias y empaquetado)

### Estructura del Proyecto

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

## Pruebas del funcionamiento

### Menu Principal
<img width="958" height="474" alt="{79966AB6-7950-425E-A85E-5CD971FBA57E}" src="https://github.com/user-attachments/assets/113c05e6-1eff-4811-9548-789d67d80f50" />

### Categorias
<img width="953" height="435" alt="{6C393578-888A-4027-A114-FF87C1549DB6}" src="https://github.com/user-attachments/assets/f9217d57-b4af-4ee5-9f35-1c0508d47c49" />

### Artistas
<img width="951" height="436" alt="{F2962161-35D2-4D2D-9E7D-90A4F18CFD2A}" src="https://github.com/user-attachments/assets/d7ce0f7a-82a1-4dd1-b615-c1bd763f7c40" />

### Salas
<img width="947" height="433" alt="{844E6247-F340-48DB-9830-7ED9611F5EC9}" src="https://github.com/user-attachments/assets/b50b1021-fabc-4fa9-8cff-79772ab181d9" />

### Piezas
<img width="952" height="430" alt="{79C89E01-F2DF-4B1F-9F67-C199BA3D7685}" src="https://github.com/user-attachments/assets/c628b002-e425-4cb3-acb6-d6c00df6e1f9" />

### Recorridos
<img width="948" height="469" alt="{2FFBD9D2-4191-47C0-82A5-EF7B771209A1}" src="https://github.com/user-attachments/assets/e30b37b9-eaab-4e30-81db-45abc92d894d" />


### Base de datos

<img width="522" height="205" alt="{6AC77E89-727E-452E-8A2B-771928652CF0}" src="https://github.com/user-attachments/assets/8059bde7-c0f3-45e3-9a46-c9e7a46b19ec" />
<img width="476" height="206" alt="{ECAC494E-9AF0-45C6-952C-DB5BB4E1ED95}" src="https://github.com/user-attachments/assets/0bd9d33e-f0a4-4081-81b3-84b3c2426d15" />
<img width="481" height="218" alt="{03B193D6-C884-4AE0-BF9E-599D7B8DE7C4}" src="https://github.com/user-attachments/assets/51916bc5-d22c-4b97-b7b3-6a8f27c774c8" />
<img width="473" height="210" alt="{F78B99B7-4542-423B-B00B-2F247A3434AA}" src="https://github.com/user-attachments/assets/d95bf61d-4313-4541-baa2-e6e5e3357307" />
<img width="830" height="164" alt="image" src="https://github.com/user-attachments/assets/1ab3c748-6334-4c8f-a4d4-62a3afb80fcb" />
<img width="547" height="161" alt="image" src="https://github.com/user-attachments/assets/283d4f8b-7cd3-4fbb-8fc6-92ab9a715f37" />

