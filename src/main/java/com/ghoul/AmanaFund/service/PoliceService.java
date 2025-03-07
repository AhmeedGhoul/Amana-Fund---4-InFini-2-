package com.ghoul.AmanaFund.service;
import com.ghoul.AmanaFund.entity.FrequencyPolice;
import com.ghoul.AmanaFund.repository.IpoliceRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.ghoul.AmanaFund.entity.Police;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;


@Service
@AllArgsConstructor

public class PoliceService implements IpoliceService{
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
            twilioService.sendSms(phoneNumber, message);

        return savedPolice;
    }

    @Override
    public List<Police> retrievePolices() {
        return ipoliceRepository.findAll();
    }

    @Override
    public Police updatePolice(Police police) {
        if (police==null)
            throw new RuntimeException("police should not be null");
        return ipoliceRepository.save(police);
    }

    @Override
    public Police retrievePolice(Long idPolice) {
        return ipoliceRepository.findById(idPolice).orElseThrow(() -> new RuntimeException("idPolice not found"));
    }

    @Override
    public void removePolice(Long idPolice) {
        if (idPolice==null)
            throw new RuntimeException("idPolice should not be null");
        ipoliceRepository.deleteById(idPolice);
    }

    @Override
    public Page<Police> getAllPaginated(Pageable  pageable) {
        return ipoliceRepository.findAll(pageable);
    }

    @Override
    public List<Police> searchPolice(Date start, Double amount, Long id) {
        return ipoliceRepository.searchPolice(start, amount, id);
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
