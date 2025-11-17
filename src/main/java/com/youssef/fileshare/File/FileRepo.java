package com.youssef.fileshare.File;

import com.youssef.fileshare.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepo extends JpaRepository<File,Integer> {
    List<File> findAllByOwner(User user);
}
