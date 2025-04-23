package com.leonardogarza.bootcamp.tasksService.controller;

import com.leonardogarza.bootcamp.tasksService.model.Usuario;
import com.leonardogarza.bootcamp.tasksService.repository.TareaRepository;
import com.leonardogarza.bootcamp.tasksService.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final TareaRepository tareaRepository;
    public UsuarioController(UsuarioRepository usuarioRepository, TareaRepository tareaRepository){
        this.usuarioRepository = usuarioRepository;
        this.tareaRepository = tareaRepository;
    }

    @GetMapping("/holamundo")
    public static ResponseEntity<String> holaMundo() {
        return ResponseEntity.ok("Hola Mundo! Project Works!");
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listAll(){
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> findById(@PathVariable Long id){
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.isPresent() ? ResponseEntity.ok(usuario.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario newUsuario, UriComponentsBuilder ucb){
        try {
            Usuario usuarioSaved = usuarioRepository.save(newUsuario);
            URI uriUsuario = ucb.path("usuarios/{id}").buildAndExpand(usuarioSaved.getId()).toUri();
            return ResponseEntity.created(uriUsuario).build();
        } catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@RequestBody Usuario updatedUsuario, @PathVariable Long id){
        try {
            return ResponseEntity.ok(usuarioRepository.findById(id)
                    .map(usuario -> {
                        usuario.setNombre(updatedUsuario.getNombre());
                        usuario.setEmail(updatedUsuario.getEmail());
                        usuarioRepository.save(usuario);
                        return ResponseEntity.noContent().build();
                    }).orElseGet(() -> {
                        usuarioRepository.save(updatedUsuario);
                        return ResponseEntity.ok(updatedUsuario);
                    }));
        } catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id){
        if (usuarioRepository.existsById(id)) {
            if (tareaRepository.existsByUsuarioAsignadoId(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede eliminar el usuario porque tiene tareas asignadas.");
            }
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
