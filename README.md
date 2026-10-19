# TicketDesk

TicketDesk es una aplicación web para la gestión de incidencias y solicitudes de soporte dentro de una organización.

El proyecto está desarrollado como proyecto personal de portfolio con el objetivo de aplicar y demostrar conocimientos de desarrollo backend con Java y Spring Boot siguiendo prácticas utilizadas en aplicaciones empresariales.

## Objetivo

El objetivo de TicketDesk es permitir gestionar todo el ciclo de vida de una incidencia:

- Creación de tickets.
- Clasificación por categoría y prioridad.
- Asignación a técnicos.
- Seguimiento del estado.
- Gestión de usuarios y roles.
- Registro del historial de cambios.
- Control de tiempos de resolución.
- Gestión de SLA.
- Generación de estadísticas.

El proyecto irá creciendo progresivamente, incorporando nuevas funcionalidades y tecnologías a medida que avance el desarrollo.

## Tecnologías iniciales

- Java 25
- Spring Boot 4.1.1
- Maven
- Spring Web
- Bean Validation
- Spring Boot DevTools

Durante las siguientes fases se incorporarán otras tecnologías como:

- PostgreSQL
- Spring Data JPA
- Hibernate
- Spring Security
- JWT
- OpenAPI / Swagger
- JUnit
- Mockito
- Docker
- GitHub Actions

## Arquitectura

La aplicación seguirá inicialmente una arquitectura por capas:

Controller → Service → Repository → Database

También se utilizarán entidades, DTOs, validaciones, gestión centralizada de excepciones y seguridad.

La arquitectura evolucionará conforme aumenten las necesidades del proyecto.

## Estado del proyecto

Actualmente se encuentra completada la configuración inicial del proyecto Spring Boot.

Próximo objetivo:

Configurar la persistencia de datos utilizando PostgreSQL y Spring Data JPA.