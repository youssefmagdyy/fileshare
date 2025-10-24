package com.youssef.fileshare.File;

import com.youssef.fileshare.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "files")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String s3key;

    private String contentType;

    @ManyToOne
//    @JoinColumn(name = "user_id")
    private User owner;

    private Instant timestamp;

    private long size;


}
