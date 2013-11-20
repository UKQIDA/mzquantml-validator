/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.jmzqml.model.mzqml.PeptideConsensusList;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @time 19:08:11 12-May-2012
 * @institution University of Liverpool
 */
public class FinalResultRule {

    Iterator<PeptideConsensusList> pepCnsLstIt;
    ArrayList<Message> msgs = new ArrayList<Message>();

    /*
     * constructor
     */
    public FinalResultRule() {
        this.pepCnsLstIt = null;
    }

    public FinalResultRule(Iterator<PeptideConsensusList> peptideConsensusListIter) {
        this.pepCnsLstIt = peptideConsensusListIter;
    }

    /*
     * public methods
     */
    public void check() {
        int count = 0;
        //when the pepCnsListIt is empty, skip the checking
        if (this.pepCnsLstIt != null && this.pepCnsLstIt.hasNext()) {
         
            while (this.pepCnsLstIt.hasNext()) {
                PeptideConsensusList peptideConsensusList = this.pepCnsLstIt.next();
                if (peptideConsensusList.isFinalResult()) {
                    count++;
                }
            }
            if (count != 1) {
                msgs.add(new Message("Exactly one PeptideConsensusList "
                        + "MUST have isFinalResult=\"true\"", Level.INFO));
                msgs.add(new Message(String.valueOf(count)
                        + " PeptideConsensusList(s) have/has isFinalResult=\"true\"\n", Level.ERROR));
            }
        }
    }

    public List<Message> getMsgs() {
        return msgs;
    }

}
