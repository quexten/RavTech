/*******************************************************************************
 * Copyright 2014-2016 Bernd Schoolmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ravelsoftware.ravtech.dk.ui.editor;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

class LimitLinesDocumentListener implements DocumentListener {

    private boolean isRemoveFromStart;
    private int maximumLines;

    /*
     * Specify the number of lines to be stored in the Document. Extra lines will be removed from the start of the Document.
     */
    public LimitLinesDocumentListener(int maximumLines) {
        this(maximumLines, true);
    }

    /*
     * Specify the number of lines to be stored in the Document. Extra lines will be removed from the start or end of the
     * Document, depending on the boolean value specified.
     */
    public LimitLinesDocumentListener(int maximumLines, boolean isRemoveFromStart) {
        setLimitLines(maximumLines);
        this.isRemoveFromStart = isRemoveFromStart;
    }

    public void changedUpdate (DocumentEvent e) {
    }

    /*
     * Return the maximum number of lines to be stored in the Document
     */
    public int getLimitLines () {
        return maximumLines;
    }

    // Handle insertion of new text into the Document
    public void insertUpdate (final DocumentEvent e) {
        // Changes to the Document can not be done within the listener
        // so we need to add the processing to the end of the EDT
        SwingUtilities.invokeLater(new Runnable() {

            public void run () {
                removeLines(e);
            }
        });
    }

    /*
     * Remove lines from the end of the Document
     */
    private void removeFromEnd (Document document, Element root) {
        // We use start minus 1 to make sure we remove the newline
        // character of the previous line
        Element line = root.getElement(root.getElementCount() - 1);
        int start = line.getStartOffset();
        int end = line.getEndOffset();
        try {
            document.remove(start - 1, end - start);
        } catch (BadLocationException ble) {
            System.out.println(ble);
        }
    }

    /*
     * Remove lines from the start of the Document
     */
    private void removeFromStart (Document document, Element root) {
        Element line = root.getElement(0);
        int end = line.getEndOffset();
        try {
            document.remove(0, end);
        } catch (BadLocationException ble) {
            System.out.println(ble);
        }
    }

    /*
     * Remove lines from the Document when necessary
     */
    private void removeLines (DocumentEvent e) {
        // The root Element of the Document will tell us the total number
        // of line in the Document.
        Document document = e.getDocument();
        Element root = document.getDefaultRootElement();
        while (root.getElementCount() > maximumLines)
            if (isRemoveFromStart)
                removeFromStart(document, root);
            else
                removeFromEnd(document, root);
    }

    public void removeUpdate (DocumentEvent e) {
    }

    /*
     * Set the maximum number of lines to be stored in the Document
     */
    public void setLimitLines (int maximumLines) {
        if (maximumLines < 1) {
            String message = "Maximum lines must be greater than 0";
            throw new IllegalArgumentException(message);
        }
        this.maximumLines = maximumLines;
    }
}

/*
 * Create a simple console to display text messages. Messages can be directed here from different sources. Each source can have
 * its messages displayed in a different color. Messages can either be appended to the console or inserted as the first line of
 * the console You can limit the number of lines to hold in the Document.
 */
public class MessageConsole {

    /*
     * Class to intercept output from a PrintStream and add it to a Document. The output can optionally be redirected to a
     * different PrintStream. The text displayed in the Document can be color coded to indicate the output source.
     */
    public class ConsoleOutputStream extends ByteArrayOutputStream {

        private SimpleAttributeSet attributes;
        private StringBuffer buffer = new StringBuffer(80);
        private final String EOL = System.getProperty("line.separator");
        private boolean isFirstLine;
        private PrintStream printStream;

        /*
         * Specify the option text color and PrintStream
         */
        public ConsoleOutputStream(Color textColor, PrintStream printStream) {
            if (textColor != null) {
                attributes = new SimpleAttributeSet();
                StyleConstants.setForeground(attributes, textColor);
            }
            this.printStream = printStream;
            if (isAppend) isFirstLine = true;
        }

        /*
         * The message and the newLine have been added to the buffer in the appropriate order so we can now update the Document
         * and send the text to the optional PrintStream.
         */
        private void clearBuffer () {
            // In case both the standard out and standard err are being
            // redirected
            // we need to insert a newline character for the first line only
            if (isFirstLine && document.getLength() != 0) buffer.insert(0, "\n");
            isFirstLine = false;
            String line = buffer.toString();
            try {
                if (isAppend) {
                    int offset = document.getLength();
                    document.insertString(offset, line, attributes);
                    textComponent.setCaretPosition(document.getLength());
                } else {
                    document.insertString(0, line, attributes);
                    textComponent.setCaretPosition(0);
                }
            } catch (BadLocationException ble) {
            }
            if (printStream != null) printStream.print(line);
            buffer.setLength(0);
        }

        /*
         * Override this method to intercept the output text. Each line of text output will actually involve invoking this method
         * twice: a) for the actual text message b) for the newLine string The message will be treated differently depending on
         * whether the line will be appended or inserted into the Document
         */
        public void flush () {
            String message = toString();
            if (message.length() == 0) return;
            if (isAppend)
                handleAppend(message);
            else
                handleInsert(message);
            reset();
        }

        /*
         * We don't want to have blank lines in the Document. The first line added will simply be the message. For additional
         * lines it will be: newLine + message
         */
        public void handleAppend (String message) {
            // This check is needed in case the text in the Document has been
            // cleared. The buffer may contain the EOL string from the previous
            // message.
            if (document.getLength() == 0) buffer.setLength(0);
            if (EOL.equals(message))
                buffer.append(message);
            else {
                buffer.append(message);
                clearBuffer();
            }
        }

        /*
         * We don't want to merge the new message with the existing message so the line will be inserted as: message + newLine
         */
        private void handleInsert (String message) {
            buffer.append(message);
            if (EOL.equals(message)) clearBuffer();
        }
    }

    private Document document;
    private boolean isAppend;
    private DocumentListener limitLinesListener;
    private JTextComponent textComponent;

    public MessageConsole(JTextComponent textComponent) {
        this(textComponent, true);
    }

    /*
     * Use the text component specified as a simply console to display text messages. The messages can either be appended to the
     * end of the console or inserted as the first line of the console.
     */
    public MessageConsole(JTextComponent textComponent, boolean isAppend) {
        this.textComponent = textComponent;
        this.document = textComponent.getDocument();
        this.isAppend = isAppend;
        textComponent.setEditable(false);
    }

    /*
     * Redirect the output from the standard error to the console using the default text color and null PrintStream
     */
    public void redirectErr () {
        redirectErr(null, null);
    }

    /*
     * Redirect the output from the standard error to the console using the specified color and PrintStream. When a PrintStream is
     * specified the message will be added to the Document before it is also written to the PrintStream.
     */
    public void redirectErr (Color textColor, PrintStream printStream) {
        ConsoleOutputStream cos = new ConsoleOutputStream(textColor, printStream);
        System.setErr(new PrintStream(cos, true));
    }

    /*
     * Redirect the output from the standard output to the console using the default text color and null PrintStream
     */
    public void redirectOut () {
        redirectOut(null, null);
    }

    /*
     * Redirect the output from the standard output to the console using the specified color and PrintStream. When a PrintStream
     * is specified the message will be added to the Document before it is also written to the PrintStream.
     */
    public void redirectOut (Color textColor, PrintStream printStream) {
        ConsoleOutputStream cos = new ConsoleOutputStream(textColor, printStream);
        System.setOut(new PrintStream(cos, true));
    }

    /*
     * To prevent memory from being used up you can control the number of lines to display in the console This number can be
     * dynamically changed, but the console will only be updated the next time the Document is updated.
     */
    public void setMessageLines (int lines) {
        if (limitLinesListener != null) document.removeDocumentListener(limitLinesListener);
        limitLinesListener = new LimitLinesDocumentListener(lines, isAppend);
        document.addDocumentListener(limitLinesListener);
    }
}
