package backend.service;

import backend.dto.AnnouncementRequest;
import backend.entity.Announcement;
import backend.entity.User;
import backend.repository.AnnouncementRepository;
import backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final EventService eventService;

    public AnnouncementService(AnnouncementRepository announcementRepository, UserRepository userRepository, EventService eventService) {
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
        this.eventService = eventService;
    }

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    public Optional<Announcement> getAnnouncementById(Long id) {
        return announcementRepository.findById(id);
    }

    @Transactional
    public Announcement createAnnouncement(AnnouncementRequest request, String authorUsername) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setDescription(request.getDescription());
        
        if (authorUsername != null) {
            User author = userRepository.findByUsername(authorUsername).orElse(null);
            announcement.setAuthor(author);
        }
        
        return announcementRepository.save(announcement);
    }

    @Transactional
    public Optional<Announcement> updateStatus(Long id, String status) {
        return announcementRepository.findById(id).map(announcement -> {
            announcement.setStatus(status);
            
            if ("PUBLISHED".equalsIgnoreCase(status) && !Boolean.TRUE.equals(announcement.getIsBroadcasted())) {
                String message = announcement.getTitle();
                if (announcement.getDescription() != null && !announcement.getDescription().isEmpty()) {
                    message += ": " + announcement.getDescription();
                }
                
                eventService.broadcast("New Announcement: " + message);
                announcement.setIsBroadcasted(true);
            }
            
            return announcementRepository.save(announcement);
        });
    }

    @Transactional
    public boolean deleteAnnouncement(Long id) {
        if (announcementRepository.existsById(id)) {
            announcementRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
