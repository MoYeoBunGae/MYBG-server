package com.midasdev.mochat.group.service.component;

import com.midasdev.mochat.global.exception.ApplicationException;
import com.midasdev.mochat.global.exception.ApplicationExceptionType;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class InvitationCodeGenerator {

    private static final int CODE_LENGTH = 8;
    private static final int NUMBER = 0;
    private static final int ALPHABET = 1;

    // 숫자 및 영어 대문자 8자리로 이루어진 랜덤 초대 코드 생성
    public String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(generateRandomSingleCode());
        }
        return code.toString();
    }

    private char generateRandomSingleCode() {
        int random = generateRandomNumber(2);
        return switch (random) {
            case NUMBER -> (char)('0' + generateRandomNumber(10));
            case ALPHABET -> (char)('A' + generateRandomNumber(26));
            default -> throw new ApplicationException(ApplicationExceptionType.GLOBAL_INTERNAL_SERVER_ERROR, String.format("random(%d)은 0과 1중 하나여야 합니다.", random));
        };
    }

    private int generateRandomNumber(int max) {
        return (int) (Math.random() * max);
    }

}
