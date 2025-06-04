package org.example.topcitonthehoseo.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long rankId;

    @Column(nullable = false)
    private String rankName;

    @Column(nullable = false)
    private String rankImage;

}
