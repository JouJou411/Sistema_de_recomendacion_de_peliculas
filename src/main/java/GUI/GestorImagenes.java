package GUI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class GestorImagenes {

    /**
     * Copia la imagen seleccionada por el usuario a la carpeta local del proyecto.
     * * @param archivoOrigen El archivo que el usuario seleccionó con el JFileChooser.
     * @param cvePelicula La clave autogenerada de la película (ej. "P001").
     * @return La ruta relativa que se guardará en el objeto GUI.Pelicula.
     * @throws IOException Si ocurre un error al copiar el archivo.
     */
    public static String guardarImagenPelicula(File archivoOrigen, String cvePelicula) throws IOException {
        // 1. Definir la ruta de la carpeta del proyecto
        File directorioDestino = new File("resources/imgMovies");

        // 2. Si la carpeta no existe, la crea automáticamente (junto con carpetas padre si aplica)
        String nombreNuevoArchivo = getNombreNuevoArchivo(archivoOrigen, cvePelicula, directorioDestino);
        File archivoDestino = new File(directorioDestino, nombreNuevoArchivo);

        // 5. Copiar el archivo reemplazándolo si ya existía uno con el mismo nombre
        Files.copy(archivoOrigen.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // 6. Retornar la ruta relativa exacta para guardarla en la instancia de GUI.Pelicula
        return "resources/imgMovies/" + nombreNuevoArchivo;
    }

    private static String getNombreNuevoArchivo(File archivoOrigen, String cvePelicula, File directorioDestino) {
        if (!directorioDestino.exists()) {
            directorioDestino.mkdirs();
        }

        // 3. Extraer la extensión original del archivo (jpg, png, etc.)
        String nombreOriginal = archivoOrigen.getName();
        String extension;
        int i = nombreOriginal.lastIndexOf('.');
        if (i > 0) {
            extension = nombreOriginal.substring(i); // Retorna algo como ".jpg" o ".png"
        } else {
            extension = ".jpg"; // Extensión por defecto si no se detecta
        }

        // 4. Construir el nuevo archivo de destino (ejemplo: assets/imgMovies/P001.jpg)
        String nombreNuevoArchivo = cvePelicula + extension;
        return nombreNuevoArchivo;
    }
}