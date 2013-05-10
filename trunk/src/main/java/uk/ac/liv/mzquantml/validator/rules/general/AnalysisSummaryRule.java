/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.liv.jmzqml.model.mzqml.AnalysisSummary;
import uk.ac.liv.jmzqml.model.mzqml.CvParam;
import uk.ac.liv.mzquantml.validator.utils.AnalysisSummaryElement;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi @institute University of Liverpool @time 13-Nov-2012 15:07:07
 */
public class AnalysisSummaryRule {

    private static final Logger logger = Logger.getLogger(AssayLabelRule.class);
    private AnalysisSummary anlaysisSummary;
    private AnalysisType at;
    private HashMap<String, AnalysisSummaryElement> cvTermMap;
    List<Message> msgs = new ArrayList<Message>();

    /*
     * constructor
     */
    public AnalysisSummaryRule(AnalysisSummary analysisSummary) {
        this.anlaysisSummary = analysisSummary;
        this.at = new AnalysisType(analysisSummary);
    }
    /*
     * public methods
     */

    public boolean check() {

        boolean ret = true;
        /**
         * initialise @cvTermMap It records which valid cv terms are in
         * AnalsisSummary by mapping accession to AnalysisSummaryElement
         */
        cvTermMap = new HashMap<String, AnalysisSummaryElement>();
        // gather the cvTermMap
        for (CvParam cv : this.anlaysisSummary.getCvParam()) {
            String accession = cv.getAccession();
            for (AnalysisSummaryElement ase : AnalysisSummaryElement.values()) {
                if (ase.getAccession() != null && ase.getAccession().equals(accession)) {
                    AnalysisSummaryElement a = cvTermMap.get(accession);
                    if (a == null) {
                        cvTermMap.put(accession, ase);
                    } else {
                        msgs.add(new Message("Duplicate cv term with accession: " + a.getAccession(), Level.ERROR));
                    }
                }
            }
        }

        if (missCvTerm(cvTermMap, this.at)) {
            ret = false;
        }

        return ret;
    }

    public List<Message> getMsgs() {
        return msgs;
    }

    /*
     * private methods
     */
    private boolean missCvTerm(HashMap<String, AnalysisSummaryElement> cvTermMap,
            AnalysisType at) {

        boolean ret = false;

        for (AnalysisSummaryElement ase : AnalysisSummaryElement.valuesByType(at)) {
            String accession = ase.getAccession();
            if (cvTermMap.get(accession) == null) {
                msgs.add(new Message("Missing cv term: " + ase.getAccession() + "(" + ase.getName() + ")" + " for " + at.getAnalysisType().getName(), Level.ERROR));
                ret = true;
            }
        }

        return ret;
    }
}
