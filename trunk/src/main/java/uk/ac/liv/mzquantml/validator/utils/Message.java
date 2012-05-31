/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.utils;

import java.util.ArrayList;
import org.apache.log4j.Level;

/**
 *
 * @author Da Qi @institute University of Liverpool @time May 2, 2012 3:52:41 PM
 */
public class Message {

    private String message;
    private Level level;

    public Message() {
        this("", Level.DEBUG);
    }

    public Message(String msg) {
        this(msg, Level.DEBUG);
    }

    public Message(String msg, Level l) {
        this.message = msg;
        this.level = l;
    }

    public Message(ArrayList<String> msgs) {
        this.message = "";
        for (String s : msgs) {
            this.message = this.message + "\n" + s;
        }
        this.level = Level.ALL;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return this.message;
    }

    public void setLevel(Level l) {
        this.level = l;
    }

    public Level getLevel() {
        return this.level;
    }

    @Override
    public String toString() {
        return ("\n" + this.level.toString() + ": " + this.message);
    }
}
