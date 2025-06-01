package com.example.mh_3.controller;

import com.example.mh_3.model.User;
import com.example.mh_3.service.UserService;
import com.example.mh_3.repository.NewbornRepository;
import com.example.mh_3.service.NewbornService;
import com.example.mh_3.repository.AppointmentRepository;
import com.example.mh_3.model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private NewbornRepository newbornRepository;

    @Autowired
    private NewbornService newbornService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        
        // Add role-specific data
        switch (user.getRole()) {
            case ADMIN:
                long patientCount = userService.countUsersByRole(User.Role.PATIENT);
                long doctorCount = userService.countUsersByRole(User.Role.DOCTOR);
                long newbornCount = newbornService.countAllNewborns();
                long appointmentCount = appointmentRepository.count();

                model.addAttribute("patientCount", patientCount);
                model.addAttribute("doctorCount", doctorCount);
                model.addAttribute("newbornCount", newbornCount);
                model.addAttribute("appointmentCount", appointmentCount);

                // Add doctor and patient counts for chart
                model.addAttribute("doctorCountForChart", doctorCount);
                model.addAttribute("patientCountForChart", patientCount);

                long maleNewbornCount = newbornService.countByGender("MALE");
                long femaleNewbornCount = newbornService.countByGender("FEMALE");

                model.addAttribute("maleNewbornCountForChart", maleNewbornCount);
                model.addAttribute("femaleNewbornCountForChart", femaleNewbornCount);

                return "dashboard/admin";
            case DOCTOR:
                return "dashboard/doctor";
            case PATIENT:
                return "dashboard/patient";
            default:
                return "redirect:/";
        }
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "dashboard/profile";
    }

    @GetMapping("/medical-records")
    public String medicalRecords(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "dashboard/medical-records";
    }

    @GetMapping("/users")
    public String listUsers(@RequestParam(value = "role", required = false) User.Role role, Model model) {
        List<User> users;
        if (role != null) {
            users = userService.findUsersByRole(role);
        } else {
            users = userService.findAllUsers();
        }
        model.addAttribute("users", users);
        model.addAttribute("roles", List.of(User.Role.ADMIN, User.Role.DOCTOR, User.Role.PATIENT));
        model.addAttribute("selectedRole", role);
        return "dashboard/users";
    }

    @GetMapping("/patient/doctors")
    public String listDoctorsForPatient(Model model) {
        List<User> doctors = userService.findUsersByRole(User.Role.DOCTOR); // Предполагается, что у вас есть такой метод в UserService
        model.addAttribute("doctors", doctors);
        return "dashboard/doctors";
    }

    @GetMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(id); // Предполагается, что у вас есть такой метод в UserService
        redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно удален.");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", List.of(User.Role.ADMIN, User.Role.DOCTOR, User.Role.PATIENT)); // Assuming these are the allowed roles for creation
        return "dashboard/add-user";
    }

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        // Basic validation (can add more as needed)
        if (result.hasErrors()) {
            // Need to re-add roles if validation fails
            model.addAttribute("roles", List.of(User.Role.ADMIN, User.Role.DOCTOR, User.Role.PATIENT)); // Need access to Model here
            return "dashboard/add-user";
        }

        try {
            userService.registerUser(user); // Use existing registration logic
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь " + user.getUsername() + " успешно добавлен.");
        } catch (RuntimeException e) {
            // Handle potential errors like duplicate username/email
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении пользователя: " + e.getMessage());
             return "redirect:/admin/users/add"; // Redirect back to the form on error
        }

        return "redirect:/admin/users"; // Redirect to user list on success
    }

    @GetMapping("/newborns/add")
    public String showAddNewbornForm(Model model) {
        model.addAttribute("newborn", new com.example.mh_3.model.Newborn());
        List<User> patients = userService.findUsersByRole(User.Role.PATIENT);
        model.addAttribute("patients", patients);
        return "dashboard/add-newborn";
    }

    @PostMapping("/newborns/add")
    public String addNewborn(@ModelAttribute("newborn") com.example.mh_3.model.Newborn newborn, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            // Need to re-add patients if validation fails
            List<User> patients = userService.findUsersByRole(User.Role.PATIENT);
            model.addAttribute("patients", patients);
            return "dashboard/add-newborn";
        }

        try {
            newbornService.saveNewborn(newborn);
            redirectAttributes.addFlashAttribute("successMessage", "Информация о новорожденном успешно добавлена.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении информации о новорожденном: " + e.getMessage());
            // Need to re-add patients on error redirect as well
            return "redirect:/admin/newborns/add"; // Redirect back to the form on error
        }

        return "redirect:/admin/newborns"; // Redirect to newborns list on success
    }

    @GetMapping("/newborns")
    public String listNewborns(@RequestParam(value = "gender", required = false) String gender, Model model) {
        List<com.example.mh_3.model.Newborn> newborns;
        if (gender != null && !gender.isEmpty()) {
            newborns = newbornService.findByGender(gender);
        } else {
            newborns = newbornService.findAllNewborns();
        }
        model.addAttribute("newborns", newborns);
        model.addAttribute("selectedGender", gender);
        return "dashboard/newborns";
    }

    @PostMapping("/newborns/{id}/delete")
    public String deleteNewborn(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            newbornService.deleteNewbornById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Информация о новорожденном успешно удалена.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении информации о новорожденном: " + e.getMessage());
        }
        return "redirect:/admin/newborns";
    }

    @GetMapping("/patient/my-newborns")
    public String listPatientNewborns(@AuthenticationPrincipal User currentUser, Model model) {
        List<com.example.mh_3.model.Newborn> myNewborns = newbornService.findByMotherId(currentUser.getId());
        model.addAttribute("newborns", myNewborns);
        model.addAttribute("user", currentUser);
        return "dashboard/patient-newborns";
    }

    @GetMapping("/doctor/patients")
    public String listPatientsForDoctor(Model model) {
        List<User> patients = userService.findUsersByRole(User.Role.PATIENT);
        model.addAttribute("patients", patients);
        return "dashboard/doctor-patients";
    }

    @GetMapping("/doctor/my-appointments")
    public String listDoctorAppointments(@AuthenticationPrincipal User currentUser, Model model) {
        List<Appointment> doctorAppointments = appointmentRepository.findByDoctorIdAndStatusIsNot(currentUser.getId(), Appointment.AppointmentStatus.COMPLETED);
        model.addAttribute("appointments", doctorAppointments);
        model.addAttribute("user", currentUser);
        return "dashboard/doctor-appointments";
    }

    @GetMapping("/appointments")
    public String listAllAppointments(Model model) {
        List<com.example.mh_3.model.Appointment> allAppointments = appointmentRepository.findAll();
        model.addAttribute("appointments", allAppointments);
        return "dashboard/all-appointments";
    }

    @PostMapping("/appointments/create")
    public String createAppointment(@RequestParam("patientId") Long patientId, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        User patient = userService.findById(patientId);
        if (patient == null || patient.getRole() != User.Role.PATIENT) {
             redirectAttributes.addFlashAttribute("errorMessage", "Пациент не найден.");
             return "redirect:/admin/doctor/patients";
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(currentUser); // Assuming the current user is the doctor creating the appointment
        appointment.setAppointmentDateTime(java.time.LocalDateTime.now()); // Set current time for now, can be changed later
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED); // Set initial status

        appointmentRepository.save(appointment);

        redirectAttributes.addFlashAttribute("successMessage", "Прием для пациента " + patient.getFullName() + " успешно создан.");
        return "redirect:/admin/doctor/my-appointments";
    }

    @PostMapping("/appointments/{id}/complete")
    public String completeAppointment(@PathVariable Long id, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Прием не найден.");
            return "redirect:/admin/doctor/my-appointments";
        }

        // Check if the current user is the doctor for this appointment
        if (!appointment.getDoctor().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "У вас нет прав на завершение этого приема.");
            return "redirect:/admin/doctor/my-appointments";
        }

        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        redirectAttributes.addFlashAttribute("successMessage", "Прием успешно завершен.");
        return "redirect:/admin/doctor/my-appointments";
    }

    @GetMapping("/doctor/completed-appointments")
    public String listDoctorCompletedAppointments(@AuthenticationPrincipal User currentUser, Model model) {
        List<Appointment> completedAppointments = appointmentRepository.findByDoctorIdAndStatus(currentUser.getId(), Appointment.AppointmentStatus.COMPLETED);
        model.addAttribute("appointments", completedAppointments);
        model.addAttribute("user", currentUser);
        return "dashboard/doctor-completed-appointments";
    }

    @PostMapping("/appointments/{id}/delete")
    public String deleteAppointment(@PathVariable Long id, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Прием не найден.");
            return "redirect:/admin/doctor/my-appointments";
        }

        // Check if the current user is the doctor for this appointment
        if (!appointment.getDoctor().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "У вас нет прав на отмену этого приема.");
            return "redirect:/admin/doctor/my-appointments";
        }

        appointmentRepository.delete(appointment);

        redirectAttributes.addFlashAttribute("successMessage", "Прием успешно отменен.");
        return "redirect:/admin/doctor/my-appointments";
    }

    @GetMapping("/patient/my-appointments")
    public String listPatientAppointments(@AuthenticationPrincipal User currentUser, Model model) {
        List<Appointment> patientAppointments = appointmentRepository.findByPatientId(currentUser.getId());
        model.addAttribute("appointments", patientAppointments);
        model.addAttribute("user", currentUser);
        return "dashboard/patient-appointments";
    }

    @PostMapping("/patient/appointments/create-with-doctor/{doctorId}")
    public String createAppointmentByPatient(@PathVariable Long doctorId, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        User doctor = userService.findById(doctorId);
        if (doctor == null || doctor.getRole() != User.Role.DOCTOR) {
             redirectAttributes.addFlashAttribute("errorMessage", "Выбранный врач не найден.");
             return "redirect:/admin/patient/doctors";
        }

        // Ensure the current user is a patient
        if (currentUser.getRole() != User.Role.PATIENT) {
            redirectAttributes.addFlashAttribute("errorMessage", "У вас нет прав для создания приема.");
            return "redirect:/admin"; // Or an appropriate page
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(currentUser);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(java.time.LocalDateTime.now()); // Set current time for simplicity, can be expanded
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED); // Set initial status

        appointmentRepository.save(appointment);

        redirectAttributes.addFlashAttribute("successMessage", "Вы успешно записаны на прием к доктору " + doctor.getFullName() + ".");
        return "redirect:/admin/patient/my-appointments"; // Redirect to patient's appointments page
    }

    @PostMapping("/patient/appointments/{id}/delete")
    public String deletePatientAppointment(@PathVariable Long id, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Прием не найден.");
            return "redirect:/admin/patient/my-appointments";
        }

        // Check if the current user is the patient for this appointment
        if (!appointment.getPatient().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "У вас нет прав на удаление этого приема.");
            return "redirect:/admin/patient/my-appointments";
        }

        appointmentRepository.delete(appointment);

        redirectAttributes.addFlashAttribute("successMessage", "Прием успешно удален.");
        return "redirect:/admin/patient/my-appointments";
    }
} 