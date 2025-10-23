package Pontos.de.Coleta.demo; // <-- Verifique o nome do seu pacote

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Esta interface nos dá métodos como save(), findAll(), findById() de graça!
public interface PontoRepository extends JpaRepository<PontoDeColeta, Long> {

    // (Podemos adicionar buscas customizadas aqui no futuro, se precisar)
    
}