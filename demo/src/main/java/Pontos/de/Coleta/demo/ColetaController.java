package Pontos.de.Coleta.demo;

// Imports do Spring (GET, POST, Autowired, etc.)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Imports das Entidades e Repositories
import java.util.List;

// Imports do HttpClient e JSON (para a API)
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

// Imports para os Headers da API
import org.springframework.http.MediaType;
import java.util.Collections;

@Controller// controla as rotas web
public class ColetaController {

    @Autowired // injeta uma instância do Repositório
    private PontoRepository pontoRepository;
    
    @Autowired
    private EnderecoCompletoRepository enderecoRepository;

    @GetMapping("/api/pontos")
    @ResponseBody
    public List<PontoDeColeta> getPontosApi() {
        return pontoRepository.findAll(); 
    }

    
    @GetMapping("/")
    public String paginaInicial(Model model) {
        List<PontoDeColeta> pontos = pontoRepository.findAll();
        model.addAttribute("listaDePontos", pontos);
        return "index";
    }

   @PostMapping("/indicar")
    public String salvarIndicacao(PontoDeColeta ponto, EnderecoCompleto endereco, RedirectAttributes redirectAttributes) {
        
        try {
            // --- INÍCIO DA GEOCODIFICAÇÃO ---
            
            String enderecoParaBusca = String.join(" ", 
                endereco.getLogradouro(),
                endereco.getNImovel(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado()
            );

            String baseUrl = "http://api.positionstack.com/v1/forward";
            String apiKey = "b0cfa17e15717b787d1731d7b2cfa75d";

            String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("access_key", apiKey)
                    .queryParam("query", enderecoParaBusca)
                    .queryParam("limit", 1)
                    .toUriString();

            System.out.println("CHAMANDO API (HttpClient): " + url);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "AppColetaSorocaba/1.0 (pevesalima@gmail.com)")
                    .header("Accept", "application/json")
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println("RESPOSTA DA API RECEBIDA PELO JAVA: " + responseBody);

            JSONObject responseObj = new JSONObject(responseBody);
            JSONArray jsonArray = responseObj.getJSONArray("data");
            
            if (jsonArray.length() > 0) {
                JSONObject firstResult = jsonArray.getJSONObject(0);
                double lat = firstResult.getDouble("latitude");
                double lon = firstResult.getDouble("longitude");

                ponto.setLatitude(lat);
                ponto.setLongitude(lon);
                
                ponto.setEnderecoCompleto(endereco);

            } else {
                throw new Exception("Endereço não localizado. Verifique se está correto.");
            }
            // --- FIM DA GEOCODIFICAÇÃO ---
            
            pontoRepository.save(ponto);
            
            
            redirectAttributes.addFlashAttribute("mensagem_sucesso", "Ponto de coleta salvo com sucesso!");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("mensagem_erro", "Erro ao salvar: E-mail ou Nome do Local já existe no banco.");
        } catch (Exception e) {
            e.printStackTrace(); 
            redirectAttributes.addFlashAttribute("mensagem_erro", "Erro ao salvar: " + e.getMessage());
        }
        
        return "redirect:/";
    }
}
