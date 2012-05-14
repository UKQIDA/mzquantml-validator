/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import org.apache.log4j.Level;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time May 2, 2012 3:23:05 PM
 */
public class ObjectRefTypeMatchRule {

    private static ArrayList<Message> msg;

    public ObjectRefTypeMatchRule() {
        msg = new ArrayList<Message>();
    }

    public <T> void check(Object obj, Class<T> cls) {
        if (obj != null) {
            if (!(obj.getClass().equals(cls))) {
                msg.add(new Message(("The object reference doesnot match the correct object type " + cls.getName()), Level.ERROR));
            }
        }
    }

    public ArrayList<Message> getMessage() {
        return msg;
    }
}
