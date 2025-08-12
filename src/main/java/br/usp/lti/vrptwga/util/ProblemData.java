package br.usp.lti.vrptwga.util;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Classe utilitária para baixar instâncias Solomon e retornar InputStream.
 */
public final class ProblemData {

    private static final String DEFAULT_BASE_URL =
            "http://vrp.galgos.inf.puc-rio.br/media/com_vrp/instances/Solomon/";

    private final Path file;      // caminho local
    private final String baseUrl; // url base

    public ProblemData(Path file) {
        this(file, DEFAULT_BASE_URL);
    }

    public ProblemData(Path file, String baseUrl) {
        this.file = file;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    /**
     * Garante que o arquivo esteja disponível localmente, e retorna um InputStream.
     */
    public InputStream getInputStream() throws IOException {
        if (Files.exists(file)) {
            return Files.newInputStream(file);
        }

        Files.createDirectories(file.getParent());

        String remoteUrl = baseUrl + file.getFileName().toString();
        System.out.println("Arquivo não encontrado localmente. Baixando: " + remoteUrl);

        // Baixa para arquivo temporário e depois abre InputStream
        try (InputStream remoteStream = URI.create(remoteUrl).toURL().openStream()) {
            Files.copy(remoteStream, file);
        }

        return Files.newInputStream(file);
    }

    /**
     * Caminho absoluto para uso alternativo (ex: logging).
     */
    public Path getPath() {
        return file;
    }
}

