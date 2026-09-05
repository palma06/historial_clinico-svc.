package cl.duoc.caso07.historialclinico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.caso07.historialclinico.model.Expediente;

public interface ExpedienteRepository extends JpaRepository<Expediente, Long> {
}
