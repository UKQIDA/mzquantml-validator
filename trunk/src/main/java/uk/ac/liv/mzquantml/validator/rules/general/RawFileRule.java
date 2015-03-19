
package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.jmzqml.model.mzqml.InputFiles;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @time 18:44:31 12-May-2012
 * @institution University of Liverpool
 */
public class RawFileRule {

    List<AnalysisType> atList;
    InputFiles infls;
    List<Message> msgs = new ArrayList<>();

    /*
     * constructor
     */
    public RawFileRule() {
        this.atList = new ArrayList<>();
    }

    public RawFileRule(List<AnalysisType> analysisTypeList,
                       InputFiles inputFiles) {
        this.atList = analysisTypeList;
        this.infls = inputFiles;
    }

    /*
     * public methods
     */
    public void check() {
        for (AnalysisType at : atList) {
            if (at == AnalysisType.LabelFree) {
                checkLCMS();
            }
            else if (at == AnalysisType.MS2TagBased) {
                checkMS2();
            }
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
            msgs.add(new Message("InputFiles MUST contain raw files.\n", Level.INFO));
            msgs.add(new Message("InputFiles do not contain raw files.\n", Level.ERROR));
        }
    }

    private void checkMS2() {
        if (infls.getRawFilesGroup() == null) {
            msgs.add(new Message("InputFiles MUST contain raw files.\n", Level.INFO));
            msgs.add(new Message("InputFiles do not contain raw files.\n", Level.ERROR));
        }
    }

}
