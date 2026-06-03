package com.maharashtra.rccms.repository;

import com.maharashtra.rccms.model.filing.FilingApplication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Deletes nested filing rows before replace-on-save. Hibernate {@code orphanRemoval} on
 * {@code clear()} is unreliable when the aggregate is merged via {@code JpaRepository.save()}.
 */
@Repository
public class FilingApplicationChildCleanup {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void deleteAllChildren(Long applicationId) {
        if (applicationId == null) {
            return;
        }
        entityManager.createNativeQuery("""
                DELETE FROM application_vakalatnama_group_applicant
                WHERE vakalatnama_group_id IN (
                    SELECT id FROM application_vakalatnama_group WHERE application_id = :appId
                )
                """).setParameter("appId", applicationId).executeUpdate();

        entityManager.createNativeQuery("""
                DELETE FROM application_vakalatnama_co_advocate
                WHERE vakalatnama_group_id IN (
                    SELECT id FROM application_vakalatnama_group WHERE application_id = :appId
                )
                """).setParameter("appId", applicationId).executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM application_vakalatnama_group WHERE application_id = :appId"
        ).setParameter("appId", applicationId).executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM application_applicant WHERE application_id = :appId"
        ).setParameter("appId", applicationId).executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM application_respondent WHERE application_id = :appId"
        ).setParameter("appId", applicationId).executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM application_disputed_land WHERE application_id = :appId"
        ).setParameter("appId", applicationId).executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM application_document_checklist WHERE application_id = :appId"
        ).setParameter("appId", applicationId).executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM application_attachment WHERE application_id = :appId"
        ).setParameter("appId", applicationId).executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM application_disputed_order WHERE application_id = :appId"
        ).setParameter("appId", applicationId).executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM application_description_paragraph WHERE application_id = :appId"
        ).setParameter("appId", applicationId).executeUpdate();

        entityManager.flush();
    }

    /**
     * Returns a clean managed {@link FilingApplication} after child deletes.
     * Must not call {@code refresh()} on the pre-delete instance: {@code disputedOrder} and
     * collections may still point at removed rows (e.g. ApplicationDisputedOrder#7).
     */
    public FilingApplication reloadApplication(Long applicationId) {
        FilingApplication stale = entityManager.find(FilingApplication.class, applicationId);
        if (stale != null) {
            stale.setDisputedOrder(null);
            stale.getVakalatnamaGroups().clear();
            stale.getApplicants().clear();
            stale.getRespondents().clear();
            stale.getDisputedLands().clear();
            stale.getAttachments().clear();
            stale.getDescriptionParagraphs().clear();
            entityManager.detach(stale);
        }
        entityManager.clear();
        return entityManager.find(FilingApplication.class, applicationId);
    }
}
