package com.leonardogarza.bootcamp.tasksService;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.leonardogarza.bootcamp.tasksService.model.Tarea;
import com.leonardogarza.bootcamp.tasksService.model.Usuario;
import com.leonardogarza.bootcamp.tasksService.util.Constantes;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TareaControllerTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void retornaListaTareas() {
        ResponseEntity<String> response = restTemplate.getForEntity("/tareas", String.class);
        //Valida HTTP Status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        //Valida retorno del get
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int tareasCount = documentContext.read("$.length()");
        assertThat(tareasCount).isEqualTo(3);

        //Valida ids
        JSONArray ids = documentContext.read("$[*].id");
        assertThat(ids).containsExactlyInAnyOrder(1, 2, 3);

        //Valida las tareas
        JSONArray titulos = documentContext.read("$..titulo");
        assertThat(titulos).containsExactlyInAnyOrder("Proyecto Bootcamp", "Presentacion Trabajo", "Nota Verde");
    }

    @Test
    void retornaUnTareaPorId() {

        ResponseEntity<String> response = restTemplate.getForEntity("/tareas/3", String.class);
        //Valida HTTP Status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        //Valida retorno del get
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(3);

        //Valida titulo
        String titulo = documentContext.read("$.titulo");
        assertThat(titulo).isEqualTo("Nota Verde");

        //Valida el estatus
        String estado = documentContext.read("$.estado");
        assertThat(estado).isIn(Constantes.ESTADOS_VALIDOS);

        //Valida la fecha
        String fecha = documentContext.read("$.fechaLimite");
        assertThat(fecha).isEqualTo("2025-05-16");

        //Valida el usuario asignado
        Number usuario = documentContext.read("$.usuarioAsignado.id");
        assertThat(usuario).isEqualTo(3);
    }

    //Valida que el servicio devuelva un 404 cuando no encuentra un tarea
    @Test
    void shouldNotReturnAnTareaWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/tareas/9999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    // Valida que el servicio crea un nuevo tarea
    @Test
    @DirtiesContext
    void shouldCreateANewTarea() {
        ResponseEntity<Usuario> userResponse = restTemplate.getForEntity("/usuarios/3", Usuario.class);
        Usuario usuario = userResponse.getBody();
        Tarea tarea = new Tarea("Emision", "Preparar la nueva emision",
                Constantes.TAREA_ESTADO_PENDIENTE, LocalDate.of(2027, 7, 12), usuario);
        ResponseEntity<Void> response = restTemplate
                .postForEntity("/tareas", tarea, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI location = response.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(location, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String titulo = documentContext.read("$.titulo");
        String descripcion = documentContext.read("$.descripcion");
        String estado = documentContext.read("$.estado");
        String fechaLimite = documentContext.read("$.fechaLimite");
        Number usuarioAsignado = documentContext.read("$.usuarioAsignado.id");

        assertThat(id).isNotNull();
        assertThat(titulo).isEqualTo("Emision");
        assertThat(descripcion).isEqualTo("Preparar la nueva emision");
        assertThat(estado).isEqualTo(Constantes.TAREA_ESTADO_PENDIENTE);
        assertThat(fechaLimite).isEqualTo("2027-07-12");
        assertThat(usuarioAsignado).isEqualTo(3);
    }

    // Valida que el servicio no crea una nueva tarea si el estatus es inválido
    @Test
    @DirtiesContext
    void shouldNotCreateANewTareaWhenEstatusInvalid() {
        ResponseEntity<Usuario> userResponse = restTemplate.getForEntity("/usuarios/3", Usuario.class);
        Usuario usuario = userResponse.getBody();
        Tarea tarea = new Tarea("Emision", "Preparar la nueva emision",
                "Estatus invalido", LocalDate.of(2027, 7, 12), usuario);
        ResponseEntity<Void> response = restTemplate
                .postForEntity("/tareas", tarea, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // Valida que el servicio no crea una nueva tarea si el usuario no existe
    @Test
    @DirtiesContext
    void shouldNotCreateANewTareaWhenUsuarioNotFound() {
        Usuario usuario = new Usuario("Prueba", "inexistente@no.com");
        usuario.setId(999L);
        Tarea tarea = new Tarea("Emision", "Preparar la nueva emision",
                Constantes.TAREA_ESTADO_EN_PROGRESO, LocalDate.of(2027, 7, 12), usuario);
        ResponseEntity<Void> response = restTemplate
                .postForEntity("/tareas", tarea, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    //Valida que el servicio actualice correctamente un tarea existente
    @Test
    @DirtiesContext
    void shouldUpdateAnExistingTarea() {
        ResponseEntity<Usuario> userResponse = restTemplate.getForEntity("/usuarios/3", Usuario.class);
        Usuario usuario = userResponse.getBody();
        Tarea tarea = new Tarea("Proyecto Bootcamp cambio", "CAMBIO DE TEXTO",
                Constantes.TAREA_ESTADO_COMPLETADA, LocalDate.of(2026,6,2), usuario);

        HttpEntity<Tarea> request = new HttpEntity<>(tarea);
        ResponseEntity<Void> response = restTemplate
                .exchange("/tareas/1", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/tareas/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String titulo = documentContext.read("$.titulo");
        String descripcion = documentContext.read("$.descripcion");
        String estado = documentContext.read("$.estado");
        String fechaLimite = documentContext.read("$.fechaLimite");
        Number usuarioAsignado = documentContext.read("$.usuarioAsignado.id");

        assertThat(id).isNotNull();
        assertThat(titulo).isEqualTo("Proyecto Bootcamp cambio");
        assertThat(descripcion).isEqualTo("CAMBIO DE TEXTO");
        assertThat(estado).isEqualTo(Constantes.TAREA_ESTADO_COMPLETADA);
        assertThat(fechaLimite).isEqualTo("2026-06-02");
        assertThat(usuarioAsignado).isEqualTo(3);
    }

    // Valida que el servicio no actualiza una nueva tarea si el estatus es inválido
    @Test
    @DirtiesContext
    void shouldNotUpdateANewTareaWhenEstatusInvalid() {
        ResponseEntity<Usuario> userResponse = restTemplate.getForEntity("/usuarios/3", Usuario.class);
        Usuario usuario = userResponse.getBody();
        Tarea tarea = new Tarea("Emision", "Preparar la nueva emision",
                "Estatus invalido", LocalDate.of(2027, 7, 12), usuario);
        HttpEntity<Tarea> request = new HttpEntity<>(tarea);
        ResponseEntity<Void> response = restTemplate
                .exchange("/tareas/1", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // Valida que el servicio no actualiza una nueva tarea si el usuario no existe
    @Test
    @DirtiesContext
    void shouldNotUpdateANewTareaWhenUsuarioNotFound() {
        Usuario usuario = new Usuario("Prueba", "inexistente@no.com");
        usuario.setId(999L);
        Tarea tarea = new Tarea("Emision", "Preparar la nueva emision",
                Constantes.TAREA_ESTADO_EN_PROGRESO, LocalDate.of(2027, 7, 12), usuario);
        HttpEntity<Tarea> request = new HttpEntity<>(tarea);
        ResponseEntity<Void> response = restTemplate
                .exchange("/tareas/1", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    //Valida que el servicio regrese un not found cuando se intenta actualizar una tarea inexistente
    @Test
    @DirtiesContext
    void shouldNotUpdateAnNonExistingUsuario() {
        ResponseEntity<Usuario> userResponse = restTemplate.getForEntity("/usuarios/3", Usuario.class);
        Usuario usuario = userResponse.getBody();
        Tarea tarea = new Tarea("Proyecto Bootcamp cambio", "CAMBIO DE TEXTO",
                Constantes.TAREA_ESTADO_COMPLETADA, LocalDate.of(2026,6,2), usuario);
        HttpEntity<Tarea> request = new HttpEntity<>(tarea);

        ResponseEntity<Void> response = restTemplate
                .exchange("/tareas/9999", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // Valida que el servicio elimine un tarea existente correctamente
    @Test
    @DirtiesContext
    void shouldDeleteAnTareaById() {
        ResponseEntity<Void> response = restTemplate
                .exchange("/tareas/3", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/tareas/3", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    //Valida que el servicio regrese un not found cuando se intenta eliminar una tarea inexistente
    @Test
    @DirtiesContext
    void shouldNotDeleteAnNonExistingTarea() {
        ResponseEntity<Void> response = restTemplate
                .exchange("/tareas/9999", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
