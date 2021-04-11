package com.panto.attendance.service;

import com.panto.attendance.dto.ApplicationUserInsertRequest;
import com.panto.attendance.dto.ApplicationUserResponse;
import com.panto.attendance.dto.ApplicationUserUpdateRequest;
import com.panto.attendance.mapper.ApplicationUserMapper;
import com.panto.attendance.model.ApplicationUser;
import com.panto.attendance.model.Personnel;
import com.panto.attendance.repository.ApplicationUserRepository;
import com.panto.attendance.repository.PersonnelRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ApplicationUserService {
    private final ApplicationUserRepository applicationUserRepository;
    private final ApplicationUserMapper applicationUserMapper;
    private final PersonnelRepository personnelRepository;
    private final PasswordEncoder passwordEncoder;

    public List<ApplicationUserResponse> get(){
        return applicationUserRepository
                .findAll()
                .stream()
                .map(applicationUserMapper::mapToDtoResponse)
                .collect(Collectors.toList());
    }

    public boolean toggleActive(Long id) {
        Optional<ApplicationUser> user = applicationUserRepository.findById(id);
        if(user.isEmpty()) return false;
        user.get().setAccountActive(!user.get().isAccountActive());
        applicationUserRepository.save(user.get());
        return true;
    }

    public ApplicationUserResponse create(ApplicationUserInsertRequest entity) throws Exception {
        List<ApplicationUser> userWithSameUsernameList = applicationUserRepository.findByUsername(entity.getUsername());
        if(userWithSameUsernameList.size() > 0) throw new Exception();

        ApplicationUser newUser = new ApplicationUser();

        if(entity.getPersonnelId() != null && entity.getPersonnelId() != 0) {
            Personnel personnel = personnelRepository.findById(entity.getPersonnelId())
                    .orElseThrow(Exception::new);
            newUser.setPersonnel(personnel);
        }

        newUser.setAccountActive(true);
        newUser.setUsername(entity.getUsername());
        newUser.setPasswordHash(passwordEncoder.encode(entity.getPassword()));
        applicationUserRepository.save(newUser);
        return applicationUserMapper.mapToDtoResponse(newUser);
    }

    public boolean update(Long id, ApplicationUserUpdateRequest entity) {
        Optional<ApplicationUser> user = applicationUserRepository.findById(id);
        if(user.isEmpty()) return false;
        if(!passwordEncoder.encode(entity.getOldPassword()).equals(passwordEncoder.encode(user.get().getPasswordHash()))) return false;
        user.get().setPasswordHash(passwordEncoder.encode(entity.getNewPassword()));
        applicationUserRepository.save(user.get());
        return true;
    }
    public ApplicationUser get(String username)  throws UsernameNotFoundException {
        List<ApplicationUser> user = applicationUserRepository.findByUsername(username);
        if(user.isEmpty()) throw new UsernameNotFoundException("No user with that username.");
        if(!user.get(0).isAccountActive()) throw new UsernameNotFoundException("No user with that username or account not active.");
        return user.get(0);
    }

    public ApplicationUserResponse getApplicationUserResponse(String username){
        List<ApplicationUser> user = applicationUserRepository.findByUsername(username);
        if(user.isEmpty()) throw new UsernameNotFoundException("No user with that username.");
        return applicationUserMapper.mapToDtoResponse(user.get(0));
    }

    public void updatePassword(String username, String newPassword) throws UsernameNotFoundException{
        List<ApplicationUser> user = applicationUserRepository.findByUsername(username);
        if(user.isEmpty()) throw new UsernameNotFoundException("No user with that username.");
        user.get(0).setPasswordHash(passwordEncoder.encode(newPassword));
        applicationUserRepository.save(user.get(0));
    }

    public boolean delete(Long id){
        if(applicationUserRepository.existsById(id)){
            applicationUserRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
