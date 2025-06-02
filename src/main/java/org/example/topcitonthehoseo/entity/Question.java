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
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long questionId;

    @Column(nullable = false)
    private String questionContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false)
    private boolean questionType;

    @Column
    private String answerText;

    @Column(nullable = false)
    @Builder.Default
    private int triedNum = 0;

    @Column(nullable = false)
    @Builder.Default
    private int correctedNum = 0;

    @Column(nullable = false)
    @Builder.Default
    private double correctRate = 0.0;

}
