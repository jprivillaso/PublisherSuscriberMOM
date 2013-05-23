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
public class PublisherPanel {

    private static Publisher publisher;
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {

        String url = ActiveMQConnection.DEFAULT_BROKER_URL;

        try {
            //Creates an instance of the Publisher
            publisher = new Publisher(url);
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
        //Default Option
        int opcion = 0;
        PublisherPanel panel = new PublisherPanel();
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
     * Menu of options that the publisher can choose
     */
    public void menu() {
        System.out.println("\n\n"
                + "Select an option please:\n"
                + "1. Create Channel.\n"
                + "2. Publish Message.\n"
                + "3. Get Topics.\n"
                + "4. Exit\n");
    }
    
    /**
     * Inputs that the Publisher can choose. Each one has an action
     * @param option
     * @return 
     */
    public boolean inputMethod(int option) {

        String topic;
        String message;
        
        switch (option){
            case 1:
                System.out.println("Ingrese el nombre del canal");
                try {
                    topic = br.readLine();
                    publisher.createTopic(topic);
                    System.out.println("topico " + topic + " creado con exito");
                } catch (IOException | JMSException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 2:
                System.out.println("Enter the name of the channel");
                try {
                    topic = br.readLine();
                    System.out.println("Enter message");
                    message = br.readLine();
                    publisher.publishMessage(message, topic);
                    System.out.println("Message: " + message 
                            + " pubicado con exito en: " + topic);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 3:
                try {
                    publisher.getTopics();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 4:
                try {
                    publisher.closeSession();
                } catch (JMSException e) {
                    System.out.println(e.getMessage());
                }
                return false;
            default:
                System.out.println
                        ("Not found option. Please enter a valid option");
                break;
        }
        return true;
    }
}
