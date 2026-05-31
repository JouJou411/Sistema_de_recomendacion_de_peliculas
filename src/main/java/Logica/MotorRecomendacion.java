package Logica;

import Modelo.Pelicula;
import Modelo.Usuario;

import java.util.*;

public class MotorRecomendacion {

    public static List<Pelicula> obtenerRecomendaciones(Usuario objetivo, List<Usuario> todosLosUsuarios) {
        List<Pelicula> sugerencias = new ArrayList<>();
        List<Usuario> vecinosSimilares = getVecinosSimilares(objetivo, todosLosUsuarios);

        // 2. Reclectar y filtrar películas de los vecinos similares
        Set<Pelicula> candidatos = new HashSet<>();
        for (Usuario vecino : vecinosSimilares) {
            for (Pelicula p : vecino.getPeliculasFavoritas()) {
                // Restricción: No incluir inactivas ni las que el usuario original ya vio
                if (p.isActiva() && !objetivo.getPeliculasFavoritas().contains(p)) {
                    candidatos.add(p);
                }
            }
        }

        // 3. Convertir a lista y ordenar por mejor calificación promedio
        sugerencias.addAll(candidatos);
        sugerencias.sort((p1, p2) -> Double.compare(p2.getCalificacionPromedio(), p1.getCalificacionPromedio()));

        return sugerencias;
    }

    private static List<Usuario> getVecinosSimilares(Usuario objetivo, List<Usuario> todosLosUsuarios) {
        List<Usuario> vecinosSimilares = new ArrayList<>();

        // 1. BFS para encontrar usuarios vecinos similares
        Queue<Usuario> colaBFS = new LinkedList<>(todosLosUsuarios);

        while (!colaBFS.isEmpty()) {
            Usuario vecino = colaBFS.poll();

            // Evitamos compararlo con sigo mismo
            if (vecino.getCve().equals(objetivo.getCve())) continue;

            // Contar películas compartidas (ambas deben estar activas)
            int coincidencias = 0;
            for (Pelicula p : vecino.getPeliculasFavoritas()) {
                if (p.isActiva() && objetivo.getPeliculasFavoritas().contains(p)) {
                    coincidencias++;
                }
            }

            // Regla de operación: Compartir al menos 2 películas en común
            if (coincidencias >= 2) {
                vecinosSimilares.add(vecino);
            }
        }
        return vecinosSimilares;
    }
}