package uk.ac.liv.mzquantml.validator;

import info.psidev.psi.pi.mzquantml._1_0.*;
import info.psidev.psi.pi.mzquantml.io.MzQuantMLUnmarshaller;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.log4j.Level;
import uk.ac.liv.mzquantml.validator.rules.general.*;
import uk.ac.liv.mzquantml.validator.utils.AnalysisSummaryElement;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi @time 10:28:09 14-Mar-2012 @institution University of Liverpool
 */
public class MzQuantMLValidator {

    private static AnalysisType at = new AnalysisType();
    private static HashMap<AnalysisSummaryElement, Boolean> analysisSummaryMap;
    private static List<Message> msgs = new ArrayList<Message>();

    /**
     * @param args the command line arguments
     */
    public static List<Message> main(String fileName) throws FileNotFoundException {
        // TODO code application logic here
        msgs.clear();
        msgs.add(new Message("Starting validation process......", Level.INFO));
        msgs.add(new Message("Loading MzQuantML file......", Level.INFO));

        MzQuantMLUnmarshaller unmarshaller = new MzQuantMLUnmarshaller(fileName);
        MzQuantMLType mzq = (MzQuantMLType) unmarshaller.unmarshall();

        /*
         * get all mzQuantML elements
         */
        ParamListType analysisSummary = mzq.getAnalysisSummary();
        AssayListType assayList = mzq.getAssayList();
        AuditCollectionType auditCollection = mzq.getAuditCollection();
        List<BibliographicReferenceType> bibliographicReferences = mzq.getBibliographicReference();
        XMLGregorianCalendar creationDate = mzq.getCreationDate();
        CvListType cvList = mzq.getCvList();
        DataProcessingListType dataProcessingList = mzq.getDataProcessingList();
        List<FeatureListType> featureLists = mzq.getFeatureList();
        InputFilesType inputFiles = mzq.getInputFiles();
        List<PeptideConsensusListType> peptideConsensusLists = mzq.getPeptideConsensusList();
        ProteinGroupListType proteinGroupList = mzq.getProteinGroupList();
        ProteinListType proteinList = mzq.getProteinList();
        ProviderType provider = mzq.getProvider();
        RatioListType ratioList = mzq.getRatioList();
        SmallMoleculeListType smallMoleculeList = mzq.getSmallMoleculeList();
        SoftwareListType softwareList = mzq.getSoftwareList();
        StudyVariableListType studyVariableList = mzq.getStudyVariableList();
        String version = mzq.getVersion();

        /*
         * check all mzQuantML elements one by one
         */
        if (analysisSummary != null) {
            at = new AnalysisType(analysisSummary);
            checkAnalysisSummary(analysisSummary);
        } else {
            // message there is no AnalysisSummary which is not acceptable
        }

        if (assayList != null) {
            checkAssayList(assayList);
        } else {
            // message there is no AssayList which is not acceptable
        }


        if (auditCollection != null) {
            checkAuditCollection(auditCollection);
        } else {
            // message
        }

        if (bibliographicReferences != null) {
            checkBibliographicReference(bibliographicReferences);
        } else {
            // message
        }

        if (creationDate != null) {
            checkCreationDate(creationDate);
        } else {
            // message
        }

        if (cvList != null) {
            checkCvList(cvList);
        } else {
            // message
        }

        if (dataProcessingList != null) {
            checkDataProcessingList(dataProcessingList);
        } else {
            // message
        }

        if (featureLists != null) {
            checkFeatureList(featureLists);
        } else {
            // messages
        }

        if (inputFiles != null) {
            checkInputFiles(inputFiles);
            /*
             * RawFileRule start here
             */
            RawFileRule rfl = new RawFileRule(at, inputFiles);
            rfl.check();
            msgs.addAll(rfl.getMsgs());
        } else {
            // message there is no inputFiles which is not acceptable
        }

        if (peptideConsensusLists != null) {
            checkPeptideConsensusLists(peptideConsensusLists);
            /*
             * FinalResultRule start here
             */
            FinalResultRule frr = new FinalResultRule(peptideConsensusLists);
            frr.check();
            msgs.addAll(frr.getMsgs());
        } else {
            // some message
        }

        if (proteinGroupList != null) {
            checkProteinGroupList(proteinGroupList);
        } else {
            // message
        }

        if (proteinList != null) {
            checkProteinList(proteinList);
        } else {
            // message
        }

        if (provider != null) {
            checkProvider(provider);
        } else {
            // message
        }

        if (ratioList != null) {
            checkRatioList(ratioList);
        } else {
            // message
        }

        if (smallMoleculeList != null) {
            checkSmallMoleculeList(smallMoleculeList);
        } else {
            // message
        }

        if (softwareList != null) {
            checkSoftwareList(softwareList);
        } else {
            // message
        }

        if (studyVariableList != null) {
            checkStudyVariableList(studyVariableList);
        } else {
            // message
        }

        if (version != null) {
            checkVersion(version);
        } else {
            // message
        }

        /*
         * General rule instantces
         */

        UniqueObjectRefRule uniqueObjectRefRule = new UniqueObjectRefRule();
        NumeratorDenominatorRule numeratorDenominatorRule = new NumeratorDenominatorRule();

        /*
         * ListsRule start here
         */
        ListsRule listsRule = new ListsRule(at, inputFiles, proteinGroupList, proteinList,
                peptideConsensusLists, featureLists);
        listsRule.check();
        msgs.addAll(listsRule.getMsgs());

        /*
         * QuantLayerRule start here
         */
        QuantLayerRule quantLayerRule = new QuantLayerRule(analysisSummaryMap, proteinGroupList, proteinList, peptideConsensusLists, featureLists);
        quantLayerRule.check();
        msgs.addAll(quantLayerRule.getMsgs());
        /*
         * final output
         */
        System.out.println("MzQuantML validation process is finished");
        msgs.add(new Message("MzQuantML validation process is finished", Level.INFO));
        return msgs;
    }

//    private static String checkRefNumConsistency(QuantLayerType ql) {
//        String output = "";
//        int refNum = ql.getColumnIndex().size();
//        DataMatrixType dm = ql.getDataMatrix();
//        List<RowType> row_list = dm.getRow();
//        Iterator i_row_list = row_list.iterator();
//        while (i_row_list.hasNext()) {
//            RowType row = (RowType) i_row_list.next();
//            int colNum = row.getValue().size();
//
//            if (colNum != refNum) {
//                output = output + "Row \"" + row.getObjectRef().toString()
//                        + "\" has difference numbers of value from column indices.\n";
//                //break;
//            }
//        }
//        return output;
//    }
//    private static String checkPeptideConsensusAssayRefFeatureRef(PeptideConsensusType pep) {
//        String output = "";
//        int assayRef_num = pep.getAssayRefs().size();
//        int featureRef_num = pep.getFeatureRefs().size();
//        if (assayRef_num != featureRef_num) {
//            output = "Peptide \"" + pep.getId() + "\" is not valid:\n";
//            output = output + "The size of the Assay_refs list MUST always match the size of the Feature_refs list.";
//        }
//        return output;
//    }

    /*
     * All the check*** functions start here
     */
    static public void checkAnalysisSummary(ParamListType analysisSummary) {
        List<AbstractParamType> paramGroup = analysisSummary.getParamGroup();

        analysisSummaryMap = new HashMap<AnalysisSummaryElement, Boolean>();
        for (AbstractParamType param : paramGroup) {
            String cvTerm = param.getName();
            String value = param.getValue();
            AnalysisSummaryElement key = AnalysisSummaryElement.getType(cvTerm);
            if (key != null) {
                analysisSummaryMap.put(key, Boolean.valueOf(value));
            }
        }

        checkParamGroup(paramGroup);
    }

    static public void checkAssayList(AssayListType assayList) {
        List<AssayType> assays = assayList.getAssay();
        String id = assayList.getId();

//        Object rawFilesGroupRef = assayList.getRawFileGroupRef();
//        if (rawFilesGroupRef != null) {
//            msgs.addAll(checkObjectRef(rawFilesGroupRef, info.psidev.psi.pi.mzquantml._1_0.RawFilesGroupType.class));
//        }

        checkAssay(assays);

        //AssayLabelRule start here
        AssayLabelRule alr = new AssayLabelRule(at, assayList);
        alr.check();
        msgs.addAll(alr.getMsgs());
    }

    static public void checkAuditCollection(AuditCollectionType auditCollection) {
        List<AbstractContactType> personOrOrganization = auditCollection.getPersonOrOrganization();

        checkPersonOrOrganization(personOrOrganization);
    }

    static public void checkBibliographicReference(List<BibliographicReferenceType> bibliographicReferences) {
        bibliographicReferences.iterator();
    }

    static public void checkCreationDate(XMLGregorianCalendar creationDate) {
    }

    static public void checkCvList(CvListType cvList) {
        List<CvType> cvs = cvList.getCv();

        checkCv(cvs);
    }

    static public void checkDataProcessingList(DataProcessingListType dataProcessingList) {
        List<DataProcessingType> dataProcessings = dataProcessingList.getDataProcessing();

        checkDataProcessing(dataProcessings);
    }

    /*
     * validate FeatureList
     */
    static public void checkFeatureList(List<FeatureListType> featureLists) {
        for (FeatureListType featureList : featureLists) {
            List<FeatureType> features = featureList.getFeature();
            checkFeature(features);

            List<GlobalQuantLayerType> featureQuantLayers = featureList.getFeatureQuantLayer();
            checkGlobalQuantLayer(featureQuantLayers);

            String id = featureList.getId();

            List<QuantLayerType> ms2AssayQuantLayers = featureList.getMS2AssayQuantLayer();
            checkQuantLayer(ms2AssayQuantLayers);

            List<QuantLayerType> ms2RatioQuantLayers = featureList.getMS2RatioQuantLayer();
            checkQuantLayer(ms2RatioQuantLayers);

            List<QuantLayerType> ms2StudyVariableQuantLayers = featureList.getMS2StudyVariableQuantLayer();
            checkQuantLayer(ms2StudyVariableQuantLayers);

            List<AbstractParamType> paramGroups = featureList.getParamGroup();
            checkParamGroup(paramGroups);

            Object rawFileGroupRef = featureList.getRawFilesGroupRef();
            String targetClassId = featureList.getId();
            msgs.addAll(checkObjectRef(targetClassId, rawFileGroupRef, info.psidev.psi.pi.mzquantml._1_0.RawFilesGroupType.class));

        }

//        if (featureLists != null) {
//            System.out.println("\nValidating FeatureLists......");
//
//            Iterator i_featureLists = featureLists.iterator();
//            while (i_featureLists.hasNext()) {
//                FeatureListType feature_list = (FeatureListType) i_featureLists.next();
//                String feature_list_id = feature_list.getId();
//
//
//
// 
//
//                // get FeatureQuantLayer
//                feature_list.getFeatureQuantLayer();
//            }
//        }
    }

    static public void checkFeature(List<FeatureType> features) {
        for (FeatureType feature : features) {
            feature.getChromatogramRefs();
            feature.getSpectrumRefs();
            feature.getParamGroup();
        }
    }

    static public void checkInputFiles(InputFilesType inputFiles) {
        IdentificationFilesType identificationFiles = inputFiles.getIdentificationFiles();
        if (identificationFiles != null) {
            List<IdentificationFileType> identifications = identificationFiles.getIdentificationFile();
            if (!identifications.isEmpty()) {
                for (IdentificationFileType identificationFile : identifications) {
                    checkIdentificationFile(identificationFile);
                }
            }
        }

        MethodFilesType methodFiles = inputFiles.getMethodFiles();
        if (methodFiles != null) {
            List<MethodFileType> methods = methodFiles.getMethodFile();
            if (!methods.isEmpty()) {
                for (MethodFileType methodFile : methods) {
                    methodFile.getFileFormat();
                }
            }
        }

        List<RawFilesGroupType> rawFilesGroups = inputFiles.getRawFilesGroup();
        checkRawFilesGroup(rawFilesGroups);

        List<SearchDatabaseType> searchDatabases = inputFiles.getSearchDatabase();
        checkSearchDatabases(searchDatabases);

        List<SourceFileType> sourceFiles = inputFiles.getSourceFile();
        checkSourceFiles(sourceFiles);
    }

    static public void checkIdentificationFile(IdentificationFileType identificationFile) {
        Object searchDatabaseRef = identificationFile.getSearchDatabaseRef();
        String targetClassId = identificationFile.getId();
        checkObjectRef(targetClassId, searchDatabaseRef, info.psidev.psi.pi.mzquantml._1_0.SearchDatabaseType.class);
    }

    static public void checkSourceFiles(List<SourceFileType> sourceFiles) {
        if (!sourceFiles.isEmpty()) {
            for (SourceFileType sourceFile : sourceFiles) {
                sourceFile.getFileFormat();
            }
        }
    }

    static public void checkSearchDatabases(List<SearchDatabaseType> searchDatabases) {
        if (!searchDatabases.isEmpty()) {
            for (SearchDatabaseType searchDatabase : searchDatabases) {
                List<CVParamType> cvParams = searchDatabase.getCvParam();
                String targetClassId = searchDatabase.getId();
                if (!cvParams.isEmpty()) {
                    for (CVParamType cvParam : cvParams) {
                        checkCvParam(targetClassId, cvParam);
                    }
                }

                ParamType databaseName = searchDatabase.getDatabaseName();
                if (databaseName.getCvParam() != null) {
                    checkCvParam(targetClassId, databaseName.getCvParam());
                }

                searchDatabase.getFileFormat();

            }
        }
    }

    /*
     * validate PeptideConsensusList
     */
    static public void checkPeptideConsensusLists(List<PeptideConsensusListType> peptideConsensusLists) {
        if (!peptideConsensusLists.isEmpty()) {
            for (PeptideConsensusListType peptideConsensusList : peptideConsensusLists) {
                checkPeptideConsensusList(peptideConsensusList);
            }
        }

//        if (peptideConsensusLists != null) {
//            System.out.println("\nValidating PeptideConsensusLists......");
//
//            Iterator i_peptideConsensusLists = peptideConsensusLists.iterator();
//            while (i_peptideConsensusLists.hasNext()) {
//                PeptideConsensusListType pep_con_list = (PeptideConsensusListType) i_peptideConsensusLists.next();
//                String pep_con_list_id = pep_con_list.getId();
//
//                List<PeptideConsensusType> pep_cons = pep_con_list.getPeptideConsensus();
//                Iterator i_pep_cons = pep_cons.iterator();
//                while (i_pep_cons.hasNext()) {
//                    PeptideConsensusType peptideConsensus = (PeptideConsensusType) i_pep_cons.next();
//
//                }
//
//
//                // get GlobalQuantLayer
//                pep_con_list.getGlobalQuantLayer();
//
//
//            }
//        }
    }

    static public void checkPeptideConsensusList(PeptideConsensusListType peptideConsensusList) {
        List<QuantLayerType> assayQuantLayers = peptideConsensusList.getAssayQuantLayer();
        List<GlobalQuantLayerType> globalQuantLayers = peptideConsensusList.getGlobalQuantLayer();
        String id = peptideConsensusList.getId();
        List<AbstractParamType> paramGroups = peptideConsensusList.getParamGroup();
        List<PeptideConsensusType> peptideConsensuses = peptideConsensusList.getPeptideConsensus();
        List<QuantLayerType> ratioQuantLayers = peptideConsensusList.getRatioQuantLayer();
        List<QuantLayerType> studyVariableQuantLayers = peptideConsensusList.getStudyVariableQuantLayer();

        checkQuantLayer(assayQuantLayers);
        checkGlobalQuantLayer(globalQuantLayers);
        checkParamGroup(paramGroups);
        checkPeptideConsensuses(peptideConsensuses);
        checkQuantLayer(ratioQuantLayers);
        checkQuantLayer(studyVariableQuantLayers);
    }

    static public void checkPeptideConsensuses(List<PeptideConsensusType> peptideConsensuses) {
        if (!peptideConsensuses.isEmpty()) {
            for (PeptideConsensusType peptideConsensus : peptideConsensuses) {
                checkPeptideConsensus(peptideConsensus);
            }
        }
    }

    static public void checkPeptideConsensus(PeptideConsensusType peptideConsensus) {
        List<String> charges = peptideConsensus.getCharge();
        List<FeatureRefType> featureRefs = peptideConsensus.getFeatureRef();
        String id = peptideConsensus.getId();
        List<PeptideIdentificationRefType> identificationRefs = peptideConsensus.getIdentificationRef();
        List<ModificationType> modifications = peptideConsensus.getModification();
        List<AbstractParamType> paramGroups = peptideConsensus.getParamGroup();
        String sequence = peptideConsensus.getPeptideSequence();
        Object searchDBRef = peptideConsensus.getSearchDatabaseRef();

        String targetClassId = peptideConsensus.getId();
        if (!featureRefs.isEmpty()) {
            for (FeatureRefType featureRef : featureRefs) {
                checkFeatureRef(targetClassId, featureRef);
            }
        }

        if (!identificationRefs.isEmpty()) {
            for (IdentificationRefType identificationRef : identificationRefs) {
                checkIdentificationRef(targetClassId, identificationRef);
            }
        }
    }

    static public void checkFeatureRef(String tarClsId, FeatureRefType featureRef) {
        List<Object> assayRefs = featureRef.getAssayRefs();
        if (!assayRefs.isEmpty()) {
            for (Object assayRef : assayRefs) {
                String targetClassId = tarClsId;
                msgs.addAll(checkObjectRef(targetClassId, assayRef, info.psidev.psi.pi.mzquantml._1_0.AssayType.class));
            }
        }

        Object ref = featureRef.getFeatureRef();
        String targetClassId = tarClsId;
        msgs.addAll(checkObjectRef(targetClassId, ref, info.psidev.psi.pi.mzquantml._1_0.FeatureType.class));
    }

    static public void checkIdentificationRef(String tarClsId, IdentificationRefType identificationRef) {
        Object ref = identificationRef.getIdentificationFileRef();
        String targetClassId = tarClsId;
        msgs.addAll(checkObjectRef(targetClassId, ref, info.psidev.psi.pi.mzquantml._1_0.PeptideIdentificationRefType.class));
    }

    static public void checkProteinGroupList(ProteinGroupListType proteinGroupList) {
        List<QuantLayerType> assayQuantLayers = proteinGroupList.getAssayQuantLayer();
        List<GlobalQuantLayerType> globalQuantLayers = proteinGroupList.getGlobalQuantLayer();
        String id = proteinGroupList.getId();
        List<AbstractParamType> paramGroups = proteinGroupList.getParamGroup();
        List<ProteinGroupType> proteinGroups = proteinGroupList.getProteinGroup();
        List<QuantLayerType> ratioQuantLayers = proteinGroupList.getRatioQuantLayer();
        List<QuantLayerType> studyVariableQuantLayers = proteinGroupList.getStudyVariableQuantLayer();

        checkQuantLayer(assayQuantLayers);
        checkGlobalQuantLayer(globalQuantLayers);
        checkParamGroup(paramGroups);
        checkProteinGroups(proteinGroups);
        checkQuantLayer(ratioQuantLayers);
        checkQuantLayer(studyVariableQuantLayers);
    }

    /*
     * validate ProteinList
     */
    static public void checkProteinList(ProteinListType proteinList) {
        List<QuantLayerType> assayQuantLayers = proteinList.getAssayQuantLayer();
        List<GlobalQuantLayerType> globalQuantLayers = proteinList.getGlobalQuantLayer();
        String id = proteinList.getId();
        List<AbstractParamType> paramGroups = proteinList.getParamGroup();
        List<ProteinType> proteins = proteinList.getProtein();
        List<QuantLayerType> ratioQuantLayers = proteinList.getRatioQuantLayer();
        List<QuantLayerType> studyVariableQuantLayers = proteinList.getStudyVariableQuantLayer();

        checkQuantLayer(assayQuantLayers);
        checkGlobalQuantLayer(globalQuantLayers);
        checkParamGroup(paramGroups);
        checkProtein(proteins);
        checkQuantLayer(ratioQuantLayers);
        checkQuantLayer(studyVariableQuantLayers);

        if (proteinList != null) {
            System.out.println("\nValidating ProteinList......");

            // get AssayQuantLayer
//            List<QuantLayerType> prot_assay_QL_list = proteinList.getAssayQuantLayer();
//            Iterator i_prot_assay_QL_list = prot_assay_QL_list.iterator();
//            while (i_prot_assay_QL_list.hasNext()) {
//                QuantLayerType QL = (QuantLayerType) (i_prot_assay_QL_list.next());
//
//                //System.out.println(QL.getColumnIndex().toString());
//                String output_checkRefNumConsistency = checkRefNumConsistency(QL);
//                if (!output_checkRefNumConsistency.isEmpty()) {
//                    String ql_id = QL.getId();
//                    System.out.println("AssayQuantLayer in ProteinList is not validated in \"" + ql_id + "\"");
//                    System.out.println(checkRefNumConsistency(QL));
//
//                    results = results + "AssayQuantLayer in ProteinList is not validated in \"" + ql_id + "\"\n";
//                    results = results + checkRefNumConsistency(QL) + "\n";
//                }
//            }

            // get GlobalQuantLayer
            proteinList.getGlobalQuantLayer();

            // get StudyVariableQuantLayer
//            List<QuantLayerType> prot_sv_QL_list = proteinList.getStudyVariableQuantLayer();
//            Iterator i_prot_sv_QL_list = prot_sv_QL_list.iterator();
//            while (i_prot_sv_QL_list.hasNext()) {
//                QuantLayerType QL = (QuantLayerType) (i_prot_sv_QL_list.next());
//
//                //System.out.println(QL.getColumnIndex().toString());
//                String output_checkRefNumConsistency = checkRefNumConsistency(QL);
//                if (!output_checkRefNumConsistency.isEmpty()) {
//                    String ql_id = QL.getId();
//                    System.out.println("StudyVariableQuantLayer in ProteinList is not validated in \"" + ql_id + "\"");
//                    System.out.println(checkRefNumConsistency(QL));
//
//                    results = results + "StudyVariableQuantLayer in ProteinList is not validated in \"" + ql_id + "\"\n";
//                    results = results + checkRefNumConsistency(QL) + "\n";
//                }
//            }
        }
    }

    static public void checkProvider(ProviderType provider) {
        Object analysisSoftwareRef = provider.getAnalysisSoftwareRef();
        String targetClassId = provider.getId();
        msgs.addAll(checkObjectRef(targetClassId, analysisSoftwareRef, info.psidev.psi.pi.mzquantml._1_0.SoftwareType.class));

        ContactRoleType contactRole = provider.getContactRole();

        String id = provider.getId();

        String name = provider.getName();
    }

    /*
     * Validate RatioList
     */
    static public void checkRatioList(RatioListType ratioList) {

        List<RatioType> ratios = ratioList.getRatio();
        checkRatio(ratios);

        NumeratorDenominatorRule numeratorDenominatorRule = new NumeratorDenominatorRule(ratioList);
        numeratorDenominatorRule.check();
        msgs.addAll(numeratorDenominatorRule.getMsgs());
    }

    static public void checkSmallMoleculeList(SmallMoleculeListType smallMoleculeList) {
        List<QuantLayerType> assayQuantLayers = smallMoleculeList.getAssayQuantLayer();
        List<GlobalQuantLayerType> globalQuantLayers = smallMoleculeList.getGlobalQuantLayer();
        String id = smallMoleculeList.getId();
        List<AbstractParamType> paramGroups = smallMoleculeList.getParamGroup();
        List<QuantLayerType> ratioQuantLayers = smallMoleculeList.getRatioQuantLayer();
        List<SmallMoleculeType> smallMolecules = smallMoleculeList.getSmallMolecule();
        List<QuantLayerType> studyVariableQuantLayers = smallMoleculeList.getStudyVariableQuantLayer();

        checkQuantLayer(assayQuantLayers);
        checkGlobalQuantLayer(globalQuantLayers);
        checkParamGroup(paramGroups);
        checkSmallMolecule(smallMolecules);
        checkQuantLayer(ratioQuantLayers);
        checkQuantLayer(studyVariableQuantLayers);
    }

    static public void checkSoftwareList(SoftwareListType softwareList) {
        List<SoftwareType> softwares = softwareList.getSoftware();
        checkSoftware(softwares);
    }

    static public void checkStudyVariableList(StudyVariableListType studyVariableList) {
        List<StudyVariableType> studyVariables = studyVariableList.getStudyVariable();
        checkStudyVariable(studyVariables);
    }

    static public void checkVersion(String version) {
    }

    static public void checkParamGroup(List<AbstractParamType> paramGroups) {
        for (AbstractParamType param : paramGroups) {
            param.getUnitCvRef(); //String
        }
    }

    static public void checkAssay(List<AssayType> assays) {
        for (AssayType assay : assays) {
            String targetClassId = assay.getId();

            CVParamType cvParam = assay.getCvParam();
            if (cvParam != null) {
                checkCvParam(targetClassId, cvParam);
            }

            Object rawFilesGroupRef = assay.getRawFilesGroupRef();

            if (rawFilesGroupRef != null) {
                msgs.addAll(checkObjectRef(targetClassId, rawFilesGroupRef,
                        info.psidev.psi.pi.mzquantml._1_0.RawFilesGroupType.class));
            }

        }

    }

    static public void checkCvParam(String tarClsId, CVParamType cvParam) {
        Object cvRef = cvParam.getCvRef();
        if (cvRef != null) {
            msgs.addAll(checkObjectRef(tarClsId, cvRef, info.psidev.psi.pi.mzquantml._1_0.CvType.class));
        }
    }

    static public void checkPersonOrOrganization(List<AbstractContactType> personOrOrganizations) {
        for (AbstractContactType personOrOrganization : personOrOrganizations) {
            List<AbstractParamType> paramGroups = personOrOrganization.getParamGroup();
            checkParamGroup(paramGroups);
        }
    }

    static public void checkCv(List<CvType> cvs) {
        for (CvType cv : cvs) {
            cv.getFullName();
            cv.getId();
            cv.getUri();
            cv.getVersion();
        }
    }

    static public void checkDataProcessing(List<DataProcessingType> dataProcessings) {
        for (DataProcessingType dataProcessing : dataProcessings) {
            List<Object> inputObjectRefs = dataProcessing.getInputObjectRefs();
            List<Object> outputObjectRefs = dataProcessing.getOutputObjectRefs();
            String targetClassId = dataProcessing.getId();
            Object softwareRef = dataProcessing.getSoftwareRef();
            msgs.addAll(checkObjectRef(targetClassId, softwareRef, info.psidev.psi.pi.mzquantml._1_0.SoftwareType.class));

            dataProcessing.getId();

            dataProcessing.getOrder();

            dataProcessing.getProcessingMethod();

        }
    }

    static public void checkRawFilesGroup(List<RawFilesGroupType> rawFilesGroups) {
        for (RawFilesGroupType rawFilesGroup : rawFilesGroups) {
            String targetClassId = rawFilesGroup.getId();
            List<AbstractParamType> paramGroups = rawFilesGroup.getParamGroup();
            checkParamGroup(paramGroups);

            List<RawFileType> rawFiles = rawFilesGroup.getRawFile();
            checkRawFile(targetClassId, rawFiles);
        }
    }

    static public void checkRawFile(String tarClsId, List<RawFileType> rawFiles) {
        for (RawFileType rawFile : rawFiles) {

            Object methodFileRef = rawFile.getMethodFileRef();
            msgs.addAll(checkObjectRef(tarClsId, methodFileRef, info.psidev.psi.pi.mzquantml._1_0.MethodFileType.class));

            List<AbstractParamType> paramGroups = rawFile.getParamGroup();
            checkParamGroup(paramGroups);
        }
    }
//

    static public void checkQuantLayer(List<QuantLayerType> quantLayers) {
        for (QuantLayerType quantLayer : quantLayers) {
            quantLayer.getColumnIndex();

            quantLayer.getDataMatrix();

            quantLayer.getDataType();

            quantLayer.getId();

            ColIndRowValNumMatchRule colIndRowValNumMatchRule = new ColIndRowValNumMatchRule();
            colIndRowValNumMatchRule.check(quantLayer);
            msgs.addAll(colIndRowValNumMatchRule.getMessage());
        }
    }

    static public void checkGlobalQuantLayer(List<GlobalQuantLayerType> globalQuantLayers) {
        for (GlobalQuantLayerType globalQuantLayer : globalQuantLayers) {
            globalQuantLayer.getColumnDefinition();

            globalQuantLayer.getDataMatrix();

            globalQuantLayer.getId();

            ColIndRowValNumMatchRule colIndRowValNumMatchRule = new ColIndRowValNumMatchRule();
            colIndRowValNumMatchRule.check(globalQuantLayer);
            msgs.addAll(colIndRowValNumMatchRule.getMessage());
        }
    }

    static public void checkProteinGroups(List<ProteinGroupType> proteinGroups) {
        proteinGroups.iterator();
    }

    static public void checkProtein(List<ProteinType> proteins) {
        proteins.iterator();
    }

    static public void checkRatio(List<RatioType> ratios) {
        ratios.iterator();
    }

    static public void checkSmallMolecule(List<SmallMoleculeType> smallMolecules) {
        smallMolecules.iterator();
    }

    static public void checkSoftware(List<SoftwareType> softwares) {
        softwares.iterator();
    }

    static public void checkStudyVariable(List<StudyVariableType> studyVariables) {
        studyVariables.iterator();
    }

    static public <T> ArrayList<Message> checkObjectRef(String tarClsId, Object objRef, Class<T> cls) {
        ObjectRefTypeMatchRule objectRefTypeMatchRule = new ObjectRefTypeMatchRule(tarClsId, objRef, cls);
        objectRefTypeMatchRule.check();
        return objectRefTypeMatchRule.getMessage();
    }
}
