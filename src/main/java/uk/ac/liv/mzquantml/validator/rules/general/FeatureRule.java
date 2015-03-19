
package uk.ac.liv.mzquantml.validator.rules.general;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import uk.ac.liv.jmzqml.model.mzqml.CvParam;
import uk.ac.liv.jmzqml.model.mzqml.Feature;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @time 18:44:31 12-May-2012
 * @institution University of Liverpool
 */
public class FeatureRule {

    // isolation window target m/z
    final CvParam isolWndTrgMz = new CvParam();
    // isolation window lower offset
    final CvParam isolWndLowOfs = new CvParam();
    // isolation window upper offset
    final CvParam isolWndUppOfs = new CvParam();
    // product retention time
    final CvParam localRT = new CvParam();
    // product charge
    final CvParam prodChr = new CvParam();
    List<AnalysisType> atList;
    Feature feature;
    List<Message> msgs = new ArrayList<>();

    /*
     * constructor
     */
    public FeatureRule() {
        this.atList = new ArrayList<>();
        this.isolWndTrgMz.setName("isolation window target m/z");
        this.isolWndTrgMz.setAccession("MS:1000827");
        this.isolWndLowOfs.setName("isolation window lower offset");
        this.isolWndLowOfs.setAccession("MS:1000828");
        this.isolWndUppOfs.setName("isolation window upper offset");
        this.isolWndUppOfs.setAccession("MS:1000829");
        this.localRT.setName("local retention time");
        //Todo: add cv term
        this.localRT.setAccession("MS:1000895");
        this.prodChr.setName("charge state");
        this.prodChr.setAccession("MS:1000041");
    }

    public FeatureRule(List<AnalysisType> analysisTypeList, Feature ft) {
        this();
        this.atList = analysisTypeList;
        this.feature = ft;
    }

    /*
     * public methods
     */
    public void check() {
        if (this.atList.contains(AnalysisType.SRM)) {
            checkCv();
        }
    }

    public List<Message> getMsgs() {
        return msgs;
    }
    /*
     * private methods
     */

    private void checkCv() {
        boolean hasIsoMz = false;
        boolean hasIsoLow = false;
        boolean hasIsoUpp = false;
        boolean hasProRT = false;
        boolean hasProChr = false;

        List<CvParam> cvParams = this.feature.getCvParam();
        for (CvParam cp : cvParams) {
            if (cp.getName().equals(this.isolWndTrgMz.getName())) {
                hasIsoMz = true;
            }
            else if (cp.getName().equals(this.isolWndLowOfs.getName())) {
                hasIsoLow = true;
            }
            else if (cp.getName().equals(this.isolWndUppOfs.getName())) {
                hasIsoUpp = true;
            }
            else if (cp.getName().equals(this.localRT.getName())) {
                hasProRT = true;
            }
            else if (cp.getName().equals(this.prodChr.getName())) {
                hasProChr = true;
            }
        }

        if (!hasIsoMz) {
            msgs.add(new Message("None of the given CvTerms were found at '/MzQuantML/FeatureList/Feature/cvParam/@accession' because no values were found:\n"
                    + "\t-The sole term " + this.isolWndTrgMz.getAccession() + " (" + this.isolWndTrgMz.getName() + ") or any of its children. "
                    + "A single instance of this term can be specified. The matching value has to be the name of the term, not its identifier.\n", Level.ERROR));
        }

        if (!hasIsoLow && !hasIsoUpp) {
            msgs.add(new Message("None of the given CvTerms were found at '/MzQuantML/FeatureList/Feature/cvParam/@accession' because no values were found:\n"
                    + "\t-The sole term " + this.isolWndLowOfs.getAccession() + " (" + this.isolWndLowOfs.getName() + ") or any of its children. "
                    + "A single instance of this term can be specified. The matching value has to be the name of the term, not its identifier.\n"
                    + "\t-The sole term " + this.isolWndUppOfs.getAccession() + " (" + this.isolWndUppOfs.getName() + ") or any of its children. "
                    + "A single instance of this term can be specified. The matching value has to be the name of the term, not its identifier.\n", Level.INFO));
        }

        if (!hasProRT) {
            msgs.add(new Message("None of the given CvTerms were found at '/MzQuantML/FeatureList/Feature/cvParam/@accession' because no values were found:\n"
                    + "\t-The sole term " + this.localRT.getAccession() + " (" + this.localRT.getName() + ") or any of its children. "
                    + "A single instance of this term can be specified. The matching value has to be the name of the term, not its identifier.\n", Level.WARN));
        }

        if (!hasProChr) {
            msgs.add(new Message("None of the given CvTerms were found at '/MzQuantML/FeatureList/Feature/cvParam/@accession' because no values were found:\n"
                    + "\t-The sole term " + this.prodChr.getAccession() + " (" + this.prodChr.getName() + ") or any of its children. "
                    + "A single instance of this term can be specified. The matching value has to be the name of the term, not its identifier.\n", Level.INFO));
        }
    }

}
