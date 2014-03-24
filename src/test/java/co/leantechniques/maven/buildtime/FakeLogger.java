package co.leantechniques.maven.buildtime;

import org.apache.maven.cli.logging.Slf4jStdoutLogger;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FakeLogger extends Slf4jStdoutLogger {
    private final List<String> list;

    public FakeLogger(){
        this(0,"");
    }

    public FakeLogger(int threshold, String name) {
        this.list = new ArrayList<String>();
    }

    @Override
    public void debug(String s) {
        list.add(s);
        super.debug(s);
    }

    @Override
    public void info(String s) {
        list.add(s);
        super.info(s);
    }

    @Override
    public void warn(String s) {
        list.add(s);
        super.warn(s);
    }

    @Override
    public void error(String s) {
        list.add(s);
        super.error(s);
    }

    public String getLine(int i) {
        return list.get(i);
    }

    public String output() {
        return StringUtils.join(list.toArray(), "\n");
    }
}
