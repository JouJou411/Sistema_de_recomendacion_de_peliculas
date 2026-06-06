# CineMatch — Sistema de Recomendación Cinematográfica

Sistema de recomendación de películas basado en filtrado colaborativo con BFS, desarrollado en Java con interfaz gráfica Swing.

---

## Estructura del proyecto

```
ProyectoEDD/
├── resources/
│   ├── archivos/
│   │   └── cinematch.dat        ← persistencia automática (se genera al cerrar)
│   └── imgMovies/               ← pósters de películas
├── src/
│   └── main/
│       └── java/
│           ├── Componentes/
│           │   ├── RoundedPanel.java
│           │   └── TarjetaPelicula.java
│           ├── Estructuras/
│           │   ├── Nodo.java
│           │   ├── Pila.java
│           │   └── Cola.java
│           ├── Logica/
│           │   ├── GestorImagenes.java
│           │   ├── MotorRecomendacion.java
│           │   └── SistemaRecomendacion.java
│           ├── Modelo/
│           │   ├── Entidad.java
│           │   ├── Pelicula.java
│           │   └── Usuario.java
│           └── Vistas/
│               ├── PantallaMotor.java
│               ├── PantallaPeliculas.java
│               ├── PantallaUsuarios.java
│               └── VentanaPrincipal.java
├── .gitignore
├── pom.xml
└── README.md
```

---

## Descripción de paquetes

### `Modelo`
Clases de datos puras que representan las entidades del dominio.

| Clase | Descripción |
|---|---|
| `Entidad` | Clase base abstracta con `cve` y `nombre`. Serializable. |
| `Pelicula` | Extiende `Entidad`. Tiene género, año, director, calificación, estado activo y ruta de imagen. |
| `Usuario` | Extiende `Entidad`. Contiene edad, país, y las tres estructuras de datos: `Pila`, `Cola` y `LinkedList`. |

### `Estructuras`
Implementaciones propias de estructuras de datos con nodos enlazados. No dependen del JDK para su lógica interna.

| Clase | Tipo | Uso en el sistema |
|---|---|---|
| `Nodo<T>` | Nodo genérico serializable | Base compartida de `Pila` y `Cola` |
| `Pila<T>` | LIFO — enlace simple desde el tope | Historial de vistas del usuario · Vecinos válidos en BFS |
| `Cola<T>` | FIFO — punteros a frente y último | Watchlist del usuario · Recorrido BFS en el motor |

### `Logica`
Backend del sistema: algoritmos, reglas de negocio y persistencia.

| Clase | Descripción |
|---|---|
| `SistemaRecomendacion` | Núcleo del sistema. Gestiona las listas de usuarios y películas, el CRUD y la persistencia binaria automática. |
| `MotorRecomendacion` | Implementa el algoritmo BFS de filtrado colaborativo usando `Cola<Usuario>` y `Pila<Usuario>`. |
| `GestorImagenes` | Copia la imagen seleccionada por el usuario a `resources/imgMovies/` con el CVE como nombre de archivo. |

### `Componentes`
Widgets visuales reutilizables que no son pantallas completas.

| Clase | Descripción |
|---|---|
| `RoundedPanel` | `JPanel` con esquinas redondeadas, usado en tarjetas y secciones de la GUI. |
| `TarjetaPelicula` | Tarjeta visual con póster, calificación y porcentaje de compatibilidad BFS. |

### `Vistas`
Pantallas de la interfaz gráfica Swing.

| Clase | Descripción |
|---|---|
| `VentanaPrincipal` | Contenedor raíz con sidebar de navegación y `CardLayout`. Gestiona el auto-guardado al cerrar. |
| `PantallaMotor` | Motor BFS: selector de usuario, grid de recomendaciones y watchlist lateral. |
| `PantallaUsuarios` | CRUD de usuarios con tabla, panel de detalle y diálogos para gestionar favoritas. |
| `PantallaPeliculas` | Catálogo de películas con CRUD, control de estatus y asignación a watchlists. |

---

## Algoritmo BFS

El motor de recomendación usa **Breadth-First Search (Búsqueda por Anchura)** con filtrado colaborativo.

```
1. Encolar todos los usuarios en Cola<Usuario>
2. Desencolar uno a uno (FIFO)
3. Contar películas activas en común con el usuario objetivo
4. Si coincidencias ≥ 2 → apilar en Pila<Usuario> como vecino válido
5. Vaciar la Pila para obtener la lista de vecinos
6. Recolectar películas de los vecinos que el objetivo no tenga
7. Ordenar por calificación promedio y retornar las mejores 4
```

**Regla de negocio:** un usuario es considerado "vecino similar" si comparte un mínimo de **2 películas favoritas activas** con el usuario objetivo.

---

## Persistencia automática

El sistema serializa todo el estado en un único archivo binario.

| Evento | Acción |
|---|---|
| **Al abrir** | `cargarOInicializar()` lee `resources/archivos/cinematch.dat`. Si no existe, carga datos de ejemplo. |
| **Al cerrar** | `WindowListener` llama a `guardarEstadoAuto()` antes de terminar la JVM. |
| **Exportar / Importar** | El sidebar permite guardar y cargar copias de seguridad en cualquier ruta. |

La serialización es en cascada: al serializar `SistemaRecomendacion` se serializan automáticamente todos los `Usuario`, `Pelicula`, `Pila`, `Cola` y `Nodo` que contiene.

---

## Dependencias

| Librería | Versión | Uso |
|---|---|---|
| [FlatLaf](https://www.formdev.com/flatlaf/) | 3.5.1 | Look & Feel oscuro moderno para Swing |

Gestionada con **Maven** (`pom.xml`).

---

## Requisitos

- Java 11 o superior
- Maven 3.6+

---

## Ejecución

```bash
mvn compile
mvn exec:java -Dexec.mainClass="Vistas.VentanaPrincipal"
```

O directamente desde el IDE ejecutando `VentanaPrincipal.main()`.

---

## Integrantes

- PULIDO AMBRIZ JOAB JAAZIEL
- DAVILA RUIZ ANGEL DANIEL
- GOMEZ ZARAGOZA ANDREA

---

## Materia

Estructuras de Datos — Ingeniería en Sistemas
