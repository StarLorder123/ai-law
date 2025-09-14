package com.law.rag.util;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

public class Neo4jUtil {

    // Method to create a node
    public static String createNode(Session session, String name, int age) {
        return session.writeTransaction(tx -> {
            String query = "CREATE (n:Person {name: $name, age: $age}) RETURN 'Node created'";
            return tx.run(query, Values.parameters("name", name, "age", age))
                    .single()
                    .get(0)
                    .asString();
        });
    }

    // Method to query a node
    public static String queryNode(Session session, String name) {
        return session.readTransaction(tx -> {
            String query = "MATCH (n:Person {name: $name}) RETURN n.name AS name, n.age AS age";
            Result result = tx.run(query, Values.parameters("name", name));
            if (result.hasNext()) {
                Record record = result.single();
                return "Name: " + record.get("name").asString() + ", Age: " + record.get("age").asInt();
            } else {
                return "No node found with the given name.";
            }
        });
    }
}
