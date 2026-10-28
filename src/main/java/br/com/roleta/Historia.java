package br.com.roleta;

public class Historia {
    private final String nome;
    private final String pista;
    private final String livro;
    private final String personagem;

    public Historia(String nome, String pista, String livro, String personagem) {
        this.nome = nome;
        this.pista = pista;
        this.livro = livro;
        this.personagem = personagem;
    }

    public String getNome() { return nome; }
    public String getPista() { return pista; }
    public String getLivro() { return livro; }
    public String getPersonagem() { return personagem; }
}
