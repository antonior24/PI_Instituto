package com.ies.poligono.sur.app.horario.service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ies.poligono.sur.app.horario.model.Horario;
import com.ies.poligono.sur.app.horario.model.Profesor;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

@Service
public class HorarioPDFService {

    public byte[] generarHorarioPDF(Profesor profesor, List<Horario> horarios) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            // Fuentes
            PdfFont fontTitulo = cargarFuentePreferida(true);
            PdfFont fontSubtitulo = cargarFuentePreferida(true);
            PdfFont fontNormal = cargarFuentePreferida(false);
            
            // Agregar título
            Paragraph titulo = new Paragraph("HORARIO PERSONAL")
                    .setFont(fontTitulo)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            document.add(titulo);
            
            // Agregar nombre del profesor
                Paragraph nombreProfesor = new Paragraph("Profesor: " + (profesor.getNombre() != null ? profesor.getNombre() : ""))
                    .setFont(fontSubtitulo)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(nombreProfesor);
            
            // Crear tabla con 6 columnas: Día, Franja Horaria, Asignatura, Curso, Aula, Puntos
            float[] columnWidths = {1.2f, 1.5f, 2f, 1f, 1f, 1f};
            Table horarioTable = new Table(columnWidths);
            horarioTable.setMarginBottom(20);
            
            // Headers de la tabla
            String[] headers = {"Día", "Franja Horaria", "Asignatura", "Curso", "Aula", "Puntos"};
            for (String header : headers) {
                Cell headerCell = new Cell()
                        .add(new Paragraph(header).setFont(fontSubtitulo).setFontSize(11))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setPadding(8);
                horarioTable.addCell(headerCell);
            }
            
            // Agregar datos de los horarios
            for (Horario horario : horarios) {
                String dia = horario.getDia() != null ? horario.getDia() : "—";
                String franja = horario.getFranja() != null 
                    ? horario.getFranja().getHoraInicio() + " - " + horario.getFranja().getHoraFin()
                    : "—";
                String asignatura = horario.getAsignatura() != null ? horario.getAsignatura().getNombre() : "—";
                String curso = horario.getCurso() != null ? horario.getCurso().getNombre() : "—";
                String aula = horario.getAula() != null ? horario.getAula().getCodigo() : "—";
                String puntos = calcularPuntos(curso);
                
                // Agregar celdas
                Cell cellDia = new Cell().add(new Paragraph(dia).setFont(fontNormal))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(6);
                horarioTable.addCell(cellDia);
                
                Cell cellFranja = new Cell().add(new Paragraph(franja).setFont(fontNormal))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(6);
                horarioTable.addCell(cellFranja);
                
                Cell cellAsignatura = new Cell().add(new Paragraph(asignatura).setFont(fontNormal))
                        .setPadding(6);
                horarioTable.addCell(cellAsignatura);
                
                Cell cellCurso = new Cell().add(new Paragraph(curso).setFont(fontNormal))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(6);
                horarioTable.addCell(cellCurso);
                
                Cell cellAula = new Cell().add(new Paragraph(aula).setFont(fontNormal))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(6);
                horarioTable.addCell(cellAula);
                
                Cell cellPuntos = new Cell().add(new Paragraph(puntos).setFont(fontNormal))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(6);
                horarioTable.addCell(cellPuntos);
            }
            
            document.add(horarioTable);
            
            // Resumen
            if (!horarios.isEmpty()) {
                int totalPuntos = horarios.stream()
                        .map(h -> h.getCurso() != null ? h.getCurso().getNombre() : "")
                        .map(this::convertirPuntosString)
                        .mapToInt(Integer::intValue)
                        .sum();
                
                Paragraph resumen = new Paragraph("Total de clases: " + horarios.size() + " | Total de puntos: " + totalPuntos)
                        .setFont(fontSubtitulo)
                        .setFontSize(11)
                        .setMarginTop(15);
                document.add(resumen);
            }
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del horario: " + e.getMessage(), e);
        }
    }
    
    private String calcularPuntos(String curso) {
        if (curso == null || curso.equals("—")) {
            return "0";
        }
        
        if (curso.toUpperCase().contains("1") || curso.toUpperCase().contains("1º")) {
            return "3";
        } else if (curso.toUpperCase().contains("2") || curso.toUpperCase().contains("2º")) {
            return "2";
        } else {
            return "1";
        }
    }
    
    private int convertirPuntosString(String puntos) {
        try {
            return Integer.parseInt(puntos);
        } catch (Exception e) {
            return 0;
        }
    }

    private PdfFont cargarFuentePreferida(boolean negrita) {
        String[] rutas = negrita
                ? new String[] {
                        "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
                        "/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf",
                        "C:/Windows/Fonts/arialbd.ttf",
                        "/System/Library/Fonts/Supplemental/Arial Bold.ttf"
                }
                : new String[] {
                        "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
                        "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf",
                        "C:/Windows/Fonts/arial.ttf",
                        "/System/Library/Fonts/Supplemental/Arial.ttf"
                };

        for (String ruta : rutas) {
            try {
                if (Files.exists(Path.of(ruta))) {
                    return PdfFontFactory.createFont(ruta, PdfEncodings.IDENTITY_H);
                }
            } catch (Exception ignored) {
            }
        }

        try {
            return PdfFontFactory.createFont(negrita ? StandardFonts.HELVETICA_BOLD : StandardFonts.HELVETICA);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar una fuente para generar el PDF", e);
        }
    }
}
