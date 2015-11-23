package co.leantechniques.maven.buildtime;

/**
 * Visitor Interface for Timer classes.
 *
 * It should not be implemented directly, use {@link AbstractTimerVisitor} instead.
 */
public interface TimerVisitor {

    void visit(SessionTimer sessionTimer);

    void visit(ProjectTimer projectTimer);

    void visit(MojoTimer mojoTimer);
}
