package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnection;

/**
 *
 * @author Juan Rivillas & Santiago Moreno
 */
public class ConsumerPanel {

    private static Consumer consumer;
    private static BufferedReader br = 
            new BufferedReader(new InputStreamReader(System.in));

    /**
     * Main Method
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String url = ActiveMQConnection.DEFAULT_BROKER_URL;
        String email;

        try {
            //Enter the email that it's the id in this case
            System.out.println("Enter your email please");
            email = br.readLine();

            //Creates a new Consumer
            consumer = new Consumer(email, url);
        } catch (JMSException | IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        int opcion = 0;

        //Creates a new ConsumerPanel
        ConsumerPanel panel = new ConsumerPanel();
        do {
            panel.menu();
            try {
                opcion = Integer.valueOf(br.readLine());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } while (panel.inputMethod(opcion));
    }

    /**
     * This menu contains the options that the consumer can have
     */
    public void menu() {
        System.out.println("\n\n"
                + "Select an option please:\n"
                + "1. Suscribe to a Channel.\n"
                + "2. Get Topics.\n"
                + "3. Receive Message.\n"
                + "4. Exit\n");
    }

    /**
     * This are the inputs of the consumer. Each one has an action
     *
     * @param opcion
     * @return
     */
    public boolean inputMethod(int opcion) {
        String topic;

        switch (opcion) {
            case 1:
                System.out.println("Ingrese el nombre del canal");
                try {
                    topic = br.readLine();
                    consumer.subscribe(topic);
                } catch (IOException | JMSException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 2:
                try {
                    consumer.getTopics();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 3:
                try {
                    System.out.println("Enter the channel to receive messages");
                    topic = br.readLine();
                    consumer.receive(topic, 0);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 4:
                return false;
            default:
                System.out.println("Not found option");
                break;
        }
        return true;
    }
}
