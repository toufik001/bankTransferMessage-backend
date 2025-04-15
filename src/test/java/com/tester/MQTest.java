package com.tester; 


import com.ibm.mq.MQQueue;
import com.ibm.mq.jakarta.jms.MQConnectionFactory;

import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.JMSProducer;

public class MQTest {
    public static void main(String[] args) {
        // Configuration de connexion MQ
        String host = "localhost";
        int port = 1414;
        String channel = "DEV.APP.SVRCONN";
        String queueManager = "QM1";
        String queueName = "PAYMENT.INPUT.QUEUE";
        String user = "app";              // DOIT être 'dev'
        String password = "passw0rd";     // Pas vérifié, mais requis pour la connexion


        // Créer la factory de connexion
        MQConnectionFactory factory = new MQConnectionFactory();
       
        try {
            factory.setHostName(host);
            factory.setPort(port);
            factory.setChannel(channel);
            factory.setQueueManager(queueManager);
            factory.setTransportType(1); // 1 = CLIENT
    
            // Connexion JMS
            while (true) {
                try {
                    factory.createConnection().start();
                    System.out.println("Connexion réussie à la file d'attente MQ !");

                    break; // Sortir de la boucle si la connexion réussit
                } catch (JMSException e) {
                    System.err.println("Échec de la connexion, nouvelle tentative dans 30 secondes..." + e.getMessage());
                    try {
                        Thread.sleep(30000); // Attendre 5 secondes avant de réessayer
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } catch (JMSException e) {
            System.err.println("Erreur lors de la configuration du port : " + e.getMessage());
            e.printStackTrace();
            return;
        }

        try (JMSContext context = factory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            // Création de la destination
            jakarta.jms.Destination destination = context.createQueue("queue:///" + queueName);
            JMSProducer producer = context.createProducer();

            for (int i = 0; i < 5403; i++) {
                // Créer le message JSON
                String message = """
                    {
                    "content": "Test NOTIFICATION %d MQ depuis client Java",
                    "timestamp": "2025-04-13T16:00:00",
                    "processedFlowType": "NOTIFICATION"
                    }
                    """;
    
                // Envoi du message
                producer.send(destination, String.format(message, i));
                System.out.println("Message envoyé avec succès !");
            }

            
            for (int i = 0; i < 10023; i++) {
                // Créer le message JSON
                String message = """
                    {
                    "content": "Test ALERTING %d MQ depuis client Java",
                    "timestamp": "2025-04-13T16:00:00",
                    "processedFlowType": "ALERTING"
                    }
                    """;
    
                // Envoi du message
                producer.send(destination, String.format(message, i));
                System.out.println("Message envoyé avec succès !");
            }

            
            for (int i = 0; i < 1034; i++) {
                // Créer le message JSON
                String message = """
                    {
                    "content": "Test MESSAGE %d MQ depuis client Java",
                    "timestamp": "2025-04-13T16:00:00",
                    "processedFlowType": "MESSAGE"
                    }
                    """;
    
                // Envoi du message
                producer.send(destination, String.format(message, i));
                System.out.println("Message envoyé avec succès !");
            }
        }
    }
}
