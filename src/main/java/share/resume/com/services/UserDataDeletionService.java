package share.resume.com.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.resume.com.entities.*;
import share.resume.com.repositories.*;
import share.resume.com.services.files.FileService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDataDeletionService {
    
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final FileService fileService;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void deleteUserData(UUID userId) {
        log.info("Starting deletion of all data for user with ID: {}", userId);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        deleteDocumentsFromMinioForUser(user);
        
        deleteUserDataWithNativeQueries(userId);
        
        log.info("Successfully deleted all data for user with ID: {}", userId);
    }
    
    private void deleteDocumentsFromMinioForUser(UserEntity user) {
        log.info("Deleting documents from MINIO for user: {}", user.getId());
        
        try {
            List<ResumeEntity> userResumes = resumeRepository.findAllByAuthor(user);
            
            for (ResumeEntity resume : userResumes) {
                deleteDocumentsFromMinio(resume.getDocuments());
            }
            
            log.info("Completed MINIO document deletion for {} resumes", userResumes.size());
        } catch (Exception e) {
            log.error("Error deleting MINIO files, continuing with database deletion", e);
        }
    }
    
    private void deleteDocumentsFromMinio(List<DocumentEntity> documents) {
        for (DocumentEntity document : documents) {
            try {
                fileService.delete(document.getDirectory(), document.getName());
                log.info("Deleted file from MINIO: {}/{}", document.getDirectory(), document.getName());
            } catch (Exception e) {
                log.error("Failed to delete file from MINIO: {}/{}, error: {}", 
                         document.getDirectory(), document.getName(), e.getMessage());
            }
        }
    }
    
    private void deleteUserDataWithNativeQueries(UUID userId) {
        log.info("Deleting user data using native SQL queries for user: {}", userId);
        
        try {
            int deletedVotes = jdbcTemplate.update(
                "DELETE FROM user_comment_vote_state WHERE user_id = ?", userId);
            log.info("Deleted {} user comment vote states", deletedVotes);
            
            int deletedFavorites = jdbcTemplate.update(
                "DELETE FROM users_favorites_resumes WHERE user_id = ?", userId);
            log.info("Deleted {} user favorites", deletedFavorites);
            
            int deletedCommentVotes = jdbcTemplate.update(
                "DELETE FROM user_comment_vote_state WHERE comment_id IN (" +
                "SELECT c.id FROM comments c " +
                "INNER JOIN resumes r ON c.resume_id = r.id " +
                "WHERE r.author_id = ?)", userId);
            log.info("Deleted {} comment vote states on user's resumes", deletedCommentVotes);
            
            int deletedComments = jdbcTemplate.update(
                "DELETE FROM comments WHERE resume_id IN (" +
                "SELECT id FROM resumes WHERE author_id = ?)", userId);
            log.info("Deleted {} comments on user's resumes", deletedComments);
            
            int deletedDocuments = jdbcTemplate.update(
                "DELETE FROM documents WHERE resume_id IN (" +
                "SELECT id FROM resumes WHERE author_id = ?)", userId);
            log.info("Deleted {} documents", deletedDocuments);
            
            int deletedResumeCompanies = jdbcTemplate.update(
                "DELETE FROM resumes_companies WHERE resume_id IN (" +
                "SELECT id FROM resumes WHERE author_id = ?)", userId);
            log.info("Deleted {} resume-company relationships", deletedResumeCompanies);
            
            int deletedResumes = jdbcTemplate.update(
                "DELETE FROM resumes WHERE author_id = ?", userId);
            log.info("Deleted {} resumes", deletedResumes);
            
            int deletedUsers = jdbcTemplate.update(
                "DELETE FROM users WHERE id = ?", userId);
            log.info("Deleted {} user entities", deletedUsers);
            
        } catch (Exception e) {
            log.error("Error during native SQL deletion for user: {}", userId, e);
            throw new RuntimeException("Failed to delete user data", e);
        }
    }
} 