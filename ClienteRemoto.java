
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClienteRemoto extends Remote {
    // Recebe uma mensagem de outro cliente
    void receberMensagem(String mensagem) throws RemoteException;

    // Conecta-se a outro cliente para comunicação P2P
    void conectarClienteP2P(String ip, int porta) throws RemoteException;
}
