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
 * @author Da Qi @institute University of Liverpool @time May 2, 2012 3:23:05 PM
 */
public class ObjectRefTypeMatchRule {

    private static ArrayList<Message> msgs = new ArrayList<Message>();

    public ObjectRefTypeMatchRule() {
        msgs = new ArrayList<Message>();
    }

    public <T> void check(Object obj, Class<T> cls) {
        if (obj != null) {
            if (!(obj.getClass().equals(cls))) {
                msgs.add(new Message("All object reference of type IDREFS or IDREF MUST match the correct object type", Level.INFO));
                msgs.add(new Message("The object reference doesnot match the correct object type " + cls.getName(), Level.ERROR));
            }
        }
    }

    public ArrayList<Message> getMessage() {
        return msgs;
    }
}
