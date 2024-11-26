package com.midasdev.mochat.group.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class InvitationCode {

        private String invitationCode;

        public InvitationCode(String invitationCode) {
            this.invitationCode = invitationCode;
        }

        public static InvitationCode createRandomCode() {
            return new InvitationCode("randomCode");
        }

        public boolean match(String code) {
            return this.invitationCode.equals(code);
        }

}
