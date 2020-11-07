package com.panto.attendance.service;

import com.panto.attendance.dto.LeaveInsertRequest;
import com.panto.attendance.dto.LeaveResponse;
import com.panto.attendance.dto.LeaveUpdateRequest;
import com.panto.attendance.mapper.LeaveMapper;
import com.panto.attendance.model.Leave;
import com.panto.attendance.model.LeaveType;
import com.panto.attendance.model.Personnel;
import com.panto.attendance.repository.LeaveRepository;
import com.panto.attendance.repository.LeaveTypeRepository;
import com.panto.attendance.repository.PersonnelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LeaveService {
    private final LeaveRepository leaveRepository;
    private final LeaveMapper leaveMapper;
    private final LeaveTypeRepository leaveTypeRepository;
    private final PersonnelRepository personnelRepository;

    public List<LeaveResponse> getByUserForDate(Long personnelId, Date date){
        if(personnelId != null && date != null){
            return leaveRepository
                    .findByPersonnelIdAndDate(personnelId, date)
                    .stream()
                    .map(leaveMapper::mapToResponse)
                    .collect(Collectors.toList());
        }
        else if(personnelId == null && date != null){
            return leaveRepository
                    .findByDate(date)
                    .stream()
                    .map(leaveMapper::mapToResponse)
                    .collect(Collectors.toList());
        }
        else if(personnelId != null && date == null){
            return leaveRepository
                    .findByPersonnelId(personnelId)
                    .stream()
                    .map(leaveMapper::mapToResponse)
                    .collect(Collectors.toList());
        }
        return new ArrayList<LeaveResponse>();
    }
    public LeaveResponse create(LeaveInsertRequest leaveInsertRequest) throws Exception {
        if(leaveInsertRequest.getEndDate() == null || leaveInsertRequest.getStartDate().compareTo(leaveInsertRequest.getEndDate()) == 1){
            Leave insertToDb = leaveMapper.mapToDatabase(leaveInsertRequest);
            insertToDb.setDate(leaveInsertRequest.getStartDate());
            insertToDb.setPersonnel(personnelRepository.findById(leaveInsertRequest.getPersonnelId()).get());
            insertToDb.setLeaveType(leaveTypeRepository.getOne(leaveInsertRequest.getLeaveTypeId()));
            Leave saved = leaveRepository.save(insertToDb);
            return leaveMapper.mapToResponse(saved);
        }
        if(leaveInsertRequest.getStartDate().compareTo(leaveInsertRequest.getEndDate()) > 0){
            throw new Exception("End date is before start date.");
        }
        Personnel p = personnelRepository.findById(leaveInsertRequest.getPersonnelId()).get();
        LeaveType lt = leaveTypeRepository.getOne(leaveInsertRequest.getLeaveTypeId());
        List<Leave> leavesToSave = new ArrayList<>();
        Date d = new Date(leaveInsertRequest.getEndDate().getTime());
        while(d.getTime() + 86400000L  >= leaveInsertRequest.getStartDate().getTime()){
            Leave leaveToAddInList = leaveMapper.mapToDatabase(leaveInsertRequest);
            leaveToAddInList.setLeaveType(lt);
            leaveToAddInList.setPersonnel(p);
            leaveToAddInList.setDate(new Date(d.getTime()));
            leavesToSave.add(leaveToAddInList);
            d.setTime(d.getTime() - 86400000L);
        }
        leaveRepository.saveAll(leavesToSave);
        return leaveMapper.mapToResponse(leavesToSave.get(leavesToSave.size()-1));
    }
    public LeaveResponse update(LeaveUpdateRequest leaveUpdateRequest) throws Exception {
        Optional<Leave> database = leaveRepository.findById(leaveUpdateRequest.getId());
        if(database.isEmpty()) throw new Exception("Not found.");
        database.get().setLeaveType(leaveTypeRepository.getOne(leaveUpdateRequest.getLeaveTypeId()));
        database.get().setDate(leaveUpdateRequest.getStartDate());
        database.get().setApproved(leaveUpdateRequest.isApproved());
        database.get().setDescription(leaveUpdateRequest.getDescription());
        Leave toReturn = leaveRepository.save(database.get());
        return leaveMapper.mapToResponse(toReturn);
    }
    public boolean delete(Long leaveId){
        Optional<Leave> database = leaveRepository.findById(leaveId);
        if(database.isEmpty()) return false;
        leaveRepository.delete(database.get());
        return true;
    }

}
