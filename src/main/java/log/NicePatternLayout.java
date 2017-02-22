package log;

import org.apache.log4j.EnhancedPatternLayout;

public class NicePatternLayout extends EnhancedPatternLayout {
    @Override
    public boolean ignoresThrowable() {
        return false;
    }
}
