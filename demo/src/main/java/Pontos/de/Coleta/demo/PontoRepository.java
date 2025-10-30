package Pontos.de.Coleta.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// save(), findAll(), findById()
public interface PontoRepository extends JpaRepository<PontoDeColeta, Integer> {

}