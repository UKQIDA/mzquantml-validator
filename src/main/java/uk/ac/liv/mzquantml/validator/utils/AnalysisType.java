
package uk.ac.liv.mzquantml.validator.utils;

import java.util.ArrayList;
import java.util.List;
import uk.ac.liv.jmzqml.model.mzqml.AnalysisSummary;
import uk.ac.liv.jmzqml.model.mzqml.CvParam;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time May 3, 2012 2:48:50 PM
 */
public enum AnalysisType {

    /**
     *
     */
    LabelFree("LC-MS label-free quantitation analysis", "MS:1001834"),

    /**
     *
     */
    SpectralCounting("spectral counting quantitation analysis", "MS:1001836"),

    /**
     *
     */
    MS1LabelBased("MS1 label-based analysis", "MS:1002018"),

    /**
     *
     */
    MS2TagBased("MS2 tag-based analysis", "MS:1002023"),

    /**
     *
     */
    SRM("SRM quantitation analysis", "MS:1001838");

    /**
     *
     */
//    InvalidAnalysisType("invalid type", "null");

    //variables
    private String name;
    private String accession;

    private AnalysisType(String name, String accession) {
        this.name = name;
        this.accession = accession;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public String getAccession() {
        return accession;
    }

    @Override
    public String toString() {
        return (this.getAccession() + "- \"" + this.getName() + "\"");
    }

    public static List<AnalysisType> getAnalysisType(
            AnalysisSummary analysisSummary) {
        /*
         * This can deal with the situation when two valid cv terms are present (e.g. SRM and label free).
         */

        List<AnalysisType> atList = new ArrayList<>();
        List<CvParam> paramGroups = analysisSummary.getCvParam();

        for (CvParam cvParam : paramGroups) {

            if (cvParam.getAccession().equals(AnalysisType.LabelFree.getAccession())) {
                atList.add(AnalysisType.LabelFree);
            }
            else if (cvParam.getAccession().equals(AnalysisType.SpectralCounting.getAccession())) {
                atList.add(AnalysisType.SpectralCounting);
            }
            else if (cvParam.getAccession().equals(AnalysisType.MS1LabelBased.getAccession())) {
                atList.add(AnalysisType.MS1LabelBased);
            }
            else if (cvParam.getAccession().equals(AnalysisType.MS2TagBased.getAccession())) {
                atList.add(AnalysisType.MS2TagBased);
            }
            else if (cvParam.getAccession().equals(AnalysisType.SRM.getAccession())) {
                atList.add(AnalysisType.SRM);
            }
//            else {
//                atList.add(AnalysisType.InvalidAnalysisType);
//            }
        }
        return atList;
    }

//    /**
//     *
//     */
//    public AnalysisType() {
//        at = null;
//    }
    /**
     *
     * @return
     */
//    public AnalTp getAnalysisType() {
//        return this.at;
//    }
}
