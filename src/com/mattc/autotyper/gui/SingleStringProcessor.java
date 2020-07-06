package com.mattc.autotyper.gui;

import com.google.common.base.StandardSystemProperty;
import com.google.common.io.LineProcessor;

import java.io.IOException;

public class SingleStringProcessor implements LineProcessor<String> {

    private static final String SEP = StandardSystemProperty.LINE_SEPARATOR.value();
    private final StringBuilder sb = new StringBuilder();

    @Override
    public boolean processLine(String line) throws IOException {
        if (this.sb.length() == 0) {
            this.sb.append(line);
        } else {
            this.sb.append(SEP).append(line);
        }

        return true;
    }

    @Override
    public String getResult() {
        return this.sb.toString();
    }

}
