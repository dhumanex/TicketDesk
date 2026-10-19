# 02 - Arquitectura inicial

## Arquitectura elegida

TicketDesk comenzará utilizando una arquitectura por capas.

La estructura principal será:

Controller → Service → Repository → Database

Cada capa tendrá una responsabilidad concreta.

## Controller

La capa Controller será responsable de recibir las peticiones HTTP realizadas a la API.

Ejemplos:

GET /api/tickets

POST /api/tickets

GET /api/tickets/{id}

El Controller no deberá contener la lógica principal de negocio.

Su responsabilidad será recibir la petición, validarla y delegar el trabajo correspondiente.

## Service

La capa Service contendrá la lógica de negocio de la aplicación.

Por ejemplo:

- Crear un ticket.
- Asignar un técnico.
- Cambiar la prioridad.
- Resolver una incidencia.
- Calcular los tiempos de SLA.

Esta capa actuará como intermediaria entre los controladores y el acceso a datos.

## Repository

La capa Repository será responsable de comunicarse con la base de datos.

Utilizaremos Spring Data JPA para facilitar las operaciones de persistencia.

Entre sus responsabilidades estarán:

- Buscar información.
- Guardar registros.
- Actualizar datos.
- Eliminar registros.

## Entity

Las entidades representarán los objetos almacenados en la base de datos.

Algunas entidades previstas son:

- User
- Ticket
- Comment
- Department
- Category

Las entidades irán apareciendo progresivamente según las necesidades del proyecto.

## DTO

Los DTO (Data Transfer Object) se utilizarán para intercambiar información entre la API y los clientes.

Esto permitirá evitar exponer directamente las entidades de base de datos.

Por ejemplo:

CreateTicketRequest

TicketResponse

LoginRequest

## Flujo de una petición

Un flujo simplificado será:

Cliente
↓
HTTP Request
↓
Controller
↓
Service
↓
Repository
↓
Base de datos

La respuesta recorrerá posteriormente el camino inverso.

## ¿Por qué empezamos con una arquitectura por capas?

Para el tamaño inicial de TicketDesk, una arquitectura por capas proporciona una separación clara de responsabilidades sin introducir una complejidad innecesaria.

El proyecto no comenzará utilizando microservicios.

Dividir una aplicación pequeña en múltiples microservicios introduciría complejidad de despliegue, comunicación, monitorización y mantenimiento sin proporcionar inicialmente una ventaja real.

Si el proyecto alcanza un punto en el que determinadas partes necesitan escalar o evolucionar independientemente, se estudiará la separación de algunos módulos.

## Estructura prevista

El código se organizará inicialmente de la siguiente manera:

com.humanex.ticketdesk

- controller
- service
- repository
- entity
- dto
- exception
- security
- config

Esta estructura irá evolucionando conforme avance el proyecto.