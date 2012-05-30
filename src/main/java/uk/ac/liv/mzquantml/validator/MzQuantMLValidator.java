package uk.ac.liv.mzquantml.validator;

import info.psidev.psi.pi.mzquantml._1_0.*;
import info.psidev.psi.pi.mzquantml.io.MzQuantMLUnmarshaller;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EnumMap;
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
    private static EnumMap<AnalysisSummaryElement, Boolean> analysisSummaryMap;
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
            checkFeatureLists(featureLists);
        } else {
            // messages
        }

        if (inputFiles != null) {
            checkInputFiles(inputFiles);
        } else {
            // message there is no inputFiles which is not acceptable
        }

        if (peptideConsensusLists != null) {
            checkPeptideConsensusLists(peptideConsensusLists);
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


    /*
     * All the check*** functions start here. Organized alphabetically
     */
    static public void checkAnalysisSummary(ParamListType analysisSummary) {
        analysisSummaryMap = new EnumMap<AnalysisSummaryElement, Boolean>(AnalysisSummaryElement.class);

        List<AbstractParamType> paramGroups = analysisSummary.getParamGroup();
        for (AbstractParamType param : paramGroups) {
            if (param.getClass().isInstance(info.psidev.psi.pi.mzquantml._1_0.CVParamType.class)) {
                CVParamType cv = (CVParamType) param;
                String name = cv.getName();
                String value = cv.getValue();
                String accession = cv.getAccession();

                String targetClassId = "AnalysisSummary";
                Object cvRef = cv.getCvRef();
                msgs.addAll(checkObjectRef(targetClassId, cvRef,
                        info.psidev.psi.pi.mzquantml._1_0.CvType.class));

                String unitAccession = cv.getUnitAccession();
                String unitCvRef = cv.getUnitCvRef();
                String unitName = cv.getUnitName();

                AnalysisSummaryElement key = AnalysisSummaryElement.getType(name);
                if (key != null) {
                    analysisSummaryMap.put(key, Boolean.valueOf(value));
                }
            }
        }
        String targetClassId = "AnalysisSummary";
        checkParamGroups(targetClassId, paramGroups);
    }

    static public void checkAssayList(AssayListType assayList) {
        List<AssayType> assays = assayList.getAssay();
        String id = assayList.getId();

        checkAssays(assays);

        //AssayLabelRule start here
        AssayLabelRule alr = new AssayLabelRule(at, assayList);
        alr.check();
        msgs.addAll(alr.getMsgs());
    }

    static public void checkAssays(List<AssayType> assays) {
        if (!assays.isEmpty()) {
            for (AssayType assay : assays) {
                String targetClassId = assay.getId();

                List<Object> identificationFileRefs = assay.getIdentificationFileRefs();
                if (!identificationFileRefs.isEmpty()) {
                    for (Object ref : identificationFileRefs) {
                        msgs.addAll(checkObjectRef(targetClassId, ref,
                                info.psidev.psi.pi.mzquantml._1_0.IdentificationFileType.class));
                    }
                }

                List<AbstractParamType> paramGroups = assay.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                Object rawFilesGroupRef = assay.getRawFilesGroupRef();

                if (rawFilesGroupRef != null) {
                    msgs.addAll(checkObjectRef(targetClassId, rawFilesGroupRef,
                            info.psidev.psi.pi.mzquantml._1_0.RawFilesGroupType.class));
                }

            }
        }
    }

    static public void checkAuditCollection(AuditCollectionType auditCollection) {
        List<AbstractContactType> personOrOrganizations = auditCollection.getPersonOrOrganization();

        checkPersonOrOrganizations(personOrOrganizations);
    }

    static public void checkBibliographicReference(List<BibliographicReferenceType> bibliographicReferences) {
        if (!bibliographicReferences.isEmpty()) {
            for (BibliographicReferenceType bibRef : bibliographicReferences) {
                bibRef.getAuthors();
                bibRef.getDoi();
                bibRef.getEditor();
                bibRef.getIssue();
                bibRef.getPages();
                bibRef.getPublication();
                bibRef.getPublisher();
                bibRef.getTitle();
                bibRef.getVolume();
                bibRef.getYear();
            }
        }
    }

    static public void checkColumnDefinition(String targetClassId, ColumnDefinitionType columnDefinition) {
        List<ColumnType> columns = columnDefinition.getColumn();
        if (!columns.isEmpty()) {
            for (ColumnType column : columns) {
                CvParamRefType cvParamRef = column.getDataType();
                CVParamType cvParam = cvParamRef.getCvParam();
                checkCvParam(targetClassId, cvParam);
            }
        }
    }

    static public void checkContactRole(String tarClsId, ContactRoleType contactRole) {
        Object ref = contactRole.getContactRef();
        msgs.addAll(checkObjectRef(tarClsId, ref, info.psidev.psi.pi.mzquantml._1_0.AbstractContactType.class));

        RoleType role = contactRole.getRole();
        CVParamType cvParam = role.getCvParam();
        checkCvParam(tarClsId, cvParam);
    }

    static public void checkCreationDate(XMLGregorianCalendar creationDate) {
    }

    static public void checkCvList(CvListType cvList) {
        List<CvType> cvs = cvList.getCv();

        checkCvs(cvs);
    }

    static public void checkCvParam(String tarClsId, CVParamType cvParam) {
        Object cvRef = cvParam.getCvRef();
        if (cvRef != null) {
            msgs.addAll(checkObjectRef(tarClsId, cvRef, info.psidev.psi.pi.mzquantml._1_0.CvType.class));
        }

        String accession = cvParam.getAccession();
    }

    static public void checkCvs(List<CvType> cvs) {
        if (!cvs.isEmpty()) {
            for (CvType cv : cvs) {
                cv.getFullName();
                cv.getId();
                cv.getUri();
                cv.getVersion();
            }
        }
    }

    static public void checkDataMatrix(DataMatrixType dataMatrix) {
        List<RowType> rows = dataMatrix.getRow();
        if (!rows.isEmpty()) {
            for (RowType row : rows) {
                checkRow(row);
            }
        }
    }

    static public void checkDataProcessingList(DataProcessingListType dataProcessingList) {
        List<DataProcessingType> dataProcessings = dataProcessingList.getDataProcessing();

        checkDataProcessings(dataProcessings);
    }

    static public void checkDataProcessings(List<DataProcessingType> dataProcessings) {
        if (!dataProcessings.isEmpty()) {
            for (DataProcessingType dataProcessing : dataProcessings) {
                List<Object> inputObjectRefs = dataProcessing.getInputObjectRefs();
                List<Object> outputObjectRefs = dataProcessing.getOutputObjectRefs();

//                if (!inputObjectRefs.isEmpty()) {
//                    for (Object ref : inputObjectRefs) {
//                    }
//                }

                String targetClassId = dataProcessing.getId();
                Object softwareRef = dataProcessing.getSoftwareRef();
                msgs.addAll(checkObjectRef(targetClassId, softwareRef, info.psidev.psi.pi.mzquantml._1_0.SoftwareType.class));

                dataProcessing.getOrder();

                List<ProcessingMethodType> processingMethods = dataProcessing.getProcessingMethod();
                checkProcessingMethods(targetClassId, processingMethods);

            }
        }
    }

    static public void checkDBIdentificationRef(String tarClsId, DBIdentificationRefType dBidRef) {
        Object ref = dBidRef.getSearchDatabaseRef();
        checkObjectRef(tarClsId, ref, info.psidev.psi.pi.mzquantml._1_0.SearchDatabaseType.class);
    }

    static public void checkEvidenceRef(String tarClsId, EvidenceRefType evidenceRef) {
        List<Object> assayRefs = evidenceRef.getAssayRefs();
        if (!assayRefs.isEmpty()) {
            for (Object assayRef : assayRefs) {
                String targetClassId = tarClsId;
                msgs.addAll(checkObjectRef(targetClassId, assayRef, info.psidev.psi.pi.mzquantml._1_0.AssayType.class));
            }
        }

        Object ftRef = evidenceRef.getFeatureRef();
        String targetClassId = tarClsId;
        msgs.addAll(checkObjectRef(targetClassId, ftRef, info.psidev.psi.pi.mzquantml._1_0.FeatureType.class));

        Object idFileRef = evidenceRef.getIdentificationFileRef();
        msgs.addAll(checkObjectRef(targetClassId, idFileRef, info.psidev.psi.pi.mzquantml._1_0.IdentificationFileType.class));

        evidenceRef.getIdRefs();
    }

    /*
     * validate FeatureList
     */
    static public void checkFeatureLists(List<FeatureListType> featureLists) {
        if (!featureLists.isEmpty()) {
            for (FeatureListType featureList : featureLists) {
                List<FeatureType> features = featureList.getFeature();
                checkFeatures(features);

                List<GlobalQuantLayerType> featureQuantLayers = featureList.getFeatureQuantLayer();
                checkGlobalQuantLayers(featureQuantLayers);

                String id = featureList.getId();

                List<QuantLayerType> ms2AssayQuantLayers = featureList.getMS2AssayQuantLayer();
                checkQuantLayers(ms2AssayQuantLayers);

                List<QuantLayerType> ms2RatioQuantLayers = featureList.getMS2RatioQuantLayer();
                checkQuantLayers(ms2RatioQuantLayers);

                List<QuantLayerType> ms2StudyVariableQuantLayers = featureList.getMS2StudyVariableQuantLayer();
                checkQuantLayers(ms2StudyVariableQuantLayers);

                String targetClassId = id;

                List<AbstractParamType> paramGroups = featureList.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                Object rawFileGroupRef = featureList.getRawFilesGroupRef();
                msgs.addAll(checkObjectRef(targetClassId, rawFileGroupRef, info.psidev.psi.pi.mzquantml._1_0.RawFilesGroupType.class));
            }
        }
    }

    static public void checkFeatures(List<FeatureType> features) {
        if (!features.isEmpty()) {
            for (FeatureType feature : features) {
                feature.getChromatogramRefs();
                feature.getSpectrumRefs();

                String targetClassId = feature.getId();
                List<AbstractParamType> paramGroups = feature.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);
            }
        }
    }

    static public void checkFileFormat(String tarClsId, FileFormatType fileFormat) {
        if (fileFormat != null) {
            CVParamType cvParam = fileFormat.getCvParam();
            checkCvParam(tarClsId, cvParam);
        }
    }

    static public void checkGlobalQuantLayers(List<GlobalQuantLayerType> globalQuantLayers) {
        if (!globalQuantLayers.isEmpty()) {
            for (GlobalQuantLayerType globalQuantLayer : globalQuantLayers) {
                String id = globalQuantLayer.getId();

                ColumnDefinitionType columnDefinition = globalQuantLayer.getColumnDefinition();
                checkColumnDefinition(id, columnDefinition);

                DataMatrixType dataMatrix = globalQuantLayer.getDataMatrix();
                checkDataMatrix(dataMatrix);

                ColIndRowValNumMatchRule colIndRowValNumMatchRule = new ColIndRowValNumMatchRule();
                colIndRowValNumMatchRule.check(globalQuantLayer);
                msgs.addAll(colIndRowValNumMatchRule.getMessage());
            }
        }
    }

    static public void checkIdentificationFile(IdentificationFileType identificationFile) {
        List<AbstractParamType> paramGroups = identificationFile.getParamGroup();
        String targetClassId = identificationFile.getId();
        checkParamGroups(targetClassId, paramGroups);

        Object searchDatabaseRef = identificationFile.getSearchDatabaseRef();
        checkObjectRef(targetClassId, searchDatabaseRef, info.psidev.psi.pi.mzquantml._1_0.SearchDatabaseType.class);
    }

    static public void checkIdentificationRefs(String tarClsId, List<IdentificationRefType> identificationRefs) {
        if (!identificationRefs.isEmpty()) {
            for (IdentificationRefType idRef : identificationRefs) {
                Object idFileRef = idRef.getIdentificationFileRef();
                msgs.addAll(checkObjectRef(tarClsId, idFileRef, info.psidev.psi.pi.mzquantml._1_0.IdentificationFileType.class));
            }
        }
    }

    static public void checkInputFiles(InputFilesType inputFiles) {
        /*
         * RawFileRule start here
         */
        RawFileRule rfl = new RawFileRule(at, inputFiles);
        rfl.check();
        msgs.addAll(rfl.getMsgs());

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
                    FileFormatType format = methodFile.getFileFormat();
                    String targetClassId = methodFile.getId();
                    checkFileFormat(targetClassId, format);
                }
            }
        }

        List<RawFilesGroupType> rawFilesGroups = inputFiles.getRawFilesGroup();
        checkRawFilesGroups(rawFilesGroups);

        List<SearchDatabaseType> searchDatabases = inputFiles.getSearchDatabase();
        checkSearchDatabases(searchDatabases);

        List<SourceFileType> sourceFiles = inputFiles.getSourceFile();
        checkSourceFiles(sourceFiles);
    }

    static public void checkModification(String tarClsId, ModificationType modification) {
        List<CVParamType> cvParams = modification.getCvParam();
        if (!cvParams.isEmpty()) {
            for (CVParamType cvParam : cvParams) {
                checkCvParam(tarClsId, cvParam);
            }
        }

        List<Object> ftRefs = modification.getFeatureRefs();
        if (!ftRefs.isEmpty()) {
            for (Object ref : ftRefs) {
                msgs.addAll(checkObjectRef(tarClsId, ref, info.psidev.psi.pi.mzquantml._1_0.FeatureType.class));
            }
        }
    }

    static public <T> ArrayList<Message> checkObjectRef(String tarClsId, Object objRef, Class<T> cls) {
        ObjectRefTypeMatchRule objectRefTypeMatchRule = new ObjectRefTypeMatchRule(tarClsId, objRef, cls);
        objectRefTypeMatchRule.check();
        return objectRefTypeMatchRule.getMessage();
    }

    static public void checkOrganization(OrganizationType organization) {
        ParentOrganizationType parentOrg = organization.getParentOrganization();
        String targetClassId = organization.getId();
        Object parentOrgRef = parentOrg.getOrganizationRef();
        msgs.addAll(checkObjectRef(targetClassId, parentOrgRef, info.psidev.psi.pi.mzquantml._1_0.ParentOrganizationType.class));
    }

    static public void checkParamGroups(String tarClsId, List<AbstractParamType> paramGroups) {
        for (AbstractParamType param : paramGroups) {
            if (param.getClass().isInstance(info.psidev.psi.pi.mzquantml._1_0.CVParamType.class)) {
                CVParamType cv = (CVParamType) param;
                cv.getAccession();
                Object cvRef = cv.getCvRef();
                msgs.addAll(checkObjectRef(tarClsId, cvRef, info.psidev.psi.pi.mzquantml._1_0.CvType.class));
            } else if (param.getClass().isInstance(info.psidev.psi.pi.mzquantml._1_0.UserParamType.class)) {
                UserParamType userParam = (UserParamType) param;
                userParam.getType();
            }

        }
    }

    static public void checkPeptideConsensusList(PeptideConsensusListType peptideConsensusList) {
        if (peptideConsensusList != null) {
            List<QuantLayerType> assayQuantLayers = peptideConsensusList.getAssayQuantLayer();
            List<GlobalQuantLayerType> globalQuantLayers = peptideConsensusList.getGlobalQuantLayer();
            String id = peptideConsensusList.getId();
            List<AbstractParamType> paramGroups = peptideConsensusList.getParamGroup();
            List<PeptideConsensusType> peptideConsensuses = peptideConsensusList.getPeptideConsensus();
            List<QuantLayerType> ratioQuantLayers = peptideConsensusList.getRatioQuantLayer();
            List<QuantLayerType> studyVariableQuantLayers = peptideConsensusList.getStudyVariableQuantLayer();

            checkQuantLayers(assayQuantLayers);
            checkGlobalQuantLayers(globalQuantLayers);
            String targetClassId = id;
            checkParamGroups(targetClassId, paramGroups);
            checkPeptideConsensuses(peptideConsensuses);
            checkQuantLayers(ratioQuantLayers);
            checkQuantLayers(studyVariableQuantLayers);
        }
    }

    static public void checkPeptideConsensus(PeptideConsensusType peptideConsensus) {
        if (peptideConsensus != null) {
            List<String> charges = peptideConsensus.getCharge();

            String id = peptideConsensus.getId();
            String targetClassId = id;
            List<ModificationType> modifications = peptideConsensus.getModification();
            if (!modifications.isEmpty()) {
                for (ModificationType modification : modifications) {
                    checkModification(targetClassId, modification);
                }
            }

            List<AbstractParamType> paramGroups = peptideConsensus.getParamGroup();
            checkParamGroups(targetClassId, paramGroups);

            String sequence = peptideConsensus.getPeptideSequence();

            Object searchDBRef = peptideConsensus.getSearchDatabaseRef();
            msgs.addAll(checkObjectRef(targetClassId, searchDBRef, info.psidev.psi.pi.mzquantml._1_0.SearchDatabaseType.class));

            List<EvidenceRefType> evidenceRefs = peptideConsensus.getEvidenceRef();
            if (!evidenceRefs.isEmpty()) {
                for (EvidenceRefType evidenceRef : evidenceRefs) {
                    checkEvidenceRef(targetClassId, evidenceRef);
                }
            }
        }
    }

    static public void checkPeptideConsensuses(List<PeptideConsensusType> peptideConsensuses) {
        if (!peptideConsensuses.isEmpty()) {
            for (PeptideConsensusType peptideConsensus : peptideConsensuses) {
                checkPeptideConsensus(peptideConsensus);
            }
        }
    }

    /*
     * validate PeptideConsensusList
     */
    static public void checkPeptideConsensusLists(List<PeptideConsensusListType> peptideConsensusLists) {
        if (!peptideConsensusLists.isEmpty()) {
            /*
             * FinalResultRule start here
             */
            FinalResultRule frr = new FinalResultRule(peptideConsensusLists);
            frr.check();
            msgs.addAll(frr.getMsgs());

            for (PeptideConsensusListType peptideConsensusList : peptideConsensusLists) {
                checkPeptideConsensusList(peptideConsensusList);
            }
        }

    }

    static public void checkPerson(PersonType person) {
        List<AffiliationType> affiliations = person.getAffiliation();
        String id = person.getId();
        String targetClassId = id;
        if (!affiliations.isEmpty()) {
            for (AffiliationType affiliation : affiliations) {
                Object orgRef = affiliation.getOrganizationRef();
                msgs.addAll(checkObjectRef(targetClassId, orgRef, info.psidev.psi.pi.mzquantml._1_0.OrganizationType.class));
            }
        }
    }

    static public void checkPersonOrOrganizations(List<AbstractContactType> personOrOrganizations) {
        if (!personOrOrganizations.isEmpty()) {
            for (AbstractContactType personOrOrganization : personOrOrganizations) {
                String targetClassId = personOrOrganization.getId();
                List<AbstractParamType> paramGroups = personOrOrganization.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                if (personOrOrganization.getClass().isInstance(info.psidev.psi.pi.mzquantml._1_0.PersonType.class)) {
                    PersonType person = (PersonType) personOrOrganization;
                    checkPerson(person);
                }

                if (personOrOrganization.getClass().isInstance(info.psidev.psi.pi.mzquantml._1_0.OrganizationType.class)) {
                    OrganizationType org = (OrganizationType) personOrOrganization;
                    checkOrganization(org);
                }

            }
        }
    }

    static public void checkProcessingMethods(String tarClsId, List<ProcessingMethodType> processingMethods) {
        if (!processingMethods.isEmpty()) {
            for (ProcessingMethodType processingMethod : processingMethods) {
                processingMethod.getOrder();

                List<AbstractParamType> paramGroups = processingMethod.getParamGroup();
                checkParamGroups(tarClsId, paramGroups);
            }
        }
    }

    static public void checkProtein(ProteinType protein) {
        String id = protein.getId();
        String targetClassId = id;
        List<IdentificationRefType> identificationRefs = protein.getIdentificationRef();
        checkIdentificationRefs(targetClassId, identificationRefs);

        List<AbstractParamType> paramGroups = protein.getParamGroup();
        checkParamGroups(targetClassId, paramGroups);

        List<Object> pepRefs = protein.getPeptideConsensusRefs();
        if (!pepRefs.isEmpty()) {
            for (Object ref : pepRefs) {
                msgs.addAll(checkObjectRef(targetClassId, ref, info.psidev.psi.pi.mzquantml._1_0.PeptideConsensusType.class));
            }
        }

        Object searchDBRef = protein.getSearchDatabaseRef();
        msgs.addAll(checkObjectRef(targetClassId, searchDBRef, info.psidev.psi.pi.mzquantml._1_0.SearchDatabaseType.class));
    }

    static public void checkProteinGroup(ProteinGroupType proteinGroup) {
        String id = proteinGroup.getId();
        String targetClassId = id;

        List<IdentificationRefType> identificationRefs = proteinGroup.getIdentificationRef();
        checkIdentificationRefs(targetClassId, identificationRefs);

        List<AbstractParamType> paramGroups = proteinGroup.getParamGroup();
        checkParamGroups(targetClassId, paramGroups);

        List<Object> protRefs = proteinGroup.getProteinRefs();
        if (!protRefs.isEmpty()) {
            for (Object ref : protRefs) {
                msgs.addAll(checkObjectRef(targetClassId, ref, info.psidev.psi.pi.mzquantml._1_0.ProteinType.class));
            }
        }

        Object searchDBRef = proteinGroup.getSearchDatabaseRef();
        msgs.addAll(checkObjectRef(targetClassId, searchDBRef, info.psidev.psi.pi.mzquantml._1_0.SearchDatabaseType.class));
    }

    static public void checkProteinGroupList(ProteinGroupListType proteinGroupList) {
        if (proteinGroupList != null) {
            List<QuantLayerType> assayQuantLayers = proteinGroupList.getAssayQuantLayer();
            List<GlobalQuantLayerType> globalQuantLayers = proteinGroupList.getGlobalQuantLayer();
            String id = proteinGroupList.getId();
            List<AbstractParamType> paramGroups = proteinGroupList.getParamGroup();
            List<ProteinGroupType> proteinGroups = proteinGroupList.getProteinGroup();
            List<QuantLayerType> ratioQuantLayers = proteinGroupList.getRatioQuantLayer();
            List<QuantLayerType> studyVariableQuantLayers = proteinGroupList.getStudyVariableQuantLayer();

            checkQuantLayers(assayQuantLayers);
            checkGlobalQuantLayers(globalQuantLayers);
            String targetClassId = id;
            checkParamGroups(targetClassId, paramGroups);
            checkProteinGroups(proteinGroups);
            checkQuantLayers(ratioQuantLayers);
            checkQuantLayers(studyVariableQuantLayers);
        }
    }

    static public void checkProteinGroups(List<ProteinGroupType> proteinGroups) {
        if (!proteinGroups.isEmpty()) {
            for (ProteinGroupType proteinGroup : proteinGroups) {
                checkProteinGroup(proteinGroup);
            }
        }
    }

    /*
     * validate ProteinList
     */
    static public void checkProteinList(ProteinListType proteinList) {
        if (proteinList != null) {
            List<QuantLayerType> assayQuantLayers = proteinList.getAssayQuantLayer();
            List<GlobalQuantLayerType> globalQuantLayers = proteinList.getGlobalQuantLayer();
            String id = proteinList.getId();
            List<AbstractParamType> paramGroups = proteinList.getParamGroup();
            List<ProteinType> proteins = proteinList.getProtein();
            List<QuantLayerType> ratioQuantLayers = proteinList.getRatioQuantLayer();
            List<QuantLayerType> studyVariableQuantLayers = proteinList.getStudyVariableQuantLayer();

            checkQuantLayers(assayQuantLayers);
            checkGlobalQuantLayers(globalQuantLayers);
            String targetClassId = id;
            checkParamGroups(targetClassId, paramGroups);
            checkProteins(proteins);
            checkQuantLayers(ratioQuantLayers);
            checkQuantLayers(studyVariableQuantLayers);
        }
    }

    static public void checkProteins(List<ProteinType> proteins) {
        if (!proteins.isEmpty()) {
            for (ProteinType protein : proteins) {
                checkProtein(protein);
            }
        }
    }

    static public void checkProvider(ProviderType provider) {
        Object analysisSoftwareRef = provider.getAnalysisSoftwareRef();
        String targetClassId = provider.getId();
        msgs.addAll(checkObjectRef(targetClassId, analysisSoftwareRef, info.psidev.psi.pi.mzquantml._1_0.SoftwareType.class));

        ContactRoleType contactRole = provider.getContactRole();
        checkContactRole(targetClassId, contactRole);

        String name = provider.getName();
    }

    static public void checkQuantLayers(List<QuantLayerType> quantLayers) {
        if (!quantLayers.isEmpty()) {
            for (QuantLayerType quantLayer : quantLayers) {

                //TODO: need to figure out what type does columnIndex refer to 
                List<Object> columnIndex = quantLayer.getColumnIndex();

                String id = quantLayer.getId();
                String targetClassId = id;

                DataMatrixType dataMatrix = quantLayer.getDataMatrix();
                checkDataMatrix(dataMatrix);

                CvParamRefType dataType = quantLayer.getDataType();
                CVParamType cvParam = dataType.getCvParam();
                checkCvParam(targetClassId, cvParam);

                ColIndRowValNumMatchRule colIndRowValNumMatchRule = new ColIndRowValNumMatchRule();
                colIndRowValNumMatchRule.check(quantLayer);
                msgs.addAll(colIndRowValNumMatchRule.getMessage());
            }
        }
    }

    /*
     * Validate RatioList
     */
    static public void checkRatio(RatioType ratio) {
        String id = ratio.getId();
        String targetClassId = id;
        ParamListType ratioCalParamList = ratio.getRatioCalculation();
        if (ratioCalParamList != null) {
            List<AbstractParamType> paramGroups = ratioCalParamList.getParamGroup();
            checkParamGroups(targetClassId, paramGroups);
        }
    }

    static public void checkRatioList(RatioListType ratioList) {
        if (ratioList != null) {
            List<RatioType> ratios = ratioList.getRatio();
            checkRatios(ratios);

            NumeratorDenominatorRule numeratorDenominatorRule = new NumeratorDenominatorRule(ratioList);
            numeratorDenominatorRule.check();
            msgs.addAll(numeratorDenominatorRule.getMsgs());
        }
    }

    static public void checkRatios(List<RatioType> ratios) {
        if (!ratios.isEmpty()) {
            for (RatioType ratio : ratios) {
                checkRatio(ratio);
            }
        }
    }

    static public void checkRawFiles(List<RawFileType> rawFiles) {
        if (!rawFiles.isEmpty()) {
            for (RawFileType rawFile : rawFiles) {
                String id = rawFile.getId();
                String targetClassId = id;

                Object methodFileRef = rawFile.getMethodFileRef();
                msgs.addAll(checkObjectRef(targetClassId, methodFileRef, info.psidev.psi.pi.mzquantml._1_0.MethodFileType.class));

                List<AbstractParamType> paramGroups = rawFile.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                FileFormatType format = rawFile.getFileFormat();
                checkFileFormat(targetClassId, format);
            }
        }
    }

    static public void checkRawFilesGroups(List<RawFilesGroupType> rawFilesGroups) {
        if (!rawFilesGroups.isEmpty()) {
            for (RawFilesGroupType rawFilesGroup : rawFilesGroups) {
                String targetClassId = rawFilesGroup.getId();
                List<AbstractParamType> paramGroups = rawFilesGroup.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                List<RawFileType> rawFiles = rawFilesGroup.getRawFile();
                checkRawFiles(rawFiles);
            }
        }
    }

    static public void checkRow(RowType row) {
        row.getObjectRef();
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

                FileFormatType format = searchDatabase.getFileFormat();
                checkFileFormat(targetClassId, format);

            }
        }
    }

    static public void checkSmallMolecule(SmallMoleculeType smallMolecule) {
        String id = smallMolecule.getId();
        String targetClassId = id;

        List<DBIdentificationRefType> dBidentificationRefs = smallMolecule.getDBIdentificationRef();
        if (!dBidentificationRefs.isEmpty()) {
            for (DBIdentificationRefType dBidRef : dBidentificationRefs) {
                checkDBIdentificationRef(targetClassId, dBidRef);
            }
        }

        List<Object> ftRefs = smallMolecule.getFeatureRefs();
        if (!ftRefs.isEmpty()) {
            for (Object ref : ftRefs) {
                msgs.addAll(checkObjectRef(targetClassId, ref, info.psidev.psi.pi.mzquantml._1_0.FeatureType.class));
            }
        }

        List<AbstractParamType> paramGroups = smallMolecule.getParamGroup();
        checkParamGroups(targetClassId, paramGroups);

        List<SmallMolModificationType> smallMolModifications = smallMolecule.getModification();
        checkSmallMolModifications(targetClassId, smallMolModifications);

    }

    static public void checkSmallMolModifications(String tarClsId, List<SmallMolModificationType> smallMolModifications) {
        if (!smallMolModifications.isEmpty()) {
            for (SmallMolModificationType smallMolMod : smallMolModifications) {
                List<CVParamType> cvParams = smallMolMod.getCvParam();
                if (!cvParams.isEmpty()) {
                    for (CVParamType cvParam : cvParams) {
                        checkCvParam(tarClsId, cvParam);
                    }
                }
            }
        }
    }

    static public void checkSmallMolecules(List<SmallMoleculeType> smallMolecules) {
        if (!smallMolecules.isEmpty()) {
            for (SmallMoleculeType smallMol : smallMolecules) {
                checkSmallMolecule(smallMol);
            }
        }
    }

    static public void checkSmallMoleculeList(SmallMoleculeListType smallMoleculeList) {
        List<QuantLayerType> assayQuantLayers = smallMoleculeList.getAssayQuantLayer();
        List<GlobalQuantLayerType> globalQuantLayers = smallMoleculeList.getGlobalQuantLayer();
        String id = smallMoleculeList.getId();
        List<AbstractParamType> paramGroups = smallMoleculeList.getParamGroup();
        List<QuantLayerType> ratioQuantLayers = smallMoleculeList.getRatioQuantLayer();
        List<SmallMoleculeType> smallMolecules = smallMoleculeList.getSmallMolecule();
        List<QuantLayerType> studyVariableQuantLayers = smallMoleculeList.getStudyVariableQuantLayer();

        checkQuantLayers(assayQuantLayers);
        checkGlobalQuantLayers(globalQuantLayers);
        String targetClassId = id;
        checkParamGroups(targetClassId, paramGroups);
        checkSmallMolecules(smallMolecules);
        checkQuantLayers(ratioQuantLayers);
        checkQuantLayers(studyVariableQuantLayers);
    }

    static public void checkSoftwares(List<SoftwareType> softwares) {
        if (!softwares.isEmpty()) {
            for (SoftwareType software : softwares) {
                String id = software.getId();
                String targetClassId = id;

                List<AbstractParamType> paramGroups = software.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);
            }
        }
    }

    static public void checkSoftwareList(SoftwareListType softwareList) {
        if (softwareList != null) {
            List<SoftwareType> softwares = softwareList.getSoftware();
            checkSoftwares(softwares);
        }
    }

    static public void checkSourceFiles(List<SourceFileType> sourceFiles) {
        if (!sourceFiles.isEmpty()) {
            for (SourceFileType sourceFile : sourceFiles) {
                String targetClassId = sourceFile.getId();
                FileFormatType format = sourceFile.getFileFormat();
                checkFileFormat(targetClassId, format);
            }
        }
    }

    static public void checkStudyVariableList(StudyVariableListType studyVariableList) {
        if (studyVariableList != null) {
            List<StudyVariableType> studyVariables = studyVariableList.getStudyVariable();
            checkStudyVariables(studyVariables);
        }
    }

    static public void checkStudyVariables(List<StudyVariableType> studyVariables) {
        if (!studyVariables.isEmpty()) {
            for (StudyVariableType studyVariable : studyVariables) {
                String id = studyVariable.getId();
                String targetClassId = id;

                List<Object> assayRefs = studyVariable.getAssayRefs();
                if (!assayRefs.isEmpty()) {
                    for (Object assayRef : assayRefs) {
                        msgs.addAll(checkObjectRef(targetClassId, assayRef, info.psidev.psi.pi.mzquantml._1_0.AssayType.class));
                    }
                }

                List<AbstractParamType> paramGroups = studyVariable.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);
            }
        }
    }

    static public void checkVersion(String version) {
    }
}
