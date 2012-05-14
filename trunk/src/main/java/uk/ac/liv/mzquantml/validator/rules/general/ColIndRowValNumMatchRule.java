/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import info.psidev.psi.pi.mzquantml._1_0.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
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

    public void check(QuantLayerType quantLayer) {
        int indexNum = quantLayer.getColumnIndex().size();
        DataMatrixType dm = quantLayer.getDataMatrix();
        List<RowType> rows = dm.getRow();
        for (RowType row : rows) {
            int colNum = row.getValue().size();
            if (colNum != indexNum) {
                Message msg = new Message(("Row \"" + row.getObjectRef().toString()
                        + "\" has difference numbers of value from column indices.\n"), Level.ERROR);
                msgs.add(msg);
            }
        }
    }

    public void check(GlobalQuantLayerType globalQuantLayer) {
        int indexNum = globalQuantLayer.getColumnDefinition().getColumn().size();
        DataMatrixType dm = globalQuantLayer.getDataMatrix();
        List<RowType> rows = dm.getRow();
        for (RowType row : rows) {
            int colNum = row.getValue().size();
            if (colNum != indexNum) {
                Message msg = new Message(("Row \"" + row.getObjectRef().toString()
                        + "\" has difference numbers of value from column indices.\n"), Level.ERROR);
                msgs.add(msg);
            }
        }
    }

    public ArrayList<Message> getMessage() {
        return msgs;
    }
}
