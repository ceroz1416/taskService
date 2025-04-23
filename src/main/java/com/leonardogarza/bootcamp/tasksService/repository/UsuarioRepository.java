package com.leonardogarza.bootcamp.tasksService.repository;

import com.leonardogarza.bootcamp.tasksService.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
