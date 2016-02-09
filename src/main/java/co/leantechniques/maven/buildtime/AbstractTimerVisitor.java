package co.leantechniques.maven.buildtime;

/**
 * Simple abstract implementation of {@link TimerVisitor} extend this
 * class instead of {@link TimerVisitor} to be safe for future extensions.
 */
public class AbstractTimerVisitor implements TimerVisitor {
    public void visit(SessionTimer sessionTimer) {

    }

    public void visit(ProjectTimer projectTimer) {

    }

    public void visit(MojoTimer mojoTimer) {

    }
}
