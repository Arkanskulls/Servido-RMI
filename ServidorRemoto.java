import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServidorRemoto extends Remote {
    // Registra um cliente no servidor, armazenando seu nickname, IP e porta
    void registrarCliente(String nome, String ip, int porta) throws RemoteException;
    
    // Retorna uma lista de todos os clientes ativos
    List<ClienteInfo> listarClientesAtivos() throws RemoteException;
    
    // Busca um cliente específico pelo nickname
    ClienteInfo buscarCliente(String nome) throws RemoteException;
    
    // Envia uma mensagem em broadcast para todos os clientes ativos
    void enviarMensagemBroadcast(String mensagem, String remetente) throws RemoteException;
    boolean nomeJaRegistrado(String nome) throws RemoteException;  // Novo método para verificar se o nome já está registrado
}
