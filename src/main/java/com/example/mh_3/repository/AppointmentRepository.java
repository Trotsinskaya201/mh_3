package com.example.mh_3.repository;

import com.example.mh_3.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, Appointment.AppointmentStatus status);
    List<Appointment> findByDoctorIdAndStatusIsNot(Long doctorId, Appointment.AppointmentStatus status);
    List<Appointment> findByPatientIdAndStatusIsNot(Long patientId, Appointment.AppointmentStatus status);
    List<Appointment> findByPatientIdAndStatus(Long patientId, Appointment.AppointmentStatus status);
} 