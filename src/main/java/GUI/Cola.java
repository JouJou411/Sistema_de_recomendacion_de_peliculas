package GUI;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Cola<T>
 * Operaciones principales:
 *   add(T)    → encola al final
 *   poll()    → desencola desde el frente
 *   peek()    → consulta el frente sin quitar
 *   isEmpty() → verifica si está vacía
 *   size()    → número de elementos
 */
public class Cola<T> implements Serializable, Iterable<T> {

    private static final long serialVersionUID = 1L;

    // ── Estado ────────────────────────────────────────────────────────────
    private Nodo<T> frente;   // Primer nodo (se extrae primero)
    private Nodo<T> ultimo;   // Último nodo  (donde se encola)
    private int     tamanio;

    // ── Constructor ───────────────────────────────────────────────────────
    public Cola() {
        frente  = null;
        ultimo  = null;
        tamanio = 0;
    }

    // ── Operaciones principales ───────────────────────────────────────────

    /**
     * Encola un elemento al final.
     * Usa el constructor de un parametro de Nodo (siguiente = null).
     */
    public void add(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);          // siguiente = null por defecto
        if (isEmpty()) {
            frente = nuevo;
        } else {
            ultimo.setSiguiente(nuevo);
        }
        ultimo  = nuevo;
        tamanio++;
    }

    /**
     * Elimina y retorna el elemento del frente.
     * Retorna null si la cola está vacia.
     */
    public T poll() {
        if (isEmpty()) return null;
        T dato  = frente.getDato();
        frente  = frente.getSiguiente();
        if (frente == null) ultimo = null;         // Cola quedo vacia
        tamanio--;
        return dato;
    }

    /**
     * Retorna el elemento del frente sin eliminarlo.
     * Retorna null si la cola esta vacia.
     */
    public T peek() {
        if (isEmpty()) return null;
        return frente.getDato();
    }

    /** Retorna true si la cola no contiene elementos. */
    public boolean isEmpty() { return tamanio == 0; }

    /** Retorna el numero de elementos en la cola. */
    public int size() { return tamanio; }

    /** Elimina todos los elementos de la cola. */
    public void clear() {
        frente  = null;
        ultimo  = null;
        tamanio = 0;
    }

    /** Retorna true si el elemento dado esta en la cola. */
    public boolean contains(T dato) {
        Nodo<T> actual = frente;
        while (actual != null) {
            if (actual.getDato() != null && actual.getDato().equals(dato)) return true;
            actual = actual.getSiguiente();
        }
        return false;
    }

    // ── Iterador (frente → final) ─────────────────────────────────────────
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Nodo<T> actual = frente;
            public boolean hasNext() { return actual != null; }
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T dato  = actual.getDato();
                actual  = actual.getSiguiente();
                return dato;
            }
        };
    }

    @Override
    public String toString() {
        if (isEmpty()) return "Cola[]";
        StringBuilder sb = new StringBuilder("Cola[frente → ");
        Nodo<T> actual = frente;
        while (actual != null) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) sb.append(", ");
            actual = actual.getSiguiente();
        }
        return sb.append("]").toString();
    }
}
