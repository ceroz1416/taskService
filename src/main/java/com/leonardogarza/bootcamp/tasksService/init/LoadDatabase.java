package com.leonardogarza.bootcamp.tasksService.init;

import com.leonardogarza.bootcamp.tasksService.model.Tarea;
import com.leonardogarza.bootcamp.tasksService.model.Usuario;
import com.leonardogarza.bootcamp.tasksService.repository.TareaRepository;
import com.leonardogarza.bootcamp.tasksService.repository.UsuarioRepository;
import com.leonardogarza.bootcamp.tasksService.util.Constantes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDb(UsuarioRepository usuarioRepository, TareaRepository tareaRepository){
        return args -> {

            Usuario leonardo = new Usuario("Leonardo G", "leoG@hotmail.com");
            Usuario mich = new Usuario("Mich B", "michb@gmail.com");
            Usuario juan = new Usuario("Juan Carlos Bodoque", "notaverde@21minutos.com");
            Usuario rodolfo = new Usuario("Rodolfo el Reno", "rodolfoelreno@navidad.com");
            log.info("Carga inicial {}", usuarioRepository.save(leonardo));
            log.info("Carga inicial {}", usuarioRepository.save(mich));
            log.info("Carga inicial {}", usuarioRepository.save(juan));
            log.info("Carga inicial {}", usuarioRepository.save(rodolfo));

            Tarea proyectoBootcamp = new Tarea("Proyecto Bootcamp", "Proyecto final del bootcampo de microservicios con java",
                    Constantes.TAREA_ESTADO_EN_PROGRESO, LocalDate.of(2025,6,2), leonardo);
            Tarea presentacionTrabajo = new Tarea("Presentacion Trabajo", "Hacer presentación del trabajo pendiente",
                    Constantes.TAREA_ESTADO_COMPLETADA, LocalDate.of(2025, 4, 23), mich);
            Tarea notaVerde = new Tarea("Nota Verde", "Nota verde para el siguiente programa de 31 minutos",
                    Constantes.TAREA_ESTADO_PENDIENTE, LocalDate.of(2025, 5, 16), juan);
            log.info("Carga inicial {}", tareaRepository.save(proyectoBootcamp));
            log.info("Carga inicial {}", tareaRepository.save(presentacionTrabajo));
            log.info("Carga inicial {}", tareaRepository.save(notaVerde));

        };
    }

}
