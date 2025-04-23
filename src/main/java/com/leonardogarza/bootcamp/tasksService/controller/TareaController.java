package com.leonardogarza.bootcamp.tasksService.controller;

import com.leonardogarza.bootcamp.tasksService.model.Tarea;
import com.leonardogarza.bootcamp.tasksService.model.Usuario;
import com.leonardogarza.bootcamp.tasksService.repository.TareaRepository;
import com.leonardogarza.bootcamp.tasksService.repository.UsuarioRepository;
import com.leonardogarza.bootcamp.tasksService.util.Constantes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tareas")
public class TareaController {

    private final TareaRepository tareaRepository;
    private final UsuarioRepository usuarioRepository;
    public TareaController(TareaRepository tareaRepository, UsuarioRepository usuarioRepository){
        this.tareaRepository = tareaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<Tarea>> listAll(){
        return ResponseEntity.ok(tareaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarea> findById(@PathVariable Long id){
        Optional<Tarea> tarea = tareaRepository.findById(id);
        return tarea.isPresent() ? ResponseEntity.ok(tarea.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createTarea(@RequestBody Tarea newTarea, UriComponentsBuilder ucb){
        try {
            if (!Constantes.ESTADOS_VALIDOS.contains(newTarea.getEstado())) {
                return ResponseEntity.badRequest().body("Estado de tarea inválido");
            }
            Optional<Usuario> usuario = usuarioRepository.findById(newTarea.getUsuarioAsignado().getId());
            if (usuario.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
            Tarea tareaSaved = tareaRepository.save(newTarea);
            URI uriTarea = ucb.path("tareas/{id}").buildAndExpand(tareaSaved.getId()).toUri();
            return ResponseEntity.created(uriTarea).build();
        } catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTarea(@RequestBody Tarea updatedTarea, @PathVariable Long id){
        try {
            if (!Constantes.ESTADOS_VALIDOS.contains(updatedTarea.getEstado())) {
                return ResponseEntity.badRequest().body("Estado de tarea inválido");
            }
            Optional<Usuario> usuario = usuarioRepository.findById(updatedTarea.getUsuarioAsignado().getId());
            if (usuario.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
            return ResponseEntity.ok(tareaRepository.findById(id)
                    .map(tarea -> {
                        tarea.setTitulo(updatedTarea.getTitulo());
                        tarea.setDescripcion(updatedTarea.getDescripcion());
                        tarea.setEstado(updatedTarea.getEstado());
                        tarea.setFechaLimite(updatedTarea.getFechaLimite());
                        tarea.setUsuarioAsignado(updatedTarea.getUsuarioAsignado());
                        tareaRepository.save(tarea);
                        return ResponseEntity.noContent().build();
                    }).orElseGet(() -> {
                        tareaRepository.save(updatedTarea);
                        return ResponseEntity.ok(updatedTarea);
                    }));
        } catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Tarea> deleteTarea(@PathVariable Long id){
        if (tareaRepository.existsById(id)) {
            tareaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
