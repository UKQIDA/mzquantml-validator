/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import info.psidev.psi.pi.mzquantml._1_0.PeptideConsensusListType;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi @time 19:08:11 12-May-2012 @institution University of Liverpool
 */
public class FinalResultRule {

    List<PeptideConsensusListType> pepCnsLsts;
    ArrayList<Message> msgs = new ArrayList<Message>();

    /*
     * constructor
     */
    public FinalResultRule() {
        this.pepCnsLsts = null;
    }

    public FinalResultRule(List<PeptideConsensusListType> peptideConsensusLists) {
        this.pepCnsLsts = peptideConsensusLists;
    }

    /*
     * public methods
     */
    public void check() {
        int count = 0;
        for (PeptideConsensusListType peptideConsensusList : this.pepCnsLsts) {
            if (peptideConsensusList.isFinalResult()) {
                count++;
            }
        }
        if (count != 1) {
            msgs.add(new Message("Exactly one PeptideConsensusList "
                    + "MUST have isFinalResult=\"true\"", Level.INFO));
            msgs.add(new Message(String.valueOf(count)
                    + " PeptideConsensusList(s) have/has isFinalResult=\"true\"", Level.ERROR));
        }
    }

    public List<Message> getMsgs() {
        return msgs;
    }
}
