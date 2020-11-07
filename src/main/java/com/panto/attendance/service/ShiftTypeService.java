package com.panto.attendance.service;

import com.panto.attendance.dto.ShiftTypeInsertRequest;
import com.panto.attendance.mapper.ShiftTypeMapper;
import com.panto.attendance.model.ShiftType;
import com.panto.attendance.repository.ShiftTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShiftTypeService {
    private final ShiftTypeRepository shiftTypeRepository;
    private final ShiftTypeMapper shiftTypeMapper;

    public List<ShiftType> get(){
        return shiftTypeRepository.findAll()
                .stream().filter(shiftType -> shiftType.isActive())
                .collect(Collectors.toList());
    }
    public ShiftType get(Long id){
        return shiftTypeRepository.findById(id).orElseThrow();
    }
    public boolean deactivate(Long id){
        Optional<ShiftType> database = shiftTypeRepository.findById(id);
        if(database.isEmpty()) return false;
        database.get().setActive(false);
        shiftTypeRepository.save(database.get());
        return true;
    }
    public ShiftType create(ShiftTypeInsertRequest shiftType){
        return shiftTypeRepository.save(shiftTypeMapper.mapDtoToDatabase(shiftType));
    }
}
