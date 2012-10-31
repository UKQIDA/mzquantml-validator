/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.jmzqml.model.mzqml.DataMatrix;
import uk.ac.liv.jmzqml.model.mzqml.GlobalQuantLayer;
import uk.ac.liv.jmzqml.model.mzqml.QuantLayer;
import uk.ac.liv.jmzqml.model.mzqml.Row;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time May 3, 2012 2:08:35 PM
 */
public class ColIndRowValNumMatchRule {

    private static ArrayList<Message> msgs;

    public ColIndRowValNumMatchRule() {
        msgs = new ArrayList<Message>();
    }

    public void check(QuantLayer quantLayer) {
        int indexNum = quantLayer.getColumnIndex().size();
        DataMatrix dm = quantLayer.getDataMatrix();
        List<Row> rows = dm.getRow();
        for (Row row : rows) {
            int colNum = row.getValue().size();
            if (colNum != indexNum) {
                msgs.add(new Message("The number of data values in every Row of a QuantLayer "
                        + "MUST be equal to number of items in <ColumnIndex>", Level.INFO));
                msgs.add(new Message(("Row \"" + row.getObjectRef().toString()
                        + "\" has different numbers of value from column indices\n"), Level.ERROR));
            }
        }
    }

    public void check(GlobalQuantLayer globalQuantLayer) {
        int indexNum = globalQuantLayer.getColumnDefinition().getColumn().size();
        DataMatrix dm = globalQuantLayer.getDataMatrix();
        List<Row> rows = dm.getRow();
        for (Row row : rows) {
            int colNum = row.getValue().size();
            if (colNum != indexNum) {
                msgs.add(new Message("The number of data values in every Row of a QuantLayer "
                        + "MUST be equal to number of items in <ColumnDefinition>", Level.INFO));
                msgs.add(new Message(("Row \"" + row.getObjectRef().toString()
                        + "\" has different numbers of value from column indices\n"), Level.ERROR));
            }
        }
    }

    public ArrayList<Message> getMessage() {
        return msgs;
    }
}
