package uk.ac.liv.mzquantml.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import org.apache.log4j.Level;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.liv.jmzqml.model.mzqml.*;
import uk.ac.liv.jmzqml.xml.io.MzQuantMLUnmarshaller;
import uk.ac.liv.mzquantml.validator.cvmapping.CvValidator;
import uk.ac.liv.mzquantml.validator.rules.general.*;
import uk.ac.liv.mzquantml.validator.utils.AnalysisSummaryElement;
import uk.ac.liv.mzquantml.validator.utils.AnalysisType;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 * @time 10:28:09 14-Mar-2012
 * @institution University of Liverpool
 */
public class MzQuantMLValidator {

    private static AnalysisType at = new AnalysisType();
    private static EnumMap<AnalysisSummaryElement, Boolean> analysisSummaryMap;
    private static List<Message> msgs = new ArrayList<Message>();
    private String fileName;
    private boolean schemaValidating;
    private String schemaFn;

    public MzQuantMLValidator(String fileName, boolean schemaValidating,
            String schemaFn) {
        this.fileName = fileName;
        this.schemaValidating = schemaValidating;
        this.schemaFn = schemaFn;
    }

    /**
     * @param args the command line arguments
     */
    public List<Message> validate(String fileName, boolean schemaValidating,
            String schemaFn) throws FileNotFoundException {
        // TODO code application logic here
        msgs.clear();
        msgs.add(new Message("Starting validation process......", Level.INFO));
        msgs.add(new Message("Loading MzQuantML file......", Level.INFO));

        MzQuantMLUnmarshaller unmarshaller = new MzQuantMLUnmarshaller(fileName, schemaValidating, schemaFn);
        MzQuantML mzq = (MzQuantML) unmarshaller.unmarshall();

        if (!unmarshaller.getExceptionalMessages().isEmpty()) {
            msgs.add(new Message(unmarshaller.getExceptionalMessages()));
            msgs.add(new Message("Semantic validation processing will not perform as this file is not schema valid!", Level.ERROR));
        } else {
            /*
             * get all mzQuantML elements
             */
            ParamList analysisSummary = mzq.getAnalysisSummary();
            AssayList assayList = mzq.getAssayList();
            AuditCollection auditCollection = mzq.getAuditCollection();
            List<BibliographicReference> bibliographicReferences = mzq.getBibliographicReference();
            Calendar creationDate = mzq.getCreationDate();
            CvList cvList = mzq.getCvList();
            DataProcessingList dataProcessingList = mzq.getDataProcessingList();
            List<FeatureList> featureLists = mzq.getFeatureList();
            InputFiles inputFiles = mzq.getInputFiles();
            List<PeptideConsensusList> peptideConsensusLists = mzq.getPeptideConsensusList();
            ProteinGroupList proteinGroupList = mzq.getProteinGroupList();
            ProteinList proteinList = mzq.getProteinList();
            Provider provider = mzq.getProvider();
            RatioList ratioList = mzq.getRatioList();
            SmallMoleculeList smallMoleculeList = mzq.getSmallMoleculeList();
            SoftwareList softwareList = mzq.getSoftwareList();
            StudyVariableList studyVariableList = mzq.getStudyVariableList();
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

            // start validate Cv Mapping rule
            InputStream ontology = getOntologiesFileInputStream();
            InputStream mappingRule = getGeneralMappingRuleInputStream();
            CvValidator cvValidator;
            try {
                cvValidator = new CvValidator(ontology, mappingRule);
                final Collection<ValidatorMessage> validationResult = cvValidator.startValidation(new File(fileName));

                for (ValidatorMessage vMsg : validationResult) {
                    msgs.add(new Message(vMsg.getMessage()));
                }
            } catch (ValidatorException ex) {
                Logger.getLogger(MzQuantMLValidator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (OntologyLoaderException ex) {
                Logger.getLogger(MzQuantMLValidator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }

            /*
             * final output
             */
            System.out.println("MzQuantML validation process is finished");
            msgs.add(new Message("MzQuantML validation process is finished", Level.INFO));
        }

        return msgs;
    }


    /*
     * All the check*** functions start here. Organized alphabetically
     */
    static public void checkAnalysisSummary(ParamList analysisSummary) {
        analysisSummaryMap = new EnumMap<AnalysisSummaryElement, Boolean>(AnalysisSummaryElement.class);

        List<AbstractParam> paramGroups = analysisSummary.getParamGroup();
        for (AbstractParam param : paramGroups) {
            if (param.getClass().isInstance(uk.ac.liv.jmzqml.model.mzqml.CvParam.class)) {
                CvParam cv = (CvParam) param;
                String name = cv.getName();
                String value = cv.getValue();
                String accession = cv.getAccession();

                String targetClassId = "AnalysisSummary";
                Object cvRef = cv.getCvRef();
                msgs.addAll(checkObjectRef(targetClassId, cvRef,
                        uk.ac.liv.jmzqml.model.mzqml.Cv.class));

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

    static public void checkAssayList(AssayList assayList) {
        List<Assay> assays = assayList.getAssay();
        String id = assayList.getId();

        checkAssays(assays);

        //AssayLabelRule start here
        AssayLabelRule alr = new AssayLabelRule(at, assayList);
        alr.check();
        msgs.addAll(alr.getMsgs());
    }

    static public void checkAssays(List<Assay> assays) {
        if (!assays.isEmpty()) {
            for (Assay assay : assays) {
                String targetClassId = assay.getId();

                List<Object> identificationFileRefs = assay.getIdentificationFileRefs();
                if (!identificationFileRefs.isEmpty()) {
                    for (Object ref : identificationFileRefs) {
                        msgs.addAll(checkObjectRef(targetClassId, ref,
                                uk.ac.liv.jmzqml.model.mzqml.IdentificationFile.class));
                    }
                }

                List<AbstractParam> paramGroups = assay.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                Object rawFilesGroupRef = assay.getRawFilesGroupRef();

                if (rawFilesGroupRef != null) {
                    msgs.addAll(checkObjectRef(targetClassId, rawFilesGroupRef,
                            uk.ac.liv.jmzqml.model.mzqml.RawFilesGroup.class));
                }

            }
        }
    }

    static public void checkAuditCollection(AuditCollection auditCollection) {
        List<AbstractContact> personOrOrganizations = auditCollection.getPersonOrOrganization();

        checkPersonOrOrganizations(personOrOrganizations);
    }

    static public void checkBibliographicReference(
            List<BibliographicReference> bibliographicReferences) {
        if (!bibliographicReferences.isEmpty()) {
            for (BibliographicReference bibRef : bibliographicReferences) {
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

    static public void checkColumnDefinition(String targetClassId,
            ColumnDefinition columnDefinition) {
        List<Column> columns = columnDefinition.getColumn();
        if (!columns.isEmpty()) {
            for (Column column : columns) {
                CvParamRef cvParamRef = column.getDataType();
                CvParam cvParam = cvParamRef.getCvParam();
                checkCvParam(targetClassId, cvParam);
            }
        }
    }

    static public void checkContactRole(String tarClsId, ContactRole contactRole) {
        Object ref = contactRole.getContactRef();
        msgs.addAll(checkObjectRef(tarClsId, ref, uk.ac.liv.jmzqml.model.mzqml.AbstractContact.class));

        Role role = contactRole.getRole();
        CvParam cvParam = role.getCvParam();
        checkCvParam(tarClsId, cvParam);
    }

    static public void checkCreationDate(Calendar creationDate1) {
    }

    static public void checkCvList(CvList cvList) {
        List<Cv> cvs = cvList.getCv();

        checkCvs(cvs);
    }

    static public void checkCvParam(String tarClsId, CvParam cvParam) {
        Object cvRef = cvParam.getCvRef();
        if (cvRef != null) {
            msgs.addAll(checkObjectRef(tarClsId, cvRef, uk.ac.liv.jmzqml.model.mzqml.Cv.class));
        }

        String accession = cvParam.getAccession();
    }

    static public void checkCvs(List<Cv> cvs) {
        if (!cvs.isEmpty()) {
            for (Cv cv : cvs) {
                cv.getFullName();
                cv.getId();
                cv.getUri();
                cv.getVersion();
            }
        }
    }

    static public void checkDataMatrix(DataMatrix dataMatrix) {
        List<Row> rows = dataMatrix.getRow();
        if (!rows.isEmpty()) {
            for (Row row : rows) {
                checkRow(row);
            }
        }
    }

    static public void checkDataProcessingList(
            DataProcessingList dataProcessingList) {
        List<DataProcessing> dataProcessings = dataProcessingList.getDataProcessing();

        checkDataProcessings(dataProcessings);
    }

    static public void checkDataProcessings(List<DataProcessing> dataProcessings) {
        if (!dataProcessings.isEmpty()) {
            for (DataProcessing dataProcessing : dataProcessings) {
                String targetClassId = dataProcessing.getId();

                List<Object> inputObjectRefs = dataProcessing.getInputObjectRefs();
                List<Object> outputObjectRefs = dataProcessing.getOutputObjectRefs();

                InOutputObjectRefsRule inputRule = new InOutputObjectRefsRule(inputObjectRefs, targetClassId);
                inputRule.check();
                msgs.addAll(inputRule.getMsgs());

                InOutputObjectRefsRule outputRule = new InOutputObjectRefsRule(outputObjectRefs, targetClassId);
                outputRule.check();
                msgs.addAll(outputRule.getMsgs());

                Object softwareRef = dataProcessing.getSoftwareRef();
                msgs.addAll(checkObjectRef(targetClassId, softwareRef, uk.ac.liv.jmzqml.model.mzqml.Software.class));

                dataProcessing.getOrder();

                List<ProcessingMethod> processingMethods = dataProcessing.getProcessingMethod();
                checkProcessingMethods(targetClassId, processingMethods);

            }
        }
    }

    static public void checkDBIdentificationRef(String tarClsId,
            DBIdentificationRef dBidRef) {
        Object ref = dBidRef.getSearchDatabaseRef();
        checkObjectRef(tarClsId, ref, uk.ac.liv.jmzqml.model.mzqml.SearchDatabase.class);
    }

    static public void checkEvidenceRef(String tarClsId, EvidenceRef evidenceRef) {
        List<Object> assayRefs = evidenceRef.getAssayRefs();
        if (!assayRefs.isEmpty()) {
            for (Object assayRef : assayRefs) {
                String targetClassId = tarClsId;
                msgs.addAll(checkObjectRef(targetClassId, assayRef, uk.ac.liv.jmzqml.model.mzqml.Assay.class));
            }
        }

        Object ftRef = evidenceRef.getFeatureRef();
        String targetClassId = tarClsId;
        msgs.addAll(checkObjectRef(targetClassId, ftRef, uk.ac.liv.jmzqml.model.mzqml.Feature.class));

        Object idFileRef = evidenceRef.getIdentificationFileRef();
        msgs.addAll(checkObjectRef(targetClassId, idFileRef, uk.ac.liv.jmzqml.model.mzqml.IdentificationFile.class));

        evidenceRef.getIdRefs();
    }

    /*
     * validate FeatureList
     */
    static public void checkFeatureLists(List<FeatureList> featureLists) {
        if (!featureLists.isEmpty()) {
            for (FeatureList featureList : featureLists) {
                List<Feature> features = featureList.getFeature();
                checkFeatures(features);

                List<GlobalQuantLayer> featureQuantLayers = featureList.getFeatureQuantLayer();
                checkGlobalQuantLayers(featureQuantLayers);

                String id = featureList.getId();

                List<QuantLayer> ms2AssayQuantLayers = featureList.getMS2AssayQuantLayer();
                checkQuantLayers(ms2AssayQuantLayers);

                List<QuantLayer> ms2RatioQuantLayers = featureList.getMS2RatioQuantLayer();
                checkQuantLayers(ms2RatioQuantLayers);

                List<QuantLayer> ms2StudyVariableQuantLayers = featureList.getMS2StudyVariableQuantLayer();
                checkQuantLayers(ms2StudyVariableQuantLayers);

                String targetClassId = id;

                List<AbstractParam> paramGroups = featureList.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                Object rawFileGroupRef = featureList.getRawFilesGroupRef();
                msgs.addAll(checkObjectRef(targetClassId, rawFileGroupRef, uk.ac.liv.jmzqml.model.mzqml.RawFilesGroup.class));
            }
        }
    }

    static public void checkFeatures(List<Feature> features) {
        if (!features.isEmpty()) {
            for (Feature feature : features) {
                feature.getChromatogramRefs();
                feature.getSpectrumRefs();

                String targetClassId = feature.getId();
                List<AbstractParam> paramGroups = feature.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);
            }
        }
    }

    static public void checkFileFormat(String tarClsId, FileFormat fileFormat) {
        if (fileFormat != null) {
            CvParam cvParam = fileFormat.getCvParam();
            checkCvParam(tarClsId, cvParam);
        }
    }

    static public void checkGlobalQuantLayers(
            List<GlobalQuantLayer> globalQuantLayers) {
        if (!globalQuantLayers.isEmpty()) {
            for (GlobalQuantLayer globalQuantLayer : globalQuantLayers) {
                String id = globalQuantLayer.getId();

                ColumnDefinition columnDefinition = globalQuantLayer.getColumnDefinition();
                checkColumnDefinition(id, columnDefinition);

                DataMatrix dataMatrix = globalQuantLayer.getDataMatrix();
                checkDataMatrix(dataMatrix);

                ColIndRowValNumMatchRule colIndRowValNumMatchRule = new ColIndRowValNumMatchRule();
                colIndRowValNumMatchRule.check(globalQuantLayer);
                msgs.addAll(colIndRowValNumMatchRule.getMessage());
            }
        }
    }

    static public void checkIdentificationFile(
            IdentificationFile identificationFile) {
        List<AbstractParam> paramGroups = identificationFile.getParamGroup();
        String targetClassId = identificationFile.getId();
        checkParamGroups(targetClassId, paramGroups);

        Object searchDatabaseRef = identificationFile.getSearchDatabaseRef();
        checkObjectRef(targetClassId, searchDatabaseRef, uk.ac.liv.jmzqml.model.mzqml.SearchDatabase.class);
    }

    static public void checkIdentificationRefs(String tarClsId,
            List<IdentificationRef> identificationRefs) {
        if (!identificationRefs.isEmpty()) {
            for (IdentificationRef idRef : identificationRefs) {
                Object idFileRef = idRef.getIdentificationFileRef();
                msgs.addAll(checkObjectRef(tarClsId, idFileRef, uk.ac.liv.jmzqml.model.mzqml.IdentificationFile.class));
            }
        }
    }

    static public void checkInputFiles(InputFiles inputFiles) {
        /*
         * RawFileRule start here
         */
        RawFileRule rfl = new RawFileRule(at, inputFiles);
        rfl.check();
        msgs.addAll(rfl.getMsgs());

        IdentificationFiles identificationFiles = inputFiles.getIdentificationFiles();
        if (identificationFiles != null) {
            List<IdentificationFile> identifications = identificationFiles.getIdentificationFile();
            if (!identifications.isEmpty()) {
                for (IdentificationFile identificationFile : identifications) {
                    checkIdentificationFile(identificationFile);
                }
            }
        }

        MethodFiles methodFiles = inputFiles.getMethodFiles();
        if (methodFiles != null) {
            List<MethodFile> methods = methodFiles.getMethodFile();
            if (!methods.isEmpty()) {
                for (MethodFile methodFile : methods) {
                    FileFormat format = methodFile.getFileFormat();
                    String targetClassId = methodFile.getId();
                    checkFileFormat(targetClassId, format);
                }
            }
        }

        List<RawFilesGroup> rawFilesGroups = inputFiles.getRawFilesGroup();
        checkRawFilesGroups(rawFilesGroups);

        List<SearchDatabase> searchDatabases = inputFiles.getSearchDatabase();
        checkSearchDatabases(searchDatabases);

        List<SourceFile> sourceFiles = inputFiles.getSourceFile();
        checkSourceFiles(sourceFiles);
    }

    static public void checkModification(String tarClsId,
            Modification modification) {
        List<CvParam> cvParams = modification.getCvParam();
        if (!cvParams.isEmpty()) {
            for (CvParam cvParam : cvParams) {
                checkCvParam(tarClsId, cvParam);
            }
        }

        List<Object> ftRefs = modification.getFeatureRefs();
        if (!ftRefs.isEmpty()) {
            for (Object ref : ftRefs) {
                msgs.addAll(checkObjectRef(tarClsId, ref, uk.ac.liv.jmzqml.model.mzqml.Feature.class));
            }
        }
    }

    static public <T> ArrayList<Message> checkObjectRef(String tarClsId,
            Object objRef,
            Class<T> cls) {
        ObjectRefTypeMatchRule objectRefMatchRule = new ObjectRefTypeMatchRule(tarClsId, objRef, cls);
        objectRefMatchRule.check();
        return objectRefMatchRule.getMessage();
    }

    static public void checkOrganization(Organization organization) {
        ParentOrganization parentOrg = organization.getParentOrganization();
        String targetClassId = organization.getId();
        Object parentOrgRef = parentOrg.getOrganizationRef();
        msgs.addAll(checkObjectRef(targetClassId, parentOrgRef, uk.ac.liv.jmzqml.model.mzqml.ParentOrganization.class));
    }

    static public void checkParamGroups(String tarClsId,
            List<AbstractParam> paramGroups) {
        for (AbstractParam param : paramGroups) {
            if (param.getClass().isInstance(uk.ac.liv.jmzqml.model.mzqml.CvParam.class)) {
                CvParam cv = (CvParam) param;
                cv.getAccession();
                Object cvRef = cv.getCvRef();
                msgs.addAll(checkObjectRef(tarClsId, cvRef, uk.ac.liv.jmzqml.model.mzqml.Cv.class));
            } else if (param.getClass().isInstance(uk.ac.liv.jmzqml.model.mzqml.UserParam.class)) {
                UserParam userParam = (UserParam) param;
                userParam.getType();
            }

        }
    }

    static public void checkPeptideConsensusList(
            PeptideConsensusList peptideConsensusList) {
        if (peptideConsensusList != null) {
            List<QuantLayer> assayQuantLayers = peptideConsensusList.getAssayQuantLayer();
            List<GlobalQuantLayer> globalQuantLayers = peptideConsensusList.getGlobalQuantLayer();
            String id = peptideConsensusList.getId();
            List<AbstractParam> paramGroups = peptideConsensusList.getParamGroup();
            List<PeptideConsensus> peptideConsensuses = peptideConsensusList.getPeptideConsensus();
            List<QuantLayer> ratioQuantLayers = peptideConsensusList.getRatioQuantLayer();
            List<QuantLayer> studyVariableQuantLayers = peptideConsensusList.getStudyVariableQuantLayer();

            checkQuantLayers(assayQuantLayers);
            checkGlobalQuantLayers(globalQuantLayers);
            String targetClassId = id;
            checkParamGroups(targetClassId, paramGroups);
            checkPeptideConsensuses(peptideConsensuses);
            checkQuantLayers(ratioQuantLayers);
            checkQuantLayers(studyVariableQuantLayers);
        }
    }

    static public void checkPeptideConsensus(PeptideConsensus peptideConsensus) {
        if (peptideConsensus != null) {
            List<String> charges = peptideConsensus.getCharge();

            String id = peptideConsensus.getId();
            String targetClassId = id;
            List<Modification> modifications = peptideConsensus.getModification();
            if (!modifications.isEmpty()) {
                for (Modification modification : modifications) {
                    checkModification(targetClassId, modification);
                }
            }

            List<AbstractParam> paramGroups = peptideConsensus.getParamGroup();
            checkParamGroups(targetClassId, paramGroups);

            String sequence = peptideConsensus.getPeptideSequence();

            Object searchDBRef = peptideConsensus.getSearchDatabaseRef();
            msgs.addAll(checkObjectRef(targetClassId, searchDBRef, uk.ac.liv.jmzqml.model.mzqml.SearchDatabase.class));

            List<EvidenceRef> evidenceRefs = peptideConsensus.getEvidenceRef();
            if (!evidenceRefs.isEmpty()) {
                for (EvidenceRef evidenceRef : evidenceRefs) {
                    checkEvidenceRef(targetClassId, evidenceRef);
                }
            }
        }
    }

    static public void checkPeptideConsensuses(
            List<PeptideConsensus> peptideConsensuses) {
        if (!peptideConsensuses.isEmpty()) {
            for (PeptideConsensus peptideConsensus : peptideConsensuses) {
                checkPeptideConsensus(peptideConsensus);
            }
        }
    }

    /*
     * validate PeptideConsensusList
     */
    static public void checkPeptideConsensusLists(
            List<PeptideConsensusList> peptideConsensusLists) {
        if (!peptideConsensusLists.isEmpty()) {
            /*
             * FinalResultRule start here
             */
            FinalResultRule frr = new FinalResultRule(peptideConsensusLists);
            frr.check();
            msgs.addAll(frr.getMsgs());

            for (PeptideConsensusList peptideConsensusList : peptideConsensusLists) {
                checkPeptideConsensusList(peptideConsensusList);
            }
        }

    }

    static public void checkPerson(Person person) {
        List<Affiliation> affiliations = person.getAffiliation();
        String id = person.getId();
        String targetClassId = id;
        if (!affiliations.isEmpty()) {
            for (Affiliation affiliation : affiliations) {
                Object orgRef = affiliation.getOrganizationRef();
                msgs.addAll(checkObjectRef(targetClassId, orgRef, uk.ac.liv.jmzqml.model.mzqml.Organization.class));
            }
        }
    }

    static public void checkPersonOrOrganizations(
            List<AbstractContact> personOrOrganizations) {
        if (!personOrOrganizations.isEmpty()) {
            for (AbstractContact personOrOrganization : personOrOrganizations) {
                String targetClassId = personOrOrganization.getId();
                List<AbstractParam> paramGroups = personOrOrganization.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                if (personOrOrganization.getClass().isInstance(uk.ac.liv.jmzqml.model.mzqml.Person.class)) {
                    Person person = (Person) personOrOrganization;
                    checkPerson(person);
                }

                if (personOrOrganization.getClass().isInstance(uk.ac.liv.jmzqml.model.mzqml.Organization.class)) {
                    Organization org = (Organization) personOrOrganization;
                    checkOrganization(org);
                }

            }
        }
    }

    static public void checkProcessingMethods(String tarClsId,
            List<ProcessingMethod> processingMethods) {
        if (!processingMethods.isEmpty()) {
            for (ProcessingMethod processingMethod : processingMethods) {
                processingMethod.getOrder();

                List<AbstractParam> paramGroups = processingMethod.getParamGroup();
                checkParamGroups(tarClsId, paramGroups);
            }
        }
    }

    static public void checkProtein(Protein protein) {
        String id = protein.getId();
        String targetClassId = id;
        List<IdentificationRef> identificationRefs = protein.getIdentificationRef();
        checkIdentificationRefs(targetClassId, identificationRefs);

        List<AbstractParam> paramGroups = protein.getParamGroup();
        checkParamGroups(targetClassId, paramGroups);

        List<Object> pepRefs = protein.getPeptideConsensusRefs();
        if (!pepRefs.isEmpty()) {
            for (Object ref : pepRefs) {
                msgs.addAll(checkObjectRef(targetClassId, ref, uk.ac.liv.jmzqml.model.mzqml.PeptideConsensus.class));
            }
        }

        Object searchDBRef = protein.getSearchDatabaseRef();
        msgs.addAll(checkObjectRef(targetClassId, searchDBRef, uk.ac.liv.jmzqml.model.mzqml.SearchDatabase.class));
    }

    static public void checkProteinGroup(ProteinGroup proteinGroup) {
        String id = proteinGroup.getId();
        String targetClassId = id;

        List<IdentificationRef> identificationRefs = proteinGroup.getIdentificationRef();
        checkIdentificationRefs(targetClassId, identificationRefs);

        List<AbstractParam> paramGroups = proteinGroup.getParamGroup();
        checkParamGroups(targetClassId, paramGroups);

        List<Object> protRefs = proteinGroup.getProteinRefs();
        if (!protRefs.isEmpty()) {
            for (Object ref : protRefs) {
                msgs.addAll(checkObjectRef(targetClassId, ref, uk.ac.liv.jmzqml.model.mzqml.Protein.class));
            }
        }

        Object searchDBRef = proteinGroup.getSearchDatabaseRef();
        msgs.addAll(checkObjectRef(targetClassId, searchDBRef, uk.ac.liv.jmzqml.model.mzqml.SearchDatabase.class));
    }

    static public void checkProteinGroupList(ProteinGroupList proteinGroupList) {
        if (proteinGroupList != null) {
            List<QuantLayer> assayQuantLayers = proteinGroupList.getAssayQuantLayer();
            List<GlobalQuantLayer> globalQuantLayers = proteinGroupList.getGlobalQuantLayer();
            String id = proteinGroupList.getId();
            List<AbstractParam> paramGroups = proteinGroupList.getParamGroup();
            List<ProteinGroup> proteinGroups = proteinGroupList.getProteinGroup();
            List<QuantLayer> ratioQuantLayers = proteinGroupList.getRatioQuantLayer();
            List<QuantLayer> studyVariableQuantLayers = proteinGroupList.getStudyVariableQuantLayer();

            checkQuantLayers(assayQuantLayers);
            checkGlobalQuantLayers(globalQuantLayers);
            String targetClassId = id;
            checkParamGroups(targetClassId, paramGroups);
            checkProteinGroups(proteinGroups);
            checkQuantLayers(ratioQuantLayers);
            checkQuantLayers(studyVariableQuantLayers);
        }
    }

    static public void checkProteinGroups(List<ProteinGroup> proteinGroups) {
        if (!proteinGroups.isEmpty()) {
            for (ProteinGroup proteinGroup : proteinGroups) {
                checkProteinGroup(proteinGroup);
            }
        }
    }

    /*
     * validate ProteinList
     */
    static public void checkProteinList(ProteinList proteinList) {
        if (proteinList != null) {
            List<QuantLayer> assayQuantLayers = proteinList.getAssayQuantLayer();
            List<GlobalQuantLayer> globalQuantLayers = proteinList.getGlobalQuantLayer();
            String id = proteinList.getId();
            List<AbstractParam> paramGroups = proteinList.getParamGroup();
            List<Protein> proteins = proteinList.getProtein();
            List<QuantLayer> ratioQuantLayers = proteinList.getRatioQuantLayer();
            List<QuantLayer> studyVariableQuantLayers = proteinList.getStudyVariableQuantLayer();

            checkQuantLayers(assayQuantLayers);
            checkGlobalQuantLayers(globalQuantLayers);
            String targetClassId = id;
            checkParamGroups(targetClassId, paramGroups);
            checkProteins(proteins);
            checkQuantLayers(ratioQuantLayers);
            checkQuantLayers(studyVariableQuantLayers);
        }
    }

    static public void checkProteins(List<Protein> proteins) {
        if (!proteins.isEmpty()) {
            for (Protein protein : proteins) {
                checkProtein(protein);
            }
        }
    }

    static public void checkProvider(Provider provider) {
        Object analysisSoftwareRef = provider.getAnalysisSoftwareRef();
        String targetClassId = provider.getId();
        msgs.addAll(checkObjectRef(targetClassId, analysisSoftwareRef, uk.ac.liv.jmzqml.model.mzqml.Software.class));

        ContactRole contactRole = provider.getContactRole();
        checkContactRole(targetClassId, contactRole);

        String name = provider.getName();
    }

    static public void checkQuantLayers(List<QuantLayer> quantLayers) {
        if (!quantLayers.isEmpty()) {
            for (QuantLayer quantLayer : quantLayers) {

                //TODO: need to figure out what  does columnIndex refer to 
                List<Object> columnIndex = quantLayer.getColumnIndex();

                String id = quantLayer.getId();
                String targetClassId = id;

                DataMatrix dataMatrix = quantLayer.getDataMatrix();
                checkDataMatrix(dataMatrix);

                CvParamRef data = quantLayer.getDataType();
                CvParam cvParam = data.getCvParam();
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
    static public void checkRatio(Ratio ratio) {
        String id = ratio.getId();
        String targetClassId = id;
        ParamList ratioCalParamList = ratio.getRatioCalculation();
        if (ratioCalParamList != null) {
            List<AbstractParam> paramGroups = ratioCalParamList.getParamGroup();
            checkParamGroups(targetClassId, paramGroups);
        }
    }

    static public void checkRatioList(RatioList ratioList) {
        if (ratioList != null) {
            List<Ratio> ratios = ratioList.getRatio();
            checkRatios(ratios);

            NumeratorDenominatorRule numeratorDenominatorRule = new NumeratorDenominatorRule(ratioList);
            numeratorDenominatorRule.check();
            msgs.addAll(numeratorDenominatorRule.getMsgs());
        }
    }

    static public void checkRatios(List<Ratio> ratios) {
        if (!ratios.isEmpty()) {
            for (Ratio ratio : ratios) {
                checkRatio(ratio);
            }
        }
    }

    static public void checkRawFiles(List<RawFile> rawFiles) {
        if (!rawFiles.isEmpty()) {
            for (RawFile rawFile : rawFiles) {
                String id = rawFile.getId();
                String targetClassId = id;

                Object methodFileRef = rawFile.getMethodFileRef();
                msgs.addAll(checkObjectRef(targetClassId, methodFileRef, uk.ac.liv.jmzqml.model.mzqml.MethodFile.class));

                List<AbstractParam> paramGroups = rawFile.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                FileFormat format = rawFile.getFileFormat();
                checkFileFormat(targetClassId, format);
            }
        }
    }

    static public void checkRawFilesGroups(List<RawFilesGroup> rawFilesGroups) {
        if (!rawFilesGroups.isEmpty()) {
            for (RawFilesGroup rawFilesGroup : rawFilesGroups) {
                String targetClassId = rawFilesGroup.getId();
                List<AbstractParam> paramGroups = rawFilesGroup.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);

                List<RawFile> rawFiles = rawFilesGroup.getRawFile();
                checkRawFiles(rawFiles);
            }
        }
    }

    static public void checkRow(Row row) {
        row.getObjectRef();
    }

    static public void checkSearchDatabases(List<SearchDatabase> searchDatabases) {
        if (!searchDatabases.isEmpty()) {
            for (SearchDatabase searchDatabase : searchDatabases) {
                List<CvParam> cvParams = searchDatabase.getCvParam();
                String targetClassId = searchDatabase.getId();
                if (!cvParams.isEmpty()) {
                    for (CvParam cvParam : cvParams) {
                        checkCvParam(targetClassId, cvParam);
                    }
                }

                //TODO: to check the following commented code
//                Param databaseName = searchDatabase.getDatabaseName();
//                if (databaseName.getCvParam() != null) {
//                    checkCvParam(targetClassId, databaseName.getCvParam());
//                }

                FileFormat format = searchDatabase.getFileFormat();
                checkFileFormat(targetClassId, format);

            }
        }
    }

    static public void checkSmallMolecule(SmallMolecule smallMolecule) {
        String id = smallMolecule.getId();
        String targetClassId = id;

        List<DBIdentificationRef> dBidentificationRefs = smallMolecule.getDBIdentificationRef();
        if (!dBidentificationRefs.isEmpty()) {
            for (DBIdentificationRef dBidRef : dBidentificationRefs) {
                checkDBIdentificationRef(targetClassId, dBidRef);
            }
        }

        List<Object> ftRefs = smallMolecule.getFeatureRefs();
        if (!ftRefs.isEmpty()) {
            for (Object ref : ftRefs) {
                msgs.addAll(checkObjectRef(targetClassId, ref, uk.ac.liv.jmzqml.model.mzqml.Feature.class));
            }
        }

        List<AbstractParam> paramGroups = smallMolecule.getParamGroup();
        checkParamGroups(targetClassId, paramGroups);

        List<SmallMolModification> smallMolModifications = smallMolecule.getModification();
        checkSmallMolModifications(targetClassId, smallMolModifications);

    }

    static public void checkSmallMolModifications(String tarClsId,
            List<SmallMolModification> smallMolModifications) {
        if (!smallMolModifications.isEmpty()) {
            for (SmallMolModification smallMolMod : smallMolModifications) {
                List<CvParam> cvParams = smallMolMod.getCvParam();
                if (!cvParams.isEmpty()) {
                    for (CvParam cvParam : cvParams) {
                        checkCvParam(tarClsId, cvParam);
                    }
                }
            }
        }
    }

    static public void checkSmallMolecules(List<SmallMolecule> smallMolecules) {
        if (!smallMolecules.isEmpty()) {
            for (SmallMolecule smallMol : smallMolecules) {
                checkSmallMolecule(smallMol);
            }
        }
    }

    static public void checkSmallMoleculeList(
            SmallMoleculeList smallMoleculeList) {
        List<QuantLayer> assayQuantLayers = smallMoleculeList.getAssayQuantLayer();
        List<GlobalQuantLayer> globalQuantLayers = smallMoleculeList.getGlobalQuantLayer();
        String id = smallMoleculeList.getId();
        List<AbstractParam> paramGroups = smallMoleculeList.getParamGroup();
        List<QuantLayer> ratioQuantLayers = smallMoleculeList.getRatioQuantLayer();
        List<SmallMolecule> smallMolecules = smallMoleculeList.getSmallMolecule();
        List<QuantLayer> studyVariableQuantLayers = smallMoleculeList.getStudyVariableQuantLayer();

        checkQuantLayers(assayQuantLayers);
        checkGlobalQuantLayers(globalQuantLayers);
        String targetClassId = id;
        checkParamGroups(targetClassId, paramGroups);
        checkSmallMolecules(smallMolecules);
        checkQuantLayers(ratioQuantLayers);
        checkQuantLayers(studyVariableQuantLayers);
    }

    static public void checkSoftwares(List<Software> softwares) {
        if (!softwares.isEmpty()) {
            for (Software software : softwares) {
                String id = software.getId();
                String targetClassId = id;

                List<AbstractParam> paramGroups = software.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);
            }
        }
    }

    static public void checkSoftwareList(SoftwareList softwareList) {
        if (softwareList != null) {
            List<Software> softwares = softwareList.getSoftware();
            checkSoftwares(softwares);
        }
    }

    static public void checkSourceFiles(List<SourceFile> sourceFiles) {
        if (!sourceFiles.isEmpty()) {
            for (SourceFile sourceFile : sourceFiles) {
                String targetClassId = sourceFile.getId();
                FileFormat format = sourceFile.getFileFormat();
                checkFileFormat(targetClassId, format);
            }
        }
    }

    static public void checkStudyVariableList(
            StudyVariableList studyVariableList) {
        if (studyVariableList != null) {
            List<StudyVariable> studyVariables = studyVariableList.getStudyVariable();
            checkStudyVariables(studyVariables);
        }
    }

    static public void checkStudyVariables(List<StudyVariable> studyVariables) {
        if (!studyVariables.isEmpty()) {
            for (StudyVariable studyVariable : studyVariables) {
                String id = studyVariable.getId();
                String targetClassId = id;

                List<Object> assayRefs = studyVariable.getAssayRefs();
                if (!assayRefs.isEmpty()) {
                    for (Object assayRef : assayRefs) {
                        msgs.addAll(checkObjectRef(targetClassId, assayRef, uk.ac.liv.jmzqml.model.mzqml.Assay.class));
                    }
                }

                List<AbstractParam> paramGroups = studyVariable.getParamGroup();
                checkParamGroups(targetClassId, paramGroups);
            }
        }
    }

    static public void checkVersion(String version) {
    }

    /*
     * protected methods
     */
    protected InputStream getOntologiesFileInputStream() throws FileNotFoundException {

        File file = new File(getClass().getClassLoader().getResource("ontologies.xml").getFile());
        if (!file.exists()) {
            ClassLoader cl = this.getClass().getClassLoader();
            return cl.getResourceAsStream("ontologies.xml");
        }
        return new FileInputStream(file);
    }

    protected InputStream getGeneralMappingRuleInputStream() throws FileNotFoundException {
        String mappingRuleFile = getClass().getClassLoader().
                getResource("mzQuantML-mapping_1.0.0-rc2-general.xml").getFile();
        File file = new File(mappingRuleFile);
        if (!file.exists()) {
            ClassLoader cl = this.getClass().getClassLoader();
            return cl.getResourceAsStream(mappingRuleFile);
        }

        return new FileInputStream(file);
    }
}
