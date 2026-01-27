package javafish.clients.opc;

import java.io.*;
import java.nio.file.*;

public class NativeLoader {
    public static void loadLibrary(String resourcePath, String libName) throws IOException {
        // Copia DLL do JAR para pasta temporária
        InputStream in = NativeLoader.class.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new FileNotFoundException("Recurso não encontrado: " + resourcePath);
        }

        Path temp = Files.createTempFile(libName, ".dll");
        Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
        in.close();

        // Carrega DLL
        System.load(temp.toAbsolutePath().toString());
    }
}
