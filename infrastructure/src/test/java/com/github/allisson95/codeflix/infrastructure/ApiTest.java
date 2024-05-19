package com.github.allisson95.codeflix.infrastructure;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public interface ApiTest {

    RequestPostProcessor ADMIN_JWT = jwt()
            .authorities(new SimpleGrantedAuthority("ROLE_CATALOGO_ADMIN"));

    RequestPostProcessor CAST_MEMBER_JWT = jwt()
            .authorities(new SimpleGrantedAuthority("ROLE_CATALOGO_CASTMEMBERS"));

    RequestPostProcessor CATEGORY_JWT = jwt()
            .authorities(new SimpleGrantedAuthority("ROLE_CATALOGO_CATEGORIES"));

    RequestPostProcessor GENRE_JWT = jwt()
            .authorities(new SimpleGrantedAuthority("ROLE_CATALOGO_GENRES"));

    RequestPostProcessor VIDEO_JWT = jwt()
            .authorities(new SimpleGrantedAuthority("ROLE_CATALOGO_VIDEOS"));

}
