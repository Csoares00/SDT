import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Main {
    private static final String MULTICAST = "230.0.0.0"; // Endereço Multicast
    private static final int PORT = 8888; // Porta Multicast
    private static final boolean IS_LEADER = true   ; // Define se este nó é o líder (estático)

    // Array com três mensagens de exemplo
    private static final String[] MSG = {
            "HB1",
            "HB2",
            "HB3"
    };

    private static void sendHeartbeats() {
        try (MulticastSocket socket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName(MULTICAST);

            while (true) {
                for (String message : MSG) {
                    byte[] buffer = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);
                    System.out.println("Mensagem enviada: " + message);
                    Thread.sleep(5000);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void listenForHeartbeats() {
        try (MulticastSocket socket = new MulticastSocket(PORT)) {
            InetAddress group = InetAddress.getByName(MULTICAST);
            socket.joinGroup(group);

            byte[] buffer = new byte[256];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Mensagem recebida: " + receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (IS_LEADER) {
            Thread sendThread = new Thread(() -> sendHeartbeats());
            sendThread.start();
        } else {
            Thread receiveThread = new Thread(() -> listenForHeartbeats());
            receiveThread.start();
        }
    }
}
