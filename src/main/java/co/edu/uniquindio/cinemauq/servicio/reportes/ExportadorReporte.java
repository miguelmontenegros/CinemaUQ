package co.edu.uniquindio.cinemauq.servicio.reportes;

import java.io.File;
import java.io.IOException;

public interface ExportadorReporte {

    String getNombre();

    String getExtension();

    void exportar(Reporte reporte, File destino) throws IOException;
}
