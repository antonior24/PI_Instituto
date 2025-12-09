package com.ies.poligono.sur.app.horario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ies.poligono.sur.app.horario.dao.FranjaRepository;
import com.ies.poligono.sur.app.horario.model.Franja;

@Service
public class FranjaServiceImpl implements FranjaService {

	@Autowired
    private FranjaRepository franjaRepository;

    @Override
    public List<Franja> findAll() {
        return franjaRepository.findAll();
    }

    @Override
    public Optional<Franja> findById(Long id) {
        return franjaRepository.findById(id);
    }

    @Override
    public Franja save(Franja franja) {
        return franjaRepository.save(franja);
    }

    @Override
    public void deleteById(Long id) {
        franjaRepository.deleteById(id);
    }

	


}
