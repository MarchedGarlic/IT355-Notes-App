package org.garrett;

/// This shows rule OBJ14-J by avoiding a freed object
public class OBJ14JExample {
    private static class Resource implements AutoCloseable {
        private boolean closed = false;

        public void doSomething() {
            if (closed) {
                throw new IllegalStateException("Resource has been closed");
            }
            System.out.println("Resource is performing work");
        }

        @Override
        public void close() {
            closed = true;
            System.out.println("Resource has been closed");
        }
    }

    public static void main(String[] args) {
        Resource resource = new Resource();
        try (resource) {
            resource.doSomething();
        } // resource is closed here
        
        // Do not use resource after this point
        // resource.doSomething(); // This would throw IllegalStateException
    }
}
