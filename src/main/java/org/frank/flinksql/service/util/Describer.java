package org.frank.flinksql.service.util;

import java.io.PrintWriter;

public interface Describer {

    void describe(final Printer out);

    interface Printer {

        /**
         * Prints an object.
         *
         * @param x The <code>Object</code> to be printed
         * @return this printer
         */
        Printer print(final Object x);

        /**
         * Prints an Object and then terminates the line.
         *
         * @param x The <code>Object</code> to be printed.
         * @return this printer
         */
        Printer println(final Object x);
    }

    class DefaultPrinter implements Describer.Printer {

        private final PrintWriter out;

        public DefaultPrinter(PrintWriter out) {
            this.out = out;
        }

        @Override
        public Describer.Printer print(final Object x) {
            this.out.print(x);
            return this;
        }

        @Override
        public Describer.Printer println(final Object x) {
            this.out.println(x);
            return this;
        }
    }
}
