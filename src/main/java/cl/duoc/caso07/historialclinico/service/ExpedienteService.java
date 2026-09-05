package cl.duoc.caso07.historialclinico.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import cl.duoc.caso07.historialclinico.model.Expediente;
import cl.duoc.caso07.historialclinico.repository.ExpedienteRepository;

@Service
public class ExpedienteService {

    private final ExpedienteRepository repository;

    public ExpedienteService(ExpedienteRepository repository) {
        this.repository = repository;
    }

    public List<Expediente> findAll() {
        return repository.findAll();
    }

    public Optional<Expediente> findById(Long id) {
        return repository.findById(id);
    }

    public Expediente create(Expediente recurso) {
        return repository.save(recurso);
    }

    public Optional<Expediente> update(Long id, Expediente datos) {
        return repository.findById(id).map(existente -> {
            existente.setNombre(datos.getNombre());
            existente.setPaciente(datos.getPaciente());
            existente.setDiagnostico(datos.getDiagnostico());
            return repository.save(existente);
        });
    }

    public boolean delete(Long id) {
        return repository.findById(id).map(existente -> {
            repository.delete(existente);
            return true;
        }).orElse(false);
    }
}
