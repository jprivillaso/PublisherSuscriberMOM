package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;
import java.util.Set;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.command.ActiveMQTopic;

/**
 *
 * @author Juan Rivillas & Santiago Moreno
 */
public class Consumer implements MessageListener {

    private String email;
    private String url;
    private TopicConnectionFactory connectionFactory;
    private int acknowledge = Session.AUTO_ACKNOWLEDGE;
    private boolean transactionSent = false;

    public Consumer(String email, String url) throws JMSException {
        this.email = email;
        this.url = url;
        connectionFactory= new ActiveMQConnectionFactory(this.url);
    }

    /**
     * Override Methods
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        String txtMessage;
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                txtMessage = textMessage.getText();
                Date tTimeStamp = new Date(textMessage.getJMSTimestamp());
                String publicationText =
                        DateFormat.getDateTimeInstance().format(tTimeStamp);

                //Prints the message in the console
                System.out.println("New Message from topic:"
                        + publicationText + " -> " + txtMessage.toString());
            }
        } catch (JMSException e) {
            // Print the error message in the console
            System.out.println("Error receiving the message: " + e.getMessage());
        }
    }

    /**
     * Subscribe in the topic
     * @param topicName
     * @throws JMSException
     */
    public void subscribe(String topicName) throws JMSException {
        System.out.println("checkin params: " + getEmail());
        TopicConnection connection = connectionFactory.createTopicConnection();
        connection.setClientID(getEmail());
        TopicSession topicSession =
                connection.createTopicSession(transactionSent, acknowledge);

        // Start Connection
        connection.start();

        // Creates the topic
        Topic topico = topicSession.createTopic(topicName);
        topicSession.createDurableSubscriber(topico, topico.getTopicName() + "_"
                + getEmail());

        // Prints in the console
        System.out.println("Topic: " + topico.getTopicName() + "-"
                + getEmail() + " transaction made successfully");

        //Close connection
        connection.close();
    }
    
    /**
     * Receives messages from the publisher
     * @param topicName
     * @param modo
     * @throws Exception 
     */
    public void receive(String topicName, int modo) throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        TopicConnection topicConnection =
                                    connectionFactory.createTopicConnection();

        //Set Email of Consumer
        topicConnection.setClientID(getEmail());
        TopicSession tSession = 
                            topicConnection.createTopicSession
                                (this.transactionSent, this.acknowledge);

        //Start the connection
        topicConnection.start();
        
        DestinationSource destinationSource = 
                                        new DestinationSource(topicConnection);
        destinationSource.start();
        
        //Get the topics list
        Set<ActiveMQTopic> topicList = destinationSource.getTopics();
        
        //Creates a new topic with the name typed in the console
        Topic topico = tSession.createTopic(topicName);
        
        //Checks if the topic exists
        if (!topicList.contains(topico)) {
            topicConnection.close();
            throw new Exception("The channel does not exist");
        }
        destinationSource.stop();
        
        TopicSubscriber suscriber = 
                tSession.createDurableSubscriber(topico, topico.getTopicName() 
                                                            + "_" + getEmail());
        suscriber.setMessageListener(this);

        //Exit when something is pressed
        System.out.println("Receiving messages: " + topicName + 
                " (Press a key to Exit)");
        
        //Listen to messages
        if (br.read() != 0) {
            topicConnection.close();
            suscriber.close();
            System.out.println("Stop receiving messages: " + topicName);
        }
    }
    
    /**
     * Get topics
     * @throws JMSException
     * @throws InvalidDestinationException 
     */
    public void getTopics() throws JMSException, InvalidDestinationException {

        TopicConnection connection = connectionFactory.createTopicConnection();

        // Start the connection
        connection.start();

        DestinationSource destinationSource = new DestinationSource(connection);
        destinationSource.start();
        Set<ActiveMQTopic> topicList = destinationSource.getTopics();

        //Print the channel
        System.out.println("Channels:");
        for (ActiveMQTopic topic : topicList) {
            System.out.println(topic.toString());
        }
        
        //Close connection
        connection.close();
    }

    //////////////////////////// GETTERS AND SETTERS /////////////////////////
    /**
     * @return the name
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param name the name to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the URL to set
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
