package co.leantechniques.maven.buildtime.publisher;

import co.leantechniques.maven.buildtime.SessionTimer;
import org.apache.maven.execution.ExecutionEvent;

public interface Publisher {
    public String Name = Publisher.class.getName();
    void publish(ExecutionEvent event, SessionTimer session);
}
