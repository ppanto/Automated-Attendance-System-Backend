package com.panto.attendance.service;

import com.panto.attendance.dto.PersonnelShiftResponse;
import com.panto.attendance.dto.PersonnelShiftUpsertRequest;
import com.panto.attendance.mapper.PersonnelShiftMapper;
import com.panto.attendance.model.PersonnelShift;
import com.panto.attendance.repository.PersonnelRepository;
import com.panto.attendance.repository.PersonnelShiftRepository;
import com.panto.attendance.repository.ShiftTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PersonnelShiftService {
    private final PersonnelShiftRepository personnelShiftRepository;
    private final PersonnelShiftMapper personnelShiftMapper;
    private final PersonnelRepository personnelRepository;
    private final ShiftTypeRepository shiftTypeRepository;

    public List<PersonnelShiftResponse> get(){
        return personnelShiftRepository.findAll()
                .stream()
                .map(personnelShiftMapper::mapToResponse)
                .collect(Collectors.toList());
    }
    public List<PersonnelShiftResponse> getByPersonnelId(Long personnelId){
        return personnelShiftRepository.findByPersonnelId(personnelId)
                .stream()
                .map(personnelShiftMapper::mapToResponse)
                .collect(Collectors.toList());
    }
    public List<PersonnelShiftResponse> getAllOngoing(){
        List<PersonnelShiftResponse> list = get();
        return list.stream()
                .filter(p -> p.getEndDate().getTime() > System.currentTimeMillis())
                .collect(Collectors.toList());
    }
    public List<PersonnelShiftResponse> getAllOngoingByPersonnelId(Long personnelId){
        List<PersonnelShiftResponse> list = getByPersonnelId(personnelId);
        return list.stream()
                .filter(p -> p.getEndDate().getTime() > System.currentTimeMillis())
                .collect(Collectors.toList());
    }
    public PersonnelShiftResponse create_update(PersonnelShiftUpsertRequest personnelShift){
        PersonnelShift toDatabase = personnelShiftMapper.mapToDatabase(personnelShift);
        toDatabase.setPersonnel(personnelRepository.findById(personnelShift.getPersonnelId()).get());
        toDatabase.setShiftType(shiftTypeRepository.findById(personnelShift.getShiftTypeId()).get());
        PersonnelShift toModel = personnelShiftRepository.save(toDatabase);
        return personnelShiftMapper.mapToResponse(toModel);
    }
    public boolean delete(Long id){
        Optional<PersonnelShift> obj = personnelShiftRepository.findById(id);
        if(obj.isEmpty()) return false;
        personnelShiftRepository.deleteById(id);
        return true;
    }
}
