/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.jmzqml.model.mzqml.EvidenceRef;
import uk.ac.liv.jmzqml.model.mzqml.PeptideConsensus;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time 30-Nov-2012 13:43:25
 */
public class EvidenceRefRule {

    PeptideConsensus peptide;
    EvidenceRef evdRef;
    List<Message> msgs = new ArrayList<Message>();

    /*
     * constructor
     */
    public EvidenceRefRule() {
        if (this.evdRef == null) {
            this.evdRef = new EvidenceRef();
        }
        if (this.peptide == null) {
            this.peptide = new PeptideConsensus();
        }
    }

    public EvidenceRefRule(PeptideConsensus pep, EvidenceRef ref) {
        this.peptide = pep;
        this.evdRef = ref;
    }

    /*
     * public methods
     */
    public void check() {
        if ((this.evdRef.getIdRefs().isEmpty() && this.evdRef.getIdentificationFileRef() != null)
                || (!this.evdRef.getIdRefs().isEmpty() && this.evdRef.getIdentificationFileRef() == null)) {
            msgs.add(new Message("If one of EvidenceRef attributes identificationFileRef or id_refs is present, "
                    + "then they both MUST be present", Level.INFO));
            msgs.add(new Message("EvidenceRef in PeptideConsensus " + this.peptide.getId()
                    + " contains either id_refs or identificationFileRef only\n", Level.ERROR));
        }
    }

    public List<Message> getMsgs() {
        return msgs;
    }
}
