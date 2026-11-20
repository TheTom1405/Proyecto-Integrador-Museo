package museo;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "exhibition_history")
public class ExhibitionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "artifact_id", nullable = false)
    private Artifact artifact;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private ExhibitionRoom room;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    public ExhibitionHistory() {
    }

    public Long getId() {
        return id;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    public ExhibitionRoom getRoom() {
        return room;
    }

    public void setRoom(ExhibitionRoom room) {
        this.room = room;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
