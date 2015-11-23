package co.leantechniques.maven.buildtime;

public class MojoTimer {

    private final String projectName;

    private String name;
    private long startTime = 0;
    private long endTime = 0;
    private SystemClock systemClock;

    public MojoTimer(String projectName, String name, SystemClock systemClock) {
        this(projectName, name, 0,0, systemClock);
    }

    public MojoTimer(String projectName, String name, long startTime, long endTime){
        this(projectName, name, startTime, endTime, new SystemClock());
    }

    public MojoTimer(String projectName, String name, long startTime, long endTime, SystemClock systemClock) {
        this.projectName = projectName;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.systemClock = systemClock;
    }

    public Long getDuration() {
        return endTime - startTime;
    }

    public String getName() {
        return name;
    }

    public String getProjectName() {
        return projectName;
    }

    public void stop() {
        this.endTime = systemClock.currentTimeMillis();
    }

    public void start() {
        this.startTime = systemClock.currentTimeMillis();
    }

    public void accept(TimerVisitor visitor){
        visitor.visit(this);
    }
}
