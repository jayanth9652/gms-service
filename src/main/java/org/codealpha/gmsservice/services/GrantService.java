package org.codealpha.gmsservice.services;

import java.util.List;
import java.util.Optional;

import org.codealpha.gmsservice.constants.KpiType;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrantService {

    @Autowired
    private GrantRepository grantRepository;
    @Autowired
    private GranterGrantSectionRepository granterGrantSectionRepository;
    @Autowired
    private GranterGrantSectionAttributeRepository granterGrantSectionAttributeRepository;
    @Autowired
    private GrantStringAttributeRepository grantStringAttributeRepository;
    @Autowired
    private GrantDocumentAttributesRepository grantDocumentAttributesRepository;
    @Autowired
    private GrantQuantitativeDataRepository grantQuantitativeDataRepository;
    @Autowired
    private GrantKpiRepository grantKpiRepository;
    @Autowired
    private GrantQualitativeDataRepository grantQualitativeDataRepository;
    @Autowired
    private GrantDocumentDataRepository grantDocumentDataRepository;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private DocumentKpiNotesRepository documentKpiNotesRepository;
    @Autowired
    private DocKpiDataDocumentRepository docKpiDataDocumentRepository;
    @Autowired
    private QualKpiDocumentRepository qualKpiDocumentRepository;
    @Autowired
    private QualitativeKpiNotesRepository qualitativeKpiNotesRepository;
    @Autowired
    private QuantitativeKpiNotesRepository quantitativeKpiNotesRepository;
    @Autowired
    private QuantKpiDocumentRepository quantKpiDocumentRepository;

    public List<String> getGrantAlerts(Grant grant) {
        return null;
    }

    public Grant saveGrant(Grant grant) {
        return grantRepository.save(grant);
    }

    public Grant getById(Long id) {
        return grantRepository.findById(id).get();
    }

    public GranterGrantSection getGrantSectionBySectionId(Long sectionId) {

        Optional<GranterGrantSection> granterGrantSection = granterGrantSectionRepository.findById(sectionId);
        if (granterGrantSection.isPresent()) {
            return granterGrantSection.get();
        }
        return null;
    }

    public GranterGrantSectionAttribute getSectionAttributeByAttributeIdAndType(
            Long attributeId, String type) {
        if (type.equalsIgnoreCase("document")) {
            return grantDocumentAttributesRepository.findById(attributeId).get().getSectionAttribute();
        } else if (type.equalsIgnoreCase("string")) {
            Optional<GrantStringAttribute> grantStringAttribute = grantStringAttributeRepository.findById(attributeId);
            if (grantStringAttribute.isPresent()) {
                return grantStringAttribute.get().getSectionAttribute();
            }
        }
        return null;
    }

    public GrantStringAttribute getStringAttributeByAttribute(
            GranterGrantSectionAttribute grantSectionAttribute) {
        return grantStringAttributeRepository.findBySectionAttribute(grantSectionAttribute);
    }

    public GrantDocumentAttributes getDocumentAttributeById(Long docAttribId) {
        return grantDocumentAttributesRepository.findById(docAttribId).get();
    }

    public GrantStringAttribute saveStringAttribute(GrantStringAttribute grantStringAttribute) {
        return grantStringAttributeRepository.save(grantStringAttribute);
    }

    public GranterGrantSectionAttribute saveSectionAttribute(
            GranterGrantSectionAttribute sectionAttribute) {
        return granterGrantSectionAttributeRepository.save(sectionAttribute);
    }

    public GranterGrantSection saveSection(GranterGrantSection newSection) {
        return granterGrantSectionRepository.save(newSection);
    }

    public GrantQuantitativeKpiData getGrantQuantitativeKpiDataById(Long quntKpiDataId) {
        if (grantQuantitativeDataRepository.findById(quntKpiDataId).isPresent()) {
            return grantQuantitativeDataRepository.findById(quntKpiDataId).get();
        }
        return null;
    }

    public GrantQualitativeKpiData getGrantQualitativeKpiDataById(Long qualKpiDataId) {
        if (grantQualitativeDataRepository.findById(qualKpiDataId).isPresent()) {
            return grantQualitativeDataRepository.findById(qualKpiDataId).get();
        }
        return null;
    }

    public GrantQuantitativeKpiData saveGrantQunatitativeKpiData(GrantQuantitativeKpiData kpiData) {
        return grantQuantitativeDataRepository.save(kpiData);
    }

    public GrantQualitativeKpiData saveGrantQualitativeKpiData(GrantQualitativeKpiData kpiData) {
        return grantQualitativeDataRepository.save(kpiData);
    }

    public GrantKpi saveGrantKpi(GrantKpi grantKpi) {

        return grantKpiRepository.save(grantKpi);
    }

    public GrantKpi getGrantKpiById(Long id) {
        if (grantKpiRepository.findById(id).isPresent()) {
            return grantKpiRepository.findById(id).get();
        }
        return null;
    }

    public GrantKpi getGrantKpiByNameAndTypeAndGrant(String title, KpiType kpiType, Grant grant) {
        return grantKpiRepository.findByTitleAndKpiTypeAndGrant(title, kpiType, grant);
    }

    public GrantDocumentKpiData getGrantDocumentKpiDataById(Long id) {
        if (grantDocumentDataRepository.findById(id).isPresent()) {
            return grantDocumentDataRepository.findById(id).get();
        }
        return null;
    }

    public GrantDocumentKpiData saveGrantDocumentKpiData(GrantDocumentKpiData kpiData) {
        return grantDocumentDataRepository.save(kpiData);
    }

    public List<Template> getKpiTemplates(GrantKpi kpiId) {

        return templateRepository.findByKpi(kpiId);
    }

    public Template getKpiTemplateById(Long templateId) {
        if (templateRepository.findById(templateId).isPresent()) {
            return templateRepository.findById(templateId).get();
        }
        return null;
    }

    public GrantDocumentAttributes saveGrantDocumentAttribute(GrantDocumentAttributes grantDocumentAttributes) {
        return grantDocumentAttributesRepository.save(grantDocumentAttributes);
    }

    public DocumentKpiNotes getDocKpiNoteById(Long id) {
        return documentKpiNotesRepository.findById(id).get();
    }

    public DocumentKpiNotes saveDocumentKpiNote(DocumentKpiNotes documentKpiNote) {
        return documentKpiNotesRepository.save(documentKpiNote);
    }

    public QualitativeKpiNotes getQualKpiNoteById(Long id) {
        return qualitativeKpiNotesRepository.findById(id).get();
    }

    public QualitativeKpiNotes saveQualKpiNote(QualitativeKpiNotes qualKpiNote) {
        return qualitativeKpiNotesRepository.save(qualKpiNote);
    }

    public QuantitativeKpiNotes getQuantKpiNoteById(Long id) {
        return quantitativeKpiNotesRepository.findById(id).get();
    }

    public QuantitativeKpiNotes saveQuantKpiNote(QuantitativeKpiNotes quantKpiNote) {
        return quantitativeKpiNotesRepository.save(quantKpiNote);
    }

    public DocKpiDataDocument getDockpiDocById(Long id) {
        return docKpiDataDocumentRepository.findById(id).get();
    }

    public DocKpiDataDocument saveDocKpiDataDoc(DocKpiDataDocument dataDocument){
        return docKpiDataDocumentRepository.save(dataDocument);
    }

    public QualKpiDataDocument getQualkpiDocById(Long id) {
        return qualKpiDocumentRepository.findById(id).get();
    }

    public QualKpiDataDocument saveQualKpiDataDoc(QualKpiDataDocument dataDocument){
        return qualKpiDocumentRepository.save(dataDocument);
    }

    public QuantKpiDataDocument getQuantkpiDocById(Long id) {
        return quantKpiDocumentRepository.findById(id).get();
    }

    public QuantKpiDataDocument saveQuantKpiDataDoc(QuantKpiDataDocument dataDocument){
        return quantKpiDocumentRepository.save(dataDocument);
    }

    public GrantStringAttribute findGrantStringBySectionAttribueAndGrant(GranterGrantSection granterGrantSection,GranterGrantSectionAttribute granterGrantSectionAttribute, Grant grant){
        return grantStringAttributeRepository.findBySectionAndSectionAttributeAndGrant(granterGrantSection,granterGrantSectionAttribute,grant);
    }

    public GrantDocumentAttributes findGrantDocumentBySectionAttribueAndGrant(GranterGrantSection granterGrantSection,GranterGrantSectionAttribute granterGrantSectionAttribute, Grant grant){
        return grantDocumentAttributesRepository.findBySectionAndSectionAttributeAndGrant(granterGrantSection,granterGrantSectionAttribute,grant);
    }

    public GrantStringAttribute saveGrantStringAttribute(GrantStringAttribute stringAttribute){
        return grantStringAttributeRepository.save(stringAttribute);
    }

    public Template saveKpiTemplate(Template storedTemplate) {
        return templateRepository.save(storedTemplate);
    }
}