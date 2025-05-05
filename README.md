# taskService
Final Project of Bootcamp de Microservicios con Java

Pasos para ejecutar el proyecto (Windows):
    1- Tener instalado java 17 y maven
    2- Correr un ./mvnw clean install para instalación de dependencias.
    3- Correr en consola ./mvnw spring-boot:run para correr el proyecto.

Se realiza una Task App, añadiendo un poco de complejidad usando una fecha límite para las tareas.
Se utilizan reglas de negocio contemplando por ejemplo que no se pueda eliminar un usuario con tareas asignadas, o que el estatus de las tareas sea válido.
Se crean pruebas unitarias para todos los servicios y reglas de negocio.

Nota: Documentación de apis, pruebas unitarias e inicialización del Spring Initializr en ../docs

