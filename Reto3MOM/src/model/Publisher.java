package model;

import java.util.Set;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.command.ActiveMQTopic;

/**
 *
 * @author Juan Rivillas & Santiago Moreno
 */
public class Publisher {
    
    private String url;
    private ConnectionFactory clientFactory = null;
    private int acknowledge = Session.CLIENT_ACKNOWLEDGE;
    private boolean flag = false;
    private Session session;

    /**
     * Public Constructor
     * @param url 
     */
    public Publisher(String url) {
        this.url = url;
        clientFactory = new ActiveMQConnectionFactory(getUrl());
    }
    
    /**
     * Creates the topic
     * @param topicName
     * @throws JMSException 
     */
    public void createTopic(String topicName) throws JMSException {
        //Creates the connection
        Connection connection = clientFactory.createConnection();
        // Start the session
        connection.start();
        
        // Creates the topic
        session = connection.createSession(flag, this.acknowledge);
        Topic topic = session.createTopic(topicName);
        session.createProducer(topic);
        
        //Close Connection
        connection.close();
    }
    
    /**
     * Publish a message to the consumer through a specified channel
     * @param message
     * @param topic
     * @throws Exception 
     */
    public void publishMessage(String message, String topic) throws Exception{
        Connection conenection = clientFactory.createConnection();
        
        // Start the connection
        conenection.start();
        session = conenection.createSession(flag, this.acknowledge);
        
        Topic topicObj = session.createTopic(topic);

        /*
         * Checks if the topic exists in order to not create it again and
         * face problems
         */       
        if (!verifyChannel(topicObj, conenection)) {
            conenection.close();
            throw new Exception("No channel " + topic);
        }
        
        //Creates producer
        MessageProducer producer = session.createProducer(topicObj);

        TextMessage anuncio = session.createTextMessage();
        //Set and Send the message
        anuncio.setText(message);
        producer.send(anuncio);

        // Close connection and producer
        producer.close();
        conenection.close();
        
        //Success message
        System.out.println(anuncio.getText() + " Published Successfully " + topic);
    }
    
    /**
     * Verify if the channel exists
     * @param channel
     * @param connection
     * @return
     * @throws JMSException 
     */
    public boolean verifyChannel(Topic channel, Connection connection) throws JMSException {
        DestinationSource destinationSource = new DestinationSource(connection);
        destinationSource.start();
        //Get topics list
        Set<ActiveMQTopic> topicList = destinationSource.getTopics();
        
        //Check if the topic exist
        if (topicList.contains(channel)) {
            destinationSource.stop();
            return true;
        }
        destinationSource.stop();
        return false;
    }
    
    /**
     * Get the list of topics
     * @throws JMSException
     * @throws InvalidDestinationException 
     */
    public void getTopics() throws JMSException, InvalidDestinationException{
        Connection conexion = clientFactory.createConnection();
        // Start Connection
        conexion.start();
        DestinationSource destinationSource = new DestinationSource(conexion);
        destinationSource.start();
        
        //Get topics
        Set<ActiveMQTopic> topicList = destinationSource.getTopics();

        System.out.println("Available Channels:");
        for (ActiveMQTopic topic : topicList) {
            System.out.println(topic.toString());
        }
        
        // Close Connection
        conexion.close();
    }
    
    /**
     * Kills the session
     * @throws JMSException 
     */
    public void closeSession() throws JMSException {
        if (session != null) {
            session.close();
        }
    }
    
    ////////////////////////GETTERS AND SETTERS //////////////////////////////
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
