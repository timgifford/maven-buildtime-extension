package co.leantechniques.maven.buildtime;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FakeLogger extends AbstractLogger {
    private final List<String> list;

    public FakeLogger(){
        this(0,"");
    }

    public FakeLogger(int threshold, String name) {
        super(threshold, name);
        this.list = new ArrayList<String>();
    }

    @Override
    public void debug(String s, Throwable throwable) {
        list.add(s);
    }

    @Override
    public void info(String s, Throwable throwable) {
        list.add(s);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        list.add(s);
    }

    @Override
    public void error(String s, Throwable throwable) {
        list.add(s);
    }

    @Override
    public void fatalError(String s, Throwable throwable) {
        list.add(s);
    }

    @Override
    public Logger getChildLogger(String s) {
        return null;
    }

    public String getLine(int i) {
        return list.get(i);
    }

    public String output() {
        return StringUtils.join(list.toArray(), "\n");
    }
}
