
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;


public class Servidor extends UnicastRemoteObject implements ServidorRemoto {
    private List<ClienteInfo> clientesAtivos;

    public Servidor() throws RemoteException {
        clientesAtivos = new ArrayList<>();
    }

    @Override
    public synchronized void registrarCliente(String nome, String ip, int porta) throws RemoteException {
        if (!nomeJaRegistrado(nome)) {
            ClienteInfo cliente = new ClienteInfo(nome, ip, porta);
            clientesAtivos.add(cliente);
            System.out.println(nome + " registrado com sucesso.");
        } else {
            throw new RemoteException("Nome já registrado. Escolha outro nome.");
        }
    }

    @Override
    public synchronized List<ClienteInfo> listarClientesAtivos() throws RemoteException {
        return clientesAtivos;
    }

    @Override
    public synchronized ClienteInfo buscarCliente(String nome) throws RemoteException {
        for (ClienteInfo cliente : clientesAtivos) {
            if (cliente.getNome().equals(nome)) {
                return cliente;
            }
        }
        return null;
    }

    @Override
    public synchronized void enviarMensagemBroadcast(String mensagem, String remetente) throws RemoteException {
        for (ClienteInfo cliente : clientesAtivos) {
            try {
                ClienteRemoto clienteRemoto = (ClienteRemoto) Naming.lookup("rmi://" + cliente.getIp() + ":" + cliente.getPorta() + "/" + cliente.getNome());
                clienteRemoto.receberMensagem("[" + remetente + "]: " + mensagem);
            } catch (Exception e) {
                System.out.println("Erro ao enviar mensagem para " + cliente.getNome());
            }
        }
    }

    @Override
    public synchronized boolean nomeJaRegistrado(String nome) throws RemoteException {
        for (ClienteInfo cliente : clientesAtivos) {
            if (cliente.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            Servidor servidor = new Servidor();
            Naming.rebind("Servidor", servidor);
            System.out.println("Servidor pronto e registrado no RMI registry.");
        } catch (Exception e) {
            System.out.println("Erro ao iniciar o servidor RMI:");
            e.printStackTrace();
        }
    }
}