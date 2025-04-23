package com.leonardogarza.bootcamp.tasksService.util;

import java.util.List;

public class Constantes {

    private Constantes() {}

    //Estados para las tareas
    public static final String TAREA_ESTADO_PENDIENTE = "Pendiente";
    public static final String TAREA_ESTADO_EN_PROGRESO = "En progreso";
    public static final String TAREA_ESTADO_VENCIDA = "Vencida";
    public static final String TAREA_ESTADO_COMPLETADA = "Completada";

    public static final List<String> ESTADOS_VALIDOS = List.of(
            TAREA_ESTADO_PENDIENTE,
            TAREA_ESTADO_EN_PROGRESO,
            TAREA_ESTADO_VENCIDA,
            TAREA_ESTADO_COMPLETADA
    );

}
