
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;


public class Cliente extends UnicastRemoteObject implements ClienteRemoto {
    private String nome;
    private String ip;
    private int porta;
    private ServidorRemoto servidor;

    public Cliente(String nome, String ip, int porta, ServidorRemoto servidor) throws RemoteException {
        super();
        this.nome = nome;
        this.ip = ip;
        this.porta = porta;
        this.servidor = servidor;
        servidor.registrarCliente(nome, ip, porta);
    }

    @Override
    public void receberMensagem(String mensagem) throws RemoteException {
        System.out.println(mensagem);
    }

    @Override
    public void conectarClienteP2P(String ip, int porta) throws RemoteException {
        System.out.println("Conectando-se ao cliente em " + ip + ":" + porta);
    }

    public void enviarMensagemPrivada(String destinatario, String mensagem) throws Exception {
        ClienteInfo cliente = servidor.buscarCliente(destinatario);
        if (cliente != null) {
            ClienteRemoto clienteRemoto = (ClienteRemoto) Naming.lookup("rmi://" + cliente.getIp() + ":" + cliente.getPorta() + "/" + cliente.getNome());
            clienteRemoto.receberMensagem("[" + nome + "]: " + mensagem);
        } else {
            System.out.println("Cliente não encontrado.");
        }
    }

    public void enviarMensagemBroadcast(String mensagem) throws RemoteException {
        servidor.enviarMensagemBroadcast(mensagem, nome);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Verificação inicial da disponibilidade do servidor
            ServidorRemoto servidor = null;
            try {
                servidor = (ServidorRemoto) Naming.lookup("rmi://localhost/Servidor");
                System.out.println("Conectado ao servidor.");
            } catch (Exception e) {
                System.out.println("Servidor indisponível. Não é possível prosseguir.");
                System.exit(0);
            }

            Cliente cliente = null;
            boolean clienteCadastrado = false;
            while (!clienteCadastrado) {
                System.out.println("\nMenu de Opções:");
                System.out.println("1. Cadastrar novo cliente");
                System.out.println("2. Listar clientes ativos");
                System.out.println("3. Sair");
                System.out.print("Escolha uma opção: ");
                int opcao = lerOpcaoValida(scanner);

                switch (opcao) {
                    case 1:
                        clienteCadastrado = cadastrarCliente(scanner, servidor);
                        if (clienteCadastrado) {
                            // Após cadastro bem-sucedido, inicializa o cliente
                            cliente = new Cliente(scanner.nextLine().trim(), scanner.nextLine().trim(), Integer.parseInt(scanner.nextLine().trim()), servidor);
                        }
                        break;
                    case 2:
                        listarClientesAtivos(servidor);
                        break;
                    case 3:
                        System.out.println("Saindo...");
                        scanner.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            }

            // Menu de opções do cliente após o cadastro
            while (true) {
                System.out.println("\nOpções do Cliente:");
                System.out.println("1. Enviar mensagem privada");
                System.out.println("2. Enviar mensagem para a sala de chat (broadcast)");
                System.out.println("3. Sair");
                System.out.print("Escolha uma opção: ");
                int opcao = lerOpcaoValida(scanner);

                switch (opcao) {
                    case 1:
                        if (cliente != null) {
                            System.out.print("Digite o nome do destinatário (comece com '@'): ");
                            String destinatario = scanner.nextLine().trim();
                            System.out.print("Digite a mensagem: ");
                            String mensagemPrivada = scanner.nextLine().trim();
                            cliente.enviarMensagemPrivada(destinatario, mensagemPrivada);
                        } else {
                            System.out.println("Cliente não cadastrado.");
                        }
                        break;
                    case 2:
                        if (cliente != null) {
                            System.out.print("Digite a mensagem para a sala: ");
                            String mensagemBroadcast = scanner.nextLine().trim();
                            cliente.enviarMensagemBroadcast(mensagemBroadcast);
                        } else {
                            System.out.println("Cliente não cadastrado.");
                        }
                        break;
                    case 3:
                        System.out.println("Saindo do chat...");
                        scanner.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidIP(String ip) {
        String ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(ipRegex);
    }

    private static int lerOpcaoValida(Scanner scanner) {
        int opcao;
        while (true) {
            String entrada = scanner.nextLine().trim();
            if (entrada.matches("\\d+")) {
                opcao = Integer.parseInt(entrada);
                if (opcao >= 1 && opcao <= 3) {
                    break;
                }
            }
            System.out.print("Opção inválida. Por favor, insira um número entre 1 e 3: ");
        }
        return opcao;
    }

    private static boolean cadastrarCliente(Scanner scanner, ServidorRemoto servidor) {
        try {
            String nome;
            do {
                System.out.print("Digite seu nome de usuário (deve começar com '@'): ");
                nome = scanner.nextLine().trim();
                if (!nome.startsWith("@")) {
                    System.out.println("O nome de usuário deve começar com '@'.");
                    continue;
                }

                if (servidor.nomeJaRegistrado(nome)) {
                    System.out.println("Nome já registrado. Escolha outro nome.");
                    nome = "";  // Resetar nome para forçar nova entrada
                } else {
                    String ip;
                    do {
                        System.out.print("Digite seu IP: ");
                        ip = scanner.nextLine().trim();
                        if (!isValidIP(ip)) {
                            System.out.println("IP inválido. Por favor, insira um IP válido.");
                        }
                    } while (!isValidIP(ip));

                    int porta;
                    do {
                        System.out.print("Digite sua porta: ");
                        String portaStr = scanner.nextLine().trim();
                        if (!portaStr.matches("\\d+") || (porta = Integer.parseInt(portaStr)) < 1024 || porta > 65535) {
                            System.out.println("Porta inválida. Por favor, insira uma porta entre 1024 e 65535.");
                            porta = -1; // Resetar para forçar nova entrada
                        }
                    } while (porta == -1);

                    Cliente cliente = new Cliente(nome, ip, porta, servidor);
                    Naming.rebind("rmi://" + ip + ":" + porta + "/" + nome, cliente);

                    System.out.println("Cliente cadastrado com sucesso.");
                    return true;  // Cadastro concluído com sucesso
                }
            } while (nome.isEmpty());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;  // Cadastro não concluído
    }

    private static void listarClientesAtivos(ServidorRemoto servidor) throws RemoteException {
        List<ClienteInfo> clientes = servidor.listarClientesAtivos();
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente ativo no momento.");
        } else {
            System.out.println("Clientes ativos:");
            for (ClienteInfo cliente : clientes) {
                System.out.println(cliente.getNome() + " - IP: " + cliente.getIp() + ", Porta: " + cliente.getPorta());
            }
        }
    }
}