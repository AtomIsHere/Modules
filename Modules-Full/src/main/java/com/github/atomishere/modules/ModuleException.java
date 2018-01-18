package com.github.atomishere.modules;

public class ModuleException extends RuntimeException {
    private static final long serialVersionUID = 9018973481002L;

    public ModuleException(String message) {
        super(message);
    }

    public ModuleException(String message, Throwable ex) {
        super(message, ex);
    }

    public ModuleException(Throwable ex) {
        super(ex);
    }
}
