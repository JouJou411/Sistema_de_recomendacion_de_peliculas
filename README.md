# Sistema de Recomendación Cinematográfica (Movie Dashboard)

[cite_start]Herramienta integral desarrollada en Java para administrar una red de usuarios, películas e interacciones mediante estructuras de datos avanzadas[cite: 3]. [cite_start]El sistema modela los gustos de los usuarios y garantiza un motor de recomendación eficiente basado en filtrado colaborativo[cite: 4], presentando una interfaz gráfica moderna y oscura basada en la estética de aplicaciones web actuales.

---

## 🚀 Características del Proyecto

* [cite_start]**Motor de Recomendación Analítico:** Implementación de un algoritmo de Búsqueda en Anchura (BFS) para la exploración del grafo de usuarios[cite: 19].
* [cite_start]**Gestión de Watchlist:** Sistema de control de flujo para películas pendientes y su posterior adición al grafo de interacciones[cite: 27, 28].
* [cite_start]**Control Lógico de Catálogo:** Soporte para bajas de licencias modificando el estatus de las películas a "Inactivas" sin comprometer la integridad referencial del grafo[cite: 22, 23, 24].
* [cite_start]**Persistencia de Datos:** Serialización de objetos completa a archivos binarios para salvaguardar el estado de todas las estructuras de datos[cite: 32].
* [cite_start]**Interfaz Gráfica de Usuario (GUI):** UI interactiva implementada en Java Swing utilizando el Look and Feel moderno de **FlatLaf (FlatDarkLaf)**[cite: 31].

---

## 📐 Estructuras de Datos Utilizadas

[cite_start]El núcleo del software se rige bajo la herencia de una clase base abstracta llamada `Entidad`[cite: 6]. A partir de ella, se estructuran los datos de la siguiente manera:

1. [cite_start]**Grafo (Multilista):** * **Vértices (Nodos):** Representan a los `Usuarios` y a las `Películas`[cite: 8, 9, 10].
   * [cite_start]**Aristas (Conexiones):** Se modelan mediante una lista principal de usuarios donde cada nodo apunta a una sub-lista secundaria (lista de adyacencia) que almacena las referencias de las películas a las que el usuario ha dado "Me gusta"[cite: 12, 13, 14].
2. [cite_start]**Pilas (Stacks):** Cada usuario cuenta con una pila interna para almacenar cronológicamente su historial de "Últimas vistas"[cite: 9].
3. [cite_start]**Colas (Queues):** Utilizadas de manera estándar para gestionar la lista de pendientes (`Watchlist`) de cada espectador[cite: 27].
4. [cite_start]**Búsquedas Secuenciales:** Para localizar elementos específicos por clave (`cve`), el sistema realiza un recorrido lineal uno a uno sobre las listas correspondientes[cite: 15, 16].

---

## 🧠 Reglas de Operación y Lógica de Negocio

### Motor de Recomendación (BFS)
[cite_start]Al solicitar sugerencias para un usuario objetivo, el motor ejecuta un algoritmo **BFS** sobre el grafo para identificar "usuarios vecinos" que compartan un **mínimo de 2 películas en común** en sus sub-listas[cite: 19]. [cite_start]Posteriormente, recolecta las películas de dichos vecinos, descarta las que el usuario de origen ya visualizó y devuelve las mejores opciones ordenadas por su calificación promedio[cite: 20].

### Restricción de Contenido Inactivo
[cite_start]Si una película cambia su estatus a "Inactiva", el motor de búsqueda (BFS) y las consultas generales tienen estrictamente **prohibido procesar o recomendar** dicho nodo[cite: 24]. [cite_start]Si la película vuelve a estar activa, las aristas y referencias previas vuelven a ser válidas automáticamente[cite: 25].

### Transición de la Watchlist
[cite_start]Al marcar el elemento en la primera posición de la cola de pendientes como "Vista y Aprobada", este sale de la cola (`Dequeue`) y el sistema crea de inmediato la nueva arista en la sub-lista del usuario dentro de la Multilista[cite: 28].

---

## 📁 Estructura del Proyecto

```text
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── Entidad.java            # Clase base abstracta (Clave alfanumérica y nombre)
│   │       ├── Usuario.java            # Vértice Usuario (Contiene Pila, Cola y Sub-lista)
│   │       ├── Pelicula.java           # Vértice Película (Atributos, estatus y ruta de imagen)
│   │       ├── SistemaRecomendacion.java # Gestor central, CRUD y persistencia binaria
│   │       ├── MotorRecomendacion.java # Algoritmo analítico BFS (Filtrado colaborativo)
│   │       ├── GestorImagenes.java     # Copia física de imágenes a la ruta de assets
│   │       ├── TarjetaPelicula.java    # Componente visual personalizado para las portadas
│   │       └── VentanaPrincipal.java   # JFrame principal con Look and Feel FlatDarkLaf
│   └── test/
├── assets/
│   └── imgMovies/                      # Directorio local de almacenamiento de posters (.jpg/.png)
├── pom.xml                             # Archivo de configuración de dependencias de Maven
└── datos_sistema.dat                   # Archivo binario generado por la persistencia
