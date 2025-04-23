package com.leonardogarza.bootcamp.tasksService.repository;

import com.leonardogarza.bootcamp.tasksService.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
    boolean existsByUsuarioAsignadoId(Long usuarioId);
}
