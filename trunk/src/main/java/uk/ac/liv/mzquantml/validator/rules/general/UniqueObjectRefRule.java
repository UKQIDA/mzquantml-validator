/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.jmzqml.model.mzqml.GlobalQuantLayer;
import uk.ac.liv.jmzqml.model.mzqml.QuantLayer;
import uk.ac.liv.jmzqml.model.mzqml.Row;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @time 00:58:46 26-Apr-2012
 * @institution University of Liverpool
 */
public class UniqueObjectRefRule {

    private static List<Message> messages;

    public UniqueObjectRefRule() {
        messages = new ArrayList();
    }

    public void check(QuantLayer quantLayer) {
        List<Row> rowList = quantLayer.getDataMatrix().getRow();
        if (!rowList.isEmpty() || (rowList == null)) {
            HashSet objectRefSet = new HashSet();
            for (Row row : rowList) {
                if (!objectRefSet.add(row.getObjectRef())) {  //need adaptor
                    Message msg = new Message(("Duplicate object_ref: " + row.getObjectRef().toString())
                            + ".\n", Level.ERROR);
                    messages.add(msg);
                }
            }
        }
    }

    public void check(GlobalQuantLayer globalQuantLayer) {
        List<Row> rowList = globalQuantLayer.getDataMatrix().getRow();
        if (!rowList.isEmpty() || (rowList == null)) {
            HashSet objectRefSet = new HashSet();
            for (Row row : rowList) {
                if (!objectRefSet.add(row.getObjectRef())) {  //need adaptor
                    Message msg = new Message(("Duplicate object_ref: " + row.getObjectRef().toString()
                            + ".\n"), Level.ERROR);
                    messages.add(msg);
                }
            }
        }
    }

    public List<Message> getMessage() {
        return messages;
    }

}
