/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.validator.rules.general;

import info.psidev.psi.pi.mzquantml._1_0.InputFilesType;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType.AnalTp;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @time 18:44:31 12-May-2012
 * @institution University of Liverpool
 */
public class RawFileRule {

    AnalysisType at;
    InputFilesType infls;
    List<Message> msgs = new ArrayList<Message>();

    /*
     * constructor
     */
    public RawFileRule() {
        this.at = null;
    }

    public RawFileRule(AnalysisType analysisType, InputFilesType inputFiles) {
        this.at = analysisType;
        this.infls = inputFiles;
    }

    /*
     * public methods
     */
    public void check() {
        if (this.at.getAnalysisType() == AnalTp.LabelFree) {
            checkLCMS();
        } else if (this.at.getAnalysisType() == AnalTp.MS2TagBased) {
            checkMS2();
        }
    }

    public List<Message> getMsgs() {
        return msgs;
    }
    /*
     * private methods
     */

    private void checkLCMS() {
        if (infls.getRawFilesGroup() == null) {
            msgs.add(new Message("Rule: InputFiles MUST contain raw files", Level.ERROR));
            msgs.add(new Message("Problem: inputFiles do not contain raw files", Level.ERROR));
        }
    }

    private void checkMS2() {
        if (infls.getRawFilesGroup() == null) {
            msgs.add(new Message("Rule: InputFiles MUST contain raw files", Level.ERROR));
            msgs.add(new Message("Problem: inputFiles do not contain raw files", Level.ERROR));
        }
    }
}