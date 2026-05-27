# Sistema de Recomendación Cinematográfica (Movie Dashboard)

Herramienta integral desarrollada en Java para administrar una red de usuarios, películas e interacciones mediante estructuras de datos avanzadas. El sistema modela los gustos de los usuarios y garantiza un motor de recomendación eficiente basado en filtrado colaborativo, presentando una interfaz gráfica moderna y oscura basada en la estética de aplicaciones web actuales.

---

## 🚀 Características del Proyecto

* **Motor de Recomendación Analítico:** Implementación de un algoritmo de Búsqueda en Anchura (BFS) para la exploración del grafo de usuarios.
* **Gestión de Watchlist:** Sistema de control de flujo para películas pendientes y su posterior adición al grafo de interacciones.
* **Control Lógico de Catálogo:** Soporte para bajas de licencias modificando el estatus de las películas a "Inactivas" sin comprometer la integridad referencial del grafo.
* **Persistencia de Datos:** Serialización de objetos completa a archivos binarios para salvaguardar el estado de todas las estructuras de datos.
* **Interfaz Gráfica de Usuario (GUI):** UI interactiva implementada en Java Swing utilizando el Look and Feel moderno de **FlatLaf (FlatDarkLaf)**.

---

## 📐 Estructuras de Datos Utilizadas

El núcleo del software se rige bajo la herencia de una clase base abstracta llamada `Entidad`. A partir de ella, se estructuran los datos de la siguiente manera:

1. **Grafo (Multilista):** * **Vértices (Nodos):** Representan a los `Usuarios` y a las `Películas`.
   * **Aristas (Conexiones):** Se modelan mediante una lista principal de usuarios donde cada nodo apunta a una sub-lista secundaria (lista de adyacencia) que almacena las referencias de las películas a las que el usuario ha dado "Me gusta".
2. **Pilas (Stacks):** Cada usuario cuenta con una pila interna para almacenar cronológicamente su historial de "Últimas vistas".
3. **Colas (Queues):** Utilizadas de manera estándar para gestionar la lista de pendientes (`Watchlist`) de cada espectador.
4. **Búsquedas Secuenciales:** Para localizar elementos específicos por clave (`cve`), el sistema realiza un recorrido lineal uno a uno sobre las listas correspondientes.

---

## 🧠 Reglas de Operación y Lógica de Negocio

### Motor de Recomendación (BFS)
Al solicitar sugerencias para un usuario objetivo, el motor ejecuta un algoritmo **BFS** sobre el grafo para identificar "usuarios vecinos" que compartan un **mínimo de 2 películas en común** en sus sub-listas. Posteriormente
