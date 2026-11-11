package co.edu.uniquindio.cinemauq.servicio.reportes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ExportadorTxt implements ExportadorReporte {

    @Override
    public String getNombre() {
        return "Texto plano";
    }

    @Override
    public String getExtension() {
        return "txt";
    }

    @Override
    public void exportar(Reporte reporte, File destino) throws IOException {
        Files.writeString(destino.toPath(), TextoReporte.completo(reporte), StandardCharsets.UTF_8);
    }
}
