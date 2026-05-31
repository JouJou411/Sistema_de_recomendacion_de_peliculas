package Estructuras;

import java.io.Serializable;

/**
 * Nodo<T> — Nodo genérico de enlace simple.
 *
 * Clase independiente compartida por Pila<T> y Cola<T>.
 * Cada nodo almacena un dato de tipo T y una referencia al siguiente nodo.
 *
 *   dato      → el valor que guarda este nodo
 *   siguiente → referencia al próximo nodo en la cadena (null si es el último)
 */
public class Nodo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Campos ────────────────────────────────────────────────────────────
    T       dato;
    Nodo<T> siguiente;

    // ── Constructores ─────────────────────────────────────────────────────

    /**
     * Crea un nodo con dato y referencia al siguiente.
     * Usado por Pila: el nuevo nodo apunta al antiguo tope.
     *
     * @param dato      Valor a almacenar
     * @param siguiente Referencia al nodo siguiente
     */
    public Nodo(T dato, Nodo<T> siguiente) {
        this.dato      = dato;
        this.siguiente = siguiente;
    }

    /**
     * Crea un nodo con dato y siguiente en null.
     * Usado por Cola: el nodo se inserta al final sin sucesor.
     *
     * @param dato Valor a almacenar
     */
    public Nodo(T dato) {
        this.dato      = dato;
        this.siguiente = null;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────

    public T getDato()               { return dato; }
    public void setDato(T dato)      { this.dato = dato; }

    public Nodo<T> getSiguiente()              { return siguiente; }
    public void setSiguiente(Nodo<T> siguiente){ this.siguiente = siguiente; }

    // ── Representación textual ────────────────────────────────────────────

    @Override
    public String toString() {
        return "Nodo(" + dato + ")";
    }
}
