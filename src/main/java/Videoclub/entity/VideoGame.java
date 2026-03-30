package Videoclub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name =  "Videogames")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoGame {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = true, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private String platform;
    @Column(nullable = false)
    private String genre;
    @Column(nullable = false)
    private String producer;
    @Column(nullable = false)
    private String pegi ;
    @Column(nullable = true)
    private LocalDate releaseDate ;
    @Column(nullable = false)
    private BigDecimal rentalPrice;
    @Column(nullable = false)
    private String coverUrl;
    @Column(nullable = false)
    private boolean active;
    @Column(nullable = false)
    private LocalDate createdAt ;
    @Column(nullable = false)
    private LocalDate updatedAt ;
    @Column(nullable = false)
    private String createdBy ;
    @Column(nullable = false)
    private String updatedBy;

    //relacion con GameCopy
    @OneToMany(mappedBy = "game")
    private List<GameCopy> copies;


}
