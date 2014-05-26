public class EntryPoint {
    public static void main(String [] args) {
        Server server = new Server(3000);
        server.run();
        Client client = new Client("localhost", 3000);
        client.makeRequests(-1); //неположительное число для "бесконечного числа запросов"
    }
}
