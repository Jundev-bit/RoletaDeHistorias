package br.com.roleta;

public class Historia {
    private final String nome;
    private final String pista;
    private final String livro;
    private final String personagem;
    private final String turma;

    public Historia(String nome, String pista, String livro, String personagem, String turma) {
        this.nome = nome;
        this.pista = pista;
        this.livro = livro;
        this.personagem = personagem;
        this.turma = turma;
    }

    public String getNome() { return nome; }
    public String getPista() { return pista; }
    public String getLivro() { return livro; }
    public String getPersonagem() { return personagem; }
    public String getTurma() { return turma; }
}
