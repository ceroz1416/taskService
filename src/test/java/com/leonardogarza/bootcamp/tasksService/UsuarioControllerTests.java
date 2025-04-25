package com.leonardogarza.bootcamp.tasksService;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.leonardogarza.bootcamp.tasksService.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import net.minidev.json.JSONArray;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsuarioControllerTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void retornaListaUsuarios() {
        ResponseEntity<String> response = restTemplate.getForEntity("/usuarios", String.class);
        //Valida HTTP Status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        //Valida retorno del get
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int usuariosCount = documentContext.read("$.length()");
        assertThat(usuariosCount).isEqualTo(4);

        //Valida ids
        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(1, 2, 3, 4);

        //Valida los nombres
        JSONArray nombres = documentContext.read("$..nombre");
        assertThat(nombres).containsExactlyInAnyOrder("Leonardo G", "Mich B", "Juan Carlos Bodoque", "Rodolfo el Reno");
    }

    @Test
    void retornaUnUsuarioPorId() {

        ResponseEntity<String> response = restTemplate.getForEntity("/usuarios/3", String.class);
        //Valida HTTP Status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        //Valida retorno del get
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(3);

        //Valida nombre
        String nombre = documentContext.read("$.nombre");
        assertThat(nombre).isEqualTo("Juan Carlos Bodoque");

        //Valida el email
        String email = documentContext.read("$.email");
        assertThat(email).isEqualTo("notaverde@21minutos.com");
    }

    //Valida que el servicio devuelva un 404 cuando no encuentra un usuario
    @Test
    void shouldNotReturnAnUsuarioWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/usuarios/9999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    // Valida que el servicio crea un nuevo usuario
    @Test
    @DirtiesContext
    void shouldCreateANewUsuario() {
        Usuario usuario = new Usuario("Usuario de prueba", "prueba@prueba.com");
        ResponseEntity<Void> response = restTemplate
                .postForEntity("/usuarios", usuario, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI location = response.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(location, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String nombre = documentContext.read("$.nombre");
        String email = documentContext.read("$.email");

        assertThat(id).isNotNull();
        assertThat(nombre).isEqualTo("Usuario de prueba");
        assertThat(email).isEqualTo("prueba@prueba.com");
    }

    //Valida que el servicio actualice correctamente un usuario existente
    @Test
    @DirtiesContext
    void shouldUpdateAnExistingUsuario() {
        Usuario usuario = new Usuario("Prueba", "prueba@prueba.com");
        HttpEntity<Usuario> request = new HttpEntity<>(usuario);

        ResponseEntity<Void> response = restTemplate
                .exchange("/usuarios/1", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/usuarios/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String nombre = documentContext.read("$.nombre");
        String email = documentContext.read("$.email");

        assertThat(id).isNotNull();
        assertThat(nombre).isEqualTo("Prueba");
        assertThat(email).isEqualTo("prueba@prueba.com");
    }

    //Valida que el servicio regrese un not found cuando se intenta actualizar un usuario inexistente
    @Test
    @DirtiesContext
    void shouldNotUpdateAnNonExistingUsuario() {
        Usuario usuario = new Usuario("Prueba", "prueba@prueba.com");
        HttpEntity<Usuario> request = new HttpEntity<>(usuario);

        ResponseEntity<Void> response = restTemplate
                .exchange("/usuarios/9999", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // Valida que el servicio elimine un usuario existente correctamente
    @Test
    @DirtiesContext
    void shouldDeleteAnUsuarioById() {
        ResponseEntity<Void> response = restTemplate
                .exchange("/usuarios/4", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/usuarios/4", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    //Valida que el servicio regrese un not found cuando se intenta eliminar un usuario inexistente
    @Test
    @DirtiesContext
    void shouldNotDeleteAnNonExistingUsuario() {
        ResponseEntity<Void> response = restTemplate
                .exchange("/usuarios/9999", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // Valida que el servicio no elimine un usuario con tareas existentes
    @Test
    @DirtiesContext
    void shouldNoDeleteAnUsuarioById() {
        ResponseEntity<Void> response = restTemplate
                .exchange("/usuarios/3", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

}
