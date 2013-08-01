
package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.apache.log4j.Level;
import uk.ac.liv.jmzqml.model.mzqml.*;
import uk.ac.liv.jmzqml.xml.io.MzQuantMLUnmarshaller;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time May 2, 2012 3:23:05 PM
 *
 * Schema_rules_general.txt: All object references of type IDREFS or IDREF MUST match the correct object type
 */
public class ObjectRefTypeMatchRule {

    ArrayList<Message> msgs = new ArrayList<Message>();
    String targetClassId;
    String ref;
    Class cls;
    MzQuantMLUnmarshaller unmarshaller;

    public ObjectRefTypeMatchRule() {
    }

    public ObjectRefTypeMatchRule(String tarClsId, String ref, Class cls,
                                  MzQuantMLUnmarshaller um) {
        this.targetClassId = tarClsId;
        this.cls = cls;
        this.ref = ref;
        this.unmarshaller = um;
    }

    public void check() {
        if (this.ref != null) {

            try {
                // resolving ref to object
                Object obj = this.unmarshaller.unmarshal(this.cls, ref);

                if (!(obj.getClass().equals(this.cls)) && !(this.cls.isAssignableFrom(obj.getClass()))) {
                    
                    msgs.add(new Message("All object reference of type IDREFS or IDREF MUST match the correct object type", Level.INFO));
                    msgs.add(new Message("The object reference \""
                            + ref + "\" in \"" + this.targetClassId + "\" does not match the correct object type "
                            + cls.getName() + "\n", Level.ERROR));
                }
            }
            catch (JAXBException ex) {
                Logger.getLogger(ObjectRefTypeMatchRule.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            catch (IllegalArgumentException ilArEx) {
                msgs.add(new Message(ilArEx.getMessage(), Level.ERROR));
            }
        }
    }

    public ArrayList<Message> getMessage() {
        return msgs;
    }

}
