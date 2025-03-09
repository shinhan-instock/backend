package com.pda.piggyBank_module.service.handler;

import com.pda.core_module.events.SetMileageEvent;
import com.pda.piggyBank_module.domain.Piggy;
import com.pda.piggyBank_module.repository.PiggyRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@KafkaListener(topics = "${piggyBank.commands.topic.setMileage}", groupId = "piggyBank-group")
public class PiggyBankCommandsHandler {

    private final PiggyRepository piggyRepository;

    public PiggyBankCommandsHandler(PiggyRepository piggyRepository) {
        this.piggyRepository = piggyRepository;
    }

    @KafkaHandler
    public void handleSetMileage(@Payload SetMileageEvent event) {
        try {
            Long userId = Long.valueOf(event.getUserId());
            Piggy piggy = piggyRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        Piggy newPiggy = new Piggy(userId, 0);
                        newPiggy.setLastMileageDate(null); // 아직 지급되지 않음
                        return newPiggy;
                    });
            // 만약 마지막 지급 날짜가 오늘이면 추가 지급하지 않음
            if (LocalDate.now().equals(piggy.getLastMileageDate())) {
                System.out.println("오늘 이미 "+ userId + " 번 유저한테 이미 지급 됐음.");
                return;
            }
            piggy.addMileage(event.getMileageAmount());
            piggy.setLastMileageDate(LocalDate.now());
            piggyRepository.save(piggy);
            System.out.println("마일리지 추가 userId: " + userId);
        } catch (NumberFormatException e) {
            System.err.println("사용자 없음 userId: " + event.getUserId());
        }
    }


}
