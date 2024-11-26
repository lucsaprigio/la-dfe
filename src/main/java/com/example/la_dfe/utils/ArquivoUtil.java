package com.example.la_dfe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

public class ArquivoUtil {

    public static byte[] compactaXml(String sXml) throws IOException {
        if (sXml.isEmpty()) {
            return null;
        }

        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        try (OutputStream outStream = new GZIPOutputStream(obj)) {
            outStream.write(sXml.getBytes(StandardCharsets.UTF_8));
        }

        return obj.toByteArray();
    }

    public static String descompactaXml(byte[] xml) {
        if (xml == null) {
            return null;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(xml);
            gzip.finish();
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] localizaCertificado(String caminho) {
        if (caminho == null) {
            System.err.println("O caminho do certificado é nulo.");
            return null;
        }

        Path path = Paths.get(caminho);
        if (!Files.exists(path)) {
            System.err.println("O arquivo não foi encontrado no caminho especificado: " + caminho);
            return null;
        }

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
