package uz.pdp.task1;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.task1.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment,Integer> {
}
