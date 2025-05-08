package com.ghoul.AmanaFund.service;
import com.ghoul.AmanaFund.DTO.PoliceDTO;
import com.ghoul.AmanaFund.entity.*;
import com.ghoul.AmanaFund.repository.IpoliceRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;


@Service
@AllArgsConstructor

public class PoliceService implements IpoliceService{

    private final PoliceDTOMapper policeDTOMapper;
    @Autowired
    private IpoliceRepository ipoliceRepository;
    private final TwilioService twilioService;

    @Override
    public Police addPolice(Police police) {
        if (police==null)
            throw new RuntimeException("police should not be null");
        Police savedPolice = ipoliceRepository.save(police);
        // Ensure user and phone number exist

            String phoneNumber = "+21658413579";
            String message = "Hello, your insurance policy (ID: " + savedPolice.getIdPolice() +
                    ") has been successfully created. End date: " + savedPolice.getEnd() + ".";

            // Send SMS
            /*twilioService.sendSms(phoneNumber, message);*/

        return savedPolice;
    }

    @Override
    public List<PoliceDTO> retrievePolices() {
        return ipoliceRepository
                .findAll()
                .stream()
                .map(policeDTOMapper)
                .collect(Collectors.toList());
    }

    @Override
    public Police updatePolice(Police police) {
        if (police==null)
            throw new RuntimeException("police should not be null");
        return ipoliceRepository.save(police);
    }
    @Override
    public Police deactivatePolice(Long id) {
        Police police = ipoliceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Police not found with id " + id));
        if (police.isActive())
            police.setActive(false);
        else
            police.setActive(true);
        return ipoliceRepository.save(police);
    }

    @Override
    public PoliceDTO retrievePolice(Long idPolice) {
        return ipoliceRepository.findById(idPolice).map(policeDTOMapper).orElseThrow(() -> new RuntimeException("idPolice not found"));
    }

    @Override
    public void removePolice(Long idPolice) {
        if (idPolice==null)
            throw new RuntimeException("idPolice should not be null");
        ipoliceRepository.deleteById(idPolice);
    }
    public double getActivePolicePercentage() {
        List<Police> allPolices = ipoliceRepository.findAll();
        if (allPolices.isEmpty()) return 0;

        long activeCount = allPolices.stream()
                .filter(Police::isActive)
                .count();

        return (activeCount * 100.0) / allPolices.size();
    }
    public double getTotalPoliceAmount() {
        List<Police> allPolices = ipoliceRepository.findAll();
        return allPolices.stream()
                .mapToDouble(Police::getAmount)
                .sum();
    }

    public double calculateGuaranteedAmount(Long policeId) {
        Optional<Police> policeOptional = ipoliceRepository.findById(policeId);

        if (policeOptional.isEmpty()) {
            throw new RuntimeException("Police not found with ID: " + policeId);
        }

        Police police = policeOptional.get();

        double totalGuaranteedAmount = 0.0;

        for (Garantie garantie : police.getGaranties()) {
            if (garantie instanceof Person person) {
                totalGuaranteedAmount += person.getRevenue();
            } else if (garantie instanceof ObjectG objectG) {
                totalGuaranteedAmount += objectG.getEstimatedValue();
            }
        }

        return totalGuaranteedAmount;
    }



    @Override
    public Page<PoliceDTO> getAllPaginated(Pageable  pageable) {
        return ipoliceRepository
                .findAll(pageable)
                .map(policeDTOMapper);
    }

    @Override
    public List<Police> searchPolice(Double amount) {
        return ipoliceRepository.searchPolice(amount);
    }


    public Double calculateTotalAmountPaid(Long policeId) {
        Optional<Police> policeOptional = ipoliceRepository.findById(policeId);

        if (policeOptional.isEmpty()) {
            throw new RuntimeException("Police not found");
        }

        Police police = policeOptional.get();

        if (police.getStart() == null || police.getEnd() == null ||
                police.getAmount() == null || police.getFrequency() == null) {
            throw new IllegalStateException("Start date, end date, amount, and frequency must be set");
        }

        // Convert java.util.Date to LocalDate safely
        LocalDate startDate = convertToLocalDate(police.getStart());
        LocalDate endDate = convertToLocalDate(police.getEnd());

        long totalPayments = 0;

        switch (police.getFrequency()) {
            case MONTHLY:
                totalPayments = ChronoUnit.MONTHS.between(startDate, endDate);
                break;
            case QUARTERLY:
                totalPayments = ChronoUnit.MONTHS.between(startDate, endDate) / 3;
                break;
            case HALF_YEARLY:
                totalPayments = ChronoUnit.MONTHS.between(startDate, endDate) / 6;
                break;
            case YEARLY:
                totalPayments = ChronoUnit.YEARS.between(startDate, endDate);
                break;
            default:
                throw new IllegalArgumentException("Unsupported frequency: " + police.getFrequency());
        }
        double Payements = totalPayments * police.getAmount();
        if(Payements==0)
            Payements = police.getAmount();
        return Payements;
    }

    public LocalDate convertToLocalDate(Date sqlDate) {
        if (sqlDate == null) {
            return null;
        }

        if (sqlDate instanceof java.sql.Date) {
            return ((java.sql.Date) sqlDate).toLocalDate();
        } else {
            return sqlDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }
    public Date getNextPaymentDate(Long policeId, Date lastPaymentDate) {
        Optional<Police> policeOpt = ipoliceRepository.findById(policeId);

        if (policeOpt.isEmpty()) {
            throw new IllegalArgumentException("Police not found with ID: " + policeId);
        }

        Police police = policeOpt.get();

        if (lastPaymentDate == null) {
            throw new IllegalArgumentException("Last payment date is required");
        }

        FrequencyPolice frequency = police.getFrequency();
        if (frequency == null) {
            throw new IllegalArgumentException("Frequency is not set for this policy");
        }

        Date policyEndDate = police.getEnd();
        if (policyEndDate == null) {
            throw new IllegalArgumentException("Policy end date is not set");
        }

        // Define period based on the frequency using a switch block
        int period;
        switch (frequency) {
            case MONTHLY:
                period = 30;
                break;
            case QUARTERLY:
                period = 90;
                break;
            case HALF_YEARLY:
                period = 180;
                break;
            case YEARLY:
                period = 365;
                break;
            default:
                throw new IllegalArgumentException("Invalid frequency type");
        }

        // Calculate the next payment date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastPaymentDate);
        calendar.add(Calendar.DAY_OF_YEAR, period);
        Date nextPaymentDate = calendar.getTime();

        // Ensure the next payment date does not exceed the policy end date
        return nextPaymentDate.after(policyEndDate) ? null : nextPaymentDate;
    }

    public double calculateTotalActivePoliceAmount() {
        return ipoliceRepository.findAll()
                .stream()
                .filter(Police::isActive)
                .mapToDouble(Police::getAmount)
                .sum();
    }

    public Map<Date, Double> getAmountSumByStartDate() {
        return ipoliceRepository.findAll()
                .stream()
                .filter(police -> police.getStart() != null && police.getAmount() != null)
                .collect(Collectors.groupingBy(
                        Police::getStart,
                        Collectors.summingDouble(Police::getAmount)
                ));
    }




//    @Scheduled(cron = "30 40 5 * * ?")  // Runs daily at midnight
//    public void checkPoliceEndDates() {
//        Date todayStart = getStartOfDay(new Date());
//        Date todayEnd = getEndOfDay(new Date());
//
//        List<Police> expiredPolicies = ipoliceRepository.findByEndBetween(todayStart, todayEnd);
//        System.out.println("*************************"+expiredPolicies);
//
//        for (Police police : expiredPolicies) {
//            String phoneNumber = "+21658413579"; // Assuming User has a phone number field
//            String message = "Reminder: Your insurance policy (ID: " + police.getIdPolice() + ") expires today: " +
//                    new SimpleDateFormat("yyyy-MM-dd").format(police.getEnd()) +
//                    ". Please renew it soon.";
//
//            twilioService.sendSms(phoneNumber, message);
//        }
//    }
//
//    private Date getStartOfDay(Date date) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//        return calendar.getTime();
//    }
//
//    private Date getEndOfDay(Date date) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        calendar.set(Calendar.HOUR_OF_DAY, 23);
//        calendar.set(Calendar.MINUTE, 59);
//        calendar.set(Calendar.SECOND, 59);
//        calendar.set(Calendar.MILLISECOND, 999);
//        return calendar.getTime();
//    }


}
