package com.playdata.userservice.user.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailSenderService {

    // EmailConfig에 선언한 메일 전송 핵심 객체를 주입받기
    private final JavaMailSender mailSender;

    // 가입할 회원에게 전송할 이메일 양식 준비
    // userService가 이 메서드를 호출할 겁니다.
    public String joinMail(String email) throws MessagingException {
        int authNum = makeRandomNumber();
        String setFrom = "uiuo1266@gmail.com";
        String toMail = email; // 수신받을 이메일 (가입하고자 하는 사람의 이메일)
        String title = "회원 가입 인증 이메일 입니다.";
        String content = "홈페이지 가입을 신청해 주셔서 감사합니다." +
                "<br><br>" +
                "인증 번호는 <strong>" + authNum + "</strong> 입니다. <br>" +
                "해당 인증 번호를 인증번호 확인란에 기입해 주세요."; // 이메일에 삽입할 내용

        mailSend(setFrom, toMail, title, content);

        // 클라이언트에게 전달할 인증 코드 리턴 (문자열)
        return Integer.toString(authNum);
    }


    // 여기서 실제 이메일이 전송
    private void mailSend(String setFrom, String toMail, String title, String content) throws MessagingException {
        // MimeMessage란 JavaMail 라이브러리에서 이메일 메시지를 나타내는 클래스. (생성, 설정, 수정, 전송 담당)
        MimeMessage message = mailSender.createMimeMessage();
        /*
            기타 설정들을 담당할 MimeMessageHelper 객체를 생성
            생성자의 매개값으로 MimeMessage 객체, bool, 문자 인코딩 설정
            true 매개값을 전달하면 MultiPart 형식의 메세지 전달이 가능 (첨부 파일)
        */
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
        helper.setFrom(setFrom);
        helper.setTo(toMail);
        helper.setSubject(title);

        // 내용 채우기 (true를 주면 html이 포함되어 있다는 뜻)
        helper.setText(content, true);

        // 메일 전송
        mailSender.send(message);

    }

    private int makeRandomNumber() {
        int min = 111_111;
        int max = 999_999;
        int range = max - min + 1;   // 999999 - 111111 + 1 = 888889

        int checkNum = (int)(Math.random() * range) + min;
        log.info("인증번호: {}", checkNum);
        return checkNum;
    }


    public void sendPasswordResetMail(@NotBlank(message = "이메일을 입력해 주세요.") String email, String nickName, String code) throws MessagingException {
        String subject = "[YourApp] 비밀번호 재설정 인증 코드 안내";
        String content = new StringBuilder()
                .append(nickName).append("님, 안녕하세요!<br><br>")
                .append("아래 인증 코드를 입력하시면 비밀번호 재설정을 진행하실 수 있습니다.<br>")
                .append("<strong>").append(code).append("</strong><br><br>")
                .append("이 코드는 발송 시점부터 5분간 유효합니다.")
                .toString();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
        helper.setFrom("uiuo1266@gmail.com");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
}
