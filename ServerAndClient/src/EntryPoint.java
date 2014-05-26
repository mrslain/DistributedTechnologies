public class EntryPoint {
    public static void main(String [] args) {
        new ServerRunner().run(3000);
        Client client = new Client("localhost", 3000);
        client.makeRequests(-1); //неположительное число для "бесконечного числа запросов"
    }
}
