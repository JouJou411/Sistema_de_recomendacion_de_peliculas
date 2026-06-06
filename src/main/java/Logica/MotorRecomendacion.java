package Logica;

import Estructuras.Cola;
import Estructuras.Pila;
import Modelo.Pelicula;
import Modelo.Usuario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MotorRecomendacion — Algoritmo de filtrado colaborativo con BFS.
 *   Cola<Usuario> → recorre los usuarios como vecinos (BFS)
 *   Pila<Usuario> → registra los vecinos válidos encontrados
 * Regla de negocio: un usuario es "vecino similar" si comparte
 * al menos 2 películas activas en favoritas con el usuario objetivo.
 */
public class MotorRecomendacion {

    public static List<Pelicula> obtenerRecomendaciones(Usuario objetivo,
                                                        List<Usuario> todosLosUsuarios) {
        List<Pelicula> sugerencias = new ArrayList<>();

        // 1. BFS para encontrar vecinos similares
        List<Usuario> vecinosSimilares = getVecinosSimilares(objetivo, todosLosUsuarios);

        // 2. Recolectar y filtrar películas de los vecinos
        Set<Pelicula> candidatos = new HashSet<>();
        for (Usuario vecino : vecinosSimilares) {
            for (Pelicula p : vecino.getPeliculasFavoritas()) {
                // Solo incluir películas activas que el objetivo aún no tiene
                if (p.isActiva() && !objetivo.getPeliculasFavoritas().contains(p)) {
                    candidatos.add(p);
                }
            }
        }

        // 3. Convertir a lista y ordenar por mejor calificación promedio
        sugerencias.addAll(candidatos);
        sugerencias.sort((p1, p2) ->
            Double.compare(p2.getCalificacionPromedio(), p1.getCalificacionPromedio()));

        return sugerencias;
    }

    private static List<Usuario> getVecinosSimilares(Usuario objetivo,
                                                      List<Usuario> todosLosUsuarios) {

        // ── Cola para el recorrido BFS ──────────────────────
        Cola<Usuario> colaBFS = new Cola<>();
        for (Usuario u : todosLosUsuarios) {
            colaBFS.add(u);
        }

        // ── Pila para acumular vecinos válidos ──────────────
        // Cada vecino que supera el umbral se apila; al terminar los volcamos a List.
        // Usar Pila aquí demuestra el uso de ambas estructuras propias en el algoritmo.
        Pila<Usuario> pilaVecinos = new Pila<>();

        while (!colaBFS.isEmpty()) {
            Usuario vecino = colaBFS.poll();       // Desencola

            // Omitir al propio usuario objetivo
            if (vecino.getCve().equals(objetivo.getCve())) continue;

            // Contar películas activas en común
            int coincidencias = 0;
            for (Pelicula p : vecino.getPeliculasFavoritas()) {
                if (p.isActiva() && objetivo.getPeliculasFavoritas().contains(p)) {
                    coincidencias++;
                }
            }

            // Regla: minimo 2 películas compartidas para ser vecino válido
            if (coincidencias >= 2) {
                pilaVecinos.push(vecino);          // Apila
            }
        }

        // Volcar la Pila a una List para retornarla
        List<Usuario> resultado = new ArrayList<>();
        while (!pilaVecinos.isEmpty()) {
            resultado.add(pilaVecinos.pop());
        }
        return resultado;
    }
}
