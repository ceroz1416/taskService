package com.leonardogarza.bootcamp.tasksService.model;

import com.leonardogarza.bootcamp.tasksService.util.Constantes;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Tarea {

    @Id
    @GeneratedValue
    private Long id;
    private String titulo;
    private String descripcion;
    private String estado;
    private LocalDate fechaLimite;
    //Ligamos usuarioAsignado como llave foranea a usuario_id
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuarioAsignado;

    //Antes de insert y update validamos si la tarea ya venció para cambiar el estatus.
    @PrePersist
    @PreUpdate
    public void validaTareasVencidas() {
        if(!this.estado.equals(Constantes.TAREA_ESTADO_COMPLETADA) && this.fechaLimite != null && fechaLimite.isBefore(LocalDate.now()))
            this.estado = Constantes.TAREA_ESTADO_VENCIDA;
    }

    public Tarea() {
    }

    public Tarea(String titulo, String descripcion, String estado, LocalDate fechaLimite, Usuario usuarioAsignado){
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaLimite = fechaLimite;
        this.usuarioAsignado = usuarioAsignado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Usuario getUsuarioAsignado() {
        return usuarioAsignado;
    }

    public void setUsuarioAsignado(Usuario usuarioAsignado) {
        this.usuarioAsignado = usuarioAsignado;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tarea tarea = (Tarea) o;
        return Objects.equals(id, tarea.id) && Objects.equals(titulo, tarea.titulo) && Objects.equals(descripcion, tarea.descripcion)
                && Objects.equals(estado, tarea.estado) && Objects.equals(fechaLimite, tarea.fechaLimite)
                && Objects.equals(usuarioAsignado, tarea.usuarioAsignado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titulo, descripcion, estado, fechaLimite, usuarioAsignado.getNombre());
    }

    @Override
    public String toString() {
        return "Empleado{" + "id=" + id + ", titulo=" + titulo + ", descripcion=" + descripcion + ", estado=" + estado +
                ", fechaLimite=" + fechaLimite + ", usuarioAsignado=" + usuarioAsignado.getNombre() + "+ '}'";
    }

}
