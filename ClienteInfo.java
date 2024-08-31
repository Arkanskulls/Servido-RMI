
import java.io.Serializable;

public class ClienteInfo implements Serializable {
    private String nome;
    private String ip;
    private int porta;

    // Construtor para inicializar os atributos do cliente
    public ClienteInfo(String nome, String ip, int porta) {
        this.nome = nome;
        this.ip = ip;
        this.porta = porta;
    }

    // Métodos getter para acessar as informações do cliente
    public String getNome() {
        return nome;
    }

    public String getIp() {
        return ip;
    }

    public int getPorta() {
        return porta;
    }
}