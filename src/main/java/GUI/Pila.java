package GUI;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Pila<T> — Implementación propia de una pila (LIFO) con nodos enlazados.
 * Usa la clase independiente Nodo<T> del mismo paquete.
 *
 * Operaciones principales:
 *   push(T)   → inserta en el tope          O(1)
 *   pop()     → elimina y retorna el tope   O(1)
 *   peek()    → consulta el tope sin quitar O(1)
 *   isEmpty() → verifica si está vacía      O(1)
 *   size()    → número de elementos         O(1)
 */
public class Pila<T> implements Serializable, Iterable<T> {

    private static final long serialVersionUID = 1L;

    // ── Estado ────────────────────────────────────────────────────────────
    private Nodo<T> tope;    // Apunta al nodo en el tope de la pila
    private int     tamanio;

    // ── Constructor ───────────────────────────────────────────────────────
    public Pila() {
        tope    = null;
        tamanio = 0;
    }

    // ── Operaciones principales ───────────────────────────────────────────

    /**
     * Inserta un elemento en el tope.
     * El nuevo Nodo apunta al antiguo tope (constructor de dos parámetros).
     */
    public void push(T dato) {
        tope    = new Nodo<>(dato, tope);
        tamanio++;
    }

    /**
     * Elimina y retorna el elemento del tope.
     * @throws NoSuchElementException si la pila está vacía
     */
    public T pop() {
        if (isEmpty()) throw new NoSuchElementException("La pila está vacía.");
        T dato  = tope.getDato();
        tope    = tope.getSiguiente();
        tamanio--;
        return dato;
    }

    /**
     * Retorna el elemento del tope sin eliminarlo.
     * @throws NoSuchElementException si la pila está vacía
     */
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("La pila está vacía.");
        return tope.getDato();
    }

    /** Retorna true si la pila no contiene elementos. */
    public boolean isEmpty() { return tamanio == 0; }

    /** Retorna el número de elementos en la pila. */
    public int size() { return tamanio; }

    /** Elimina todos los elementos de la pila. */
    public void clear() {
        tope    = null;
        tamanio = 0;
    }

    /** Retorna true si el elemento dado está en la pila. */
    public boolean contains(T dato) {
        Nodo<T> actual = tope;
        while (actual != null) {
            if (actual.getDato() != null && actual.getDato().equals(dato)) return true;
            actual = actual.getSiguiente();
        }
        return false;
    }

    // ── Iterador (tope → fondo) ───────────────────────────────────────────
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Nodo<T> actual = tope;
            public boolean hasNext() { return actual != null; }
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T dato  = actual.getDato();
                actual  = actual.getSiguiente();
                return dato;
            }
        };
    }

    // ── Representación textual ────────────────────────────────────────────
    @Override
    public String toString() {
        if (isEmpty()) return "Pila[]";
        StringBuilder sb = new StringBuilder("Pila[tope → ");
        Nodo<T> actual = tope;
        while (actual != null) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) sb.append(", ");
            actual = actual.getSiguiente();
        }
        return sb.append("]").toString();
    }
}
