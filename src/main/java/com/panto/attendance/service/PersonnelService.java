package com.panto.attendance.service;

import com.panto.attendance.dto.PersonnelResponse;
import com.panto.attendance.dto.PersonnelSimpleResponse;
import com.panto.attendance.dto.PersonnelUpsertRequest;
import com.panto.attendance.mapper.PersonnelMapper;
import com.panto.attendance.model.*;
import com.panto.attendance.repository.*;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PersonnelService {
    private final PersonnelRepository personnelRepository;
    private final PersonnelMapper personnelMapper;
    private final DepartmentRepository departmentRepository;
    private final TitleRepository titleRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final PersonnelImageRepository personnelImageRepository;

    public List<PersonnelResponse> get(){
        return personnelRepository
                .findAll()
                .stream()
                //.map(i -> PersonnelMapper.INSTANCE.mapToDtoResponse(i))
                .map(personnelMapper::mapToDtoResponse)
                .collect(Collectors.toList());
    }
    public PersonnelResponse get(String username) throws UsernameNotFoundException{
        List<ApplicationUser> user = applicationUserRepository.findByUsername(username);
        if(user.isEmpty()) throw new UsernameNotFoundException("No user found.");
        return personnelMapper.mapToDtoResponse(user.get(0).getPersonnel());
    }
    public boolean toggleActive(Long id){
        Optional<Personnel> p = personnelRepository.findById(id);
        if(p.isEmpty()) return false;
        p.get().setActiveStatus(!p.get().isActiveStatus());
        personnelRepository.save(p.get());
        return true;
    }

    public PersonnelResponse create(PersonnelUpsertRequest entity) {
        Department department = null;
        if(entity.getDepartmentId() != null && entity.getDepartmentId()!=0){
            department = departmentRepository.findById(entity.getDepartmentId()).orElse(null);
        }
        Title title = null;
        if(entity.getTitleId() != null && entity.getTitleId()!=0){
            title = titleRepository.findById(entity.getTitleId()).orElse(null);
        }
        Personnel personnel = personnelMapper.mapToDatabasePersonnel(entity, title, department);
        personnel.setActiveStatus(true);
        personnel = personnelRepository.save(personnel);

        // create row for image table
        PersonnelImage pImage = new PersonnelImage(personnel.getId(), personnel,null);
        personnelImageRepository.save(pImage);

        return personnelMapper.mapToDtoResponse(personnel);
    }

    public PersonnelResponse update(Long id, PersonnelUpsertRequest entity) {
        Optional<Personnel> database = personnelRepository.findById(id);
        if(database.isEmpty()) return null;

        Department department = null;
        if(entity.getDepartmentId() != null && entity.getDepartmentId()!=0){
            department = departmentRepository.findById(entity.getDepartmentId()).orElse(null);
        }
        Title title = null;
        if(entity.getTitleId() != null && entity.getTitleId()!=0){
            title = titleRepository.findById(entity.getTitleId()).orElse(null);
        }

        Personnel obj = personnelMapper.mapToDatabasePersonnel(entity, title, department);
        obj.setId(id);
        return personnelMapper.mapToDtoResponse(personnelRepository.save(obj));
    }

    public byte[] getImage(Long id) {
        Optional<PersonnelImage> personnel = personnelImageRepository.findById(id);
        if(personnel.isEmpty()) return null;
        return personnel.get().getImage();
    }

    public void uploadImage(Long id, MultipartFile file) throws Exception {
        Optional<PersonnelImage> personnel = personnelImageRepository.findById(id);
        if(personnel.isEmpty()) throw new Exception("Personnel not found.");
        InputStream is = file.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);
        personnel.get().setImage(bytes);
        personnelImageRepository.save(personnel.get());
    }
    public List<PersonnelSimpleResponse> getSimple(){
        return personnelRepository
                .findAll()
                .stream()
                .map(personnelMapper::mapToDtoSimpleResponse)
                .collect(Collectors.toList());
    }
    public List<PersonnelSimpleResponse> getSimpleFilterNoAccount(){
        List<Personnel> personnelList = personnelRepository.findAll();
        List<ApplicationUser> userList = applicationUserRepository.findAll();
        List<Long> personnelIdsWithAccountList = new ArrayList<>();
        userList.forEach(u -> personnelIdsWithAccountList.add(u.getPersonnel().getId()));
        List<PersonnelSimpleResponse> allPersonnelWithNoUserAccount = new ArrayList<>();
        personnelList.forEach(personnel -> {
           if(!personnelIdsWithAccountList.contains(personnel.getId())){
               allPersonnelWithNoUserAccount.add(personnelMapper.mapToDtoSimpleResponse(personnel));
           }
        });
        return allPersonnelWithNoUserAccount;
    }
}
