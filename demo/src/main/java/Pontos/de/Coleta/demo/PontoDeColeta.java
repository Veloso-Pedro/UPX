package Pontos.de.Coleta.demo; // <-- Verifique se este é o nome do seu pacote

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ponto_de_coleta") // 1. Diz ao Java o nome exato da tabela
public class PontoDeColeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ponto_de_coleta") // 2. Mapeia o campo 'id' para a coluna 'id_ponto_de_coleta'
    private Integer id; // Mudei para Integer para bater com 'int' do MySQL

    @Column(name = "nome_local") // 3. Mapeia 'nome' para 'nome_local'
    private String nome;

    @Column(name = "email") // 4. Mapeia 'email' (vamos renomear o campo)
    private String email;

    @Column(name = "endereco") // 5. Mapeia 'endereco'
    private String endereco;

    // Diga ao Hibernate qual é a definição EXATA da coluna no banco
    @Column(name = "aceita")
    private String aceita; // 6. Mapeia 'aceita'
        
    @Column(name = "observacoes") // 7. Mapeia 'observacoes'
    private String observacoes;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // --- Getters e Setters ---
    // (Gerados para os campos acima)

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getAceita() {
        return aceita;
    }

    public void setAceita(String aceita) {
        this.aceita = aceita;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}