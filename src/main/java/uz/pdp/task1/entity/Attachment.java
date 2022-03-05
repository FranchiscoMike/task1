package uz.pdp.task1.entity;

import lombok.*;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String originalFileName;

    private long size;

    private String contentType;

    //=====for DB

    @Column(unique = true)
    private String name;


}
