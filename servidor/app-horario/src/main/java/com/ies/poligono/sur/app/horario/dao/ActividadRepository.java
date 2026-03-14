package com.ies.poligono.sur.app.horario.dao;

import com.ies.poligono.sur.app.horario.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    // Esta consulta cuenta cuántos eventos hay de cada tipo (CLICK, VISTA_PAGINA, etc.)
    @Query("SELECT a.tipo as tipo, COUNT(a) as total FROM Actividad a GROUP BY a.tipo")
    List<Map<String, Object>> countEventosPorTipo();

    // Esta consulta nos dice cuáles son las páginas (URLs) más visitadas
    @Query("SELECT a.url as url, COUNT(a) as total FROM Actividad a WHERE a.tipo = 'VISTA_PAGINA' GROUP BY a.url ORDER BY total DESC")
    List<Map<String, Object>> countVisitasPorPagina();
}