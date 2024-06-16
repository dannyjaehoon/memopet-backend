package com.memopet.memopet.global.common.utils;

import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberSocial;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import com.memopet.memopet.domain.member.repository.MemberRepository;
import com.memopet.memopet.domain.member.repository.MemberSocialRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusinessUtil {

    private final PetRepository petRepository;
    private final MemberRepository memberRepository;
    private final MemberSocialRepository memberSocialRepository;
    private final String IS_MOBILE = "MOBILE";
    private final String IS_PHONE = "PHONE";
    private final String IS_TABLET = "TABLET";
    private final String IS_PC = "PC";

    public MemberSocial getValidEmail(String email) {
        Optional<MemberSocial> memberSocialByEmail = memberSocialRepository.findMemberByEmail(email);
        if(memberSocialByEmail.isEmpty()) throw new UsernameNotFoundException("User Not Found");
        return memberSocialByEmail.get();
    }

    public void isAccountLock(String email) {
        Optional<MemberSocial> memberByEmail = memberSocialRepository.findMemberByEmail(email);
        if(memberByEmail.isEmpty()) throw new UsernameNotFoundException("User Not Found");

        MemberSocial memberSocial = memberByEmail.get();
        if(memberSocial.getMemberStatus().equals(MemberStatus.LOCKED)) {
            throw new BadRequestRuntimeException("Your account is locked because of 5 failed Login attempts");
        }
    }

    public void isValidEmail(String email) {
        Optional<MemberSocial> memberByEmail = memberSocialRepository.findMemberByEmail(email);
        if(memberByEmail.isEmpty()) throw new UsernameNotFoundException("User Not Found");
    }

    public String getOsInfo(HttpServletRequest request) {
        String os = null;
        String agent = request.getHeader("User-Agent");
        
        if(agent.indexOf("NT 6.0") != -1)    os = "Windows Vista/Server 2008";
        else if(agent.indexOf("NT 5.2") != -1) os = "Windows Server 2003";
        else if(agent.indexOf("NT 5.1") != -1) os = "Windows XP";
        else if(agent.indexOf("NT 5.0") != -1) os = "Windows 2000";
        else if(agent.indexOf("NT") != -1)   os = "Windows NT";
        else if(agent.indexOf("9x 4.90") != -1) os = "Windows Me";
        else if(agent.indexOf("98") != -1)   os = "Windows 98";
        else if(agent.indexOf("95") != -1)   os = "Windows 95";
        else if(agent.indexOf("Win16") != -1) os = "Windows 3.x";
        else if(agent.indexOf("Windows") != -1) os = "Windows";
        else if(agent.indexOf("Linux") != -1)   os = "Linux";
        else if(agent.indexOf("Macintosh") != -1) os = "Macintosh";
        else os = "";

        return os;
    }

    /**
     * 모바일,타블렛,PC구분
     * @param req
     * @return
     */
    public String isDevice(HttpServletRequest req) {
        String userAgent = req.getHeader("User-Agent").toUpperCase();

        if(userAgent.indexOf(IS_MOBILE) > -1) {
            if(userAgent.indexOf(IS_PHONE) == -1)
                return IS_MOBILE;
            else
                return IS_TABLET;
        } else
            return IS_PC;
    }


}
