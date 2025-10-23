package Pontos.de.Coleta.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Collections;
import org.springframework.http.MediaType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.List;

@Controller// controla as rotas web
public class ColetaController {

    @Autowired // injeta uma instância do Repositório
    private PontoRepository repository;

    @ResponseBody

    @GetMapping("/api/pontos") 
    public List<PontoDeColeta> getPontosApi() {
        return repository.findAll(); 
    }

    
    @GetMapping("/") // Método que responde quando alguém acessa a página inicial ("/")
    public String paginaInicial(Model model) {
        
        List<PontoDeColeta> pontos = repository.findAll();

        model.addAttribute("listaDePontos", pontos);

        return "index";
    }

    @PostMapping("/indicar")
    public String salvarIndicacao(PontoDeColeta ponto, RedirectAttributes redirectAttributes) {
        
        try {
            // INÍCIO DA GEOCODIFICAÇÃO
            String baseUrl = "http://api.positionstack.com/v1/forward";
            String apiKey = "b0cfa17e15717b787d1731d7b2cfa75d";

            String enderecoLimpo = ponto.getEndereco().replace(",", " "); 

            // MONTAR A URL
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("access_key", apiKey)
                    .queryParam("query", enderecoLimpo)
                    .queryParam("limit", 1)
                    .toUriString();

            System.out.println("CHAMANDO API (HttpClient): " + url);

            // FAZ A CHAMADA COM O NOVO HTTPCLIENT
            HttpClient client = HttpClient.newHttpClient();
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "AppColetaSorocaba/1.0 (pevesalima@gmail.com)")
                    .header("Accept", "application/json")
                    .build();
            
            // Envia a requisição e espera a resposta como uma String
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            String responseBody = response.body();
            System.out.println("RESPOSTA DA API RECEBIDA PELO JAVA: " + responseBody);

            // LÊ A RESPOSTA (JSON do PositionStack)
            JSONObject responseObj = new JSONObject(responseBody);
            JSONArray jsonArray = responseObj.getJSONArray("data");
            
            if (jsonArray.length() > 0) {
                JSONObject firstResult = jsonArray.getJSONObject(0);
                double lat = firstResult.getDouble("latitude");
                double lon = firstResult.getDouble("longitude");

                // ATUALIZA O OBJETO ponto
                ponto.setLatitude(lat);
                ponto.setLongitude(lon);
            } else {
                // Se o PositionStack não encontrar o endereço
                throw new Exception("Endereço não localizado. Verifique se está correto.");
            }
            // FIM DA GEOCODIFICAÇÃO 

            // SALVA NO BANCO
            repository.save(ponto);
            
            redirectAttributes.addFlashAttribute("mensagem_sucesso", "Ponto de coleta salvo com sucesso!");

        } catch (DataIntegrityViolationException e) {
            // Erro de duplicata
            redirectAttributes.addFlashAttribute("mensagem_erro", "Erro ao salvar: E-mail ou Nome do Local já existe no banco.");
        } catch (Exception e) {
            // Captura qualquer outro erro
            e.printStackTrace(); 
            redirectAttributes.addFlashAttribute("mensagem_erro", "Erro ao salvar: " + e.getMessage());
        }
        
        return "redirect:/";
    }
}
