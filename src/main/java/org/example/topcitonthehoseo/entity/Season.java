package org.example.topcitonthehoseo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private int seasonId;

    @Column(nullable = false)
    private String seasonName;

    @Column(nullable = false)
    private LocalDateTime seasonStart;

    @Column(nullable = false)
    private LocalDateTime seasonEnd;

}
