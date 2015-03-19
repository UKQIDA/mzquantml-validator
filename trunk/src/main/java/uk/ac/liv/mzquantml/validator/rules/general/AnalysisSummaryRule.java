
package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.liv.jmzqml.model.mzqml.AnalysisSummary;
import uk.ac.liv.jmzqml.model.mzqml.CvParam;
import uk.ac.liv.mzquantml.validator.utils.AnalysisSummaryElement;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time 13-Nov-2012 15:07:07
 */
public class AnalysisSummaryRule {
    
    private static final Logger logger = Logger.getLogger(AssayLabelRule.class);
    private AnalysisSummary anlaysisSummary;
    private List<AnalysisType> atList;
    private Map<String, AnalysisSummaryElement> cvTermMap;
    List<Message> msgs = new ArrayList<>();

    /*
     * constructor
     */
    public AnalysisSummaryRule(AnalysisSummary analysisSummary) {
        this.anlaysisSummary = analysisSummary;
        this.atList = AnalysisType.getAnalysisType(analysisSummary);
    }
    /*
     * public methods
     */
    
    public boolean check() {
        
        boolean ret = true;
        boolean hasSRM = false;
        
        if (atList.size() > 2) {
            msgs.add(new Message("There are " + atList.size() + " analysis types in the <AnalysisSummary>.\n"));
            msgs.add(new Message("There MUST NOT be more than 2 analysis types in <AnalysisSummary>.\n", Level.ERROR));
            return false;
        }
        else if (atList.size() > 1 && !atList.contains(AnalysisType.SRM)) {
            msgs.add(new Message("There are " + atList.size() + " analysis types in the <AnalysisSummary>.\n"));
            msgs.add(new Message("There MUST NOT be more than 1 techniques in <AnalysisSummary> if SRM technique is not present.\n", Level.ERROR));
            return false;
        }
        else if (atList.size() >= 1 && atList.contains(AnalysisType.SRM)) {
            hasSRM = true;
            if (!atList.contains(AnalysisType.LabelFree) && !atList.contains(AnalysisType.MS1LabelBased)) {
                msgs.add(new Message("If \"SRM quantitation analysis\" is present in <AnlaysisSummary>, either \"LC-MS label-free quantitation analysis\" "
                        + "or \"MS1 Label-based analysis\" MUST be present in <AnlaysisSummary>.\n", Level.ERROR));
                return false;
            }
        }
        else if (atList.size() == 1 && !atList.contains(AnalysisType.SRM)) {
            AnalysisType at = atList.get(0);
            if (at != AnalysisType.LabelFree && at != AnalysisType.MS1LabelBased && at != AnalysisType.MS2TagBased && at != AnalysisType.SpectralCounting) {
                msgs.add(new Message("If \"SRM quantitation analysis\" is not present in <AnlaysisSummary>, EXACTLY ONE of the other four CV terms "
                        + "MUST be present in <AnalysisSummary>.\n", Level.ERROR));
                return false;
            }
        }
        else if (atList.isEmpty()) {
            msgs.add(new Message("Missing CV term for specific technique in <AnalysisSummary>. "
                    + "Hence validator can not identify the technique used in this file. \n"
                    + "A valid mzq file MUST contain at least one of the following CV terms:\n"
                    + "\"" + AnalysisType.LabelFree.toString() + "\"\n"
                    + "\"" + AnalysisType.MS1LabelBased.toString() + "\"\n"
                    + "\"" + AnalysisType.MS2TagBased.toString() + "\"\n"
                    + "\"" + AnalysisType.SpectralCounting.toString() + "\"\n"
                    + "\"" + AnalysisType.SRM.toString() + "\"\n", Level.ERROR));
            return false;
        }

        /**
         * initialise
         *
         * @cvTermMap It records which valid cv terms are in
         * AnalsisSummary by mapping accession to AnalysisSummaryElement
         */
        cvTermMap = new HashMap<>();
        // gather the cvTermMap
        for (CvParam cv : this.anlaysisSummary.getCvParam()) {
            String accession = cv.getAccession();
            for (AnalysisSummaryElement ase : AnalysisSummaryElement.values()) {
                if (ase.getAccession() != null && ase.getAccession().equals(accession)) {
                    AnalysisSummaryElement a = cvTermMap.get(accession);
                    if (a == null) {
                        cvTermMap.put(accession, ase);
                    }
                    else {
                        msgs.add(new Message("Duplicate cv term with accession: " + a.getAccession() + ".\n", Level.ERROR));
                    }
                }
            }
        }
        
        ret = !missCvTerm(cvTermMap, this.atList, hasSRM);
        
        return ret;
    }
    
    public List<Message> getMsgs() {
        return msgs;
    }

    /**
     * The method check if all the required CV terms for specific analysis type are in <AnalysisSummary>.
     *
     * The CV terms for feature level quantitation, peptide level quantitation, protein level quantitation,and proteingroup level quantitation
     * from each analysis type must be present in <AnalysisSummary>. In the case of SRM technique, there are two CV terms for analysis type:
     * one is SRM and the other is either label free or MS1 label based. Only CV terms for SRM (feature, peptide, protein, proteingroup) level
     * quantitations must be present.
     *
     * @param cvTermMap the map with existing CV terms in <AnalysisSummary> and its corresponding AnalysisSummaryElement
     * @param atList    the list of analysis type (label free, MS1 label based, MS2 tag based, spectral counting and SRM) in <AnalysisSummary>
     * @param hasSRM    the flag to indicate if AnalysisSummary contains CV term for SRM quantitation analysis
     *
     * @return the boolean value of the checking result
     */
    private boolean missCvTerm(Map<String, AnalysisSummaryElement> cvTermMap,
                               List<AnalysisType> atList,
                               boolean hasSRM) {
        
        boolean missingTerm = false;
        if (hasSRM) {
            for (AnalysisSummaryElement ase : AnalysisSummaryElement.valuesByType(AnalysisType.SRM)) {
                String accession = ase.getAccession();
                if (cvTermMap.get(accession) == null) {
                    msgs.add(new Message("Missing cv term: " + ase.getAccession() + "(" + ase.getName() + ")" + " for " + AnalysisType.SRM.getName() + ".\n", Level.ERROR));
                    missingTerm = true;
                }
            }
        }
        else {
            for (AnalysisType at : atList) {
                for (AnalysisSummaryElement ase : AnalysisSummaryElement.valuesByType(at)) {
                    String accession = ase.getAccession();
                    if (cvTermMap.get(accession) == null) {
                        msgs.add(new Message("Missing cv term: " + ase.getAccession() + "(" + ase.getName() + ")" + " for " + at.getName() + ".\n", Level.ERROR));
                        missingTerm = true;
                    }
                }
            }
        }
        
        return missingTerm;
    }

    /**
     * @return the atList
     */
    public List<AnalysisType> getAtList() {
        return atList;
    }
    
}
